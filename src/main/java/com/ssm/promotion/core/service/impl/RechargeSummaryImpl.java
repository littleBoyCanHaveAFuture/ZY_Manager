package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.jedis.*;
import com.ssm.promotion.core.service.RechargeSummaryService;
import com.ssm.promotion.core.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author song minghua
 * @date 2019/11/26
 */
@Service("RechargeSummaryService")
public class RechargeSummaryImpl implements RechargeSummaryService {
    @Autowired
    JedisRechargeCache cache;

    @Override
    public List<RechargeSummary> getRechargeSummary(Map<String, Object> map,
                                                    Map<Integer, List<String>> serverIdList,
                                                    List<String> spIdList,
                                                    Integer userId) throws Exception {
        int type = Integer.parseInt(map.get("type").toString());
        Integer gameId = Integer.parseInt(map.get("gameId").toString());
        Integer serverId = Integer.parseInt(map.get("serverId").toString());
        String startTimes = map.get("startTime").toString();
        String endTimes = map.get("endTime").toString();

        //时间转化
        List<String> timeList = DateUtil.transTimes(startTimes, endTimes);

        //分游戏、区服、渠道查询
        //查询游戏自己的数据库
        switch (type) {
            case 1: {
                //全服概况
                return this.setGameRs(gameId, serverIdList, timeList);
            }
            case 2: {
                //分服概况
                return this.setServerRs(gameId, serverIdList, timeList);
            }
            case 3: {
                //渠道概况
                return this.setSpRs(gameId, serverId, spIdList, timeList);
            }
            default:
                break;
        }
        return null;
    }

    /**
     * 生成全服汇总
     *
     * @param gameId       游戏id
     * @param serverIdList 服务器-渠道Map
     * @param timeList     时间列表 yyyyMMdd
     */
    public List<RechargeSummary> setGameRs(Integer gameId, Map<Integer, List<String>> serverIdList, List<String> timeList) throws Exception {
        //该游戏全区统计
        List<RechargeSummary> serverRsList = new ArrayList<>();
        //
        for (Map.Entry<Integer, List<String>> entry : serverIdList.entrySet()) {
            Integer serverId = entry.getKey();
            List<String> spIdList = entry.getValue();

            RechargeSummary serverRs = new RechargeSummary();
            //同区服-所有渠道 时间排序的结果
            List<RechargeSummary> rsList = this.setGameTimeRs(gameId, serverId, spIdList, timeList);

            for (RechargeSummary rs : rsList) {
                serverRs.add(rs);
            }
            serverRs.calculate(1);

            //该区服结果
            serverRsList.add(serverRs);
        }
        return serverRsList;
    }

    /**
     * 生成区服汇总
     *
     * @param gameId       游戏id
     * @param serverIdList 服务器-渠道Map
     * @param timeList     时间列表 yyyyMMdd
     */
    public List<RechargeSummary> setServerRs(Integer gameId, Map<Integer, List<String>> serverIdList, List<String> timeList) throws Exception {
        List<RechargeSummary> serverRsList = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : serverIdList.entrySet()) {

            Integer serverId = entry.getKey();
            List<String> spIdList = entry.getValue();

            RechargeSummary serverRs = new RechargeSummary();

            //所有渠道的结果
            List<RechargeSummary> rsList = this.serchSpRs(gameId, serverId, spIdList, timeList);
            for (RechargeSummary rs : rsList) {
                serverRs.add(rs);
            }
            serverRs.calculate(2);
            //该区服结果
            serverRsList.add(serverRs);
        }
        return serverRsList;
    }

    /**
     * 生成渠道汇总
     *
     * @param gameId   游戏id
     * @param serverId 服务器id
     * @param timeList 时间列表 yyyyMMdd
     */
    public List<RechargeSummary> setSpRs(Integer gameId, Integer serverId, List<String> spIdList, List<String> timeList) throws Exception {
        List<RechargeSummary> serverRsList = this.serchSpRs(gameId, serverId, spIdList, timeList);
        for (RechargeSummary rs : serverRsList) {
            rs.calculate(3);
        }
        return serverRsList;
    }

    /**
     * 生成游戏汇总
     * 根据日期合并数据
     *
     * @param gameId   游戏id
     * @param spIdList 渠道列表
     * @param timeList 时间列表 yyyyMMdd
     */
    public List<RechargeSummary> setGameTimeRs(Integer gameId, Integer serverId,
                                               List<String> spIdList,
                                               List<String> timeList) throws Exception {
        Map<String, RechargeSummary> map = new LinkedHashMap<>();
        for (String time : timeList) {
            RechargeSummary timeRS = new RechargeSummary();
            //日期
            timeRS.setDate(time);
            map.put(time, timeRS);
        }

        //每个渠道 {start,end}每天的记录
        //总结：每个渠道的记录

        //gid:{gid}:sid:{sid}
        String gsKey = RedisKeyBody.genBody(2, gameId, serverId, null);
        //gid:{gid}
        String gkey = RedisKeyBody.genBody(1, gameId, serverId, null);
        //精确到游戏 UserInfo:gid:{gid}
        String userGKey = RedisGeneratorKey.genKeyTail(RedisKeyHeader.USER_INFO, gkey);
        //该游戏 所有账号
        //  BitSet allAccount = cache.getBitSet(usergKey, RedisKeyTail.GAME_ACCOUNT_ALL_NUMS);
        //该游戏 所有账号的数目
        Long allAccountSize = cache.getBitSetCount(userGKey, RedisKeyTail.GAME_ACCOUNT_ALL_NUMS);
        //活跃玩家的账号
        //  BitSet activeAccount = cache.getBitSet(userGKey, RedisKeyTail.ACTIVE_PLAYERS);

        //同区服-所有渠道
        for (String spId : spIdList) {
            //gid:{gid}:sid:{sid}:spid:{spid}
            String gssKey = RedisKeyBody.genBody(3, gameId, serverId, spId);
            //精确到游戏-区服-渠道
            String userGSSKey = RedisGeneratorKey.genKeyTail(RedisKeyHeader.USER_INFO, gssKey);
            String activeGSSKey = RedisGeneratorKey.genKeyTail(RedisKeyHeader.ACTIVE_PLAYERS_INFO, gssKey);

            //  <yyMMdd,账号数目>

            //新增创号
            Map<String, Double> timecaMap = cache.getDayBitmapCount(userGSSKey, RedisKeyTail.NEW_ADD_CREATE_ACCOUNT, timeList);
            //新增创角
            Map<String, Double> timecrMap = cache.getDayBitmapCount(userGSSKey, RedisKeyTail.NEW_ADD_CREATE_ROLE, timeList);
            //新增创角去除滚服
            Map<String, Double> timecrroMap = cache.getDayBitopAnd(userGSSKey, userGSSKey, RedisKeyTail.NEW_ADD_CREATE_ROLE, RedisKeyTail.GAME_ACCOUNT_MULTIPLE_ROLE, timeList);
            //创角率

            //活跃玩家
            Map<String, Double> timeActiveAccountMap = cache.getDayBitmapCount(userGKey, RedisKeyTail.ACTIVE_PLAYERS, timeList);
            //充值次数
            Map<String, Double> timeRechargeTimesMap = cache.getDayZScore(activeGSSKey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_TIMES, timeList);
            //充值人数
            Map<String, Double> timeRechargeAccountsMap = cache.getDayZScore(activeGSSKey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_PLAYERS, timeList);
            //充值金额
            Map<String, Double> timeRechargeAmountsMap = cache.getDayZScore(activeGSSKey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_AMOUNTS, timeList);
            //活跃付费率
            //付费ARPU
            //当日首次付费金额
            Map<String, Double> timeRechargeFirstPayersMap = cache.getDayZScore(activeGSSKey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_FIRST_AMOUNTS, timeList);
            //当日首次付费人数
            Map<String, Double> timefraMap = cache.getDayBitmapCount(activeGSSKey, RedisKeyTail.RECHARGE_ACCOUNT, timeList);
            //注册付费人数
            Map<String, Double> timeRegisteredPayersAccountMap = cache.getDayBitmapCount(activeGSSKey, RedisKeyTail.RECHARGE_ACCOUNT_NA_CA, timeList);
            //注册付费金额
            Map<String, Double> timeRegisteredPaymentMap = cache.getDayZScore(activeGSSKey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_AMOUNTS_NA_CA, timeList);
            //注册付费ARPU

            //时间排序 逐个增加
            for (String time : timeList) {
                RechargeSummary rs = map.get(time);

                //新增创号-<yyMMdd,账号数目>
                if (timecaMap.containsKey(time)) {
                    rs.setNewAddCreateAccount(rs.getNewAddCreateAccount() + timecaMap.get(time).intValue());
                }
                //新增创角-<yyMMdd,账号数目>
                if (timecrMap.containsKey(time)) {
                    rs.setNewAddCreateRole(rs.getNewAddCreateRole() + timecrMap.get(time).intValue());
                }
                //新增创角去除滚服
                if (timecrroMap.containsKey(time)) {
                    rs.setNewAddCreateRoleRemoveOld(rs.getNewAddCreateRoleRemoveOld() + timecrroMap.get(time).intValue());
                }

                //活跃玩家
                if (timeActiveAccountMap.containsKey(time)) {
                    rs.setActivePlayer(rs.getActivePlayer() + timecrroMap.get(time).intValue());
                }
                //充值次数
                if (timeRechargeTimesMap.containsKey(time)) {
                    rs.setRechargeTimes(rs.getRechargeTimes() + timeRechargeTimesMap.get(time).intValue());
                }
                //充值人数
                if (timeRechargeAccountsMap.containsKey(time)) {
                    rs.setRechargeNumber(rs.getRechargeNumber() + timeRechargeAccountsMap.get(time).intValue());
                }
                //充值金额
                if (timeRechargeAmountsMap.containsKey(time)) {
                    rs.setRechargePayment(rs.getRegisteredPayment() + timeRechargeAmountsMap.get(time).intValue());
                }

                //活跃付费率
                //付费ARPU

                //当日首次付费金额
                if (timeRechargeFirstPayersMap.containsKey(time)) {
                    rs.setNofPayment(rs.getNofPayment() + timeRechargeFirstPayersMap.get(time).intValue());
                }
                //当日首次付费人数
                if (timefraMap.containsKey(time)) {
                    rs.setNofPayers(rs.getNofPayers() + timefraMap.get(time).intValue());
                }
                //注册付费人数
                if (timeRegisteredPayersAccountMap.containsKey(time)) {
                    rs.setRegisteredPayers(rs.getRegisteredPayers() + timeRegisteredPayersAccountMap.get(time).intValue());
                }
                //注册付费金额
                if (timeRegisteredPaymentMap.containsKey(time)) {
                    rs.setRegisteredPayers(rs.getRegisteredPayers() + timeRegisteredPaymentMap.get(time).intValue());
                }
                //注册付费ARPU
            }
        }
        return (List<RechargeSummary>) map.values();
    }

    /**
     * 生成渠道汇总
     * 根据渠道合并数据
     *
     * @param gameId   游戏id
     * @param spIdList 渠道列表
     * @param timeList 时间列表 yyyyMMdd
     */
    public List<RechargeSummary> serchSpRs(Integer gameId, Integer serverId,
                                           List<String> spIdList,
                                           List<String> timeList) throws Exception {
        List<RechargeSummary> rsList = new LinkedList<>();

        //每个渠道 {start,end}每天的记录
        //总结：每个渠道的记录

        //gid:{gid}:sid:{sid}
        String gsKey = RedisKeyBody.genBody(2, gameId, serverId, null);
        //gid:{gid}
        String gkey = RedisKeyBody.genBody(1, gameId, serverId, null);
        //精确到游戏
        String userGKey = RedisGeneratorKey.genKeyTail(RedisKeyHeader.USER_INFO, gkey);

        //--所有账号
        // BitSet allAccount = cache.getBitSet(usergKey, RedisKeyTail.GAME_ACCOUNT_ALL_NUMS);
        //--所有账号的数目
        Long allAccountSize = cache.getBitSetCount(userGKey, RedisKeyTail.GAME_ACCOUNT_ALL_NUMS);
        //活跃玩家
        //  BitSet activeAccount = cache.getBitSet(usergKey, RedisKeyTail.ACTIVE_PLAYERS);


        for (String spId : spIdList) {
            //gid:{gid}:sid:{sid}:spid:{spid}
            String gssKey = RedisKeyBody.genBody(3, gameId, serverId, spId);

            //精确到游戏-区服-渠道
            String usergssKey = RedisGeneratorKey.genKeyTail(RedisKeyHeader.USER_INFO, gssKey);
            String activegssKey = RedisGeneratorKey.genKeyTail(RedisKeyHeader.ACTIVE_PLAYERS_INFO, gssKey);

            //<yyMMdd,Double>
            //新增创号
            Map<String, Double> timecaMap = cache.getDayBitmapCount(usergssKey, RedisKeyTail.NEW_ADD_CREATE_ACCOUNT, timeList);
            //新增创角
            Map<String, Double> timecrMap = cache.getDayBitmapCount(usergssKey, RedisKeyTail.NEW_ADD_CREATE_ROLE, timeList);
            //新增创角去除滚服
            Map<String, Double> timecrroMap = cache.getDayBitopAnd(usergssKey, usergssKey, RedisKeyTail.NEW_ADD_CREATE_ROLE, RedisKeyTail.GAME_ACCOUNT_MULTIPLE_ROLE, timeList);
            //创角率
            //创号转化率

            //活跃玩家
            Map<String, Double> timeActiveAccountMap = cache.getDayBitmapCount(userGKey, RedisKeyTail.ACTIVE_PLAYERS, timeList);
            //充值次数
            Map<String, Double> timeRechargeTimesMap = cache.getDayZScore(activegssKey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_TIMES, timeList);
            //充值人数
            Map<String, Double> timeRechargeAccountsMap = cache.getDayZScore(activegssKey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_PLAYERS, timeList);
            //充值金额
            Map<String, Double> timeRechargeAmountsMap = cache.getDayZScore(activegssKey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_AMOUNTS, timeList);
            //活跃付费率
            //付费ARPU
            //当日首次付费金额
            Map<String, Double> timeRechargeFirstPayersMap = cache.getDayZScore(activegssKey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_FIRST_AMOUNTS, timeList);
            //当日首次付费人数
            Map<String, Double> timefraMap = cache.getDayBitmapCount(activegssKey, RedisKeyTail.RECHARGE_ACCOUNT, timeList);
            //注册付费人数
            Map<String, Double> timeRegisteredPayersAccountMap = cache.getDayBitmapCount(activegssKey, RedisKeyTail.RECHARGE_ACCOUNT_NA_CA, timeList);
            //注册付费金额
            Map<String, Double> timeRegisteredPaymentMap = cache.getDayZScore(activegssKey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_AMOUNTS_NA_CA, timeList);
            //注册付费ARPU

            //累计充值金额
            Double timeTotalPayment = cache.getZscore(activegssKey, RedisKeyTail.RECHARGE_INFO, RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS);
            //累计创角
            Double timeTotalCreateRole = cache.getZscore(activegssKey, RedisKeyTail.RECHARGE_INFO, RedisKey.GAME_ACCUMULATION_CREATE_ROLE);
            //累计充值人数
            Double timeTotalRechargeNums = cache.getZscore(activegssKey, RedisKeyTail.RECHARGE_INFO, RedisKey.GAME_ACCUMULATION_RECHARGE_ACCOUNTS);

            //总付费率
            //渠道id
            //注收比
            //新增注收比


            RechargeSummary rs = new RechargeSummary();

            //新增创号
            rs.setNewAddCreateAccount(this.mapAddInt(timecaMap));
            //新增创角
            rs.setNewAddCreateRole(this.mapAddInt(timecrMap));
            //新增创角去除滚服
            rs.setNewAddCreateRoleRemoveOld(this.mapAddInt(timecrroMap));
            //创号转化率
            rs.setCreateAccountTransRate(0D);

            //活跃玩家
            rs.setActivePlayer(this.mapAddInt(timeActiveAccountMap));
            //充值次数
            rs.setRechargeTimes(this.mapAddInt(timeRechargeTimesMap));
            //充值人数
            rs.setRechargeNumber(this.mapAddInt(timeRechargeAccountsMap));
            //充值金额
            rs.setRechargePayment(this.mapAddInt(timeRechargeAmountsMap));
            //活跃付费率
            //付费ARPU
            //当日首次付费金额
            rs.setNofPayment(this.mapAddInt(timeRechargeFirstPayersMap));
            //当日首次付费人数
            rs.setNofPayers(this.mapAddInt(timefraMap));
            //注册付费人数
            rs.setRegisteredPayers(this.mapAddInt(timeRegisteredPayersAccountMap));
            //注册付费金额
            rs.setRegisteredPayment(this.mapAddInt(timeRegisteredPaymentMap));
            //注册付费ARPU

            //服务器id
            rs.setServerId(serverId);
            //开服天数
            rs.setOpenDay(0);
            //新增玩家
            rs.setNewaddplayer(0);

            //累计充值金额
            rs.setTotalPayment(timeTotalPayment == null ? 0D : timeTotalPayment);
            //累计创角
            rs.setTotalCreateRole(timeTotalCreateRole == null ? 0D : timeTotalCreateRole);
            //累计充值人数
            rs.setTotalRechargeNums(timeTotalRechargeNums == null ? 0D : timeTotalRechargeNums);
            //总付费率

            System.out.println("timeTotalPayment:" + timeTotalPayment);
            System.out.println("timeTotalCreateRole:" + timeTotalCreateRole);
            System.out.println("timeTotalRechargeNums:" + timeTotalRechargeNums);

            //渠道id
            rs.setSpId(Integer.parseInt(spId));
            //注收比
            //新增注收比


            System.out.println("RS : " + rs.toString());
            rsList.add(rs);
        }
        return rsList;
    }

    /**
     * Map<String, Double>
     * value值累加
     * 返回int
     */
    public int mapAddInt(Map<String, Double> map) {
        double total = 0L;
        for (Double d : map.values()) {
            total += d;
        }
        return (int) total;
    }

    /**
     * Map<String, Double>
     * value值累加
     * 返回int
     */
    public double mapAddDouble(Map<String, Double> map) {
        double total = 0L;
        for (Double d : map.values()) {
            total += d;
        }
        return total;
    }

    /**
     * 创角率：新增创角/所有账号的数目
     * 活跃付费率：充值人数/活跃玩家
     * 付费ARPU：充值金额/充值人数
     * 付费ARPU：充值金额/充值人数
     * 注册付费ARPU：注册付费金额/注册付费人数
     */
    public Map<String, Double> genAccountRate(Map<String, Double> map1, Map<String, Double> map2) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (String times : map1.keySet()) {
            Double num1 = map1.get(times);
            Double num2 = map2.get(times);
            map.put(times, num1 / num2 * 100);
        }
        return map;
    }
}



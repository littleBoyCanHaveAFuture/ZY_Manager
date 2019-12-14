package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.jedis.JedisRechargeCache;
import com.ssm.promotion.core.jedis.RedisKey;
import com.ssm.promotion.core.jedis.RedisKeyHeader;
import com.ssm.promotion.core.jedis.RedisKeyTail;
import com.ssm.promotion.core.service.RechargeSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
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
    public List<RechargeSummary> getRechargeSummary(Map<Integer, Map<Integer, List<Integer>>> sgsMap,
                                                    List<String> timeList, Integer type,
                                                    Integer userId) {
        long s = System.currentTimeMillis();

        List<RechargeSummary> rsList;

        //分游戏、区服、渠道查询
        //查询游戏自己的数据库
        if (type == 1) {                //全服概况
            rsList = this.setGameRs(sgsMap, timeList);
        } else if (type == 2) {
            //分服概况
            rsList = this.setServerRs(sgsMap, timeList);
        } else {
            //渠道概况
            rsList = this.setSpRs(sgsMap, timeList);
        }

        System.out.println("RS redis use " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - s) / 1000) + " s");

        //存储查询结果
        return rsList;
    }

    /**
     * 生成全服汇总
     * 按日期排序
     *
     * @param sgsMap   游戏id
     * @param timeList 时间列表 yyyyMMdd
     */
    public List<RechargeSummary> setGameRs(Map<Integer, Map<Integer, List<Integer>>> sgsMap, List<String> timeList) {
        //该游戏全区统计<yyyy-MM-dd,RS>
        Map<String, RechargeSummary> totalMap = new LinkedHashMap<>();

        //遍历渠道 游戏 区服
        for (Map.Entry<Integer, Map<Integer, List<Integer>>> entry : sgsMap.entrySet()) {
            Integer spId = entry.getKey();
            Map<Integer, List<Integer>> listMap = entry.getValue();

            for (Map.Entry<Integer, List<Integer>> gameEntry : listMap.entrySet()) {
                Integer gameId = gameEntry.getKey();
                List<Integer> serverIdList = gameEntry.getValue();

                //同区服-所有渠道 时间排序的结果
                Map<String, RechargeSummary> timeRsMap = this.getRsByDay(spId, gameId, serverIdList, timeList);

                for (String times : timeRsMap.keySet()) {
                    if (!totalMap.containsKey(times)) {
                        totalMap.put(times, timeRsMap.get(times));
                    } else {
                        totalMap.get(times).add(timeRsMap.get(times));
                    }
                }
            }
        }

        for (String times : totalMap.keySet()) {
            RechargeSummary totalRs = totalMap.get(times);
            totalRs.setDate(times);
            totalRs.calculate(1);
        }
        //该区服结果
        return new ArrayList<>(totalMap.values());
    }

    /**
     * 生成区服汇总
     * 按照区服排序
     *
     * @param sgsMap   游戏id
     * @param timeList 时间列表 yyyyMMdd
     */
    public ArrayList<RechargeSummary> setServerRs(Map<Integer, Map<Integer, List<Integer>>> sgsMap, List<String> timeList) {
        //渠道-游戏-全区统计<serverId,RS>
        Map<Integer, RechargeSummary> totalMap = new LinkedHashMap<>();

        //遍历渠道 游戏 区服
        for (Map.Entry<Integer, Map<Integer, List<Integer>>> entry : sgsMap.entrySet()) {
            Integer spId = entry.getKey();
            Map<Integer, List<Integer>> listMap = entry.getValue();

            for (Map.Entry<Integer, List<Integer>> gameEntry : listMap.entrySet()) {
                Integer gameId = gameEntry.getKey();
                List<Integer> serverIdList = gameEntry.getValue();

                //同渠道-同游戏- 渠道排序的结果
                Map<Integer, RechargeSummary> serverRsMap = this.getRsByServer(spId, gameId, serverIdList, timeList);

                //统计不同渠道-不同游戏-区服id相同的数据
                //统计 同一渠道同一游戏即可
                //其实没必要 这里令 sgsMap 的 key 和 嵌套的Map key 数量均为一个即可
                for (Integer serverId : serverRsMap.keySet()) {
                    if (!totalMap.containsKey(serverId)) {
                        totalMap.put(serverId, serverRsMap.get(serverId));
                    } else {
                        totalMap.get(serverId).add(serverRsMap.get(serverId));
                    }
                }
            }
        }
        for (Map.Entry<Integer, RechargeSummary> serverEntry : totalMap.entrySet()) {
            Integer serverId = serverEntry.getKey();
            RechargeSummary rs = serverEntry.getValue();
            rs.setServerId(serverId);
            rs.calculate(2);
        }

        return new ArrayList<>(totalMap.values());
    }

    /**
     * 生成渠道汇总
     * 按照渠道排序
     *
     * @param sgsMap   游戏id
     * @param timeList 时间列表 yyyyMMdd
     */
    public List<RechargeSummary> setSpRs(Map<Integer, Map<Integer, List<Integer>>> sgsMap, List<String> timeList) {
        //该游戏全区统计<spId,RS>
        Map<Integer, RechargeSummary> totalMap = new LinkedHashMap<>();

        //遍历渠道 游戏 区服
        for (Map.Entry<Integer, Map<Integer, List<Integer>>> entry : sgsMap.entrySet()) {
            Integer spId = entry.getKey();
            Map<Integer, List<Integer>> listMap = entry.getValue();

            RechargeSummary rs = new RechargeSummary();

            for (Map.Entry<Integer, List<Integer>> gameEntry : listMap.entrySet()) {
                Integer gameId = gameEntry.getKey();
                List<Integer> serverIdList = gameEntry.getValue();

                //同渠道-同游戏- 区服排序的结果
                Map<Integer, RechargeSummary> serverRsMap = this.getRsByServer(spId, gameId, serverIdList, timeList);

                for (RechargeSummary serverRs : serverRsMap.values()) {
                    rs.add(serverRs);
                }
            }
            rs.setSpId(spId);
            rs.calculate(3);
            totalMap.put(spId, rs);
        }

        return new ArrayList<>(totalMap.values());
    }

    /**
     * 以天为单位 获取充值汇总
     *
     * @param spId         渠道id
     * @param gameId       游戏id
     * @param serverIdList 区服id
     * @param timeList     时间
     * @return 返回 天数-RS 的 map
     */
    private Map<String, RechargeSummary> getRsByDay(Integer spId, Integer gameId, List<Integer> serverIdList, List<String> timeList) {
        Map<String, RechargeSummary> map = new LinkedHashMap<>();
        for (String time : timeList) {
            RechargeSummary timeRS = new RechargeSummary();
            timeRS.setDate(time);
            map.put(time, timeRS);
        }
        long s = System.currentTimeMillis();

        String userSGKey = String.format(RedisKey.FORMAT_SG_SDD, RedisKeyHeader.USER_INFO, spId, gameId);

        for (Integer serverId : serverIdList) {

            long ss = System.currentTimeMillis();

            String userSGSKey = String.format(RedisKey.FORMAT_SGS_SDDD, RedisKeyHeader.USER_INFO, spId, gameId, serverId);
            String activeSGSKey = String.format(RedisKey.FORMAT_SGS_SDDD, RedisKeyHeader.ACTIVE_PLAYERS_INFO, spId, gameId, serverId);

            //新版函数
            List<String> tailList = new ArrayList<>();
            Map<String, Map<String, Double>> resultList = new HashMap<>();

            //新增创号 渠道-游戏
            tailList.add(RedisKeyTail.NEW_ADD_CREATE_ACCOUNT);
            //新增创角去除滚服 渠道-游戏-日期
            tailList.add(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD);

            cache.getDayBitCount(userSGKey, timeList, tailList, resultList);

            Map<String, Double> timecaMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ACCOUNT);
            Map<String, Double> timecrroMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD);
            tailList.clear();

            //新增创角 渠道-游戏-区服-日期
            tailList.add(RedisKeyTail.NEW_ADD_CREATE_ROLE);
            //活跃玩家-数目 渠道-游戏-区服
            tailList.add(RedisKeyTail.ACTIVE_PLAYERS);

            cache.getDayBitCount(userSGSKey, timeList, tailList, resultList);

            Map<String, Double> timecrMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ROLE);
            Map<String, Double> timeActiveAccountMap = resultList.get(RedisKeyTail.ACTIVE_PLAYERS);
            tailList.clear();

            //当日首次付费人数
            tailList.add(RedisKeyTail.RECHARGE_ACCOUNT);
            //注册付费人数
            tailList.add(RedisKeyTail.RECHARGE_ACCOUNT_NA_CA);

            cache.getDayBitCount(activeSGSKey, timeList, tailList, resultList);

            Map<String, Double> timefraMap = resultList.get(RedisKeyTail.RECHARGE_ACCOUNT);
            Map<String, Double> timeRegisteredPayersAccountMap = resultList.get(RedisKeyTail.RECHARGE_ACCOUNT_NA_CA);
            tailList.clear();

            List<String> memberList = new ArrayList<>();
            //充值次数
            memberList.add(RedisKey.RECHARGE_TIMES);
            //充值人数
            memberList.add(RedisKey.RECHARGE_PLAYERS);
            //充值金额
            memberList.add(RedisKey.RECHARGE_AMOUNTS);
            //当日首次付费金额
            memberList.add(RedisKey.RECHARGE_FIRST_AMOUNTS);
            //注册付费金额
            memberList.add(RedisKey.RECHARGE_AMOUNTS_NA_CA);

            cache.getDayZScore(activeSGSKey, timeList, RedisKeyTail.RECHARGE_INFO, memberList, resultList);

            Map<String, Double> timeRechargeTimesMap = resultList.get(RedisKey.RECHARGE_TIMES);
            Map<String, Double> timeRechargeAccountsMap = resultList.get(RedisKey.RECHARGE_PLAYERS);
            Map<String, Double> timeRechargeAmountsMap = resultList.get(RedisKey.RECHARGE_AMOUNTS);
            Map<String, Double> timeRechargeFirstPayersMap = resultList.get(RedisKey.RECHARGE_FIRST_AMOUNTS);
            Map<String, Double> timeRegisteredPaymentMap = resultList.get(RedisKey.RECHARGE_AMOUNTS_NA_CA);


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
                    rs.setNewAddCreateRoleRemoveOld(rs.getNewAddCreateRoleRemoveOld() + (int) (double) timecrroMap.get(time));
                }

                //活跃玩家
                if (timeActiveAccountMap.containsKey(time)) {
                    rs.setActivePlayer(rs.getActivePlayer() + (int) (double) timeActiveAccountMap.get(time));
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
                    rs.setRegisteredPayment(rs.getRegisteredPayment() + timeRegisteredPaymentMap.get(time).intValue());
                }
                //注册付费ARPU
            }

            System.out.println("use : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - ss) / 1000));
        }
        System.out.println("use2 : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - s) / 1000) + "\n");
        return map;
    }

    /**
     * 以区服为单位 获取充值汇总
     *
     * @param spId         渠道id
     * @param gameId       游戏id
     * @param serverIdList 区服id
     * @param timeList     时间
     * @return 返回 区服-RS 的 map
     */
    private Map<Integer, RechargeSummary> getRsByServer(Integer spId, Integer gameId, List<Integer> serverIdList, List<String> timeList) {
        Map<Integer, RechargeSummary> map = new LinkedHashMap<>();
        for (Integer serverId : serverIdList) {
            RechargeSummary serverRs = new RechargeSummary();
            serverRs.setServerId(serverId);
            map.put(serverId, serverRs);
        }

        long s = System.currentTimeMillis();

        String userSGKey = String.format(RedisKey.FORMAT_SG_SDD, RedisKeyHeader.USER_INFO, spId, gameId);

        for (Integer serverId : serverIdList) {

            long ss = System.currentTimeMillis();

            String userSGSKey = String.format(RedisKey.FORMAT_SGS_SDDD, RedisKeyHeader.USER_INFO, spId, gameId, serverId);
            String activeSGSKey = String.format(RedisKey.FORMAT_SGS_SDDD, RedisKeyHeader.ACTIVE_PLAYERS_INFO, spId, gameId, serverId);

            //新版函数
            List<String> tailList = new ArrayList<>();
            Map<String, Map<String, Double>> resultList = new HashMap<>();

            //新增创号 渠道-游戏
            tailList.add(RedisKeyTail.NEW_ADD_CREATE_ACCOUNT);
            //新增创角去除滚服 渠道-游戏-日期
            tailList.add(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD);

            cache.getDayBitCount(userSGKey, timeList, tailList, resultList);

            Map<String, Double> timecaMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ACCOUNT);
            Map<String, Double> timecrroMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD);
            tailList.clear();

            //新增创角 渠道-游戏-区服-日期
            tailList.add(RedisKeyTail.NEW_ADD_CREATE_ROLE);
            //活跃玩家-数目 渠道-游戏-区服
            tailList.add(RedisKeyTail.ACTIVE_PLAYERS);

            cache.getDayBitCount(userSGSKey, timeList, tailList, resultList);

            Map<String, Double> timecrMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ROLE);
            Map<String, Double> timeActiveAccountMap = resultList.get(RedisKeyTail.ACTIVE_PLAYERS);
            tailList.clear();

            //当日首次付费人数
            tailList.add(RedisKeyTail.RECHARGE_ACCOUNT);
            //注册付费人数
            tailList.add(RedisKeyTail.RECHARGE_ACCOUNT_NA_CA);

            cache.getDayBitCount(activeSGSKey, timeList, tailList, resultList);

            Map<String, Double> timefraMap = resultList.get(RedisKeyTail.RECHARGE_ACCOUNT);
            Map<String, Double> timeRegisteredPayersAccountMap = resultList.get(RedisKeyTail.RECHARGE_ACCOUNT_NA_CA);
            tailList.clear();

            List<String> memberList = new ArrayList<>();
            //充值次数
            memberList.add(RedisKey.RECHARGE_TIMES);
            //充值人数
            memberList.add(RedisKey.RECHARGE_PLAYERS);
            //充值金额
            memberList.add(RedisKey.RECHARGE_AMOUNTS);
            //当日首次付费金额
            memberList.add(RedisKey.RECHARGE_FIRST_AMOUNTS);
            //注册付费金额
            memberList.add(RedisKey.RECHARGE_AMOUNTS_NA_CA);

            cache.getDayZScore(activeSGSKey, timeList, RedisKeyTail.RECHARGE_INFO, memberList, resultList);

            Map<String, Double> timeRechargeTimesMap = resultList.get(RedisKey.RECHARGE_TIMES);
            Map<String, Double> timeRechargeAccountsMap = resultList.get(RedisKey.RECHARGE_PLAYERS);
            Map<String, Double> timeRechargeAmountsMap = resultList.get(RedisKey.RECHARGE_AMOUNTS);
            Map<String, Double> timeRechargeFirstPayersMap = resultList.get(RedisKey.RECHARGE_FIRST_AMOUNTS);
            Map<String, Double> timeRegisteredPaymentMap = resultList.get(RedisKey.RECHARGE_AMOUNTS_NA_CA);


            //注册付费ARPU

            //累计充值金额
            Double timeTotalPayment = cache.getZscore(activeSGSKey, RedisKeyTail.RECHARGE_TOTAL_INFO, RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS);
            //累计创角
            Double timeTotalCreateRole = cache.getZscore(userSGKey, RedisKeyTail.ACCOUNT_INFO, RedisKey.GAME_ACCUMULATION_CREATE_ROLE);
            //累计充值人数
            Double timeTotalRechargeNums = cache.getZscore(activeSGSKey, RedisKeyTail.RECHARGE_TOTAL_INFO, RedisKey.GAME_ACCUMULATION_RECHARGE_ACCOUNTS);

            //总付费率
            //渠道id
            //注收比
            //新增注收比

            RechargeSummary rs = map.get(serverId);

            //新增创号
            rs.setNewAddCreateAccount(this.mapAddToInt(timecaMap));
            //新增创角
            rs.setNewAddCreateRole(this.mapAddToInt(timecrMap));
            //新增创角去除滚服
            rs.setNewAddCreateRoleRemoveOld(this.mapAddToInt(timecrroMap));
            //创号转化率
            rs.setCreateAccountTransRate(0D);

            //活跃玩家
            rs.setActivePlayer(this.mapAddToInt(timeActiveAccountMap));
            //充值次数
            rs.setRechargeTimes(this.mapAddToInt(timeRechargeTimesMap));
            //充值人数
            rs.setRechargeNumber(this.mapAddToInt(timeRechargeAccountsMap));
            //充值金额
            rs.setRechargePayment(this.mapAddToInt(timeRechargeAmountsMap));

            //活跃付费率
            //付费ARPU

            //当日首次付费金额
            rs.setNofPayment(this.mapAddToInt(timeRechargeFirstPayersMap));
            //当日首次付费人数
            rs.setNofPayers(this.mapAddToInt(timefraMap));
            //注册付费人数
            rs.setRegisteredPayers(this.mapAddToInt(timeRegisteredPayersAccountMap));
            //注册付费金额
            rs.setRegisteredPayment(this.mapAddToInt(timeRegisteredPaymentMap));

            //注册付费ARPU

            //服务器id
            rs.setServerId(serverId);
            //开服天数
            rs.setOpenDay(0);
            //新增玩家
            rs.setNewaddplayer(0);

            //累计充值金额
            rs.setTotalPayment(timeTotalPayment == null ? 0 : (int) (double) timeTotalPayment);
            //累计创角
            rs.setTotalCreateRole(timeTotalCreateRole == null ? 0 : (int) (double) timeTotalCreateRole);
            //累计充值人数
            rs.setTotalRechargeNums(timeTotalRechargeNums == null ? 0 : (int) (double) timeTotalRechargeNums);
            //总付费率

            //渠道id
            rs.setSpId(spId);
            //注收比
            //新增注收比

            System.out.println("use : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - ss) / 1000));
        }
        System.out.println("use2 : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - s) / 1000) + "\n");
        return map;
    }

    /**
     * Map<String, Double>
     * value值累加求和
     * 返回int
     */
    public int mapAddToInt(Map<String, Double> map) {
        double total = 0L;
        for (Double d : map.values()) {
            total += d;
        }
        return (int) total;
    }

}



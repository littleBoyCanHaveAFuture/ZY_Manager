package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.jedis.JedisRechargeCache;
import com.ssm.promotion.core.jedis.RedisKey;
import com.ssm.promotion.core.jedis.RedisKeyHeader;
import com.ssm.promotion.core.jedis.RedisKeyTail;
import com.ssm.promotion.core.service.RechargeSummaryService;
import com.ssm.promotion.core.util.DateUtil;
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
    public List<RechargeSummary> getRechargeSummary(Map<String, Object> map,
                                                    Map<Integer, List<String>> serverIdMap,
                                                    List<String> spIdList,
                                                    Integer userId) throws Exception {
        int type = Integer.parseInt(map.get("type").toString());
        Integer gameId = Integer.parseInt(map.get("gameId").toString());
        Integer serverId = Integer.parseInt(map.get("serverId").toString());
        String startTimes = map.get("startTime").toString();
        String endTimes = map.get("endTime").toString();

        long s = System.currentTimeMillis();
        //时间转化
        List<String> timeList = DateUtil.transTimes(startTimes, endTimes);
        List<RechargeSummary> rsList = null;
        //分游戏、区服、渠道查询
        //查询游戏自己的数据库
        switch (type) {
            case 1:
                //全服概况
                rsList = this.setGameRs(gameId, serverIdMap, timeList);
                break;
            case 2:
                //分服概况
                rsList = this.setServerRs(gameId, serverIdMap, timeList);
                break;
            case 3:
                //渠道概况
                rsList = this.setSpRs(gameId, serverId, spIdList, timeList);
                break;
            default:
                break;
        }
        long e = System.currentTimeMillis();
        System.out.println("RS redis use " + new DecimalFormat("0.00").format((double) (e - s) / 1000) + " s");

        //存储查询结果


        return rsList;
    }

    /**
     * 生成全服汇总
     * 游戏 -所有日期
     *
     * @param gameId       游戏id
     * @param serverIdList 服务器-渠道Map
     * @param timeList     时间列表 yyyyMMdd
     */
    public List<RechargeSummary> setGameRs(Integer gameId, Map<Integer, List<String>> serverIdList, List<String> timeList) throws Exception {
        //该游戏全区统计
        Map<String, RechargeSummary> totalMap = new LinkedHashMap<>();
        //遍历区服
        for (Map.Entry<Integer, List<String>> entry : serverIdList.entrySet()) {
            Integer serverId = entry.getKey();
            List<String> spIdList = entry.getValue();

            //同区服-所有渠道 时间排序的结果
            Map<String, RechargeSummary> timeRsMap = this.setGameTimeRs(gameId, serverId, spIdList, timeList);
            //查询结果 放入 totalMap 中
            for (String times : timeRsMap.keySet()) {
                RechargeSummary timeRs = timeRsMap.get(times);
                if (!totalMap.containsKey(times)) {
                    totalMap.put(times, timeRs);
                } else {
                    RechargeSummary totalRs = totalMap.get(times);
                    totalRs.add(timeRs);
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
     * 游戏-所有区服
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
            serverRs.setServerId(serverId);
            serverRs.calculate(2);
            //该区服结果
            serverRsList.add(serverRs);
        }
        return serverRsList;
    }

    /**
     * 生成渠道汇总
     * 游戏-区服-所有渠道
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
     * @param serverId 游戏区服id
     * @param spIdList 渠道列表
     * @param timeList 时间列表 yyyyMMdd
     */
    public Map<String, RechargeSummary> setGameTimeRs(Integer gameId, Integer serverId,
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

        //同区服-所有渠道
        for (String spId : spIdList) {
            String userSGKey = String.format(RedisKey.FORMAT_SG, RedisKeyHeader.USER_INFO, spId, gameId);
            String userSGSKey = String.format(RedisKey.FORMAT_SGS, RedisKeyHeader.USER_INFO, spId, gameId, serverId);
            String activeSGSKey = String.format(RedisKey.FORMAT_SGS, RedisKeyHeader.ACTIVE_PLAYERS_INFO, spId, gameId, serverId);

            long s = System.currentTimeMillis();
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


            System.out.println("\nuse : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - s) / 1000));

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
            System.out.println("use2 : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - s) / 1000));
        }
        return map;
    }

    /**
     * 查询redis
     * 分渠道汇总
     * 根据渠道合并数据
     *
     * @param gameId   游戏id
     * @param serverId 游戏区服id
     * @param spIdList 渠道列表
     * @param timeList 时间列表 yyyyMMdd
     */
    public List<RechargeSummary> serchSpRs(Integer gameId, Integer serverId,
                                           List<String> spIdList,
                                           List<String> timeList) {
        System.out.println(timeList.toString());
        List<RechargeSummary> rsList = new LinkedList<>();

        //每个渠道 {start,end}每天的记录
        //总结：每个渠道的记录

        for (String spId : spIdList) {
            String userSGKey = String.format(RedisKey.FORMAT_SG, RedisKeyHeader.USER_INFO, spId, gameId);
            String userSGSKey = String.format(RedisKey.FORMAT_SGS, RedisKeyHeader.USER_INFO, spId, gameId, serverId);
            String activeSGSKey = String.format(RedisKey.FORMAT_SGS, RedisKeyHeader.ACTIVE_PLAYERS_INFO, spId, gameId, serverId);

            long s = System.currentTimeMillis();
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

            System.out.println("\nuse : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - s) / 1000));

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

            printMap(timecaMap, "timecaMap");
            printMap(timecrMap, "timecrMap");
            printMap(timecrroMap, "timecrroMap");
            printMap(timecrroMap, "timecrroMap");
            printMap(timeActiveAccountMap, "timeActiveAccountMap");
            printMap(timeRechargeTimesMap, "timeRechargeTimesMap");
            printMap(timeRechargeAccountsMap, "timeRechargeAccountsMap");
            printMap(timeRechargeAmountsMap, "timeRechargeAmountsMap");
            printMap(timeRechargeFirstPayersMap, "timeRechargeFirstPayersMap");
            printMap(timefraMap, "timefraMap");
            printMap(timeRegisteredPayersAccountMap, "timeRegisteredPayersAccountMap");
            printMap(timeRegisteredPaymentMap, "timeRegisteredPaymentMap");

            RechargeSummary rs = new RechargeSummary();

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
            rs.setSpId(Integer.parseInt(spId));
            //注收比
            //新增注收比

            rsList.add(rs);
        }
        return rsList;
    }

    /**
     * Map<String, Double>
     * value值累加
     * 返回int
     */
    public int mapAddInt(Map<String, Integer> map) {
        double total = 0L;
        for (Integer d : map.values()) {
            total += d;
        }
        return (int) total;
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

    public void printMap(Map<String, Double> map, String name) {
        System.out.println("map:" + name + "----->" + map.toString());
    }
}



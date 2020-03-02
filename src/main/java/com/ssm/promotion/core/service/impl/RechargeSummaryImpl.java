package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.controller.RechargeSummaryControllr;
import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.jedis.*;
import com.ssm.promotion.core.service.RechargeSummaryService;
import org.apache.log4j.Logger;
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
    private static final Logger log = Logger.getLogger(RechargeSummaryImpl.class);
    @Autowired
    jedisRechargeCache cache;

    @Override
    public List<RechargeSummary> getRechargeSummary(Map<Integer, Map<Integer, List<Integer>>> sgsMap,
                                                    List<String> timeList, Integer type,
                                                    Integer userId) {
        long s = System.currentTimeMillis();

        List<RechargeSummary> rsList;

        //分游戏、区服、渠道查询
        //查询游戏自己的数据库
        if (type == 1) {
            //全服概况
            rsList = this.setGameRs(sgsMap, timeList);
        } else if (type == 2) {
            //分服概况
            rsList = this.setServerRs(sgsMap, timeList);
        } else {
            //渠道概况
            rsList = this.setSpRs(sgsMap, timeList);
        }

        log.info("RS redis use " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - s) / 1000) + " s");

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

        //遍历渠道
        for (Map.Entry<Integer, Map<Integer, List<Integer>>> entry : sgsMap.entrySet()) {
            Integer gameId = entry.getKey();
            Map<Integer, List<Integer>> listMap = entry.getValue();
            //同渠道 不同游戏 区服
            for (Map.Entry<Integer, List<Integer>> gameEntry : listMap.entrySet()) {
                //渠道id
                Integer spId = gameEntry.getKey();
                List<Integer> serverIdList = gameEntry.getValue();

                //同渠道-游戏 时间排序的结果
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
            Integer gameId = entry.getKey();
            Map<Integer, List<Integer>> listMap = entry.getValue();

            for (Map.Entry<Integer, List<Integer>> gameEntry : listMap.entrySet()) {
                Integer spId = gameEntry.getKey();
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
            Integer gameId = entry.getKey();
            Map<Integer, List<Integer>> listMap = entry.getValue();

            RechargeSummary rs = new RechargeSummary();

            //同一游戏 不同渠道
            for (Map.Entry<Integer, List<Integer>> gameEntry : listMap.entrySet()) {
                Integer SpId = gameEntry.getKey();
                List<Integer> serverIdList = gameEntry.getValue();
                //所有账号
//                Long allAccount = cache.getbitcount(RedisKeyNew.getKeyAccountAll(spId.toString(), gameId.toString()));
                //同渠道-同游戏- 区服排序的结果
                RechargeSummary rsBySp = this.getRsBySp(SpId, gameId, serverIdList, timeList);
//                rs.setTotalAccounts(allAccount);
                rs.add(rsBySp);
                rs.setSpId(SpId);
                rs.calculate(3);
                totalMap.put(SpId, rs);
            }
            break;
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
        Map<String, RechargeSummary> map = new HashMap<>();
        for (String time : timeList) {
            RechargeSummary timeRS = new RechargeSummary();
            timeRS.setDate(time);
            map.put(time, timeRS);
        }
        long s = System.currentTimeMillis();

        log.info("spId:" + spId);
        log.info("gameId:" + gameId);

        //<键值，<天数，分数>>
        Map<String, Map<String, Double>> resultList = new HashMap<>();

        List<String> tailList = new ArrayList<>();
        List<String> memberList = new ArrayList<>();
        Map<String, List<String>> keyMap = new HashMap<>();

        keyMap.put(RedisKeyTail.ACTIVE_PLAYERS, new ArrayList<>());
        keyMap.put(RedisKeyTail.ONLINE_PLAYERS, new ArrayList<>());
        keyMap.put(RedisKeyTail.NEW_ADD_CREATE_ROLE, new ArrayList<>());
        keyMap.put(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_ROLES_NA_CR, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS, new ArrayList<>());

        //新增创号
        tailList.add(RedisKeyTail.NEW_ADD_CREATE_ACCOUNT);
//        String keyBody = String.format(RedisKey.FORMAT_SG_SDD, RedisKeyHeader.USER_INFO, spId, gameId);
//        cache.getDayBitCount(keyBody, timeList, tailList, resultList);
        cache.getDayNewAddAccount(String.valueOf(gameId), String.valueOf(spId), timeList, resultList);
        Map<String, Double> timeCAMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ACCOUNT);

        //时间排序 逐个增加
        for (String time : timeList) {
            RechargeSummary rs = map.get(time);
            //新增创号
            if (timeCAMap.containsKey(time)) {
                rs.setNewAddCreateAccount(rs.newAddCreateAccount + timeCAMap.get(time).intValue());
            }
        }


        for (Integer serverId : serverIdList) {
            long ss = System.currentTimeMillis();
            //活跃玩家
            keyMap.get(RedisKeyTail.ACTIVE_PLAYERS).add(RedisKeyNew.getKeyRolesActiveDay(spId.toString(), gameId.toString(), serverId.toString()));
//            //在线玩家
//            keyMap.get(RedisKeyTail.ONLINE_PLAYERS).add(RedisKeyNew.getKeyRolesOnlineDay(spId.toString(), gameId.toString(), serverId.toString()));

            //新增创角
            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE).add(RedisKeyNew.getKeyRolesCreateDay(spId.toString(), gameId.toString(), serverId.toString()));
            //新增创角去除滚服
            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD).add(RedisKeyNew.getKeyRolesCreateFirst(spId.toString(), gameId.toString(), serverId.toString()));
            //注册付费金额
            keyMap.get(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR).add(RedisKeyNew.getKeyRegisterPaidAmounts(spId.toString(), gameId.toString(), serverId.toString()));
            //注册付费人数
            keyMap.get(RedisKeyTail.RECHARGE_ROLES_NA_CR).add(RedisKeyNew.getKeyRegisterPaidRoles(spId.toString(), gameId.toString(), serverId.toString()));
            //当日首次付费人数
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES).add(RedisKeyNew.getKeyFirstPaidRoles(spId.toString(), gameId.toString(), serverId.toString()));
            //当日首次付费金额
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS).add(RedisKeyNew.getKeyFirstPaidRolesAmounts(spId.toString(), gameId.toString(), serverId.toString()));
            //当前渠道的结果
            cache.getDayZScore(keyMap, timeList, resultList);

            //活跃玩家
            Map<String, Double> timeActiveAccountMap = resultList.get(RedisKeyTail.ACTIVE_PLAYERS);
            //新增创角
            Map<String, Double> timecrMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ROLE);
            //新增创角去除滚服
            Map<String, Double> timecrroMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD);
            //注册付费金额
            Map<String, Double> timeRegisteredPaymentMap = resultList.get(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR);
            //注册付费人数
            Map<String, Double> timeRegisteredPayersAccountMap = resultList.get(RedisKeyTail.RECHARGE_ROLES_NA_CR);
            //当日首次付费人数
            Map<String, Double> timefraMap = resultList.get(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES);
            //当日首次付费金额
            Map<String, Double> timeRechargeFirstPayersMap = resultList.get(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS);

            String activeSGSKey = String.format(RedisKey.FORMAT_SGS_SDDD, RedisKeyHeader.ACTIVE_PLAYERS_INFO, spId, gameId, serverId);
            memberList.add(RedisKey.RECHARGE_TIMES);
            memberList.add(RedisKey.RECHARGE_PLAYERS);
            memberList.add(RedisKey.RECHARGE_AMOUNTS);

            cache.getDayZScore(activeSGSKey, timeList, RedisKeyTail.RECHARGE_INFO, memberList, resultList);
            //充值次数
            Map<String, Double> timeRechargeTimesMap = resultList.get(RedisKey.RECHARGE_TIMES);
            //充值人数
            Map<String, Double> timeRechargeAccountsMap = resultList.get(RedisKey.RECHARGE_PLAYERS);
            //充值金额
            Map<String, Double> timeRechargeAmountsMap = resultList.get(RedisKey.RECHARGE_AMOUNTS);

//            //累计充值
//            //充值人数
//            Double timeTotalRechargeNums = cache.getZscore(RedisKeyNew.getKeyRolesPayInfoServer(spId.toString(), gameId.toString(), serverId.toString()),
//                    RedisKey.GAME_ACCUMULATION_RECHARGE_ACCOUNTS);
//            //充值金额
//            Double timeTotalPayment = cache.getZscore(RedisKeyNew.getKeyRolesPayInfoServer(spId.toString(), gameId.toString(), serverId.toString()),
//                    RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS);
//            //累计创角
//            Double timeTotalCreateRole = cache.getZscore(userSGSKey, RedisKeyTail.ACCOUNT_INFO, RedisKey.GAME_ACCUMULATION_CREATE_ROLE);

            //时间排序 逐个增加
            for (String time : timeList) {
                RechargeSummary rs = map.get(time);
                //新增创角
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
            }

            log.info("use : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - ss) / 1000));

            //重置
            keyMap.get(RedisKeyTail.ACTIVE_PLAYERS).clear();
            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE).clear();
            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD).clear();
            keyMap.get(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR).clear();
            keyMap.get(RedisKeyTail.RECHARGE_ROLES_NA_CR).clear();
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES).clear();
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS).clear();

            resultList.clear();
            memberList.clear();
        }
        //累积充值
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

        log.info("spId:" + spId);
        log.info("gameId:" + gameId);

        //<键值，<天数，分数>>
        Map<String, Map<String, Double>> resultList = new HashMap<>();

        List<String> memberList = new ArrayList<>();
        Map<String, List<String>> keyMap = new HashMap<>();

        keyMap.put(RedisKeyTail.ACTIVE_PLAYERS, new ArrayList<>());
        keyMap.put(RedisKeyTail.ONLINE_PLAYERS, new ArrayList<>());
        keyMap.put(RedisKeyTail.NEW_ADD_CREATE_ROLE, new ArrayList<>());
        keyMap.put(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_ROLES_NA_CR, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS, new ArrayList<>());

        for (Integer serverId : serverIdList) {
            long ss = System.currentTimeMillis();
            //活跃玩家
            keyMap.get(RedisKeyTail.ACTIVE_PLAYERS).add(RedisKeyNew.getKeyRolesActiveDay(spId.toString(), gameId.toString(), serverId.toString()));
//            //在线玩家
//            keyMap.get(RedisKeyTail.ONLINE_PLAYERS).add(RedisKeyNew.getKeyRolesOnlineDay(spId.toString(), gameId.toString(), serverId.toString()));
            //新增创角
            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE).add(RedisKeyNew.getKeyRolesCreateDay(spId.toString(), gameId.toString(), serverId.toString()));
//            //新增创角去除滚服
//            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD).add(RedisKeyNew.getKeyRolesCreateFirst(spId.toString(), gameId.toString(), serverId.toString()));
            //注册付费金额
            keyMap.get(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR).add(RedisKeyNew.getKeyRegisterPaidAmounts(spId.toString(), gameId.toString(), serverId.toString()));
            //注册付费人数
            keyMap.get(RedisKeyTail.RECHARGE_ROLES_NA_CR).add(RedisKeyNew.getKeyRegisterPaidRoles(spId.toString(), gameId.toString(), serverId.toString()));
            //当日首次付费人数
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES).add(RedisKeyNew.getKeyFirstPaidRoles(spId.toString(), gameId.toString(), serverId.toString()));
            //当日首次付费金额
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS).add(RedisKeyNew.getKeyFirstPaidRolesAmounts(spId.toString(), gameId.toString(), serverId.toString()));
            //当前渠道的结果
            cache.getDayZScore(keyMap, timeList, resultList);

            //活跃玩家
            Map<String, Double> timeActiveAccountMap = resultList.get(RedisKeyTail.ACTIVE_PLAYERS);
            //新增创角
            Map<String, Double> timecrMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ROLE);
//            //新增创角去除滚服
//            Map<String, Double> timecrroMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD);
            //注册付费金额
            Map<String, Double> timeRegisteredPaymentMap = resultList.get(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR);
            //注册付费人数
            Map<String, Double> timeRegisteredPayersAccountMap = resultList.get(RedisKeyTail.RECHARGE_ROLES_NA_CR);
            //当日首次付费人数
            Map<String, Double> timefraMap = resultList.get(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES);
            //当日首次付费金额
            Map<String, Double> timeRechargeFirstPayersMap = resultList.get(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS);

            String activeSGSKey = String.format(RedisKey.FORMAT_SGS_SDDD, RedisKeyHeader.ACTIVE_PLAYERS_INFO, spId, gameId, serverId);
            memberList.add(RedisKey.RECHARGE_TIMES);
            memberList.add(RedisKey.RECHARGE_PLAYERS);
            memberList.add(RedisKey.RECHARGE_AMOUNTS);

            cache.getDayZScore(activeSGSKey, timeList, RedisKeyTail.RECHARGE_INFO, memberList, resultList);
            //充值次数
            Map<String, Double> timeRechargeTimesMap = resultList.get(RedisKey.RECHARGE_TIMES);
            //充值人数
            Map<String, Double> timeRechargeAccountsMap = resultList.get(RedisKey.RECHARGE_PLAYERS);
            //充值金额
            Map<String, Double> timeRechargeAmountsMap = resultList.get(RedisKey.RECHARGE_AMOUNTS);

            //累计充值
            //充值人数
            Double timeTotalRechargeNums = cache.getZScore(RedisKeyNew.getKeyRolesPayInfoServer(spId.toString(), gameId.toString(), serverId.toString()),
                    RedisKey.GAME_ACCUMULATION_RECHARGE_ACCOUNTS);
            //充值金额
            Double timeTotalPayment = cache.getZScore(RedisKeyNew.getKeyRolesPayInfoServer(spId.toString(), gameId.toString(), serverId.toString()),
                    RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS);
            //累计创角
            Double timeTotalCreateRole = cache.getZScore(RedisKeyNew.getKeyRolesCreateServer(spId.toString(), gameId.toString(), serverId.toString()),
                    RedisKey.GAME_ACCUMULATION_CREATE_ROLE);

            RechargeSummary rs = map.get(serverId);
            //渠道id
            rs.setSpId(spId);
            //服务器id
            rs.setServerId(serverId);
            //开服天数
            rs.setOpenDay(0);

            //新增玩家
            rs.setNewaddplayer(this.mapAddToInt(timecrMap));
            //活跃玩家
            rs.setActivePlayer(this.mapAddToInt(timeActiveAccountMap));
            //充值次数
            rs.setRechargeTimes(this.mapAddToInt(timeRechargeTimesMap));
            //充值人数
            rs.setRechargeNumber(this.mapAddToInt(timeRechargeAccountsMap));
            //充值金额
            rs.setRechargePayment(this.mapAddToInt(timeRechargeAmountsMap));
            //当日首次付费金额
            rs.setNofPayment(this.mapAddToInt(timeRechargeFirstPayersMap));
            //当日首次付费人数
            rs.setNofPayers(this.mapAddToInt(timefraMap));
            //注册付费人数
            rs.setRegisteredPayers(this.mapAddToInt(timeRegisteredPayersAccountMap));
            //注册付费金额
            rs.setRegisteredPayment(this.mapAddToInt(timeRegisteredPaymentMap));
            //累计充值金额
            rs.setTotalPayment(timeTotalPayment == null ? 0 : (int) (double) timeTotalPayment);
            //累计充值人数
            rs.setTotalRechargeNums(timeTotalRechargeNums == null ? 0 : (int) (double) timeTotalRechargeNums);
            //累计创角
            rs.setTotalCreateRole(timeTotalCreateRole == null ? 0 : (int) (double) timeTotalCreateRole);

            log.info("use : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - ss) / 1000));

            //重置
            keyMap.get(RedisKeyTail.ACTIVE_PLAYERS).clear();
            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE).clear();
            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD).clear();
            keyMap.get(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR).clear();
            keyMap.get(RedisKeyTail.RECHARGE_ROLES_NA_CR).clear();
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES).clear();
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS).clear();

            memberList.clear();
            resultList.clear();
        }
        log.info("use2 : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - s) / 1000) + "\n");
        return map;
    }

    /**
     * 以渠道为单位 获取充值汇总
     *
     * @param spId         渠道id
     * @param gameId       游戏id
     * @param serverIdList 区服id
     * @param timeList     时间
     * @return 返回 区服-RS 的 map
     */
    private RechargeSummary getRsBySp(Integer spId, Integer gameId, List<Integer> serverIdList, List<String> timeList) {
        long s = System.currentTimeMillis();

        RechargeSummary sgRs = new RechargeSummary();
        sgRs.setSpId(spId);

        //<键值，<天数，分数>>
        Map<String, Map<String, Double>> resultList = new HashMap<>();

        List<String> tailList = new ArrayList<>();
        List<String> memberList = new ArrayList<>();
        Map<String, List<String>> keyMap = new HashMap<>();

        keyMap.put(RedisKeyTail.ACTIVE_PLAYERS, new ArrayList<>());
        keyMap.put(RedisKeyTail.ONLINE_PLAYERS, new ArrayList<>());
        keyMap.put(RedisKeyTail.NEW_ADD_CREATE_ROLE, new ArrayList<>());
        keyMap.put(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_ROLES_NA_CR, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES, new ArrayList<>());
        keyMap.put(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS, new ArrayList<>());

        //新增创号
        tailList.add(RedisKeyTail.NEW_ADD_CREATE_ACCOUNT);
        cache.getDayBitCount(String.format(RedisKey.FORMAT_SG_SDD, RedisKeyHeader.USER_INFO, spId, gameId), timeList, tailList, resultList);
        Map<String, Double> timecaMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ACCOUNT);
        sgRs.setNewAddCreateAccount(this.mapAddToInt(timecaMap));

        for (Integer serverId : serverIdList) {
            long ss = System.currentTimeMillis();
            //活跃玩家
            keyMap.get(RedisKeyTail.ACTIVE_PLAYERS).add(RedisKeyNew.getKeyRolesActiveDay(spId.toString(), gameId.toString(), serverId.toString()));
//            //在线玩家
//            keyMap.get(RedisKeyTail.ONLINE_PLAYERS).add(RedisKeyNew.getKeyRolesOnlineDay(spId.toString(), gameId.toString(), serverId.toString()));
            //新增创角
            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE).add(RedisKeyNew.getKeyRolesCreateDay(spId.toString(), gameId.toString(), serverId.toString()));
            //新增创角去除滚服
            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD).add(RedisKeyNew.getKeyRolesCreateFirst(spId.toString(), gameId.toString(), serverId.toString()));
            //注册付费金额
            keyMap.get(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR).add(RedisKeyNew.getKeyRegisterPaidAmounts(spId.toString(), gameId.toString(), serverId.toString()));
            //注册付费人数
            keyMap.get(RedisKeyTail.RECHARGE_ROLES_NA_CR).add(RedisKeyNew.getKeyRegisterPaidRoles(spId.toString(), gameId.toString(), serverId.toString()));
            //当日首次付费人数
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES).add(RedisKeyNew.getKeyFirstPaidRoles(spId.toString(), gameId.toString(), serverId.toString()));
            //当日首次付费金额
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS).add(RedisKeyNew.getKeyFirstPaidRolesAmounts(spId.toString(), gameId.toString(), serverId.toString()));
            //当前渠道的结果
            cache.getDayZScore(keyMap, timeList, resultList);

            //活跃玩家
            Map<String, Double> timeActiveAccountMap = resultList.get(RedisKeyTail.ACTIVE_PLAYERS);
            //新增创角
            Map<String, Double> timecrMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ROLE);
            //新增创角去除滚服
            Map<String, Double> timecrroMap = resultList.get(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD);
            //注册付费金额
            Map<String, Double> timeRegisteredPaymentMap = resultList.get(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR);
            //注册付费人数
            Map<String, Double> timeRegisteredPayersAccountMap = resultList.get(RedisKeyTail.RECHARGE_ROLES_NA_CR);
            //当日首次付费人数
            Map<String, Double> timefraMap = resultList.get(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES);
            //当日首次付费金额
            Map<String, Double> timeRechargeFirstPayersMap = resultList.get(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS);

            String activeSGSKey = String.format(RedisKey.FORMAT_SGS_SDDD, RedisKeyHeader.ACTIVE_PLAYERS_INFO, spId, gameId, serverId);
            memberList.add(RedisKey.RECHARGE_TIMES);
            memberList.add(RedisKey.RECHARGE_PLAYERS);
            memberList.add(RedisKey.RECHARGE_AMOUNTS);

            cache.getDayZScore(activeSGSKey, timeList, RedisKeyTail.RECHARGE_INFO, memberList, resultList);
            //充值次数
            Map<String, Double> timeRechargeTimesMap = resultList.get(RedisKey.RECHARGE_TIMES);
            //充值人数
            Map<String, Double> timeRechargeAccountsMap = resultList.get(RedisKey.RECHARGE_PLAYERS);
            //充值金额
            Map<String, Double> timeRechargeAmountsMap = resultList.get(RedisKey.RECHARGE_AMOUNTS);
            //累计充值
            //充值人数
            Double timeTotalRechargeNums = cache.getZScore(RedisKeyNew.getKeyRolesPayInfoServer(spId.toString(), gameId.toString(), serverId.toString()),
                    RedisKey.GAME_ACCUMULATION_RECHARGE_ACCOUNTS);
            //充值金额
            Double timeTotalPayment = cache.getZScore(RedisKeyNew.getKeyRolesPayInfoServer(spId.toString(), gameId.toString(), serverId.toString()),
                    RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS);
            //累计创角
            Double timeTotalCreateRole = cache.getZScore(RedisKeyNew.getKeyRolesCreateServer(spId.toString(), gameId.toString(), serverId.toString()),
                    RedisKey.GAME_ACCUMULATION_CREATE_ROLE);

            //新增创角
            sgRs.setNewAddCreateRole(sgRs.getNewAddCreateRole() + this.mapAddToInt(timecrMap));
            //新增创角去除滚服
            sgRs.setNewAddCreateRoleRemoveOld(sgRs.getNewAddCreateRoleRemoveOld() + this.mapAddToInt(timecrroMap));

            //活跃玩家
            sgRs.setActivePlayer(sgRs.getActivePlayer() + this.mapAddToInt(timeActiveAccountMap));
            //充值次数
            sgRs.setRechargeTimes(sgRs.getRechargeTimes() + this.mapAddToInt(timeRechargeTimesMap));
            //充值人数
            sgRs.setRechargeNumber(sgRs.getRechargeNumber() + this.mapAddToInt(timeRechargeAccountsMap));
            //充值金额
            sgRs.setRechargePayment(sgRs.getRegisteredPayment() + this.mapAddToInt(timeRechargeAmountsMap));
            //当日首次付费金额
            sgRs.setNofPayment(sgRs.getNofPayment() + this.mapAddToInt(timeRechargeFirstPayersMap));
            //当日首次付费人数
            sgRs.setNofPayers(sgRs.getNofPayers() + this.mapAddToInt(timefraMap));
            //注册付费人数
            sgRs.setRegisteredPayers(sgRs.getRegisteredPayers() + this.mapAddToInt(timeRegisteredPayersAccountMap));
            //注册付费金额
            sgRs.setRegisteredPayment(sgRs.getRegisteredPayment() + this.mapAddToInt(timeRegisteredPaymentMap));

            //累计充值人数
            sgRs.setTotalRechargeNums(sgRs.getTotalRechargeNums() + (timeTotalRechargeNums == null ? 0 : (int) (double) timeTotalRechargeNums));
            //累计充值金额
            sgRs.setTotalPayment(sgRs.getTotalPayment() + (timeTotalPayment == null ? 0 : (int) (double) timeTotalPayment));
            //累计创角
            sgRs.setTotalCreateRole(sgRs.getTotalCreateRole() + (timeTotalCreateRole == null ? 0 : (int) (double) timeTotalCreateRole));

            log.info("use : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - ss) / 1000));

            //重置
            keyMap.get(RedisKeyTail.ACTIVE_PLAYERS).clear();
            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE).clear();
            keyMap.get(RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD).clear();
            keyMap.get(RedisKeyTail.RECHARGE_AMOUNTS_NA_CR).clear();
            keyMap.get(RedisKeyTail.RECHARGE_ROLES_NA_CR).clear();
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_ROLES).clear();
            keyMap.get(RedisKeyTail.RECHARGE_FIRST_PAY_AMOUNTS).clear();

            resultList.clear();
            memberList.clear();
        }


        log.info("use2 : " + new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - s) / 1000) + "\n");

        return sgRs;
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



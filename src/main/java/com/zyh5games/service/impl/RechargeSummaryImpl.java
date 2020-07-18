package com.zyh5games.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.GameInfo;
import com.zyh5games.entity.RechargeSummary;
import com.zyh5games.jedis.JedisRechargeCache;
import com.zyh5games.jedis.RedisKey_Gen;
import com.zyh5games.jedis.RedisKey_Member;
import com.zyh5games.jedis.RedisKey_Tail;
import com.zyh5games.service.RechargeSummaryService;
import com.zyh5games.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author song minghua
 * @date 2019/11/26
 */
@Service("RechargeSummaryService")
public class RechargeSummaryImpl implements RechargeSummaryService {
    private static final Logger log = Logger.getLogger(RechargeSummaryImpl.class);
    @Autowired
    JedisRechargeCache cache;

    @Override
    public Map<String, GameInfo> getGameInfo(Integer gameId, Integer channelId, Integer serverId) {
        Map<String, GameInfo> GameInfoMap = new HashMap<>();
        //1.查询游戏id
        Set<String> GameInfoSet;
        if (gameId == -1) {
            GameInfoSet = cache.getGAMEIDInfo();
        } else {
            GameInfoSet = new HashSet<>();
            GameInfoSet.add(String.valueOf(gameId));
        }
        //2.查询游戏对应的：渠道id、渠道区服
        for (String fGameId : GameInfoSet) {
            Set<String> ChannelIdSet;
            if (channelId == -1) {
                ChannelIdSet = cache.getSPIDInfo(fGameId);
            } else {
                ChannelIdSet = new HashSet<>();
                ChannelIdSet.add(String.valueOf(channelId));
            }

            GameInfo gameInfo = new GameInfo(fGameId);

            for (String fChannelId : ChannelIdSet) {
                Set<String> ServerIdInfoSet;
                if (serverId == -1) {
                    ServerIdInfoSet = cache.getServerInfo(fGameId, fChannelId);
                } else {
                    ServerIdInfoSet = new HashSet<>();
                    ServerIdInfoSet.add(String.valueOf(serverId));
                }
                gameInfo.addServerInfo(fChannelId, ServerIdInfoSet);
            }
            GameInfoMap.put(fGameId, gameInfo);
        }
        for (GameInfo gameInfo : GameInfoMap.values()) {
            log.info("RS集合：" + gameInfo.toString());
        }
        return GameInfoMap;

    }

    /**
     * 生成全服汇总
     * 按日期排序
     *
     * @param gameInfoMap
     * @param timeList
     */
    @Override
    public List<RechargeSummary> getDayResult(Map<String, GameInfo> gameInfoMap, List<String> timeList) {
        //该游戏全区统计<yyyy-MM-dd,RS>
        Map<String, RechargeSummary> totalMap = new LinkedHashMap<>();
        for (String day : timeList) {
            RechargeSummary rs = new RechargeSummary();
            rs.setDate(day);
            totalMap.put(day, rs);
        }

        for (Map.Entry<String, GameInfo> entry : gameInfoMap.entrySet()) {
            //游戏id
            String gameId = entry.getKey();
            GameInfo gameInfo = entry.getValue();

            Map<String, Set<String>> spInfo = gameInfo.getSpInfo();

            for (Map.Entry<String, Set<String>> gameEntry : spInfo.entrySet()) {
                //渠道id
                String channelId = gameEntry.getKey();
                Set<String> serverIdSet = gameEntry.getValue();

                //游戏、同一渠道合并结果- 时间排序的结果
                Map<String, RechargeSummary> result = this.getRs_Day(gameId, channelId, serverIdSet, timeList);

                for (String times : result.keySet()) {
                    if (!totalMap.containsKey(times)) {
                        totalMap.put(times, result.get(times));
                    } else {
                        totalMap.get(times).add(result.get(times));
                    }
                }
            }
            //存储redis查询结果
            cache.setRSByDay(gameId, timeList, totalMap);
            //同一游戏活跃玩家
            for (String day : totalMap.keySet()) {
                cache.setRS_Active(gameId, null, null, null, totalMap.get(day), 1, day);
            }
        }

        for (String day : totalMap.keySet()) {
            RechargeSummary totalRs = totalMap.get(day);
            totalRs.calculate(1);
        }

        return new ArrayList<>(totalMap.values());
    }

    /**
     * 生成分服概况
     * 按服务器区服排序
     * 仅能查询一个游戏
     *
     * @param gameInfo
     * @param timeList
     */
    @Override
    public List<RechargeSummary> getServerResult(GameInfo gameInfo, List<String> timeList) {
        //这段时间的查询结果<区服id,结果>
        Map<String, RechargeSummary> totalMap = new LinkedHashMap<>();
        //获取不同渠道所有的区服-大小排序
        Set<String> sortSet = StringUtils.changeSet(gameInfo.getServerInfo());
        Map<String, Set<String>> channelInfoMap = gameInfo.getSpInfo();
        for (String serverId : sortSet) {
            RechargeSummary rs = new RechargeSummary();
            rs.setServerId(Integer.parseInt(serverId));
            totalMap.put(serverId, rs);
        }
        String gameId = gameInfo.getGameId();


//        Map<String, Map<String, Double>> resultList = new HashMap<>();
//        // 区服id 新增账号
//        Map<String, Double> newaddPlayer = new HashMap<>();
//        Set<String> serverIdSet = cache.getGameServerInfo(gameId);
//        for (String serverId : serverIdSet) {
//            //未生成的数据的日期
//            List<String> unCalDay = new ArrayList<>();
//            //已生成的数据的日期-数据
//            Map<String, String> rssMap = new HashMap<>();
//            //获取数据
//            cache.getRSByServer(gameId, serverId, timeList, unCalDay, rssMap);
//            cache.getDayNewAddAccount_Server(gameId, serverId, unCalDay, resultList);
//
//            Double score = 0D;
//            for (String day : unCalDay) {
//                Map<String, Double> timeCAServerMap = resultList.get(RedisKey_Tail.NEW_ADD_FIRST_ACCOUNT);
//                //这个数据和游戏区服有关 和渠道无关 不需要多次计算
//                if (timeCAServerMap != null && timeCAServerMap.containsKey(day)) {
//                    score += timeCAServerMap.get(day);
//                }
//            }
//            if (!newaddPlayer.containsKey(serverId)) {
//                newaddPlayer.put(serverId, score);
//            }
//        }

        //查询-游戏、不同渠道- 相同区服合并，区服id排序的结果
        for (Map.Entry<String, Set<String>> gameEntry : channelInfoMap.entrySet()) {
            int calNACA = 3;
            //渠道id
            String channelId = gameEntry.getKey();

            //先计算每个区服该时间段的数据-再计算所有区服改时间段的数据
            for (String serverId : sortSet) {
                //未生成的数据的日期
                List<String> unCalDay = new ArrayList<>();
                //已生成的数据的日期-数据
                Map<String, String> rssMap = new HashMap<>();
                //获取数据
                cache.getRSByServer(gameId, serverId, timeList, unCalDay, rssMap);

                //游戏、同一渠道合并结果- 时间排序的结果
                Map<String, RechargeSummary> result = new HashMap<>();
                if (unCalDay.size() > 0) {
                    result = this.getRsDataFromRedis(gameId, channelId, serverId, unCalDay, calNACA);
                    calNACA = 2;

                }
                if (rssMap.size() > 0) {
                    for (Map.Entry<String, String> entry : rssMap.entrySet()) {
                        //string-json-object
                        String value = entry.getValue().trim();
                        value = value.substring(1, value.length() - 1);
                        JSONObject jsonObject = JSONObject.parseObject(value);
                        result.put(entry.getKey(), JSONObject.toJavaObject(jsonObject, RechargeSummary.class));
                    }
                }
                //该区服这段时间数据之和
                RechargeSummary rs = new RechargeSummary();
                for (Map.Entry<String, RechargeSummary> serverEntry : result.entrySet()) {
                    rs.add(serverEntry.getValue());
                }
                //存储到redis
                cache.setRSByServer(gameId, serverId, timeList, result);
                //存储结果
                totalMap.get(serverId).add(rs);
                //设置累积充值
                this.setRS_ACC(gameId, channelId, serverId, timeList.get(0), timeList.get(timeList.size() - 1), totalMap.get(serverId));
                //设置活跃玩家
                cache.setRS_Active(gameId, null, serverId, timeList, totalMap.get(serverId), 3, null);
            }
        }
        for (Map.Entry<String, RechargeSummary> serverEntry : totalMap.entrySet()) {
            RechargeSummary rs = serverEntry.getValue();
            rs.calculate(2);
            String serverId = String.valueOf(rs.getServerId());
//            int newaddplayer = newaddPlayer.containsKey(serverId) ? newaddPlayer.get(serverId).intValue() : 0;
//            rs.setNewaddplayer(newaddplayer);
        }

        return new ArrayList<>(totalMap.values());
    }

    /**
     * 生成分渠道概况
     * 按渠道排序
     *
     * @param gameInfo
     * @param timeList
     */
    @Override
    public List<RechargeSummary> getChannelResult(GameInfo gameInfo, List<String> timeList) {
        Map<String, RechargeSummary> totalMap = new LinkedHashMap<>();
        Map<String, Set<String>> channelInfoMap = gameInfo.getSpInfo();
        //渠道id 小大排序
        Set<String> channelSet = StringUtils.changeSet(channelInfoMap.keySet());
        for (String channelId : channelSet) {
            RechargeSummary rs = new RechargeSummary();
            rs.setSpId(Integer.parseInt(channelId));
            totalMap.put(channelId, rs);
        }
        String gameId = gameInfo.getGameId();
        //查询-游戏、不同渠道- 相同区服合并，区服id排序的结果
        for (Map.Entry<String, Set<String>> gameEntry : channelInfoMap.entrySet()) {
            //渠道id
            String channelId = gameEntry.getKey();
            Set<String> serverIdSet = gameEntry.getValue();
            //未生成的数据的日期
            List<String> unCalDay = new ArrayList<>();
            //已生成的数据的日期-数据
            Map<String, String> rssMap = new HashMap<>();
            //获取数据
            cache.getRSByChannel(gameId, channelId, timeList, unCalDay, rssMap);
            //游戏、同一渠道合并结果- 时间排序的结果
            Map<String, RechargeSummary> result = new HashMap<>();

            //查询结果
            RechargeSummary rs = new RechargeSummary();
            int calNACA = 1;
            //先计算每个区服该时间段的数据-再计算所有区服改时间段的数据
            for (String serverId : serverIdSet) {
                Map<String, RechargeSummary> dayResult = new HashMap<>();
                if (unCalDay.size() > 0) {
                    dayResult = this.getRsDataFromRedis(gameId, channelId, serverId, unCalDay, calNACA);
                    calNACA = 0;
                }
                if (rssMap.size() > 0) {
                    for (Map.Entry<String, String> entry : rssMap.entrySet()) {
                        //string-json-object
                        String value = entry.getValue().trim();
                        value = value.substring(1, value.length() - 1);
                        JSONObject jsonObject = JSONObject.parseObject(value);
                        dayResult.put(entry.getKey(), JSONObject.toJavaObject(jsonObject, RechargeSummary.class));
                    }
                }
                //汇总
                for (Map.Entry<String, RechargeSummary> RsEntry : dayResult.entrySet()) {
                    if (result.containsKey(RsEntry.getKey())) {
                        result.get(RsEntry.getKey()).add(RsEntry.getValue());
                    } else {
                        result.put(RsEntry.getKey(), RsEntry.getValue());
                    }

                }
                //设置累积充值
                this.setRS_ACC(gameId, channelId, serverId, timeList.get(0), timeList.get(timeList.size() - 1), totalMap.get(channelId));
            }
            //汇总
            for (Map.Entry<String, RechargeSummary> RsEntry : result.entrySet()) {
                rs.add(RsEntry.getValue());
            }
            //存储到redis
            cache.setRSByChannel(gameId, channelId, unCalDay, result);
            //设置活跃玩家
            cache.setRS_Active(gameId, channelId, null, timeList, totalMap.get(channelId), 2, null);
            //存储结果
            totalMap.get(channelId).add(rs);
        }
        for (Map.Entry<String, RechargeSummary> serverEntry : totalMap.entrySet()) {
            RechargeSummary rs = serverEntry.getValue();
            rs.calculate(3);
        }

        return new ArrayList<>(totalMap.values());
    }

    /**
     * 通过查询redis获取数据
     */
    private Map<String, RechargeSummary> getRs_Day(String gameId, String channelId,
                                                   Set<String> serverIdSet, List<String> timeList) {
        Map<String, RechargeSummary> rsMap = new HashMap<>();
        //未生成的数据的日期
        List<String> unCalDay = new ArrayList<>();
        //已生成的数据的日期-数据
        Map<String, String> rssMap = new HashMap<>();
        //获取数据
        cache.getRSByDay(gameId, timeList, unCalDay, rssMap);

        int calNACA = 1;
        if (unCalDay.size() > 0) {
            for (String serverId : serverIdSet) {
                Map<String, RechargeSummary> result = this.getRsDataFromRedis(gameId, channelId, serverId, unCalDay, calNACA);
                calNACA = 0;

                for (String times : result.keySet()) {
                    if (!rsMap.containsKey(times)) {
                        rsMap.put(times, result.get(times));
                    } else {
                        rsMap.get(times).add(result.get(times));
                    }
                }
            }
        }

        if (rssMap.size() > 0) {
            for (Map.Entry<String, String> entry : rssMap.entrySet()) {
                //string-json-object
                String value = entry.getValue().trim();
                value = value.substring(1, value.length() - 1);
                JSONObject jsonObject = JSONObject.parseObject(value);
                rsMap.put(entry.getKey(), JSONObject.toJavaObject(jsonObject, RechargeSummary.class));
            }
        }
        return rsMap;
    }

    /**
     * 需要查询的游戏、渠道、区服 及时间段
     */
    private Map<String, RechargeSummary> getRsDataFromRedis(String gameId, String channelId, String serverId, List<String> unCalDay, Integer type) {
        if (unCalDay == null || unCalDay.size() == 0) {
            return null;
        }
        //准备计算的日期
        Map<String, RechargeSummary> map = new HashMap<>();
        for (String day : unCalDay) {
            RechargeSummary rs = new RechargeSummary();
            rs.setDate(day);
            map.put(day, rs);
        }
        //<键值，<天数，分数>>
        Map<String, Map<String, Double>> resultList = new HashMap<>();
        //键值,
        Map<String, List<String>> keyMap = new HashMap<>();

        keyMap.put(RedisKey_Tail.NEW_ADD_CREATE_ROLE, new ArrayList<>());
        keyMap.put(RedisKey_Tail.NEW_ADD_CREATE_ROLE_RM_OLD, new ArrayList<>());
        keyMap.put(RedisKey_Tail.RECHARGE_ACCOUNTS_AMOUNTS, new ArrayList<>());
        keyMap.put(RedisKey_Tail.RECHARGE_ACCOUNT_NA_CA, new ArrayList<>());
        keyMap.put(RedisKey_Tail.RECHARGE_FIRST_PAY_ROLES, new ArrayList<>());
        keyMap.put(RedisKey_Tail.RECHARGE_FIRST_PAY_AMOUNTS, new ArrayList<>());

        /*redis键类型：位图(bitmap)
         *新增创号
         */
        if (type == 1) {
            cache.getDayNewAddAccount(gameId, channelId, unCalDay, resultList);
        } else if (type == 2) {
//            cache.getDayNewAddAccount_Server(gameId, serverId, unCalDay, resultList);
        } else if (type == 3) {
            cache.getDayNewAddAccount(gameId, channelId, unCalDay, resultList);
//            cache.getDayNewAddAccount_Server(gameId, serverId, unCalDay, resultList);
        }
        //redis键类型：有序集合(sorted set)
        //新增创角
        keyMap.get(RedisKey_Tail.NEW_ADD_CREATE_ROLE).add(RedisKey_Gen.get_RolesCreate_Day(channelId, gameId, serverId));
        //新增创角去除滚服
        keyMap.get(RedisKey_Tail.NEW_ADD_CREATE_ROLE_RM_OLD).add(RedisKey_Gen.get_RolesCreate_First(channelId, gameId, serverId));
        //注册付费金额
        keyMap.get(RedisKey_Tail.RECHARGE_ACCOUNTS_AMOUNTS).add(RedisKey_Gen.get_RegisterPaid_Amounts(channelId, gameId, serverId));
        //注册付费账号数
        keyMap.get(RedisKey_Tail.RECHARGE_ACCOUNT_NA_CA).add(RedisKey_Gen.get_RegisterPaid_Accounts(channelId, gameId, serverId));
        //当日首次付费人数
        keyMap.get(RedisKey_Tail.RECHARGE_FIRST_PAY_ROLES).add(RedisKey_Gen.get_FirstPaid_Roles(channelId, gameId, serverId));
        //当日首次付费金额
        keyMap.get(RedisKey_Tail.RECHARGE_FIRST_PAY_AMOUNTS).add(RedisKey_Gen.get_FirstPaid_Roles_Amounts(channelId, gameId, serverId));
        //当前渠道的结果
        cache.getDayZScore(keyMap, unCalDay, resultList);
        //充值次数、充值人数、充值金额
        cache.getDayPayInfo(gameId, channelId, serverId, unCalDay, resultList);


        //取值 分服概况数据
        // 游戏-区服-天
//        Map<String, Double> timeCAServerMap = resultList.get(RedisKey_Tail.NEW_ADD_FIRST_ACCOUNT);


        //新增创号
        Map<String, Double> timeCAMap = resultList.get(RedisKey_Tail.NEW_ADD_CREATE_ACCOUNT);
        //新增创角
        Map<String, Double> timecrMap = resultList.get(RedisKey_Tail.NEW_ADD_CREATE_ROLE);
        //新增创角去除滚服
        Map<String, Double> timecrroMap = resultList.get(RedisKey_Tail.NEW_ADD_CREATE_ROLE_RM_OLD);
        //注册付费金额
        Map<String, Double> timeRegisteredPaymentMap = resultList.get(RedisKey_Tail.RECHARGE_ACCOUNTS_AMOUNTS);
        //注册付费账号数目
        Map<String, Double> timeRegisteredPayersAccountMap = resultList.get(RedisKey_Tail.RECHARGE_ACCOUNT_NA_CA);
        //当日首次付费人数
        Map<String, Double> timefraMap = resultList.get(RedisKey_Tail.RECHARGE_FIRST_PAY_ROLES);
        //当日首次付费金额
        Map<String, Double> timeRechargeFirstPayersMap = resultList.get(RedisKey_Tail.RECHARGE_FIRST_PAY_AMOUNTS);
        //充值次数
        Map<String, Double> timeRechargeTimesMap = resultList.get(RedisKey_Member.RECHARGE_TIMES);
        //充值人数
        Map<String, Double> timeRechargeAccountsMap = resultList.get(RedisKey_Member.RECHARGE_PLAYERS);
        //充值金额
        Map<String, Double> timeRechargeAmountsMap = resultList.get(RedisKey_Member.RECHARGE_AMOUNTS);

        for (String day : unCalDay) {
            RechargeSummary rs = map.get(day);
            if (type == 1 || type == 3) {
                //新增创号
                if (timeCAMap.containsKey(day)) {
                    rs.setNewAddCreateAccount(rs.getNewAddCreateAccount() + timeCAMap.get(day).intValue());
                }
            }
//            if (type == 2 || type == 3) {
//                Double score = 0D;
//                //这个数据和游戏区服有关 和渠道无关 不需要多次计算
//                if (timeCAServerMap != null && timeCAServerMap.containsKey(day)) {
//                    score = timeCAServerMap.get(day);
//                }
//                rs.setNewaddplayer(rs.getNewaddplayer() + score.intValue());
//            }
            //新增创角
            if (timecrMap.containsKey(day)) {
                rs.setNewAddCreateRole(rs.getNewAddCreateRole() + timecrMap.get(day).intValue());
            }
            //新增创角去除滚服
            if (timecrroMap.containsKey(day)) {
                rs.setNewAddCreateRoleRemoveOld(rs.getNewAddCreateRoleRemoveOld() + (int) (double) timecrroMap.get(day));
            }
            //注册付费金额
            if (timeRegisteredPaymentMap.containsKey(day)) {
                rs.setRegisteredPayment(rs.getRegisteredPayment() + timeRegisteredPaymentMap.get(day).intValue());
            }
            //注册付费人数
            if (timeRegisteredPayersAccountMap.containsKey(day)) {
                rs.setRegisteredPayers(rs.getRegisteredPayers() + timeRegisteredPayersAccountMap.get(day).intValue());
            }
            //当日首次付费人数
            if (timefraMap.containsKey(day)) {
                rs.setNofPayers(rs.getNofPayers() + timefraMap.get(day).intValue());
            }
            //当日首次付费金额
            if (timeRechargeFirstPayersMap.containsKey(day)) {
                rs.setNofPayment(rs.getNofPayment() + timeRechargeFirstPayersMap.get(day).intValue());
            }
            //充值次数
            if (timeRechargeTimesMap.containsKey(day)) {
                rs.setRechargeTimes(rs.getRechargeTimes() + timeRechargeTimesMap.get(day).intValue());
            }
            //充值人数
            if (timeRechargeAccountsMap.containsKey(day)) {
                rs.setRechargeNumber(rs.getRechargeNumber() + timeRechargeAccountsMap.get(day).intValue());
            }
            //充值金额
            if (timeRechargeAmountsMap.containsKey(day)) {
                rs.setRechargePayment(rs.getRechargePayment() + timeRechargeAmountsMap.get(day).intValue());
            }
        }

        return map;
    }

    /**
     * 计算累积充值
     * 给rs赋值
     */
    public void setRS_ACC(String gameId, String channelId, String serverId, String startDay, String endDay, RechargeSummary rs) {
        // 累积充值金额从开始日期算起+每天充值金额之和|到结束日期截止
        if (rs == null) {
            log.error("rs == null");
            return;
        }

        //累计充值-充值人数
        Double timeTotalRechargeNums = cache.getZScore(RedisKey_Gen.get_RolesPayTimes_Server_Day(channelId, gameId, serverId), endDay);
        //累计充值-充值金额
        Double timeTotalPayment = cache.getZScore(RedisKey_Gen.get_RolesPayInfo_Server_Day(channelId, gameId, serverId), endDay);
        //累计充值-累计创角
        Double timeTotalCreateRole = cache.getZScore(RedisKey_Gen.get_RolesCreate_Server_Day(channelId, gameId, serverId), endDay);


        //累计充值人数
        rs.setTotalRechargeNums(rs.getTotalRechargeNums() + (timeTotalRechargeNums == null ? 0 : (int) (double) timeTotalRechargeNums));
        //累计充值金额
        rs.setTotalPayment(rs.getTotalPayment() + (timeTotalPayment == null ? 0 : (int) (double) timeTotalPayment));
        //累计创角
        rs.setTotalCreateRole(rs.getTotalCreateRole() + (timeTotalCreateRole == null ? 0 : (int) (double) timeTotalCreateRole));
    }

    @Override
    public List<RechargeSummary> rsDay(Map<String, Set<String>> channelMap, Set<String> serverSet, List<String> dayList) {
        Map<String, RechargeSummary> dayMap = new LinkedHashMap<>();
        RechargeSummary total = new RechargeSummary();
        total.setDate("游戏汇总");
        dayMap.put("游戏汇总", total);

        for (String day : dayList) {
            RechargeSummary rs = new RechargeSummary();
            rs.setDate(day);
            dayMap.put(day, rs);
        }

        for (Map.Entry<String, Set<String>> entry : channelMap.entrySet()) {
            String appId = entry.getKey();
            Set<String> channelSet = entry.getValue();


            cache.getRsDayInfo(dayMap, dayList, appId, serverSet, channelSet);


        }

        for (String day : dayList) {
            RechargeSummary rs = dayMap.get(day);
            total.addDayInfo(rs);
            rs.calculate(1);
        }
        total.calculate(1);

        return new ArrayList<>(dayMap.values());
    }

    @Override
    public List<RechargeSummary> rsChannel(Map<String, Set<String>> channelIdMap, Set<String> serverSet, List<String> dayList) {
        Map<String, RechargeSummary> channelMap = new LinkedHashMap<>();
        RechargeSummary total = new RechargeSummary();
        total.setSpId(-1);
        channelMap.put("-1", total);


        for (Map.Entry<String, Set<String>> entry : channelIdMap.entrySet()) {
            String appId = entry.getKey();
            Set<String> channelSet = entry.getValue();
            for (String channel : channelSet) {
                RechargeSummary rs = new RechargeSummary();
                rs.setSpId(Integer.parseInt(channel));
                channelMap.put(channel, rs);
            }
            cache.getRsChannelInfo(channelMap, dayList, appId, serverSet, channelSet);

        }
        for (Map.Entry<String, Set<String>> entry : channelIdMap.entrySet()) {
            String appId = entry.getKey();
            Set<String> channelSet = entry.getValue();
            for (String channel : channelSet) {
                RechargeSummary rs = channelMap.get(channel);
                total.addDayInfo(rs);
                rs.calculate(2);
            }
        }
        total.calculate(2);

        return new ArrayList<>(channelMap.values());
    }
}



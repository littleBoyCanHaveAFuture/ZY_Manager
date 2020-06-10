package com.zyh5games.service.impl;

import com.zyh5games.entity.GameInfo;
import com.zyh5games.entity.RechargeSummary;
import com.zyh5games.jedis.JedisRechargeCache;
import com.zyh5games.service.NewRechargeSummaryService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author song minghua
 * @date 2020/06/05
 */
@Service("NewRechargeSummaryService")
public class NewRechargeSummaryImpl implements NewRechargeSummaryService {
    private static final Logger log = Logger.getLogger(NewRechargeSummaryImpl.class);
    @Autowired
    JedisRechargeCache cache;

    @Override
    public Map<String, GameInfo> getGameInfo(Integer gameId, Integer channelId, Integer serverId) {
        Map<String, GameInfo> gameInfoMap = new HashMap<>();
        //1.查询游戏id
        Set<String> gameInfoSet;
        if (gameId == -1) {
            gameInfoSet = cache.getGAMEIDInfo();
        } else {
            gameInfoSet = new HashSet<>();
            gameInfoSet.add(String.valueOf(gameId));
        }

        for (String fGameId : gameInfoSet) {
            GameInfo gameInfo = new GameInfo(fGameId);

            // 2. 游戏对应的区服id-排重
            Set<String> serverIdset = new HashSet<>();
            if (serverId == -1) {
                serverIdset.addAll(cache.getGameServerInfo(fGameId));
            } else {
                serverIdset.add(String.valueOf(serverId));
            }

            if (serverIdset.size() > 0) {
                gameInfo.addServerInfo(serverIdset);
            }

            // 3.渠道id
            Set<String> channelIdSet = new HashSet<>();
            if (channelId == -1) {
                channelIdSet.addAll(cache.getSPIDInfo(fGameId));
            } else {
                channelIdSet.add(String.valueOf(channelId));
            }

            if (channelIdSet.size() > 0) {
                gameInfo.addChannelInfo(channelIdSet);
            }

            gameInfoMap.put(fGameId, gameInfo);
        }

        for (GameInfo gameInfo : gameInfoMap.values()) {
            log.info("RS集合：" + gameInfo.toString());
        }

        return gameInfoMap;
    }

    /**
     * 生成全服汇总
     * 按日期排序
     */
    @Override
    public List<RechargeSummary> getDayResult(GameInfo gameInfo, List<String> timeList) {
        //该游戏全区统计<yyyy-MM-dd,RS>-查询的天数排序
        Map<String, RechargeSummary> totalMap = new LinkedHashMap<>();
        for (String day : timeList) {
            RechargeSummary rs = new RechargeSummary();
            rs.setDate(day);
            totalMap.put(day, rs);
        }

        //游戏id-渠道id-区服id
        String gameId = gameInfo.getGameId();
        Set<String> spIdSet = gameInfo.getSpIdInfo();
        Set<String> serverIdSet = gameInfo.getServerInfo();

        //按天计算-不同渠道-所有区服-同一天的数据
        for (String channelId : spIdSet) {
//            Map<String, RechargeSummary> result = this.getDayRechargeSummary(gameId, channelId, serverIdSet, timeList);
        }


//            for (String times : result.keySet()) {
//                if (!totalMap.containsKey(times)) {
//                    totalMap.put(times, result.get(times));
//                } else {
//                    totalMap.get(times).add(result.get(times));
//                }
//            }

        //存储redis查询结果
//            cache.setRSByDay(gameId, timeList, totalMap);
        //同一游戏活跃玩家
//            for (String day : totalMap.keySet()) {
//                cache.setRS_Active(gameId, null, null, null, totalMap.get(day), 1, day);
//            }


        for (String day : totalMap.keySet()) {
            RechargeSummary totalRs = totalMap.get(day);
            totalRs.calculate(1);
        }

        return new ArrayList<>(totalMap.values());
    }

//    private Map<String, RechargeSummary> getDayRechargeSummary(String gameId, String channelId, Set<String> serverIdSet, List<String> timeList) {
//        //同一游戏-渠道 每天数据汇总
//        //准备计算的日期
//        Map<String, RechargeSummary> daySummaryMap = new HashMap<>();
//        for (String day : timeList) {
//            RechargeSummary rs = new RechargeSummary();
//            rs.setDate(day);
//            rs.setSpId(Integer.parseInt(channelId));
//            daySummaryMap.put(day, rs);
//        }
//        //新增创角
//
//
//    }
//    private Map<String,String> getZscoreKey(){
//
//    }

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
        return null;
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
        return null;
    }

}



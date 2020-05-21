package com.zyh5games.service;

import com.zyh5games.entity.GameInfo;
import com.zyh5games.entity.RechargeSummary;

import java.util.List;
import java.util.Map;


/**
 * @author Administrator
 */
public interface RechargeSummaryService {
    /**
     * 获取需要查询的游戏、渠道、区服信息
     */
    Map<String, GameInfo> getGameInfo(Integer gameId, Integer channelId, Integer serverId) throws Exception;

    /**
     * 生成全服概况
     * 按日期排序
     */
    List<RechargeSummary> getDayResult(Map<String, GameInfo> gameInfoMap, List<String> timeList);

    /**
     * 生成分渠道概况
     * 按渠道排序
     */
    List<RechargeSummary> getChannelResult(GameInfo gameInf, List<String> timeList);

    /**
     * 生成分服概况
     * 按服务器排序
     */
    List<RechargeSummary> getServerResult(GameInfo gameInfo, List<String> timeList) throws Exception;


}

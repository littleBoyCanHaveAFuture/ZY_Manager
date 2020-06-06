package com.zyh5games.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author song minghua
 * @date 2020/1/21
 */
@Data
public class GameInfo {
    //游戏id
    private String gameId;
    //渠道对应区服id
    private Map<String, Set<String>> spInfo;
    //排重后的区服id
    private Set<String> serverInfo;
    //对应渠道id
    private Set<String> spIdInfo;

    public GameInfo(String gameId) {
        this.gameId = gameId;
    }

    public void addServerInfo(String spId, Set<String> serverInfo) {
        if (spInfo == null) {
            spInfo = new HashMap<>();
        }
        if (this.serverInfo == null) {
            this.serverInfo = new HashSet<>();
        }
        if (!spInfo.containsKey(spId)) {
            spInfo.put(spId, serverInfo);
            this.serverInfo.addAll(serverInfo);
        }
    }

    public void addServerInfo(Set<String> serverInfo) {
        if (this.serverInfo == null) {
            this.serverInfo = new HashSet<>();
        }
        this.serverInfo.addAll(serverInfo);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : spInfo.entrySet()) {
            stringBuilder.append("\n")
                    .append("channelId=").append(entry.getKey())
                    .append("\tvalue=").append(entry.getValue());
        }
        return stringBuilder.toString();
    }

    public void addChannelInfo(Set<String> channelIdSet) {
        if (this.spIdInfo == null) {
            this.spIdInfo = new HashSet<>();
        }
        this.spIdInfo.addAll(channelIdSet);

    }
}

package com.ssm.promotion.core.entity;

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
    public String gameId;
    public Set<String> spIdSet;
    public Map<String, Set<String>> spInfo;

    public GameInfo(String gameId) {
        this.gameId = gameId;
        spIdSet = new HashSet<>();
        spInfo = new HashMap<>();
    }

    public void addServerInfo(String spId, Set<String> serverInfo) {
        if (!spInfo.containsKey(spId)) {
            spInfo.put(spId, serverInfo);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        for (Map.Entry<String, Set<String>> entry : spInfo.entrySet()) {
            stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
        }
        return stringBuilder.toString();
    }
}

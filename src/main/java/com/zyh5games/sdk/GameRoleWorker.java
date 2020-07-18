package com.zyh5games.sdk;

import com.zyh5games.entity.GameRole;
import com.zyh5games.service.GameRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/6
 */
@Component
public class GameRoleWorker {
    @Autowired
    GameRoleService gameRoleService;

    public boolean createGameRole(GameRole role) {
        return gameRoleService.createGameRole(role);
    }

    public void updateGameRole(String gameId, String channelId, String channelUid, String serverId, String lastLoginTime,
                               String roleId, String balance, String userRoleName, String param) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("roleId", roleId);
        map.put("channelId", channelId);
        map.put("channelUid", channelUid);
        map.put("gameId", gameId);
        map.put("serverId", serverId);
        map.put("lastLoginTime", lastLoginTime);
        map.put("balance", balance);
        map.put("name", userRoleName);
        map.put("param", param);
        gameRoleService.updateGameRole(map);
    }

    public GameRole findGameRole(String gameId, String channelId, String serverId, String channelUid, String roleId) {
        Map<String, Object> map = new HashMap<>();
        map.put("gameId", gameId);
        map.put("channelId", channelId);
        map.put("serverId", serverId);
        map.put("channelUid", channelUid);
        map.put("roleId", roleId);
        return gameRoleService.findGameRole(map);
    }

    public boolean existRole(String accountId, String appId) {
        return gameRoleService.existRole(accountId, appId);
    }
}

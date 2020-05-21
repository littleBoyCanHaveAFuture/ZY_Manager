package com.zyh5games.sdk;

import com.zyh5games.entity.GameRole;
import com.zyh5games.service.GameRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public void updateGameRole(Map<String, Object> map) {
        gameRoleService.updateGameRole(map);
    }

    public String getLastLoginTime(Map<String, Object> map) {
        return gameRoleService.getLastLoginTime(map);
    }

    public List<GameRole> findGamerole(Map<String, Object> map) {
        return gameRoleService.findUser(map);
    }

    public GameRole findGameRole(Map<String, Object> map) {
        return gameRoleService.findGameRole(map);
    }

    public boolean existRole(String accountId) {
        return gameRoleService.existRole(accountId);
    }
}

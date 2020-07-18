package com.zyh5games.service;

import com.zyh5games.entity.GameRole;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/4
 */
public interface GameRoleService {
    /**
     * 创建角色
     *
     * @param gameRole
     * @return
     */
    boolean createGameRole(GameRole gameRole) throws DataAccessException;


    int readMaxAccountId(int maxSpid);

    List<GameRole> findUser(Map<String, Object> map);

    GameRole findGameRole(Map<String, Object> map);

    /**
     * 更新用户数据
     */
    int updateGameRole(Map<String, Object> map);

    /**
     * 返回最近登录时间
     */
    String getLastLoginTime(Map<String, Object> map);

    /**
     * 该账号是否存在角色
     */
    boolean existRole(String accountId,String appId);
}

package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.GameRole;
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
    public boolean createGameRole(GameRole gameRole) throws DataAccessException;


    int readMaxAccountId(int maxSpid);

    /**
     * 查找账号
     *
     * @param map
     * @return
     */
    public List<GameRole> findUser(Map<String, Object> map);

    /**
     * 更新用户数据
     */
    int updateGameRole(Map<String, Object> map);

    /**
     * 返回最近登录时间
     */
    String getLastLoginTime(Map<String, Object> map);

}

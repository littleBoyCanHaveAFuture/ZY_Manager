package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.GameRole;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
@Repository
public interface GameRoleDao {
//
//    /**
//     * 登录
//     *
//     * @param GameRole
//     * @return
//     */
//    public GameRole login(GameRole GameRole);
//
//    /**
//     * 查找用户列表
//     *
//     * @param map
//     * @return
//     */
//    public List<GameRole> findGameRoles(Map<String, Object> map);
//
//    /**
//     * @param map
//     * @return
//     */
//    public Long getTotalGameRole(Map<String, Object> map);
//
//    /**
//     * 实体修改
//     *
//     * @param GameRole
//     * @return
//     */
//    public int updateGameRole(GameRole GameRole);
//
//    /**
//     * 添加用户
//     *
//     * @param GameRole
//     * @return
//     */
//    public int addGameRole(GameRole GameRole);
//
//    /**
//     * 删除用户
//     *
//     * @param id
//     * @return
//     */
//    public int deleteGameRole(Integer id);
//
//    Long getTotalSameGameRole(Map<String, Object> map);

    /**
     * 创建角色
     */
    void create(GameRole GameRole);

    int updateGameRole(Map<String, Object> map);

    List<String> getLastLoginTime(Map<String, Object> map);
}

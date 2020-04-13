package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.GameRole;
import org.apache.ibatis.annotations.Param;
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
    /**
     * 创建角色
     */
    void create(GameRole GameRole);

    int updateGameRole(Map<String, Object> map);

    List<String> getLastLoginTime(Map<String, Object> map);

    List<GameRole> findGamerole(Map<String, Object> map);

    GameRole findGameRole(Map<String, Object> map);

    String existRole(@Param("accountId") String accountId);
}

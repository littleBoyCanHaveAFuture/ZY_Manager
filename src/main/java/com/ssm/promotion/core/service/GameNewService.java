package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.GameNew;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Administrator
 */
public interface GameNewService {
    /**
     * 条件批量查询
     *
     * @param map    参数
     * @param userId 后台账号id
     * @return list
     */
    List<GameNew> getGameList(Map<String, Object> map, Integer userId);

    /**
     * 条件批量查询 个数
     *
     * @param map    参数
     * @param userId 后台账号id
     * @return list
     */
    Integer getCountGame(Map<String, Object> map, Integer userId);

    /**
     * 查找不同的游戏id
     */
    List<GameNew> selectGameIdList(Integer userId);

    /**
     * 查询单条
     *
     * @param gameId 唯一游戏id
     * @param userId 后台账号id
     * @return GameNew
     */
    GameNew selectGame(Integer gameId, Integer userId);

    /**
     * 删除单条
     *
     * @param gameId 唯一游戏id
     * @param userId 后台账号id
     * @return int
     */
    int deleteGame(Integer gameId, Integer userId);

    /**
     * 更新单条
     *
     * @param game   游戏
     * @param userId 后台账号id
     * @return int
     */
    int updateGame(GameNew game, Integer userId);

    /**
     * 新增单条
     *
     * @param game   游戏
     * @param userId 后台账号id
     * @return int
     */
    int addGame(GameNew game, Integer userId);

    Integer getMaxAppid();

    int existKey(String key, Integer userId);

    GameNew getGameByKey(String key, Integer userId);
}

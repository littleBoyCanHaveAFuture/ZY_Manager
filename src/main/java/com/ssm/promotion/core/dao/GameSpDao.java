package com.ssm.promotion.core.dao;


import com.ssm.promotion.core.entity.GameSp;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * 2019/11/15
 */
public interface GameSpDao {

    /**
     * 条件查询
     *
     * @param map id
     *            gameId
     *            spId
     *            uid
     *            start
     *            size
     * @return List GameSp
     */
    List<GameSp> selectGameSp(Map<String, Object> map);

    /**
     * 查询所有
     */
    List<GameSp> selectAllGameSp();

    /**
     * 条件查询
     *
     * @param id 主键id
     * @return List GameSp
     */
    int deleteGameSp(Integer id);

    /**
     * 条件更新
     *
     * @param map id
     *            gameId
     *            spId
     *            uid
     *            start
     *            size
     * @return List GameSp
     */
    int updateGameSp(Map<String, Object> map);

    /**
     * 插入新的GameSp
     * 查询成功 主键会给sp.id赋值
     *
     * @param sp GameSp
     * @return int
     */
    int insertGameSp(GameSp sp);

    /**
     * 总数
     */
    Long getCountGameSp(Map<String, Object> map);
}

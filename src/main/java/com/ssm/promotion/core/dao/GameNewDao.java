package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.GameNew;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface GameNewDao {
    int insertGame(GameNew game);

    int deleteGame(Integer gameId);

    int updateGame(GameNew game);

    GameNew selectGame(Integer gameId);

    List<GameNew> selectGameList(Map<String, Object> map);

    Integer getCountGame(Map<String, Object> map);

    Integer readMaxAppId();


    List<GameNew> selectGameIdList();
}

package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.Game;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface GameDao {
    List<Game> selectAllGame();

    List<Game> selectGameList(Map<String, Object> map);

    Game selectGame(Integer gameid);

    int deleteGame(Integer gameid);

    int updateGame(Game gamename);

    int insertGame(Game gamename);

}

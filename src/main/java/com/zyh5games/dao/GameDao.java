package com.zyh5games.dao;

import com.zyh5games.entity.Game;

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

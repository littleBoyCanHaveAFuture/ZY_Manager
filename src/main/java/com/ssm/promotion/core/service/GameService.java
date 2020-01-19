package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.Game;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface GameService {
    List<Game> getGameList(Map<String, Object> map, Integer userid);

    int deleteGame(Integer gameId, Integer userid);

    /**
     * 修改服务器
     */
    int updateGame(Game game, Integer userId);

    int addGame(Game game, Integer userId);
}
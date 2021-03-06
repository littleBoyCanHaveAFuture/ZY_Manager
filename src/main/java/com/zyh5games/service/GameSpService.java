package com.zyh5games.service;

import com.zyh5games.entity.GameSp;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface GameSpService {

    List<GameSp> selectGameSpList(Map<String, Object> map, Integer userId);

    GameSp selectGameSp(Integer gameId, Integer channelId, Integer userId);

    int deleteGameSp(Integer id, Integer userId);

    int updateGameSp(Map<String, Object> map, Integer userId);

    int insertGameSp(GameSp gameSp, Integer userId);

    Long getCountGameSp(Map<String, Object> map, Integer userId);

    List<Integer> DistSpIdByGameId(Integer gameId, Integer userId);

    List<Integer> DistGameIdBySpId(Integer spId, Integer userId);
}

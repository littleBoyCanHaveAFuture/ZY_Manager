package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.GameSp;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface GameSpService {

    List<GameSp> selectGameSp(Map<String, Object> map, Integer userId);

    List<GameSp> selectAllGameSp(Integer userId);

    int deleteGameSp(Integer id, Integer userId);

    int updateGameSp(Map<String, Object> map, Integer userId);

    int insertGameSp(GameSp gameSp, Integer userId);

    Long getCountGameSp(Map<String, Object> map, Integer userId);

}
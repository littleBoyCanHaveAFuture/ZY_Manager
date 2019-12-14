package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.GameName;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface GameNameService {
    public List<GameName> getGameList(Map<String, Object> map, Integer userid);

    int deleteGame(Integer gameId, Integer userid);

    /**
     * 修改服务器
     */
    int updateGame(Integer id, String name, Integer userId);

    int addGame(Integer id, String name, Integer userId);
}
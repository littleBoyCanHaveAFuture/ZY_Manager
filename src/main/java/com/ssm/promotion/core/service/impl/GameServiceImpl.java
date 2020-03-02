package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.GameDao;
import com.ssm.promotion.core.entity.Game;
import com.ssm.promotion.core.service.GameService;
import com.ssm.promotion.core.util.RandomUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/11/26
 */
@Service("GameNameService")
public class GameServiceImpl implements GameService {
    @Resource
    GameDao dao;

    @Override
    public List<Game> getGameList(Map<String, Object> map, Integer userid) {
        return dao.selectGameList(map);
    }

    @Override
    public Game selectGame(Integer gameId, Integer userid) {
        return dao.selectGame(gameId);
    }

    @Override
    public int deleteGame(Integer gameId, Integer userid) {
        return dao.deleteGame(gameId);
    }

    @Override
    public int updateGame(Game game, Integer userId) {
        return dao.updateGame(game);
    }

    @Override
    public int addGame(Game game, Integer userId) {
        game.setSecertKey(RandomUtil.rndSecertKey());
        dao.insertGame(game);
        return game.getId();
    }

}

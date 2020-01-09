package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.GameNameDao;
import com.ssm.promotion.core.entity.GameName;
import com.ssm.promotion.core.service.GameNameService;
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
public class GameNameServiceImpl implements GameNameService {
    @Resource
    GameNameDao dao;

    @Override
    public List<GameName> getGameList(Map<String, Object> map, Integer userid) {
        return dao.selectGame(map);
    }

    @Override
    public int deleteGame(Integer gameId, Integer userid) {
        return dao.deleteGame(gameId);
    }

    @Override
    public int updateGame(GameName gameName, Integer userId) {
        return dao.updateGame(gameName);
    }

    @Override
    public int addGame(GameName gameName, Integer userId) {
        gameName.setSecertKey(RandomUtil.rndSecertKey());
        dao.insertGame(gameName);
        return gameName.getId();
    }

}

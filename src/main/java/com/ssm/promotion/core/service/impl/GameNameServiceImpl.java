package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.GameNameDao;
import com.ssm.promotion.core.entity.GameName;
import com.ssm.promotion.core.service.GameNameService;
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
        return dao.select(map);
    }

    @Override
    public int deleteGame(Integer gameId, Integer userid) {
        return dao.delete(gameId);
    }

    @Override
    public int updateGame(Integer id, String name, Integer userId) {
        return dao.update(new GameName(id, name));
    }

    @Override
    public int addGame(Integer id, String name, Integer userId) {
        return dao.insert(new GameName(id, name));
    }

}

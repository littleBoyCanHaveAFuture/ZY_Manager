package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.GameNewDao;
import com.ssm.promotion.core.entity.GameNew;
import com.ssm.promotion.core.sdk.AccountWorker;
import com.ssm.promotion.core.service.GameNewService;
import com.ssm.promotion.core.util.MysqlUtil;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2020/04/27
 */
@Service("GameService")
public class GameNewServiceImpl implements GameNewService {
    private static final Logger log = Logger.getLogger(GameNewServiceImpl.class);
    @Resource
    GameNewDao dao;

    @Override
    public List<GameNew> getGameList(Map<String, Object> map, Integer userId) {
        return dao.selectGameList(map);
    }

    @Override
    public Integer getCountGame(Map<String, Object> map, Integer userId) {
        return dao.getCountGame(map);
    }

    @Override
    public GameNew selectGame(Integer gameId, Integer userId) {
        return dao.selectGame(gameId);
    }

    @Override
    public int deleteGame(Integer gameId, Integer userId) {
        return dao.deleteGame(gameId);
    }

    @Override
    public int updateGame(GameNew game, Integer userId) {
        return dao.updateGame(game);
    }

    @Override
    public int addGame(GameNew game, Integer userId) {
        int id = AccountWorker.lastAppId.get();

        do {
            game.setAppId(++id);

            try {
                int res = dao.insertGame(game);
                log.info("addGame success:" + res);
                break;
            } catch (DataAccessException e) {
                String err = e.getMessage();
                //仅使主键重复异常被忽略
                if (err.contains(MysqlUtil.excep_sql) && err.contains(MysqlUtil.excep_pri)) {
                    log.info("err1");
                    continue;
                } else if (err.contains(MysqlUtil.excep_uni)) {
                    log.info("err2");
                    return -2;
                } else {
                    return -1;
                }
            } catch (Exception e) {
                log.info("err4:" + e.getMessage());
                return -3;
            }
        } while (true);

        AccountWorker.lastAppId.set(id);
        log.info(AccountWorker.lastAppId.get());
        return 1;
    }

    @Override
    public Integer getMaxAppid() {
        return dao.readMaxAppId();
    }

}

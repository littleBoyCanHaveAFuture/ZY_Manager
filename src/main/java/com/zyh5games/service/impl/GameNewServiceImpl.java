package com.zyh5games.service.impl;

import com.zyh5games.dao.GameNewDao;
import com.zyh5games.entity.GameNew;
import com.zyh5games.sdk.AccountWorker;
import com.zyh5games.service.GameNewService;
import com.zyh5games.util.MysqlUtil;
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
    public List<GameNew> selectGameIdList(Integer userId) {
        return dao.selectGameIdList();
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

    @Override
    public int existKey(String key, Integer userId) {
        return dao.existKey(key);
    }

    @Override
    public GameNew getGameByKey(String key, Integer userId) {
        return dao.getGameByKey(key);
    }

    @Override
    public String getCallbackKey(Integer gameId, Integer userId) {
        return dao.getCallbackKey(gameId);
    }


}

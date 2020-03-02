package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.GameRoleDao;
import com.ssm.promotion.core.entity.GameRole;
import com.ssm.promotion.core.service.GameRoleService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/6
 */
@Service("GameRoleService")
public class GameRoleServiceImpl implements GameRoleService {
    private static final Logger log = Logger.getLogger(GameRoleServiceImpl.class);
    @Autowired
    GameRoleDao gameRoleDao;

    @Override
    public boolean createGameRole(GameRole gameRole) throws DataAccessException {
        boolean res = true;
        try {
            this.gameRoleDao.create(gameRole);
            log.info("createGameRole success");
        } catch (DataAccessException e) {
            String err = e.getMessage();
            //仅使主键重复异常被忽略
            if (err.contains("SQLIntegrityConstraintViolationException") && err.contains("for key 'PRIMARY'")) {
                log.info("err1");
            } else if (err.contains("for key 'name_unique'")) {
                log.info("err2");
            } else {
                log.info("err3:" + err);
            }
            res = false;
        } catch (Exception e) {
            log.info("err4:" + e.getMessage());
            res = false;
        }
        return res;
    }

    @Override
    public int readMaxAccountId(int maxSpid) {
        return 0;
    }

    @Override
    public List<GameRole> findUser(Map<String, Object> map) {
        return gameRoleDao.findGamerole(map);
    }

    @Override
    public GameRole findGameRole(Map<String, Object> map) {
        return gameRoleDao.findGameRole(map);
    }

    @Override
    public int updateGameRole(Map<String, Object> map) {
        return gameRoleDao.updateGameRole(map);
    }

    @Override
    public String getLastLoginTime(Map<String, Object> map) {
        List<String> res = gameRoleDao.getLastLoginTime(map);
        if (res.size() > 0) {
            return res.get(0);
        }

        return null;
    }
}

package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.GameSpDao;
import com.ssm.promotion.core.entity.GameSp;
import com.ssm.promotion.core.service.GameSpService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/11/26
 */
@Service("GameSpService")
public class GameSpServiceImpl implements GameSpService {
    @Resource
    GameSpDao gameSpDao;

    /**
     * zy_game_sp
     * <p>
     * 查询所有
     *
     * @param userId 用户id
     */
    @Override
    public List<GameSp> selectAllGameSp(Integer userId) {
        return gameSpDao.selectAllGameSp();
    }

    /**
     * zy_game_sp
     * <p>
     * 条件查询
     *
     * @param map id        主键<p>
     *            gameId    游戏id<p>
     *            spId      渠道id<p>
     *            uid       账号id<p>
     */
    @Override
    public List<GameSp> selectGameSpList(Map<String, Object> map, Integer userId) {
        return gameSpDao.selectGameSpList(map);
    }

    @Override
    public GameSp selectGameSp(Integer gameId, Integer channelId, Integer userId) {
        return gameSpDao.selectGameSp(gameId, channelId);
    }

    /**
     * zy_game_sp
     * <p>
     * 通过主键删除
     *
     * @param id 主键id
     */
    @Override
    public int deleteGameSp(Integer id, Integer userId) {
        return gameSpDao.deleteGameSp(id);
    }

    /**
     * zy_game_sp
     * <p>
     * 通过主键删除
     *
     * @param map id        主键<p>
     *            gameId    游戏id<p>
     *            spId      渠道id<p>
     *            uid       账号id<p>
     */
    @Override
    public int updateGameSp(Map<String, Object> map, Integer userId) {
        return gameSpDao.updateGameSp(map);
    }

    /**
     * zy_game_sp
     * <p>
     * 插入GameSp
     */
    @Override
    public int insertGameSp(GameSp gameSp, Integer userId) {
        return gameSpDao.insertGameSp(gameSp);
    }

    @Override
    public Long getCountGameSp(Map<String, Object> map, Integer userId) {
        return gameSpDao.getCountGameSp(map);
    }

    @Override
    public List<Integer> DistSpIdByGameId(Integer gameId, Integer userId) {
        return gameSpDao.DistSpIdByGameId(String.valueOf(gameId));
    }

    @Override
    public List<Integer> DistGameIdBySpId(Integer spId, Integer userId) {
        return gameSpDao.DistGameIdBySpId(String.valueOf(spId));
    }
}

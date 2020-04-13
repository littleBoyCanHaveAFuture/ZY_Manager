package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.GameDiscountDao;
import com.ssm.promotion.core.entity.GameDiscount;
import com.ssm.promotion.core.service.GameDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("GameDiscountService")
public class GameDiscountServiceImpl implements GameDiscountService {
    @Autowired
    GameDiscountDao gameDiscountDao;

    @Override
    public List<GameDiscount> selectGameDiscountList(Map<String, Object> map, Integer userid) {
        return gameDiscountDao.selectGameDiscountList(map);
    }

    @Override
    public GameDiscount selectGameDiscount(Integer gameId, Integer channelId, Integer userid) {
        return gameDiscountDao.selectGameDiscount(gameId, channelId);
    }

    @Override
    public int deleteGameDiscount(Integer gameId, Integer channelId, Integer userid) {
        return gameDiscountDao.deleteGameDiscount(gameId,channelId);
    }

    @Override
    public int updateGameDiscount(GameDiscount gameDiscount, Integer userid) {
        return gameDiscountDao.updateGameDiscount(gameDiscount);
    }

    @Override
    public int insertGameDiscount(GameDiscount gameDiscount, Integer userid) {
        return gameDiscountDao.insertGameDiscount(gameDiscount);
    }

    @Override
    public Long getCountGameDiscount(Map<String, Object> map, Integer userid) {
        return gameDiscountDao.getCountGameDiscount(map);
    }
}

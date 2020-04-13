package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.Game;
import com.ssm.promotion.core.entity.GameDiscount;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface GameDiscountService {
    List<GameDiscount> selectGameDiscountList(Map<String, Object> map, Integer userid);

    GameDiscount selectGameDiscount(Integer gameId, Integer channelId, Integer userid);

    int deleteGameDiscount(Integer gameId, Integer channelId, Integer userid);

    int updateGameDiscount(GameDiscount gameDiscount, Integer userid);

    int insertGameDiscount(GameDiscount gameDiscount, Integer userid);

    Long getCountGameDiscount(Map<String, Object> map, Integer userid);
}

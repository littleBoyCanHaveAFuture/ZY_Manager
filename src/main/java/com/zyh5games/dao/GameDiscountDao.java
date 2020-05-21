package com.zyh5games.dao;

import com.zyh5games.entity.GameDiscount;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface GameDiscountDao {
    List<GameDiscount> selectGameDiscountList(Map<String, Object> map);

    GameDiscount selectGameDiscount(@Param("gameId") Integer gameId, @Param("channelId") Integer channelId);

    int deleteGameDiscount(@Param("gameId") Integer gameId, @Param("channelId") Integer channelId);

    int updateGameDiscount(GameDiscount gameDiscount);

    int insertGameDiscount(GameDiscount gameDiscount);

    Long getCountGameDiscount(Map<String, Object> map);
}

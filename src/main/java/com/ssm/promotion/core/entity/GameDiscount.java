package com.ssm.promotion.core.entity;

import lombok.Data;

/**
 * @author song minghua
 */
@Data
public class GameDiscount {
    /**
     * id
     */
    private Integer id;
    /**
     * gameid
     */
    private Integer gameId;
    /**
     * 游戏名称
     */
    private String name;
    /**
     * 折扣信息 百分制
     */
    private Integer disCount;
    /**
     * 渠道id
     */
    private Integer channelId;
    /**
     * 操作账号id
     */
    private Integer uid;

    public GameDiscount(Integer id, Integer gameId, String name) {
        this.id = id;
        this.gameId = gameId;
        this.name = name;
    }

    public GameDiscount() {
    }
}

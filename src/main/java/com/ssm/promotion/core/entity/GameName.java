package com.ssm.promotion.core.entity;

import lombok.Data;

/**
 * @author
 */
@Data
public class GameName {
    /**
     * 游戏id
     */
    private Integer gameId;
    /**
     * 游戏名称
     */
    private String name;

    public GameName(Integer gameId, String name) {
        this.gameId = gameId;
        this.name = name;
    }

     GameName() {

    }

}
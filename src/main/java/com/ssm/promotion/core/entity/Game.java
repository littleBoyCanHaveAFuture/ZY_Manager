package com.ssm.promotion.core.entity;

import lombok.Data;

/**
 * @author song minghua
 */
@Data
public class Game {
    /**
     * id
     */
    private Integer id;
    /**
     * 游戏名称
     */
    private String name;
    /**
     * 创建者uid
     */
    private Integer uid;

    /**
     * 秘钥 32位小写字母和数字
     */
    private String secertKey;
    /**
     * 登陆地址
     */
    private String loginUrl;
    /**
     * 支付回调地址
     */
    private String paycallbackUrl;

    public Game(String name, Integer uid) {
        this.name = name;
        this.uid = uid;
    }

    Game() {

    }

}
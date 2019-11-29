package com.ssm.promotion.core.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author
 */
@Data
public class Servername implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 游戏id
     */
    private Integer gameId;
    /**
     * 游戏名称
     */
    private String name;


}
package com.zyh5games.entity;

import lombok.Data;

/**
 * @author song minghua
 * @date 2020/5/8
 */
@Data
public class HuoguoExchangeRecord {
    /**
     * 主键id
     */
    Integer id;
    /**
     * 玩家id
     */
    String openId;
    /**
     * 商品id
     */
    Integer itemId;
    /**
     * 申请时间
     */
    String exchangeTime;
    /**
     * 申请状态
     */
    Integer status;
    /**
     * 信息
     */
    String message;
    /**
     * 完成时间
     */
    String finishedTime;
    /**
     * 收货地址
     */
    String address;
    /**
     * 手机
     */
    String phone;
    /**
     * 玩家姓名
     */
    String name;
}

package com.ssm.promotion.core.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author song minghua
 * @date 2019/12/9
 */
@Data
public class PayRecord {
    /**
     * 订单编号
     */
    private String id;
    /**
     * 玩家编号
     */
    private Integer playerId;
    /**
     * 玩家付款账号
     */
    private String payAccount;
    /**
     * 游戏id
     */
    private Integer gameId;
    /**
     * 购买数量
     */
    private Integer amount;
    /**
     * 商品编号
     */
    private Integer goodsId;
    /**
     * 商品名
     */
    private String goodsName;
    /**
     * 支付类型 渠道id
     */
    private String payCode;
    /**
     * 平台订单号
     */
    private String payTradeCode;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 账单时间
     */
    private Timestamp createTime;
    /**
     * 商品单价
     */
    private Integer goodsPrice;
    /**
     * 实际支付金额
     */
    private float payMoney;
    /**
     * 补单账号
     */
    private String changeManager;
    /**
     * 补单时间
     */
    private Timestamp changeTime;
    /**
     * 补单原因
     */
    private String changeReason;
    /**
     * 玩家渠道
     */
    private String spName;

    /**
     * 玩家姓名
     */
    private String playerName;


}

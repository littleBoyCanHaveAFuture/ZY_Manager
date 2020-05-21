package com.zyh5games.util.enums;

/**
 * @author song minghua
 * 支付状态
 */
public class OrderState {
    /**
     * 点开充值界面
     * 未点充值按钮（取消支付）
     *
     * @deprecated
     */
    public static final int STATE_OPEN_SHOP = 0;
    /**
     * 选择充值方式界面
     * 未选择充值方式（取消支付）
     * 前置：商品页面：
     * 1.点击购买，
     * 2.显示弹窗：商品金额、选择支付方式
     * 3.关闭弹窗，即此状态
     */
    public static final int STATE_OPEN_SELECT = 1;
    /**
     * 支付宝微信界面
     * 未支付（取消支付）
     * 前置：选择充值方式界面：
     * 1.选择支付方式，
     * 2.跳转到网页，使用支付宝H5、微信H5 支付
     * 3.取消支付，即此状态
     */
    public static final int STATE_OPEN_PAY = 2;
    /**
     * 支付成功	未发货
     * 前置：支付宝微信界面：
     * 1.支付成功，
     * 2.跳转回游戏界面
     * 3.此时若未收到商品，即此状态。
     */
    public static final int STATE_PAY_SUCCESS = 3;
    /**
     * 支付成功 已发货(交易完成)
     * 前置：支付宝微信界面：
     * 1.支付成功，
     * 2.跳转回游戏界面
     * 3.此时收到商品，即此状态。
     */
    public static final int STATE_PAY_FINISHED = 4;
    /**
     * 支付成功 补单(交易完成)
     * 前置：支付宝微信界面：
     * 1.支付成功，
     * 2.跳转回游戏界面
     * 3.此时若未收到商品.
     * 4.游戏补发商品，即此状态。
     */
    public static final int STATE_PAY_SUPPLEMENT = 5;
}

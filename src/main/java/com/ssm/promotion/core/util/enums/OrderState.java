package com.ssm.promotion.core.util.enums;

/**
 * @author song minghua
 * 支付状态
 * 点开充值界面	未点充值按钮（取消支付）	0
 * 点击充值按钮跳转到	                    1
 * <p>
 * 选择充值方式界面	未选择充值方式（取消支付）	1
 * 选择充值方式然后跳转	                    2
 * <p>
 * 支付宝微信界面	未支付（取消支付）           	2
 * 支付成功跳转回来	                        3
 * <p>
 * 支付成功	未发货	                        3
 * 已发货(交易完成)	                        4
 * <p>
 * 未发货	未发货                           3
 * 补单	                                    5
 */
public class OrderState {
    /**
     * 点开充值界面
     * 未点充值按钮（取消支付）
     */
    public static final int STATE_OPEN_SHOP = 0;
    /**
     * 选择充值方式界面
     * 未选择充值方式（取消支付）
     */
    public static final int STATE_OPEN_SELECT = 1;
    /**
     * 支付宝微信界面
     * 未支付（取消支付）
     */
    public static final int STATE_OPEN_PAY = 2;
    /**
     * 支付成功	未发货
     */
    public static final int STATE_PAY_SUCCESS = 3;
    /**
     * 支付成功 已发货(交易完成)
     */
    public static final int STATE_PAY_FINISHED = 4;
    /**
     * 支付成功 补单(交易完成)
     */
    public static final int STATE_PAY_SUPPLEMENT = 5;
}

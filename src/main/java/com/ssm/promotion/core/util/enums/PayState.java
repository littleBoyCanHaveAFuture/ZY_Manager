package com.ssm.promotion.core.util.enums;

/**
 * 支付状态
 */
public class PayState {
    /**
     * 支付取消
     */
    public static final int STATE_CANCEL = -2;
    /**
     * 支付失败
     */
    public static final int STATE_FAIL = -1;
    /**
     * 创建未支付
     */
    public static final int STATE_NEW = 0;
    /**
     * 支付成功 未到账
     */
    public static final int STATE_SUCCESS = 1;
    /**
     * 交易完成 已到账
     */
    public static final int STATE_PAY_DONE = 2;
    /**
     * 有效订单
     */
    public static final int PRDER_INVALID = 3;
    /**
     * 签名错误
     */
    public static final int PRDER_SIGN_ERROR = 4;
}

package com.zyh5games.util.enums;

/**
 * 唯一状态码
 *
 * @author Administrator
 */
public class StateCode {

    public static final int CODE_LOGIN_CHECK_SUCCESS = 0;
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_GAME_NONE = 2;
    public static final int CODE_CHANNEL_NONE = 3;
    public static final int CODE_MASTER_NONE = 4;
    public static final int CODE_AUTH_FAILED = 5;
    //角色不存在
    public static final int CODE_USER_NONE = 6;
    public static final int CODE_VERIFY_FAILED = 8;
    public static final int CODE_TOKEN_ERROR = 9;
    //金额错误
    public static final int CODE_MONEY_ERROR = 10;
    //订单错误
    public static final int CODE_ORDER_ERROR = 11;
    public static final int CODE_SIGN_ERROR = 12;
    //充值未开放
    public static final int CODE_PAY_CLOSED = 13;
    //渠道和游戏没有对上
    public static final int CODE_CHANNEL_NOT_MATCH = 14;
    // CUSTOM商品ID跟角色ID解析错误
    public static final int CODE_ORDER_CUSTOM_ERROR = 15;
    // 创建订单号失败
    public static final int CODE_ORDER_GET_ERROR = 15;
    public static final int CODE_USER_IP_BLOCK = 16;
    //参数不一致
    public static final int CODE_PARAM_DIFF = 17;
    //参数为空或非法
    public static final int CODE_PARAM_ERROR = 18;
    //账号不存在
    public static final int CODE_ACCOUNT_NONE = 19;
}

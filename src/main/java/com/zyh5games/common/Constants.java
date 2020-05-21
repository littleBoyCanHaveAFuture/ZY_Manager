package com.zyh5games.common;

/**
 * @author 13
 * @date 2017/6/26
 */
public class Constants {
    // 成功处理请求
    public static final int RESULT_CODE_SUCCESS = 200;
    // bad request
    public static final int RESULT_CODE_BAD_REQUEST = 412;
    public static final int RESULT_CODE_SERVER_ERROR = 500;  // 没有对应结果
    public static final int RESULT_CODE_SERVER_RELOGIN = 501;//重新登录
    public static final String ARTICLE_CACHE_KEY = "perfect-ssm:article:";//文章key
    public static final String PICTURE_CACHE_KEY = "perfect-ssm:picture:";//图片key
    public static final int RESULT_CODE_FAIL = 600;

    public static final int SDK_LOGIN_SUCCESS = 0;  // 验证成功
    public static final int SDK_LOGIN_FAIL_TOKEN = 1;  // 验证失败 token错误
    public static final int SDK_LOGIN_FAIL_SIGN = 2;  // 验证失败 签名错误
    public static final int SDK_PARAM = 3;  // 验证失败 参数错误

    public static final int ERR_RSTYPE = 1;//充值汇总-查询类型-错误
    public static final int ERR_GAMEID = 2;//充值汇总-游戏id-错误
    public static final int ERR_SERVERID = 3;//充值汇总-区服id-错误
    public static final int ERR_SPID = 3;//充值汇总-渠道id-错误
}

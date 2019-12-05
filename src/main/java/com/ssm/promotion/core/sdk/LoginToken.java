package com.ssm.promotion.core.sdk;


import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.MD5Util;
import com.ssm.promotion.core.util.StringUtil;
import com.ssm.promotion.core.util.enums.ServiceType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登陆令牌
 *
 * @author Hawk Wu
 * 2014-8-16
 */
public class LoginToken {
    /**
     * 登录令牌有效时间
     */
    private static final long LOGIN_TOKEN_VALID_MILLS = DateUtil.SECOND_MILLIS * 15;
    /**
     * 令牌列表：账号-令牌对象
     */
    private static final Map<Integer, LoginToken> tokenMap = new ConcurrentHashMap<>();
    /**
     * 用户编号
     */
    private int userId;
    /**
     * 登录令牌
     */
    private String token;
    /**
     * 失效时间
     */
    private long invalidTime;

    private LoginToken() {
    }

    /**
     * 创建一个注册的新令牌
     */
    public static String getToken(Integer userId, String gameId, ServiceType serviceType) {
        synchronized (userId) {
            LoginToken loginToken = tokenMap.get(userId);
            long now = System.currentTimeMillis();
            if (loginToken == null) {
                loginToken = new LoginToken();
                loginToken.userId = userId;

                StringBuilder sb = new StringBuilder().
                        append(serviceType.getId()).append(StringUtil.COLON).
                        append(userId.longValue()).append(gameId).
                        append(now);

                String token = MD5Util.made(sb.toString());
                sb.setLength(0);
                sb.append(serviceType.getId()).append(StringUtil.COLON).append(token);
                loginToken.token = sb.toString();

                tokenMap.put(loginToken.userId, loginToken);
            }
            loginToken.invalidTime = now + LOGIN_TOKEN_VALID_MILLS;
            return loginToken.token;
        }
    }

    /**
     * 验证令牌
     */
    public static boolean check(int userId, String token) {
        LoginToken loginToken = tokenMap.remove(userId);
        return loginToken != null && loginToken.token.equals(token);
    }

    /**
     * 清理过期令牌
     */
    public static void cleanInvalid(long now) {
        tokenMap.values().removeIf(loginToken -> now > loginToken.invalidTime);
    }
}

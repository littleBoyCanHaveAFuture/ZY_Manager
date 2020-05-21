package com.zyh5games.sdk;


import com.zyh5games.util.DateUtil;
import com.zyh5games.util.MD5Util;
import com.zyh5games.util.StringUtil;
import com.zyh5games.util.enums.ServiceType;

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
     *
     * @param accountId   指悦账号id
     * @param gameId      游戏id
     * @param serviceType 服务类型
     */
    public static String getToken(Integer accountId, Integer gameId, ServiceType serviceType) {
        synchronized (tokenMap) {
            LoginToken loginToken = tokenMap.get(accountId);
            long now = System.currentTimeMillis();
            if (loginToken == null) {
                loginToken = new LoginToken();
                loginToken.userId = accountId;

                StringBuilder sb = new StringBuilder();
                sb.append(serviceType.getId()).append(StringUtil.COLON).
                        append(accountId.longValue()).
                        append(gameId).
                        append(now);
                //此处加密可以使用特定的 key 增加安全性
                String token = MD5Util.md5(sb.toString());
                sb.setLength(0);

                sb.append(serviceType.getId()).append(StringUtil.COLON).
                        append(token);

                loginToken.token = sb.toString();

                tokenMap.put(loginToken.userId, loginToken);
            }
            loginToken.invalidTime = now + LOGIN_TOKEN_VALID_MILLS;
            return loginToken.token;
        }
    }

    /**
     * 验证令牌
     *
     * @param accountId 账号id
     * @param token     token
     */
    public static boolean check(int accountId, String token) {
        LoginToken loginToken = tokenMap.remove(accountId);
        return loginToken != null && loginToken.token.equals(token);
    }

    /**
     * 清理过期令牌
     */
    public static void cleanInvalid(long now) {
        tokenMap.values().removeIf(loginToken -> now > loginToken.invalidTime);
    }
}

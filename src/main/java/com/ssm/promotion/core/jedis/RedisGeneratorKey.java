package com.ssm.promotion.core.jedis;


import static com.ssm.promotion.core.util.StringUtil.COLON;
import static com.ssm.promotion.core.util.StringUtil.NUMBER_SIGN;

/**
 * @author song minghua
 * @date 2019/11/27
 */
public class RedisGeneratorKey {
    /**
     * {header}:{body}#{tail}
     * ex:
     * UserInfo
     * :
     * gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}
     * #
     * activePlayers
     */
    static String genKey(String header, String body, String tail) throws Exception {
        StringBuilder key = new StringBuilder();
        //头
        key.append(header).append(COLON);
        //身
        key.append(body).append(NUMBER_SIGN);
        //尾
        key.append(tail);

        System.out.println(key.toString());
        return key.toString();
    }

    /**
     * {header}:{body}#{tail}
     * ex:
     * UserInfo
     * :
     * gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}
     * #
     * activePlayers
     */
    public static String genKeyTail(String header, String body) throws Exception {
        StringBuilder key = new StringBuilder();
        //头
        key.append(header).append(COLON);
        //身
        key.append(body).append(NUMBER_SIGN);

        System.out.println(key.toString());
        return key.toString();
    }

}

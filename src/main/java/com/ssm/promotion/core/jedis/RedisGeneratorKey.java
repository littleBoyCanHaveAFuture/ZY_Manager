package com.ssm.promotion.core.jedis;


import com.ssm.promotion.core.controller.TtController;
import org.apache.log4j.Logger;

import static com.ssm.promotion.core.util.StringUtil.COLON;
import static com.ssm.promotion.core.util.StringUtil.NUMBER_SIGN;

/**
 * @author song minghua
 * @date 2019/11/27
 */
public class RedisGeneratorKey {
    private static final Logger log = Logger.getLogger(RedisGeneratorKey.class);

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

        log.info(key.toString());
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
        key.append(body);

        log.info(key.toString());
        return key.toString();
    }

}

package com.ssm.promotion.core.jedis;

import com.ssm.promotion.core.util.DateUtil;

import static com.ssm.promotion.core.util.StringUtil.COLON;
import static com.ssm.promotion.core.util.StringUtil.NUMBER_SIGN;

/**
 * @author song minghua
 * @date 2019/11/28
 */
public class RedisKeyBody {
    public static final String GAME_ID = "gid";
    public static final String SERVER_ID = "sid";
    public static final String SP_ID = "spid";
    public static final String DATE = "date";

    /**
     * 生成body
     *
     * @param gameId   游戏id
     * @param serverId 区服id
     * @param spId     渠道id
     * @param type     控住层级 1+1+1
     * @param times    添加时间 -1 不添加 null 当天
     */
    public static String generatorBody(Integer gameId, Integer serverId, String spId, Integer type, String times) throws Exception {
        if (type < 1 || type > 3) {
            return null;
        }
        StringBuilder body = new StringBuilder();
        if (type == 3) {
            if (gameId == null || serverId == null || spId.isEmpty()) {
                return null;
            }
            //到渠道
            //游戏id
            body.append(GAME_ID).append(COLON);
            body.append(gameId).append(COLON);
            //区服id
            body.append(SERVER_ID).append(COLON);
            body.append(serverId).append(COLON);
            //渠道id
            body.append(SP_ID).append(COLON);
            body.append(spId);
        } else if (type == 2) {
            if (gameId == null || serverId == null) {
                return null;
            }
            //到区服
            //游戏id
            body.append(GAME_ID).append(COLON);
            body.append(gameId).append(COLON);
            //区服id
            body.append(SERVER_ID).append(COLON);
            body.append(serverId);
        } else {
            if (gameId == null) {
                return null;
            }
            //到游戏
            body.append(GAME_ID).append(COLON);
            body.append(gameId);
        }
        if (times.isEmpty()) {
            String day = DateUtil.getCurrentDayStr();
            body.append(COLON);
            //时间 yyyyMMdd
            body.append(DATE).append(COLON);
            body.append(day);
        } else if ("-1".equals(times)) {

        } else {
            body.append(times);
        }
        body.append(NUMBER_SIGN);
        System.out.println("generatorBody body:" + body.toString());
        return body.toString();
    }

    public static StringBuilder genBody(Integer type, Integer gameId, Integer serverId, String spId) {
        if (type < 1 || type > 3) {
            return null;
        }
        StringBuilder body = new StringBuilder();
        if (type == 3) {
            if (gameId == null || serverId == null || spId.isEmpty()) {
                return null;
            }
            //到渠道
            //游戏id
            body.append(GAME_ID).append(COLON);
            body.append(gameId).append(COLON);
            //区服id
            body.append(SERVER_ID).append(COLON);
            body.append(serverId).append(COLON);
            //渠道id
            body.append(SP_ID).append(COLON);
            body.append(spId);
        } else if (type == 2) {
            if (gameId == null || serverId == null) {
                return null;
            }
            //到区服
            //游戏id
            body.append(GAME_ID).append(COLON);
            body.append(gameId).append(COLON);
            //区服id
            body.append(SERVER_ID).append(COLON);
            body.append(serverId);
        } else {
            if (gameId == null) {
                return null;
            }
            //到游戏
            body.append(GAME_ID).append(COLON);
            body.append(gameId);
        }
        return body;
    }

    public static String appendBodyTimes(String body, String times) throws Exception {
        StringBuilder bodys = new StringBuilder();
        bodys.append(body);
        if (times.isEmpty()) {
            String day = DateUtil.getCurrentDayStr();
            bodys.append(COLON);
            //时间 yyyyMMdd
            bodys.append(DATE).append(COLON);
            bodys.append(day);
        } else if ("-1".equals(times)) {

        } else {
            bodys.append(COLON);
            bodys.append(DATE).append(COLON);
            bodys.append(times);
        }
        return bodys.toString();
    }

    public static String appendBodyTail(String body, String tail) throws Exception {
        StringBuilder bodys = new StringBuilder();
        bodys.append(body);
        bodys.append(NUMBER_SIGN);
        bodys.append(tail);
        return bodys.toString();
    }

}

package com.ssm.promotion.core.sdk;

import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/14
 */
public class ChannelToken {
    private static final Logger log = Logger.getLogger(ChannelToken.class);

    public String loadChannelToken(Map<String, String[]> parameterMap) {
        int appId = Integer.parseInt(parameterMap.get("GameId")[0]);
        String appKey = parameterMap.get("GameKey")[0];
        int channelId = Integer.parseInt(parameterMap.get("ChannelCode")[0]);

        String token = "";

        switch (channelId) {
            case ChannelId.h5_zhiyue:
                break;
            case ChannelId.h5_ziwan:
                token = ziwanToken(parameterMap);
                break;
            case ChannelId.h5_baijia:
                token = baijiaLib(parameterMap);
                break;
            default:
                break;
        }
        return token;
    }

    public String ziwanToken(Map<String, String[]> parameterMap) {
        return parameterMap.get("userToken")[0];
    }

    public String baijiaLib(Map<String, String[]> parameterMap) {
        return "";

    }

}

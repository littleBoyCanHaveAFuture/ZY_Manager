package com.zyh5games.sdk;

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
            case ChannelId.H5_ZHIYUE:
                break;
            case ChannelId.H5_ZIWAN:
                token = ziWanToken(parameterMap);
                break;
            case ChannelId.H5_BAIJIA:
                token = baiJiaLib(parameterMap);
                break;
            default:
                break;
        }
        return token;
    }

    public String ziWanToken(Map<String, String[]> parameterMap) {
        return parameterMap.get("userToken")[0];
    }

    public String baiJiaLib(Map<String, String[]> parameterMap) {
        return "";

    }

}

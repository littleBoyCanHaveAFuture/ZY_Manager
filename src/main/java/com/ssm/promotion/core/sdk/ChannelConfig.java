package com.ssm.promotion.core.sdk;

import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

/**
 * @author song minghua
 * @date 2020/5/14
 */
public class ChannelConfig {
    private static final Logger log = Logger.getLogger(ChannelConfig.class);

    //加载通用-渠道js
    public void loadChannelConfig(Integer type) {
        switch (type) {
            case ChannelId.h5_ziwan:
                break;
            case ChannelId.h5_baijia:
                break;
            default:
                break;
        }

    }

    public void ziwanConfig() {

    }

    public void baijiaConfig() {
    }

}

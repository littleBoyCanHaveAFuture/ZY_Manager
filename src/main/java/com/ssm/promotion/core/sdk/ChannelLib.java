package com.ssm.promotion.core.sdk;

import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

/**
 * @author song minghua
 * @date 2020/5/14
 */
public class ChannelLib {
    private static final Logger log = Logger.getLogger(ChannelLib.class);

    //加载通用-渠道js
    public JSONArray loadChannelLib(Integer type) {
        JSONArray libUrl = new JSONArray();
        libUrl.add("http://zy.hysdgame.cn/sdk/common/md5.js");
        libUrl.add("http://zy.hysdgame.cn/sdk/common/jquery-3.4.1.min.js");
        switch (type) {
            case ChannelId.h5_ziwan:
                ziwanLib(libUrl);
                break;
            case ChannelId.h5_baijia:
                baijiaLib(libUrl);
                break;
            default:
                break;
        }
        return libUrl;
    }

    public void ziwanLib(JSONArray libUrl) {

    }

    public void baijiaLib(JSONArray libUrl) {
        libUrl.add("http://dm.233h5.com/static/sdk/xianxia.sdk.js?version=1.0");
    }

}

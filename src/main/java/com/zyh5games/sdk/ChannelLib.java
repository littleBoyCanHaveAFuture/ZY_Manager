package com.zyh5games.sdk;

import com.alibaba.fastjson.JSONObject;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

/**
 * @author song minghua
 * @date 2020/5/14
 */
public class ChannelLib {
    private static final Logger log = Logger.getLogger(ChannelLib.class);

    //加载通用-渠道js
    public  JSONArray loadChannelLib(Integer type, JSONObject channelData) {
        JSONArray libUrl = new JSONArray();
        libUrl.add("https://zyh5games.com/sdk/common/md5.js");
        libUrl.add("https://zyh5games.com/sdk/common/jquery-3.4.1.min.js");
        switch (type) {
            case ChannelId.H5_ZIWAN:
                ziWanLib(channelData);
                break;
            case ChannelId.H5_BAIJIA:
                baiJiaLib(channelData);
                break;
            default:
                break;
        }
        return libUrl;
    }

    public void ziWanLib(JSONObject channelData) {
        channelData.put("name", "LuoTuoH5");
    }

    public void baiJiaLib(JSONObject channelData) {
        channelData.put("name", "xianxia.sdk");
    }
}

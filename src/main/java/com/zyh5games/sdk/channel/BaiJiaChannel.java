package com.zyh5games.sdk.channel;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.ChannelWorker;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/21
 */
public class BaiJiaChannel implements ChannelWorker {
    private static final Logger log = Logger.getLogger(BaiJiaChannel.class);

    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @param channelData 渠道数据
     * @return boolean
     */
    @Override
    public JSONArray channelLib(JSONObject channelData) {
        JSONArray libUrl = new JSONArray();
        libUrl.add("https://zyh5games.com/sdk/common/md5.js");
        libUrl.add("https://zyh5games.com/sdk/common/jquery-3.4.1.min.js");

        channelData.put("name", "xianxia.sdk");
        return libUrl;
    }

    /**
     * 2.渠道初始化 设置渠道参数token
     *
     * @param map 渠道传入参数
     * @return boolean
     */
    @Override
    public String channelToken(Map<String, String[]> map) {
        return null;
    }

    /**
     * 3.渠道登录<p>
     * 3.1 向渠道校验 获取用户数据 <p>
     * 3.2 设置token<p>
     *
     * @param map      渠道传入参数
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        return false;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        return false;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId        指悦游戏id
     * @param parameterMap 渠道回调参数
     * @param channelOrder 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrder) {
        return false;
    }
}

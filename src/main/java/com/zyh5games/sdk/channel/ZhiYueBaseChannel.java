package com.zyh5games.sdk.channel;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.ChannelId;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/21
 */
@Component("0")
public class ZhiYueBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(ZhiYueBaseChannel.class);

    ZhiYueBaseChannel() {
        channelId = ChannelId.H5_ZHIYUE;
    }

    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @return boolean
     */
    @Override
    public JSONObject channelLib() {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "");
        return channelData;
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
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        return true;
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
        Integer appId = orderData.getInteger("appId");

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId        指悦游戏id
     * @param parameterMap 渠道回调参数
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String channelGameId = configMap.get(appId).getString(BaiJiaConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(BaiJiaConfig.PAY_KEY);

        return true;
    }
}

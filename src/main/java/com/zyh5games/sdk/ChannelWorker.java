package com.zyh5games.sdk;

import com.alibaba.fastjson.JSONObject;
import net.sf.json.JSONArray;

import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/21
 */
public interface ChannelWorker {
    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @param channelData 渠道数据
     * @return JSONArray
     */
    JSONArray channelLib(JSONObject channelData);

    /**
     * 2.渠道初始化 设置渠道参数token
     *
     * @param map 渠道传入参数
     * @return boolean
     */
    String channelToken(Map<String, String[]> map);

    /**
     * 3.渠道登录<p>
     * 3.1 向渠道校验 获取用户数据 <p>
     * 3.2 设置token<p>
     *
     * @param map      渠道传入参数
     * @param userData 渠道用户数据
     * @return boolean
     */
    boolean channelLogin(Map<String, String[]> map, JSONObject userData);

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     * @return boolean
     */
    boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo);

    /**
     * 5.渠道支付订单校验
     *
     * @param appId        指悦游戏id
     * @param parameterMap 渠道回调参数
     * @param channelOrder 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrder);

}

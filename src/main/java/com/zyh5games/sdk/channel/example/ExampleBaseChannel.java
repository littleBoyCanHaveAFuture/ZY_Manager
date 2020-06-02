package com.zyh5games.sdk.channel.example;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("-1")
public class ExampleBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(ExampleBaseChannel.class);

    ExampleBaseChannel() {
        channelId = ChannelId.H5_EXAMPLE;
        configMap = new HashMap<>();
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("" + "?" + System.currentTimeMillis());
        return libUrl;
    }

    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @return boolean
     */
    @Override
    public JSONObject channelLib(Integer appId) {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "XXXH5");
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
        String[] mustKey = {""};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(ExampleConfig.LOGIN_KEY);

        // 加密串
        StringBuilder param = new StringBuilder();
        super.addParam(param, "", "");
        super.addParamAnd(param, "", "");

        log.info("param = " + param.toString());

        // 签名验证
        String sign = map.get("sign")[0];
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelLogin serverSign = " + serverSign);
        log.info("channelLogin sign       = " + sign);

        log.info("channelLogin serverSign = " + serverSign);
        log.info("channelLogin sign       = " + sign);

        if (!sign.equals(serverSign)) {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }

        setUserData(userData, "channelUid", "userName", String.valueOf(channelId), "openid");
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
        String channelGameId = configMap.get(appId).getString(ExampleConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ExampleConfig.PAY_KEY);

        long time = System.currentTimeMillis() / 1000;

        // 加密串
        StringBuilder param = new StringBuilder();
        super.addParam(param, "", "");
        super.addParamAnd(param, "", "");

        log.info("param = " + param.toString());

        // 签名验证
        String sign = "";
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayInfo serverSign = " + serverSign);
        log.info("channelPayInfo sign       = " + sign);

        log.info("channelPayInfo serverSign = " + serverSign);
        log.info("channelPayInfo sign       = " + sign);

        if (!sign.equals(serverSign)) {
            return false;
        }

        // 渠道订单数据
        JSONObject data = new JSONObject();
/*        data.put("uid", "uid");
        data.put("gameId", "gameId");
        data.put("time", "time");
        data.put("server", "server");
        data.put("role", "role");
        data.put("goodsId", "goodsId");
        data.put("goodsName", "goodsName");
        data.put("money", "money");
        data.put("cpOrderId", "cpOrderId");
        data.put("ext", "ext");
        data.put("signType", "md5");
        data.put("sign", "sign");*/

        log.info("channelPayInfo data: " + data);
        channelOrderNo.put("data", data.toJSONString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String channelGameId = configMap.get(appId).getString(ExampleConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ExampleConfig.PAY_KEY);

        // 加密串
        StringBuilder param = new StringBuilder();
        super.addParam(param, "", "");
        super.addParamAnd(param, "", "");

        log.info("param = " + param.toString());

        // 签名验证
        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);

        if (!sign.equals(serverSign)) {
            return false;
        }
        // 渠道订单赋值
        setChannelOrder(channelOrderNo, "", "cpOrderId", "channelOrderId", "money");
        return true;
    }
}

package com.zyh5games.sdk;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.jedis.jedisRechargeCache;
import com.zyh5games.util.MD5Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author song minghua
 * @date 2020/5/14
 */
public class ChannelPayInfo {
    private static final Logger log = Logger.getLogger(ChannelPayInfo.class);
    @Autowired
    jedisRechargeCache cache;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestOperations restOperations;


    public boolean loadPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        boolean isOk = false;
        Integer channelId = orderData.getInteger("channelId");

        switch (channelId) {
            case ChannelId.H5_ZHIYUE:
                //指悦官方
                isOk = zhiYuePayInfo(orderData, channelOrderNo);
                break;
            case ChannelId.H5_ZIWAN:
                //紫菀、骆驼
                isOk = ziWanPayInfo(orderData, channelOrderNo);
                break;
            case ChannelId.H5_BAIJIA:
                isOk = baiJiaPayInfo(orderData, channelOrderNo);
                break;
            default:
                break;
        }

        return isOk;
    }

    public boolean zhiYuePayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        return false;
    }


    public boolean ziWanPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        String payUrl = "https://gameluotuo.com/index.php?g=Home&m=GameOauth&a=pay_info";

        String channelToken = orderData.getString("channelToken");
        String channel_Id = "c53457d37e949c6133752a3bd41f44f1";
        String channelSecertKey = "PGM2FPCTO94DFLWJJE6KMJ6T2QA10V8P";
        StringBuilder param = new StringBuilder();
        //升序排列
        param.append("channel_id").append("=").append(channel_Id);
        param.append("&").append("item_id").append("=").append(orderData.get("goodsId"));
        param.append("&").append("orderid").append("=").append(orderData.get("cpOrderNo"));
        param.append("&").append("other").append("=").append("");
        param.append("&").append("price").append("=").append(orderData.get("amount"));
        param.append("&").append("userToken").append("=").append(channelToken);

        //参数赋值 并签名

        String sign = MD5Util.md5(param.toString() + channelSecertKey);
        param.append("&").append("sign").append("=").append(sign);

        String url = payUrl + "&" + param;

        JSONObject rsp = httpGet(url);
        System.out.println("ziWanPayInfo\n " + rsp);
        if (rsp.containsKey("status") && rsp.getInteger("status") == 1001) {
            String info = rsp.getString("info");
            String domain = rsp.getString("domain");

            JSONObject data = rsp.getJSONObject("data");

            channelOrderNo.put("domain", domain);
            channelOrderNo.put("data", data.toJSONString());
            return true;
        }

        return false;
    }

    public boolean baiJiaPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        return false;
    }

    public JSONObject httpGet(String notifyUrl) {
        String rsp = restOperations.getForObject(notifyUrl, String.class);
        log.info("cp支付回调：" + rsp);
        JSONObject json = JSONObject.parseObject(rsp);
//        System.out.println(json);
        return json;
    }
}

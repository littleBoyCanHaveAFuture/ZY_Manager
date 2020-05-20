package com.ssm.promotion.core.sdk;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.service.AccountService;
import com.ssm.promotion.core.util.MD5Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author song minghua
 * @date 2020/5/14
 */
public class ChannelPayInfo {
    private static final Logger log = Logger.getLogger(ChannelPayInfo.class);
    @Autowired
    jedisRechargeCache cache;
    @Resource
    AccountWorker accountWorker;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestOperations restOperations;
    @Resource
    private AccountService accountService;

    public boolean loadPayInfo(JSONObject orderData, JSONObject channelOrderNo) throws Exception {
        boolean isOk = false;
        Integer channelId = orderData.getInteger("channelId");

        switch (channelId) {
            case ChannelId.h5_zhiyue:
                //指悦官方
                isOk = zhiyuePayInfo(orderData, channelOrderNo);
                break;
            case ChannelId.h5_ziwan:
                //紫菀、骆驼
                isOk = ziwanPayInfo(orderData, channelOrderNo);
                break;
            case ChannelId.h5_baijia:
                isOk = baijiaPayInfo(orderData, channelOrderNo);
                break;
            default:
                break;
        }

        return isOk;
    }

    public boolean zhiyuePayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        return false;
    }


    public boolean ziwanPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
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
        System.out.println("ziwanPayInfo\n " + rsp);
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

    public boolean baijiaPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
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

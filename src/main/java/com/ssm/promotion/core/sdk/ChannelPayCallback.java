package com.ssm.promotion.core.sdk;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.service.AccountService;
import com.ssm.promotion.core.util.MD5Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/14
 */
public class ChannelPayCallback {
    private static final Logger log = Logger.getLogger(ChannelPayCallback.class);
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

    /**
     * 渠道支付订单校验
     */
    public boolean loadPayCallback(Integer appId, Integer channelId, Map<String, String> parameterMap, JSONObject channelOrder) throws Exception {
        boolean isOk = false;

        switch (channelId) {
            case ChannelId.h5_zhiyue:
                //指悦官方
                isOk = zhiyuePayCallback(parameterMap, channelOrder);
                break;
            case ChannelId.h5_ziwan:
                //紫菀、骆驼
                isOk = ziwanPayCallback(parameterMap, channelOrder);
                break;
            case ChannelId.h5_baijia:
                isOk = baijiaPayCallback(parameterMap, channelOrder);
                break;
            default:
                break;
        }

        return isOk;
    }

    public void setChannelOrder(JSONObject channelOrderNo, String zyUid, String cpOrderId, String channelOrderId, String price) {
        channelOrderNo.put("zy_uid", zyUid);
        channelOrderNo.put("price", price);
        channelOrderNo.put("channelOrderId", channelOrderId);
        channelOrderNo.put("cpOrderId", cpOrderId);
    }

    public boolean zhiyuePayCallback(Map<String, String> parameterMap, JSONObject channelOrderNo) {
        return false;
    }


    public boolean ziwanPayCallback(Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String checkOrderUrl = "https://gameluotuo.com/index.php?g=Home&m=GameOauth&a=check_order";
        String[] mustKey = {"openid", "price", "other", "item_id", "orderid", "sign"};
        for (String key : mustKey) {
            if (!parameterMap.containsKey(key)) {
                System.out.println("ziwanPayCallback 缺少key：" + key);
                return false;
            }
        }
        String channel_Id = "c53457d37e949c6133752a3bd41f44f1";
        String channelSecertKey = "PGM2FPCTO94DFLWJJE6KMJ6T2QA10V8P";

        // 单位是元是分？
        String price = parameterMap.get("price");
        //升序排列
        StringBuilder param = new StringBuilder();
        param.append("channel_id").append("=").append(channel_Id);
        param.append("&").append("orderid").append("=");
        param.append(channelSecertKey);

        //参数赋值 并签名
        String sign = MD5Util.md5(param.toString());
        param.append("&").append("sign").append("=").append(sign);

        JSONObject rsp = httpGet(checkOrderUrl + param);

        System.out.println("ziwanPayCallback 渠道回调 " + rsp);

        if (rsp.containsKey("status")) {
            if (rsp.getInteger("status") == 1001) {
                String openid = rsp.getString("openid");
                String time = rsp.getString("time");
                String item_id = rsp.getString("item_id");
                String orderid = rsp.getString("orderid");
                System.out.println("ziwanPayCallback 支付成功：");

                // openid 可能重复 需要处理 todo
                Integer zyUid = 0;
                Account account = accountService.findUser(String.valueOf(ChannelId.h5_ziwan), openid);
                if (account == null) {
                    return false;
                } else {
                    zyUid = account.getId();
                }
                setChannelOrder(channelOrderNo, String.valueOf(zyUid), orderid, "", price);
                return true;
            } else if (rsp.getInteger("status") == 4001 || rsp.getInteger("status") == 4002) {
                System.out.println("ziwanPayCallback 参数缺少：");
            } else if (rsp.getInteger("status") == 1002) {
                System.out.println("ziwanPayCallback 订单未支付：");
            }
        }
        return false;
    }

    public boolean baijiaPayCallback(Map<String, String> parameterMap, JSONObject channelOrderNo) {
        return false;
    }

    public JSONObject httpGet(String notifyUrl) {
        String rsp = restOperations.getForObject(notifyUrl, String.class);
        log.info("cp支付回调：" + rsp);
        JSONObject json = JSONObject.parseObject(rsp);
        System.out.println(json);
        return json;
    }
}

package com.ssm.promotion.core.sdk;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.service.AccountService;
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
public class PayInfo {
    private static final Logger log = Logger.getLogger(PayInfo.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestOperations restOperations;
    @Resource
    private AccountService accountService;
    @Autowired
    jedisRechargeCache cache;
    @Resource
    AccountWorker accountWorker;

    public boolean loadPayInfo(Map<String, String[]> map) throws Exception {

        boolean isOk = false;
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("channelId")[0]);

        switch (channelId) {
            case 0:
                //指悦官方
                isOk = zhiyuePayInfo();
                break;
            case 8:
                //紫菀、骆驼
                isOk = ziwanPayInfo();
                break;
            case 9:
                isOk = baijiaPayInfo();
                break;
            default:
                break;
        }
        if (isOk) {

        }
        return isOk;
    }


    // 指悦官方登录
    public boolean zhiyuePayInfo() {
        return false;
    }


    public boolean ziwanPayInfo() {
        return false;
    }

    public boolean baijiaPayInfo() {
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

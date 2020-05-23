package com.zyh5games.sdk;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {
    private static final Logger log = LoggerFactory.getLogger(HttpService.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestOperations restOperations;

    public JSONObject httpGet(String notifyUrl) {
        log.info("BaseChannel httpGet =" + notifyUrl);

        JSONObject json = new JSONObject();
        try {
            String rsp = restOperations.getForObject(notifyUrl, String.class);
            log.info("httpGet：" + rsp);
            json = JSONObject.parseObject(rsp);
            System.out.println(json);
        } catch (Exception e) {
            json.put("status", false);
            json.put("message", e.getMessage());
        }
        return json;
    }

}

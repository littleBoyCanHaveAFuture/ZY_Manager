package com.zyh5games.sdk.channel;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    public JSONObject httpGetJson(String notifyUrl) {
        log.info("BaseChannel httpGetJson =" + notifyUrl);

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

    public String httpGetString(String notifyUrl) {
        log.info("BaseChannel httpGetString =" + notifyUrl);

        String rsp = restOperations.getForObject(notifyUrl, String.class);
        log.info("httpGet：" + rsp);
        System.out.println(rsp);

        return rsp;
    }


    public JSONObject httpPostJson(String notifyUrl, JSONObject data) {
        log.info("BaseChannel httpPostJson =" + notifyUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
        HttpEntity<String> strEntity = new HttpEntity<>(data.toJSONString(), headers);

        JSONObject json = new JSONObject();
        try {
            String rsp = restOperations.postForObject(notifyUrl, strEntity, String.class);
            log.info("httpPost：" + rsp);
            json = JSONObject.parseObject(rsp);
            System.out.println(json);
        } catch (Exception e) {
            json.put("status", false);
            json.put("message", e.getMessage());
        }
        return json;
    }
}

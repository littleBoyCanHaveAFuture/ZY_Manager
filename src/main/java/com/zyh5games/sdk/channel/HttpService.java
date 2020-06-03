package com.zyh5games.sdk.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.game1758.Game1758Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {
    private static final Logger log = LoggerFactory.getLogger(HttpService.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestOperations restOperations;

    public void main(String[] args) {
        String url = Game1758Config.PAY_URL;
        String data = "{\"ext\":\"\",\"gid\":\"a740d6085c568d29e85aa5ed0369fa04\",\"roleId\":\"2097153\",\"sign\":\"005056e58affd7bccbe65fc466245441\",\"txId\":\"5f95835c-34b7-4661-bf7c-09c825c04bb2\",\"serverName\":\"1区\",\"nonce\":\"swD7pQ69\",\"serverId\":\"1\",\"productDesc\":\"1000元宝\",\"vipLevel\":\"0\",\"totalFee\":\"1000\",\"roleCoins\":\"0\",\"roleName\":\"名_花开尘埃\",\"gameRolePower\":\"0\",\"appKey\":\"152d0a875b3acdd3b96a62f12025021c\",\"state\":\"5f95835c-34b7-4661-bf7c-09c825c04bb2\",\"roleLevel\":\"1\",\"timestamp\":\"1591172885552\"}";
        JSONObject jsonObject = JSON.parseObject(data);
        httpPostXwwFormUrlEncoded(url, jsonObject);
    }

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
        log.info("httpPostJson =" + notifyUrl);
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
    //long 参数报错
    public JSONObject httpPostXwwFormUrlEncoded(String notifyUrl, JSONObject data) {
        log.info("httpPostXwwFormUrlEncoded = " + notifyUrl);
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        for (String key : data.keySet()) {
            postParameters.add(key, data.get(key));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, Object>> r = new HttpEntity<>(postParameters, headers);

        log.info("httpPostXwwFormUrlEncoded = data = " + data.toString());

        JSONObject json = new JSONObject();
        try {
            String rsp = restTemplate.postForObject(notifyUrl, r, String.class);
            log.info("httpPost：" + rsp);
            json = JSONObject.parseObject(rsp);
            System.out.println(json);
        } catch (Exception e) {
            log.info("httpPostXwwFormUrlEncoded = 3 e=" + e);
            data.put("status", false);
            data.put("message", e.getMessage());
        }

        return json;
    }


}

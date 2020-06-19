package com.zyh5games.sdk.channel;

import com.alibaba.fastjson.JSONObject;
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

    /**
     * 盛娱请求头加额外参数
     */
    public JSONObject httpGetJsonExtraHeader(String notifyUrl, HttpHeaders headers) {
        log.info("BaseChannel httpGetJsonExtraHeader =" + notifyUrl);

        JSONObject json = new JSONObject();
        try {
            HttpEntity<String> strEntity = new HttpEntity<>("", headers);
            String rsp = restOperations.getForObject(notifyUrl, String.class, strEntity);
            log.info("httpGet：" + rsp);
            json = JSONObject.parseObject(rsp);
            System.out.println(json);
        } catch (Exception e) {
            json.put("status", false);
            json.put("message", e.getMessage());
        }
        return json;
    }

    /**
     * 盛娱请求头加额外参数
     */
    public JSONObject httpPostXwwFormUrlEncodedExtraHeader(String notifyUrl, JSONObject data, HttpHeaders headers) {
        log.info("httpPostXwwFormUrlEncodedExtraHeader = " + notifyUrl);
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        for (String key : data.keySet()) {
            postParameters.add(key, data.get(key));
        }

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
            log.info("httpPostXwwFormUrlEncoded  e = " + e);
            json.put("status", false);
            json.put("message", e.getMessage());
        }

        return json;
    }


    public JSONObject httpGetJsonNo(String notifyUrl) {
        JSONObject json = new JSONObject();
        try {
            String rsp = restOperations.getForObject(notifyUrl, String.class);
            json = JSONObject.parseObject(rsp);
        } catch (Exception e) {
            json.put("status", false);
            json.put("message", e.getMessage());
        }
        return json;
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
            log.info("httpPostXwwFormUrlEncoded  e = " + e);
            json.put("status", false);
            json.put("message", e.getMessage());
        }

        return json;
    }

}

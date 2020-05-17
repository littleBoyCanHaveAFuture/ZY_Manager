package com.ssm.promotion.core.sdk;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.service.AccountService;
import com.ssm.promotion.core.util.MD5Util;
import com.ssm.promotion.core.util.RandomUtil;
import com.ssm.promotion.core.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/14
 */
public class ChannelLogin {
    private static final Logger log = Logger.getLogger(ChannelLogin.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestOperations restOperations;
    @Resource
    private AccountService accountService;

    public boolean loadChannelLogin(Integer channelId, Map<String, String[]> map, JSONObject userData) {
        boolean isOk = false;
        // 渠道uid
        userData.put("uid", "");
        // 	渠道username
        userData.put("username", "");
        // 是否游客,登录后此值为true
        userData.put("isLogin", "");
        // 当前时间戳 单位：秒
        userData.put("time", System.currentTimeMillis() / 1000);
        // 	token 游戏服务器需通过v2/checkUserInfo接口(参见服务器接口文档)验证token和UID的正确性
        userData.put("token", RandomUtil.rndSecertKey());
        // 渠道ID
        userData.put("channelId", "");

        // 需要存入redis [appId-channelId-uid]-token
        switch (channelId) {
            case 0:
                //指悦官方
                isOk = zhiyueLogin(map, userData);
                break;
            case 8:
                //紫菀、骆驼
                isOk = ziwanLogin(map, userData);
                break;
            case 9:
                isOk = baijiaLogin(map, userData);
                break;
            default:
                break;
        }
        return isOk;
    }

    public boolean zhiyueLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("channelId")[0]);

        if (!map.containsKey("zy_channelUid") || !map.containsKey("zy_account") || !map.containsKey("zy_password")) {
            return false;
        }
        String channelUid = map.get("zy_channelUid")[0];
        Map<String, Object> objectMap = new HashMap<>(6);
        objectMap.put("channelId", channelId);
        objectMap.put("channelUid", channelUid);
        Account account = accountService.findUserBychannelUid(String.valueOf(channelId), channelUid);
        if (account != null) {
            userData.replace("uid", channelUid);
            userData.replace("username", account.getChannelUserName());
            userData.replace("isLogin", true);
            // 当前时间戳 单位：秒
            userData.replace("time", System.currentTimeMillis() / 1000);
            // 	token 游戏服务器需通过v2/checkUserInfo接口(参见服务器接口文档)验证token和UID的正确性
            userData.replace("token", RandomUtil.rndSecertKey());
            // 渠道ID
            userData.replace("channelId", channelUid);

            return true;
        } else {
            return false;
        }

    }

    public boolean ziwanLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);

        if (!map.containsKey("channel_id") || !map.containsKey("userToken") || !map.containsKey("other")) {
            return false;
        }
        String channel_id = map.get("channel_id")[0];
        String userToken = map.get("userToken")[0];
        String other = map.get("other")[0];

        String loginKey = "";
        String payKey = "";
        String serverSign = "";
        StringBuilder param = new StringBuilder();
        StringBuilder url = new StringBuilder();

        if (appId == AppId.julongzhange) {
            url = new StringBuilder("https://gameluotuo.com/index.php?g=Home&m=GameOauth&a=get_userinfo");
            // channelId =c53457d37e949c6133752a3bd41f44f1
            loginKey = "PGM2FPCTO94DFLWJJE6KMJ6T2QA10V8P";
            payKey = "";
        }
        //升序排列
        ArrayList<String> arr = new ArrayList<>();
        arr.add("channel_id");
        arr.add("userToken");
        arr.add("other");
        StringUtil.sort(arr);
        //参数赋值 并签名
        for (String s : arr) {
            param.append("&").append(s).append("=").append(map.get(s)[0]);
        }
        param.append(loginKey);
        serverSign = MD5Util.md5(param.substring(1));
        param.append("&sign=").append(serverSign);

        JSONObject jsonObject = httpGet(url + param.toString());

        if (jsonObject.containsKey("status") && jsonObject.getInteger("status") == 1001) {
            System.out.println("紫菀平台 登录校验成功");
            return true;
        } else {
            return false;
        }
    }

    public boolean baijiaLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);

        if (!map.containsKey("gameId") || !map.containsKey("uid") || !map.containsKey("userName") ||
                !map.containsKey("birthday") || !map.containsKey("idCard") || !map.containsKey("sign")) {
            return false;
        }

        String channel_id = map.get("channel_id")[0];
        String userToken = map.get("userToken")[0];
        String other = map.get("other")[0];

        String loginKey = "";
        String payKey = "";
        String serverSign = "";
        StringBuilder param = new StringBuilder();
        StringBuilder url = new StringBuilder();
        //百家-http://www.test.com/index.php?ac=game&id=1&avatar=http%3A%2F%2Fh5.6816.com%2Fstatic%2Fattachment%2Fuser%2F20160816%2F1471334322441376.png&gameId=113&signType=md5&time=1475042060&uid=29923&userName=dreamfly_1981&userSex=male&sign=6a3f16124a0c641082c17a438d1323a8
        //解释|必选-参与加密
        // 游戏ID,通过商务获得
        String gameId = map.get("gameId")[0];
        // 用户UID (唯一) 我方用户的UID
        String uid = map.get("uid")[0];
        // 用户名（urlencode）
        String userName = map.get("userName")[0];
        // 当前时间unix时间戳
        String time = map.get("time")[0];
        // 用户头像|否-否
        String avatar = map.containsKey("avatar") ? map.get("avatar")[0] : "none";
        // 玩家性别[no 末设置 male 男 famale 女]|否-否
        String userSex = map.containsKey("avatar") ? map.get("userSex")[0] : "none";
        // 邀请进入游戏的用户 ID|否-否
        String fuid = map.containsKey("avatar") ? map.get("fuid")[0] : "none";
        // 是否身份证实名，注:yes有身份证 no 无身份证|否-否
        String authentication = map.containsKey("avatar") ? map.get("authentication")[0] : "none";
        // 用户的年龄 游戏商请自行判断是否开启防沉迷，返回0或空为用户末实名|否-否
        String age = map.containsKey("avatar") ? map.get("age")[0] : "none";
        // 生日|是-否
        String birthday = map.get("birthday")[0];
        // MD5的身份证号|是-否
        String idCard = map.get("idCard")[0];
        // 加密串|是-否
        String sign = map.get("sign")[0];


        if (appId == AppId.julongzhange) {
            loginKey = "e3101115202b291b1c942fbc417ba064";
            payKey = "82937ce89565d82c09422e54f1fc4e24";
        }

        param.append("gameId=").append(gameId);
        param.append("uid=").append(gameId);
        param.append("userName=").append(userName);
        param.append("time=").append(time);
        param.append("key=").append(loginKey);

        serverSign = MD5Util.md5(param.toString());
        if (!serverSign.equals(sign)) {
            return false;
        } else {
            //查找用户是否存在 不存在则创建 todo
            return true;
        }
    }

    public JSONObject httpGet(String notifyUrl) {
        String rsp = restOperations.getForObject(notifyUrl, String.class);
        log.info("cp支付回调：" + rsp);
        JSONObject json = JSONObject.parseObject(rsp);
        System.out.println(json);
        return json;
    }
}

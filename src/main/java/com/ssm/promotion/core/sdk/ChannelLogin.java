package com.ssm.promotion.core.sdk;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.service.AccountService;
import com.ssm.promotion.core.util.MD5Util;
import com.ssm.promotion.core.util.RandomUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/14
 */
public class ChannelLogin {
    private static final Logger log = Logger.getLogger(ChannelLogin.class);
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

    //    向渠道校验 获取用户数据
    public boolean loadChannelLogin(Map<String, String[]> map, JSONObject userData) throws Exception {
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
        // 指悦uid
        userData.put("zhiyueUid", "");

        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        switch (channelId) {
            case ChannelId.h5_zhiyue:
                //指悦官方
                isOk = zhiyueLogin(map, userData);
                break;
            case ChannelId.h5_ziwan:
                //紫菀、骆驼
                isOk = ziwanLogin(map, userData);
                break;
            case ChannelId.h5_baijia:
                isOk = baijiaLogin(map, userData);
                break;
            default:
                break;
        }
        if (isOk) {
            //没有则注册指悦账号
            //1.判断账号是否存在
            String channelUid = userData.getString("uid");
            String token = userData.getString("token");
            String openid = userData.getString("openid");

            Account account = channelReg(appId, channelId, channelUid, openid);
            if (account != null) {
                setToken(String.valueOf(appId), String.valueOf(channelId), channelUid, token);

            } else {
                isOk = false;
            }
        }
        return isOk;
    }

    /**
     * 需要存入redis [appId-channelId-uid]-token
     */
    public void setToken(String gameId, String channelId, String channelUid, String token) {
        cache.setChannelLoginToken(gameId, channelId, channelUid, token);
    }

    public Account channelReg(Integer appId, Integer channelId, String channelUid, String openId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("isChannel", "true");
        map.put("channelId", channelId);
        map.put("channelUid", channelUid);

        Account account = accountWorker.getAccount(map);
        if (account != null) {
            return account;
        } else {
            JSONObject jsonObject = new JSONObject();
            JSONObject reply = new JSONObject();

            jsonObject.put("appId", appId);
            jsonObject.put("channelId", channelId);
            jsonObject.put("channelUid", channelUid);
            jsonObject.put("openId", openId);
            return accountWorker.commonReg(reply, jsonObject);
        }
    }

    public void setUserData(JSONObject userData, String channelUid, String username, String channelId, String openid) {
        String token = RandomUtil.rndSecertKey();
        // 渠道uid
        userData.replace("uid", channelUid);
        // 	渠道username
        userData.put("username", username);
        // 是否游客,登录后此值为true
        userData.put("isLogin", false);
        // 当前时间戳 单位：秒
        userData.put("time", System.currentTimeMillis() / 1000);
        // 	token 游戏服务器需通过v2/checkUserInfo接口(参见服务器接口文档)验证token和UID的正确性
        userData.put("token", token);
        // 渠道ID
        userData.put("channelId", channelId);
        // 渠道用户标识
        userData.put("openid", openid);
    }

    // 指悦官方登录
    public boolean zhiyueLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        if (!map.containsKey("zy_channelUid") || !map.containsKey("zy_account") || !map.containsKey("zy_password")) {
            return false;
        }
        String channelUid = map.get("zy_channelUid")[0];

        Map<String, Object> objectMap = new HashMap<>(6);
        objectMap.put("channelId", channelId);
        objectMap.put("channelUid", channelUid);

        Account account = accountService.findUserBychannelUid(String.valueOf(channelId), channelUid);
        if (account != null) {
            String token = RandomUtil.rndSecertKey();
            userData.replace("uid", channelUid);
            userData.replace("username", account.getChannelUserName());
            userData.replace("isLogin", true);
            // 当前时间戳 单位：秒
            userData.replace("time", System.currentTimeMillis() / 1000);
            // 	token 游戏服务器需通过v2/checkUserInfo接口(参见服务器接口文档)验证token和UID的正确性
            userData.replace("token", token);
            // 渠道ID
            userData.replace("channelId", channelId);

            return true;
        } else {
            return false;
        }
    }


    public boolean ziwanLogin(Map<String, String[]> map, JSONObject userData) throws Exception {
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        if (!map.containsKey("channel_id") || !map.containsKey("userToken") || !map.containsKey("other")) {
            return false;
        }
        String channel_id = map.get("channel_id")[0];
        String userToken = map.get("userToken")[0];
        String other = map.get("other")[0];

        String loginKey = "";
        String payKey = "";

        StringBuilder url = new StringBuilder();

        if (appId == AppId.julongzhange) {
            url = new StringBuilder("https://gameluotuo.com/index.php?g=Home&m=GameOauth&a=get_userinfo");
            // channelId =c53457d37e949c6133752a3bd41f44f1
            loginKey = "PGM2FPCTO94DFLWJJE6KMJ6T2QA10V8P";
            payKey = "";
        }
        //升序排列

        StringBuilder param = new StringBuilder();
        param.append("channel_id").append("=").append(channel_id);
        param.append("&").append("userToken").append("=").append(userToken);

        String sign = MD5Util.md5(param.toString() + loginKey);
        param.append("&sign=").append(sign);

        JSONObject jsonObject = httpGet(url.toString() + "&" + param.toString());
        if (jsonObject.containsKey("info")) {
            System.out.println("紫菀平台 登录校验 info:" + jsonObject.getString("info"));
        }
        if (jsonObject.containsKey("userinfo")) {
            System.out.println("紫菀平台 登录校验 userinfo:" + jsonObject.getString("userinfo"));
        }
        if (jsonObject.containsKey("status") && jsonObject.getInteger("status") == 1001) {
            System.out.println("紫菀平台 登录校验成功");
//            userinfo (获取到的用户信息，status为1001时有，包含wechaname，用户名称；portrait，用户头像；sex，性别；city，城市；province 省会;openid 用户标识，uid 用户ID)
            JSONObject userinfo = jsonObject.getJSONObject("userinfo");

            String wechaname = userinfo.getString("wechaname");
            String portrait = userinfo.getString("portrait");
            String sex = userinfo.getString("sex");
            String city = userinfo.getString("city");
            String province = userinfo.getString("province");
            String openid = userinfo.getString("openid");
            String uid = userinfo.getString("uid");

            setUserData(userData, uid, wechaname, String.valueOf(channelId), openid);
            return true;
        } else {
            return false;
        }
    }

    public boolean baijiaLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);
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
            String GameId = "6";
            loginKey = "e3101115202b291b1c942fbc417ba064";
            payKey = "82937ce89565d82c09422e54f1fc4e24";
        }

        param.append("gameId=").append(gameId);
        param.append("uid=").append(uid);
        param.append("userName=").append(userName);
        param.append("time=").append(time);
        param.append("key=").append(loginKey);

        serverSign = MD5Util.md5(param.toString());
        if (!sign.equals(serverSign)) {
            return false;
        } else {
            setUserData(userData, uid, userName, String.valueOf(channelId), "");
            return true;
        }
    }

    public JSONObject httpGet(String notifyUrl) {
        JSONObject json = new JSONObject();
        System.out.println("httpGet " + notifyUrl);
        try {
            String rsp = restOperations.getForObject(notifyUrl, String.class);
            log.info("cp支付回调：" + rsp);
            json = JSONObject.parseObject(rsp);
            System.out.println(json);
        } catch (Exception e) {
            json.put("status", false);
            json.put("message", e.getMessage());
        }

        return json;
    }
}

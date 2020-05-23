package com.zyh5games.sdk.channel;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.ChannelId;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/21
 */
@Component("10")
public class Wan5144BaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(Wan5144BaseChannel.class);

    Wan5144BaseChannel() {
        channelId = ChannelId.H5_5144WAN;
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("https://dm.5144wan.com/static/sdk/xianxia.sdk.js");
        return libUrl;
    }

    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @return boolean
     */
    @Override
    public JSONObject channelLib() {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "Wan5144H5");
        return channelData;
    }

    /**
     * 2.渠道初始化 设置渠道参数token
     *
     * @param map 渠道传入参数
     * @return boolean
     */
    @Override
    public String channelToken(Map<String, String[]> map) {
        return null;
    }

    /**
     * 3.渠道登录<p>
     * 3.1 向渠道校验 获取用户数据 <p>
     * 3.2 设置token<p>
     *
     * @param map      渠道传入参数
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        if (!map.containsKey("gameId") || !map.containsKey("uid") || !map.containsKey("userName") ||
//                !map.containsKey("birthday") || !map.containsKey("idCard") ||
                !map.containsKey("sign")) {
            return false;
        }

        String loginKey = configMap.get(appId).getString(Wan5144Config.LOGIN_KEY);
        String payKey = configMap.get(appId).getString(Wan5144Config.PAY_KEY);


        //百家-http://www.test.com/index.php?ac=game&id=1&avatar=http%3A%2F%2Fh5.6816.com%2Fstatic%2Fattachment%2Fuser%2F20160816%2F1471334322441376.png&gameId=113&signType=md5&time=1475042060&uid=29923&userName=dreamfly_1981&userSex=male&sign=6a3f16124a0c641082c17a438d1323a8
        // 解释|必选-参与加密
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
        String birthday = map.containsKey("birthday") ? map.get("birthday")[0] : "none";
        // MD5的身份证号|是-否
        String idCard = map.containsKey("idCard") ? map.get("idCard")[0] : "none";
        // 加密串|是-否
        String sign = map.get("sign")[0];
        String deCodeUserName = "";
        try {
            deCodeUserName = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Md5(gameId=113&time=1475042196&uid=29923&userName=dreamfly_1981&key=testappykey)
        StringBuilder param = new StringBuilder();
        param.append("gameId").append("=").append(gameId);
        param.append("&").append("time").append("=").append(time);
        param.append("&").append("uid").append("=").append(uid);
        param.append("&").append("userName").append("=").append(deCodeUserName);
        param.append("&").append("key").append("=").append(loginKey);

        System.out.println("param = " + param.toString());

        String serverSign = MD5Util.md5(param.toString());
        log.info("channelLogin = " + serverSign);
        log.info("sign = " + sign);

        System.out.println("channelLogin = " + serverSign);
        System.out.println("sign = " + sign);

        if (!sign.equals(serverSign)) {
            setUserData(userData, "", userName, String.valueOf(channelId), "");
            return false;
        } else {
            setUserData(userData, uid, userName, String.valueOf(channelId), "");
            return true;
        }
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(Wan5144Config.GAME_ID);
        String payKey = configMap.get(appId).getString(Wan5144Config.PAY_KEY);

        long time = System.currentTimeMillis() / 1000;

        //Md5(cpOrderId=1475049097&gameId=113&goodsId=1&goodsName=测试商品&money=1&role=1&server=1&time=1475049097&uid=6298253&key=testpaykey)
        StringBuilder param = new StringBuilder();
        param.append("cpOrderId").append("=").append(orderData.getString("cpOrderNo"));
        param.append("&").append("gameId").append("=").append(channelGameId);
        param.append("&").append("goodsId").append("=").append(orderData.getString("goodsId"));
        param.append("&").append("goodsName").append("=").append(orderData.getString("subject"));
        param.append("&").append("money").append("=").append(orderData.getString("amount"));
        param.append("&").append("role").append("=").append(orderData.getString("userRoleId"));
        param.append("&").append("server").append("=").append(orderData.getString("serverId"));
        param.append("&").append("time").append("=").append(time);
        param.append("&").append("uid").append("=").append(orderData.getString("uid"));
        param.append("&").append("key").append("=").append(payKey);

        System.out.println("channelPayInfo : " + param.toString());

        String sign = MD5Util.md5(param.toString());
        System.out.println("channelPayInfo sign: " + sign);

        String urlUid = "";
        String urlChannelGameId = "";
        String urlTime = "";
        String urlServer = "";
        String urlUserRoleId = "";
        String urlGoodsId = "";
        String urlGoodsName = "";
        String urlMoney = "";
        String urlCpOrderId = "";
        String urlExtrasParams = "";

        try {
            urlUid = URLEncoder.encode(orderData.getString("uid"), String.valueOf(StandardCharsets.UTF_8));
            urlChannelGameId = URLEncoder.encode(channelGameId, String.valueOf(StandardCharsets.UTF_8));
            urlTime = URLEncoder.encode(String.valueOf(time), String.valueOf(StandardCharsets.UTF_8));
            urlServer = URLEncoder.encode(orderData.getString("serverId"), String.valueOf(StandardCharsets.UTF_8));
            urlUserRoleId = URLEncoder.encode(orderData.getString("userRoleId"), String.valueOf(StandardCharsets.UTF_8));

            urlGoodsId = URLEncoder.encode(orderData.getString("goodsId"), String.valueOf(StandardCharsets.UTF_8));
            urlGoodsName = URLEncoder.encode(orderData.getString("subject"), String.valueOf(StandardCharsets.UTF_8));
            urlMoney = URLEncoder.encode(orderData.getString("amount"), String.valueOf(StandardCharsets.UTF_8));
            urlCpOrderId = URLEncoder.encode(orderData.getString("cpOrderNo"), String.valueOf(StandardCharsets.UTF_8));
            urlExtrasParams = URLEncoder.encode(orderData.getString("extrasParams"), String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        JSONObject data = new JSONObject();
        data.put("uid", urlUid);
        data.put("gameId", urlChannelGameId);
        data.put("time", urlTime);
        data.put("server", urlServer);
        data.put("role", urlUserRoleId);
        data.put("goodsId", urlGoodsId);
        data.put("goodsName", urlGoodsName);
        data.put("money", urlMoney);
        data.put("cpOrderId", urlCpOrderId);
        data.put("ext", urlExtrasParams);
        data.put("signType", "md5");
        data.put("sign", sign);

        System.out.println("channelPayInfo data: " + data);
        channelOrderNo.put("data", data.toJSONString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId        指悦游戏id
     * @param parameterMap 渠道回调参数
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String channelGameId = configMap.get(appId).getString(Wan5144Config.GAME_ID);
        String payKey = configMap.get(appId).getString(Wan5144Config.PAY_KEY);

        String sign = parameterMap.get("sign");

        //MD5(cpOrderId=1475049097&gameId=113&goodsId=1&goodsName=测试商品&money=1.00&orderId=201801241127404978&role=1&server=1&status=success&time=1475049097&uid=6298253&userName=dreamfly_1981&key=testpaykey)
        StringBuilder param = new StringBuilder();
        param.append("cpOrderId").append("=").append(parameterMap.get("cpOrderId"));
        param.append("&").append("gameId").append("=").append(channelGameId);
        param.append("&").append("goodsId").append("=").append(parameterMap.get("goodsId"));
        param.append("&").append("goodsName").append("=").append(parameterMap.get("goodsName"));
        param.append("&").append("money").append("=").append(parameterMap.get("money"));
        param.append("&").append("orderId").append("=").append(parameterMap.get("orderId"));
        param.append("&").append("role").append("=").append(parameterMap.get("role"));
        param.append("&").append("server").append("=").append(parameterMap.get("server"));
        param.append("&").append("status").append("=").append(parameterMap.get("status"));
        param.append("&").append("time").append("=").append(parameterMap.get("time"));
        param.append("&").append("uid").append("=").append(parameterMap.get("uid"));
        param.append("&").append("userName").append("=").append(parameterMap.get("userName"));
        param.append("&").append("key").append("=").append(payKey);

        System.out.println("channelPayCallback : " + param.toString());

        String serverSign = MD5Util.md5(param.toString());
        System.out.println("channelPayInfo sign: " + serverSign);

        if (sign.equals(serverSign)) {
            setChannelOrder(channelOrderNo, "", parameterMap.get("orderId"), "", parameterMap.get("money"));
            return true;
        }

        return false;
    }
}

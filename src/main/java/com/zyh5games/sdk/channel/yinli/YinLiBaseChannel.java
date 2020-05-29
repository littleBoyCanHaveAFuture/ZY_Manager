package com.zyh5games.sdk.channel.yinli;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.util.FeeUtils;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 欢聚
 * 未接的 todo
 * 1.游戏礼包领取接口
 * 2.游戏登录被顶
 * 3.游戏服务器列表查询
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("13")
public class YinLiBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(YinLiBaseChannel.class);
    @Autowired
    HttpService httpService;

    YinLiBaseChannel() {
        configMap = new HashMap<>();
        channelId = ChannelId.H5_HUANJU;
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        String jsUrl = "https://game.kuku168.cn/public/js/kuku_js_sdk.min.js?timestamp=" + System.currentTimeMillis();
        libUrl.add(jsUrl);
        return libUrl;
    }

    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @return boolean
     */
    @Override
    public JSONObject channelLib(Integer appId) {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "YinLiH5");

        JSONObject config = configMap.get(appId);
        if (config != null && !config.isEmpty()) {
            JSONObject c = new JSONObject();
            c.put("GameKey", config.getString(YinLiConfig.GAME_KEY));
            channelData.put("config", c.toJSONString());
        }
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
     *                 接收参数(CGI) 类型         必选  参于加密      说明
     *                 gameId	    int		    是	是	        产品合作ID
     *                 uid	        string		是	是	        用户UID (唯一) 我方用户的UID
     *                 userName	    string		是	是	        用户名（urlencode）
     *                 time	        int		    是	是	        当前时间unix时间戳(服务端会判断时间是否超过配置时间)
     *                 avatar	    String		否	否	        用户头像
     *                 userSex	    String		否	否	        玩家性别[no 末设置 male 男 famale 女]
     *                 fromUid	    string		否	否	        来自分享者的UID
     *                 isAdult	    string		是	否	        玩家是否成年[no未成年，yes成年]
     *                 sign	        string		是	否	        加密串
     *                 signType	    string		是	否	        固定md5
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String[] mustKey = {"token"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        String userInfoUrl = YinLiConfig.USERINFO_URL;
        String gameKey = configMap.get(appId).getString(YinLiConfig.GAME_KEY);
        String gameSecret = configMap.get(appId).getString(YinLiConfig.GAME_SECRET);


        String token = map.get("token")[0];
        //  根据token获取用户信息接口
        //  https://game.kuku168.cn/gameSDK/getUserInfoByToken?token=xxxxxx&gameKey=xxx&sign=xxxx

        String sign = MD5Util.md5(gameKey + token + gameSecret);

        StringBuilder param = new StringBuilder();
        super.addParam(param, "token", token);
        super.addParamAnd(param, "gameKey", gameKey);
        super.addParamAnd(param, "sign", sign);

        System.out.println("channelLogin param = " + token + gameKey + gameSecret);
        String url = userInfoUrl + param.toString();

        /*{
              "msg": "ok",
              "code": 1,
              "data": {
                "uid": 123,
                "nickName": "kuku",
                "avatarUrl": "xxxx.png"
              }
        }*/
        JSONObject rsp = httpService.httpGetJson(url);
        if (rsp != null) {
            if (rsp.containsKey("code") && rsp.containsKey("data")) {
                Integer code = rsp.getInteger("code");
                if (code == 1) {
                    //&& rsp.getString("msg").equals("ok")
                    JSONObject data = rsp.getJSONObject("data");
                    setUserData(userData, data.getString("uid"), data.getString("nickName"), String.valueOf(channelId), "");
                    return true;
                }
            }
        }

        setUserData(userData, "", "", "", "");
        return false;

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
        int channelId = orderData.getInteger("channelId");

        String gameKey = configMap.get(appId).getString(YinLiConfig.GAME_KEY);
        String gameSecret = configMap.get(appId).getString(YinLiConfig.GAME_SECRET);

        String cpOrderNo = orderData.getString("cpOrderNo");
        String channelUid = orderData.getString("uid");
        String serverId = orderData.getString("serverId");
        String userRoleId = orderData.getString("userRoleId");
        String goodsId = orderData.getString("goodsId");
        String goodsName = orderData.getString("subject");
        String money = orderData.getString("amount");
        String ext = orderData.getString("extrasParams");

        /*
            API002:游戏调用此函数完成支付页面调起
            参数说明
            productCost:1000,           必填，     道具支付金额，单位分
            productId:'PRO_1234',       必填，     游戏道具ID
            productName:'100元宝',       必填，     游戏道具名称
            gameUid:'123456',           可选，     游戏方用户ID
            gameOrderNo:'ORD_123456',   可选，     游戏方订单ID
            ext1:'123456',              可选，     扩展字段1，支付回调游戏方时原样返回
            ext2:'7890',                可选，     扩展字段2，支付回调游戏方时原样返回
        */

        StringBuilder param = new StringBuilder();
        super.addParam(param, "productCost", FeeUtils.yuanToFen(money));
        super.addParamAnd(param, "productId", goodsId);
        super.addParamAnd(param, "productName", goodsName);
        super.addParamAnd(param, "gameUid", channelUid);
        super.addParamAnd(param, "gameOrderNo", cpOrderNo);
        super.addParamAnd(param, "ext1", ext);
        super.addParamAnd(param, "ext2", "");


        JSONObject data = new JSONObject();
        data.put("productCost", FeeUtils.yuanToFen(money));
        data.put("productId", goodsId);
        data.put("productName", goodsName);
        data.put("gameUid", channelUid);
        data.put("gameOrderNo", cpOrderNo);
        data.put("ext1", ext);
        data.put("ext2", "");

        System.out.println("channelPayInfo data: " + data);
        channelOrderNo.put("data", data.toJSONString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String gameSecret = configMap.get(appId).getString(YinLiConfig.GAME_SECRET);
        boolean result = true;
        String[] mustKey = {"uid", "orderNo", "productId", "gameOrderNo", "gameKey", "payCost", "ext1", "ext2", "sign"};

        if (!super.channelMustParamS(mustKey, parameterMap)) {
            return false;
        }
        Arrays.sort(mustKey);
        StringBuilder param = new StringBuilder();
        for (String s : mustKey) {
            if (parameterMap.get(s) == null || parameterMap.get(s).isEmpty()) {
                continue;
            }
            if ("sign".equals(s)) {
                continue;
            }
            param.append(parameterMap.get(s));
        }
        param.append(gameSecret);

        System.out.println("param = " + param.toString());

        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString());

        System.out.println("channelPayCallback : " + param.toString());
        System.out.println("channelPayInfo sign: " + sign);
        System.out.println("channelPayInfo serverSign: " + serverSign);

        if (sign.equals(serverSign)) {
            setChannelOrder(channelOrderNo, "", parameterMap.get("gameOrderNo"), parameterMap.get("orderNo"), parameterMap.get("money"));
            return true;
        }

        return false;
    }
}

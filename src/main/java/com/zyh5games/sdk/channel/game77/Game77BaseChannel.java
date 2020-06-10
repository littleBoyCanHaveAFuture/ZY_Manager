package com.zyh5games.sdk.channel.game77;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模板
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("22")
public class Game77BaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(Game77BaseChannel.class);

    private static Map<String, String> channelMap;
    private static Map<String, String> extlMap;

    Game77BaseChannel() {
        channelId = ChannelId.H5_77;
        configMap = new ConcurrentHashMap<>();
        channelMap = new ConcurrentHashMap<>();
        extlMap = new ConcurrentHashMap<>();
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("https://pulsdk.7724.com/channelsdk/sbpulsdk.js" + "?" + System.currentTimeMillis());
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
        channelData.put("name", "Game77H5");
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
     *                 字段	            类型	        参与签名	        描述
     *                 qqesuid	        String	    是           SDK用户id
     *                 channelid	    String	    是           渠道id
     *                 channeluid	    String	    是           渠道用户id
     *                 qqesnickname	    String	    是           SDK用户昵称
     *                 qqesavatar	    String	    否           SDK用户头像
     *                 cpgameid	        String	    是           cp游戏id
     *                 ext	            String	    是           透传用户信息字段，支付的时候必须原样回传.防沉迷参数说明，is_realname，实名情况，0未实名，1已实名，is_adult，成年情况， 0未成年，1已成年，age，用户年龄，请cp接入
     *                 qqestimestamp	String	    是           请求时间戳，Unix时间戳，10位
     *                 sign     	    String	    否           签名字符串，算法： 除非字段标注为不需要参与签名，否则请求参数都参与签名，按参数值自然升序，然后拼接成字符串(比如a=1&b=2&c=3&签名秘钥)，然后md5生成签名字符串
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        String[] mustKey = {"qqesuid", "channelid", "channeluid", "qqesnickname", "qqesavatar", "cpgameid", "ext", "qqestimestamp", "sign"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(Game77Config.COMMON_KEY);

        String sdkUid = map.get("qqesuid")[0];
        String qqesnickname = map.get("qqesnickname")[0];
        String channelid = map.get("channelid")[0];
        String channeluid = map.get("channeluid")[0];
        String ext = map.get("ext")[0];
        // 加密串
        String[] signKey = {"qqesuid", "channelid", "channeluid", "qqesnickname", "cpgameid", "ext", "qqestimestamp"};

        StringBuilder param = new StringBuilder();
        Arrays.sort(signKey);
        boolean isFirst = true;
        for (String key : signKey) {
            String value = map.get(key)[0];
            if (isFirst) {
                isFirst = false;
                super.addParam(param, key, value);
            } else {
                super.addParamAnd(param, key, value);
            }
        }

        param.append("&").append(loginKey);

        log.info("param = " + param.toString());

        // 签名验证
        String sign = map.get("sign")[0];
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelLogin serverSign = " + serverSign);
        log.info("channelLogin sign       = " + sign);

        if (!sign.equals(serverSign)) {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }
        if (channelMap.containsKey(sdkUid)) {
            channelMap.replace(sdkUid, channelid + "|" + channeluid);
        } else {
            channelMap.put(sdkUid, channelid + "|" + channeluid);
        }
        if (extlMap.containsKey(sdkUid)) {
            extlMap.replace(sdkUid, ext);
        } else {
            extlMap.put(sdkUid, ext);
        }
        setUserData(userData, sdkUid, qqesnickname, String.valueOf(channelId), "");
        return true;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     *                       order	        String	是       cp生成的订单号(请保证每次下单的订单号都不同)
     *                       cpgameid	    String	是       cp在我们平台的游戏id
     *                       qqesuid	    String	是       SDK用户id
     *                       channelid	    String	是       渠道id
     *                       channeluid	    String	是       渠道用户id
     *                       cpguid	        String	是       cp在我们平台的唯一ID
     *                       goodsname	    String	是       商品名称
     *                       fee	        Float	是       商品价格(元)，(最多两位小数点)
     *                       ext	        String	是       用户登录时回调的ext原样传给我们
     *                       timestamp	    String	是       请求时间戳，Unix时间戳，10位
     *                       sign	        String	否
     *                       签名字符串，算法： 除非字段标注为不需要参与签名，否则请求参数都参与签名，按参数值自然升序，然后拼接成字符串(比如a=1&b=2&c=3&签名秘钥)，然后md5生成签名字符串
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(Game77Config.GAME_ID);
        String cpId = configMap.get(appId).getString(Game77Config.CP_ID);
        String payKey = configMap.get(appId).getString(Game77Config.COMMON_KEY);

        String channelUid = orderData.getString("uid");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String amount = orderData.getString("amount");
        String subject = orderData.getString("subject");
        String extrasParams = orderData.getString("extrasParams");
        long timestamp = System.currentTimeMillis() / 1000;

        String cdata = channelMap.getOrDefault(channelUid, "");
        if (cdata.isEmpty()) {
            log.info("cdata.isEmpty");
            return false;
        }
        String[] cUserData = cdata.split("\\|");
        if (cUserData.length != 2) {
            log.info("cUserData.length = " + cUserData.length);

            return false;
        }
        String cid = cUserData[0];
        String cuid = cUserData[1];
        String ext = extlMap.get(channelUid);
        // 加密串
        StringBuilder param = new StringBuilder();
        super.addParam(param, "channelid", cid);
        super.addParamAnd(param, "channeluid", cuid);
        super.addParamAnd(param, "cpgameid", channelGameId);
        super.addParamAnd(param, "cpguid", cpId);
        super.addParamAnd(param, "ext", ext);
        super.addParamAnd(param, "fee", amount);
        super.addParamAnd(param, "goodsname", subject);
        super.addParamAnd(param, "order", cpOrderNo);
        super.addParamAnd(param, "qqesuid", channelUid);
        super.addParamAnd(param, "timestamp", String.valueOf(timestamp));

        log.info("param = " + param.toString());
        param.append("&").append(payKey);

        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayInfo serverSign = " + serverSign);


        // 渠道订单数据
        JSONObject data = new JSONObject();
        data.put("order", cpOrderNo);
        data.put("cpgameid", channelGameId);
        data.put("qqesuid", channelUid);
        data.put("channelid", cid);
        data.put("channeluid", cuid);
        data.put("cpguid", cpId);
        data.put("goodsname", subject);
        data.put("fee", amount);
        data.put("ext", ext);
        data.put("timestamp", timestamp);
        data.put("sign", serverSign);

        log.info("channelPayInfo data: " + data);
        channelOrderNo.put("data", data.toJSONString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       qqes_order	String	是   SDK订单号
     *                       cp_order	String	是   cp订单号
     *                       fee	    float	是   订单金额（元）
     *                       cpgameid	Integer	是   cp在我们平台的游戏id
     *                       timestamp	String	是   请求时间戳，Unix时间戳，10位
     *                       sign	    String	否   签名字符串，算法： 除非字段标注为不需要参与签名，否则请求参数都参与签名，按参数值自然升序，然后拼接成字符串(比如a=1&b=2&c=3&签名秘钥)，然后md5生成签名字符串
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String channelGameId = configMap.get(appId).getString(Game77Config.GAME_ID);
        String cpId = configMap.get(appId).getString(Game77Config.CP_ID);
        String payKey = configMap.get(appId).getString(Game77Config.COMMON_KEY);

        // 加密串
        String[] signKey = {"qqes_order", "cp_order", "fee", "cpgameid", "timestamp"};
        StringBuilder param = super.signMap(signKey, parameterMap);
        param.append("&").append(payKey);

        log.info("param = " + param.toString());

        // 签名验证
        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);

        if (!sign.equals(serverSign)) {
            return false;
        }
        // 渠道订单赋值
        setChannelOrder(channelOrderNo, "", parameterMap.get("cp_order"), parameterMap.get("qqes_order"), parameterMap.get("fee"));
        return true;
    }

    @Override
    public JSONObject ajaxGetSignature(Integer appId, JSONObject requestInfo, JSONObject result) {
        int type = requestInfo.getInteger("type");
        log.info("type = " + type);

        String roleKey = configMap.get(appId).getString(Game77Config.COMMON_KEY);
        String cpId = configMap.get(appId).getString(Game77Config.CP_ID);
        long time = System.currentTimeMillis() / 1000;

        log.info("roleKey = " + roleKey);

        requestInfo.put("cpguid", cpId);
        requestInfo.put("timestamp", time);

        JSONObject userData = new JSONObject();

        // 加密字符串
        StringBuilder param = new StringBuilder();

        if (type == 3) {
            String[] signKey3 = {"cpgameid", "qqesuid", "channelid", "cpguid", "rolename", "serverid",
                    "level", "servername", "vip", "ext", "timestamp"};

            for (String index : signKey3) {
                if (!requestInfo.containsKey(index)) {
                    result.put("message", "缺失参数：" + index);
                    result.put("status", false);
                    return null;
                }
            }

            super.signJson(param, signKey3, requestInfo);

            // 签名验证
            String sign = MD5Util.md5(param.toString() + "&" + roleKey);

            log.info("serverSign = " + sign);

            userData.put("cpgameid", requestInfo.getString("cpgameid"));
            userData.put("qqesuid", requestInfo.getString("qqesuid"));
            userData.put("channelid", requestInfo.getString("channelid"));
            userData.put("cpguid", requestInfo.getString("cpguid"));
            userData.put("rolename", requestInfo.getString("rolename"));
            userData.put("serverid", requestInfo.getString("serverid"));
            userData.put("level", requestInfo.getString("level"));
            userData.put("servername", requestInfo.getString("servername"));
            userData.put("vip", requestInfo.getString("vip"));
            userData.put("ext", requestInfo.getString("ext"));
            userData.put("timestamp", requestInfo.getString("timestamp"));
            userData.put("sign", sign);

            return userData;
        } else if (type == 2) {
            String[] signKey2 = {"cpgameid", "qqesuid", "channelid", "cpguid", "roleName", "serverId",
                    "level", "ext", "timestamp"};
            super.signJson(param, signKey2, requestInfo);

            // 签名验证
            String sign = MD5Util.md5(param.toString() + "&" + roleKey);

            log.info("serverSign = " + sign);

            userData.put("cpgameid", requestInfo.getString("cpgameid"));
            userData.put("qqesuid", requestInfo.getString("qqesuid"));
            userData.put("channelid", requestInfo.getString("channelid"));
            userData.put("cpguid", requestInfo.getString("cpguid"));
            userData.put("roleName", requestInfo.getString("roleName"));
            userData.put("serverId", requestInfo.getString("serverId"));
            userData.put("level", requestInfo.getString("level"));
            userData.put("ext", requestInfo.getString("ext"));
            userData.put("timestamp", requestInfo.getString("timestamp"));
            userData.put("sign", sign);

            return userData;
        }
        return null;
    }
}

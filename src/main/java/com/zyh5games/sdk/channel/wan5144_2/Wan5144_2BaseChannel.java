package com.zyh5games.sdk.channel.wan5144_2;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.wan5144.Wan5144Config;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 5144玩
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("38")
public class Wan5144_2BaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(Wan5144_2BaseChannel.class);

    Wan5144_2BaseChannel() {
        channelId = ChannelId.H5_5144WAN2;
        channelName = "5144玩_2";
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("https://play.5144wan.com/xykj/resource/sdk.min.js" + "?" + System.currentTimeMillis());
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
        channelData.put("name", "Wan5144H5_2");
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
        int appId = Integer.parseInt(map.get("GameId")[0]);
        if (!map.containsKey("pid")) {
            return "";
        }
        String pid = map.get("pid")[0];
        long time = System.currentTimeMillis();
        String loginKey = configMap.get(appId).getString(Wan5144Config.LOGIN_KEY);
        String param = pid + "#" + loginKey + "#" + time;
//                MD5（pid#gkey#time）
        String sign = MD5Util.md5(param);
        return time + "|" + sign;
    }

    /**
     * 3.渠道登录<p>
     * 3.1 向渠道校验 获取用户数据 <p>
     * 3.2 设置token<p>
     *
     * @param map      渠道传入参数
     *                 名称	    参数名	    类型	    提供方	必填	    说明
     *                 商户ID	pid	        string	我	    是	    贵方在我方唯一标识
     *                 游戏ID	gid	        string	贵	    是	    贵方游戏唯一标识
     *                 游戏服ID	sid	        string	贵	    是	    贵方游戏服唯一标识
     *                 玩家id	uid	        string	我	    是	    我方玩家唯一标识
     *                 时间戳	time	    string	-	    是	    Unix time
     *                 防沉迷	fcm	        string	我	    是	    1代表通过，0代表未通过
     *                 登录端	client	    string	我	    是	    1代表微端，0代表网页
     *                 请求类型	type	    string	我	    是	    固定值：game
     *                 签名      sign	    string			是	    MD5（pid#gid#sid#uid#gkey#time），Md5只加密参数值，32位小写，用英文#连接各参数值
     *                 登录密钥	gkey				我方提供不以明文显示在前端，用于加密
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String[] mustKey = {"pid", "gid", "sid", "uid", "time", "fcm", "client", "type", "sign"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }

        String loginKey = configMap.get(appId).getString(Wan5144Config.LOGIN_KEY);
        String payKey = configMap.get(appId).getString(Wan5144Config.PAY_KEY);

        // 解释|必选-参与加密

        String pid = map.get("pid")[0];
        String gid = map.get("gid")[0];
        String sid = map.get("sid")[0];
        String uid = map.get("uid")[0];
        String time = map.get("time")[0];
        String fcm = map.get("fcm")[0];
        String client = map.get("client")[0];
        String type = map.get("type")[0];
        String sign = map.get("sign")[0];

        // MD5（pid#gid#sid#uid#gkey#time）
        StringBuilder param = new StringBuilder();
        param.append(pid);
        param.append("#").append(gid);
        param.append("#").append(sid);
        param.append("#").append(uid);
        param.append("#").append(loginKey);
        param.append("#").append(time);
        System.out.println("param = " + param.toString());

        String serverSign = MD5Util.md5(param.toString());
        log.info("channelLogin = " + serverSign);
        log.info("sign = " + sign);

        System.out.println("channelLogin = " + serverSign);
        System.out.println("sign = " + sign);

        if (!sign.equals(serverSign)) {
            setUserData(userData, "", sid, String.valueOf(channelId), "");
            return false;
        } else {
            setUserData(userData, uid, sid, String.valueOf(channelId), "");
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

        channelOrderNo.put("data", "");
        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       <p>5144玩_2
     *                       名称	        参数名	    类型	    提供方	必填      说明
     *                       商户ID	        pid	        string	我	    是	    贵方在我方唯一标识
     *                       游戏ID	        gid	        string	贵	    是	    贵方游戏唯一标识
     *                       游戏服ID	    sid	        string	贵	    是	    贵方游戏服唯一标识
     *                       玩家id	        uid	        string	我	    是	    我方玩家唯一标识
     *                       时间戳	        time	    string	-	    是	    Unix time
     *                       订单号	        orderid	    string	我	    是	    我方订单唯一标识
     *                       充值金额	    money	    string	我	    是	    人民币，单位元
     *                       请求类型	    type	    string	我	    是	    固定值：pay
     *                       自定义参数	    other	    string	贵	    是	    贵方自定义参数值，如贵方订单号，我方会在充值回调里，传回给贵方，可为空
     *                       Sign	        sign        string  我      是	    MD5（pid#gid#sid#uid#time#orderid#money#pkey）,Md5只加密参数值，32位小写，用英文#连接各参数值
     *                       充值密钥	    pkey		        我		        我方提供不以明文显示在前端，只用于加密
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String channelGameId = configMap.get(appId).getString(Wan5144Config.GAME_ID);
        String payKey = configMap.get(appId).getString(Wan5144Config.PAY_KEY);

        String sign = parameterMap.get("sign");

        // MD5（pid#gid#sid#uid#time#orderid#money#pkey）
        StringBuilder param = new StringBuilder();
        param.append(parameterMap.get("pid"));
        param.append("#").append(parameterMap.get("gid"));
        param.append("#").append(parameterMap.get("sid"));
        param.append("#").append(parameterMap.get("uid"));
        param.append("#").append(parameterMap.get("time"));
        param.append("#").append(parameterMap.get("orderid"));
        param.append("#").append(parameterMap.get("money"));
        param.append("#").append(payKey);


        log.info("channelPayCallback : " + param.toString());

        String serverSign = MD5Util.md5(param.toString());
        log.info("channelPayInfo sign: " + serverSign);

        String channelOrderId = parameterMap.get("orderid");
        String money = parameterMap.get("money");
        String cpOrderId = parameterMap.get("other");

        if (sign.equals(serverSign)) {
            setChannelOrder(channelOrderNo, "", cpOrderId, channelOrderId, money);
            return true;
        }

        return false;
    }

    @Override
    public JSONObject ajaxGetSignature(Integer appId, JSONObject requestInfo, JSONObject result) {
        String pid = requestInfo.getString("pid");

        long time = System.currentTimeMillis();
        String loginKey = configMap.get(appId).getString(Wan5144Config.LOGIN_KEY);
        String param = pid + "#" + loginKey + "#" + time;
        // MD5（pid#gkey#time）
        String sign = MD5Util.md5(param);

        JSONObject userData = new JSONObject();
        userData.put("time", time);
        userData.put("sign", sign);
        return userData;
    }
}

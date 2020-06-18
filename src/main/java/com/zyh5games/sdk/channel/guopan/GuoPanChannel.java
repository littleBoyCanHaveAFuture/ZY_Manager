package com.zyh5games.sdk.channel.guopan;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.util.MD5Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 果盘
 *
 * @author song minghua
 * @date 2020年6月15日09:39:09
 */
@Component("27")
public class GuoPanChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(GuoPanChannel.class);

    @Autowired
    HttpService httpService;

    GuoPanChannel() {
        channelId = ChannelId.H5_GuoPan;
        channelName = "果盘";
        configMap = new ConcurrentHashMap<>();
    }

    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @return boolean
     */
    @Override
    public JSONObject channelLib(Integer appId) {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "GuoPanH5");
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
     *                 参数名	        是否必须	        含义	                备注
     *                 source	        Y	            来源	                固定值 guopan
     *                 guopanAppId	    Y	 	                            由果盘分配 比如 105756
     *                 game_uin	        Y	            果盘游戏账号	        一般 16至18位随机字符 如 19LSWY8YKE3JH9G3。
     *                 time	            Y	            时间戳	            如 1472005799
     *                 sign	            Y	            签名字符串	        生成规则：md5(guopanAppId . game_uin . time . SERVER_KEY ); 也就是把接口里的几个变量拼接起来后的md5值， 其中SERVER_KEY由我方提供
     *                 gid	            N	            游戏方定义的游戏id
     *                 sid	            N	            服id
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        String[] mustKeyLogin = {"source", "guopanAppId", "game_uin", "time", "sign"};
        if (!super.channelMustParam(mustKeyLogin, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(GuoPanConfig.COMMON_KEY);
        String verifyUrl = GuoPanConfig.LOGIN_URL;

        /**
         * game_uin     true  string  果盘分配给该游戏对应的唯一账号，16至 18 位长字符，可通过客户端 SDK 方法获取得到。安卓：IGPApi+(String)getLoginUin()iOS： [GPGameSDKdefaultGPGame].loginUin
         * appid        true  string
         * token        true  string  可通过客户端 SDK 方法获取得到。
         * t            true  string  时间戳(请填写服务器发起请求的北京时间)
         * sign         true  string  加密串sign=md5(game_uin+appid+t+SERVER_KEY) 是四个变量值拼接后经 md5 后的值，其中 SERVER_KEY 在果盘开放平台上获得。
         * */

        String game_uin = map.get("game_uin")[0];
        String appid = map.get("appid")[0];
        String t = map.get("time")[0];

        // 加密串-带键值的字符串

        // 签名验证
        String sign = map.get("sign")[0];
        String serverSign = MD5Util.md5(game_uin + appid + t + loginKey);
        log.info("channelLogin serverSign = " + serverSign);
        log.info("channelLogin       sign = " + sign);

        if (!sign.equals(serverSign)) {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }

        setUserData(userData, game_uin, "", String.valueOf(channelId), "");
        return true;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     *                       guopanAppId	Y	 	由果盘分配
     *                       serialNumber	Y	    游戏方订单唯一id	 
     *                       goodsId	    N	    购买的游戏内物品id	 
     *                       goodsName	    N	    购买的游戏内物品名称	 
     *                       game_uin	    Y	    游戏帐号id	 果盘之前给予的帐号
     *                       ext	        N	    透传字段	 
     *                       gameUrl	    Y	    游戏的url	支付完成后返回到的游戏网址
     *                       time	        N	    时间戳	 
     *                       money	        Y	    金额，单位 元	 
     *                       sign	        Y	    签名	加密串 sign=md5(orderId + appid + money + time + game_uin + SERVER_KEY) 是六个变量值拼接后经md5后的值，其中SERVER_KEY在果盘开放平台上获得。
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(GuoPanConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(GuoPanConfig.COMMON_KEY);

        long time = System.currentTimeMillis() / 1000;

        String cpOrderNo = orderData.getString("cpOrderNo");
        String channelUid = orderData.getString("uid");
        String amount = orderData.getString("amount");
        String subject = orderData.getString("subject");
        String goodsId = orderData.getString("goodsId");

        // 加密串 sign=md5(orderId + appid + money + time + game_uin + SERVER_KEY)
        StringBuilder param = new StringBuilder();
        param.append(cpOrderNo);
        param.append(channelGameId);
        param.append(amount);
        param.append(time);
        param.append(channelUid);

        log.info("param = " + param.toString());

        // 渠道订单数据
        JSONObject data = new JSONObject();
        data.put("guopanAppId", channelGameId);
        data.put("serialNumber", cpOrderNo);
        data.put("goodsId", goodsId);
        data.put("goodsName", subject);
        data.put("game_uin", channelUid);
        data.put("ext", cpOrderNo);
        data.put("gameUrl", "");
        data.put("time", time);
        data.put("money", amount);
        data.put("sign", sign(param, payKey));

        log.info("channelPayInfo data: " + data);

        channelOrderNo.put("data", super.jsonToUrlString(data));

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       trade_no     true  string  果盘唯一订单号
     *                       serialNumber true  string  游戏方订单序列号
     *                       money        true  string  消费金额。单位是元，精确到分，如10.00。请务必校验金额与玩家下单的商品价值是否一致
     *                       status       true  string  状态；0=失败；1=成功；2=失败，原因是余额不足。
     *                       t            true  string  时间戳(果盘服务器发起通知的北京时间)
     *                       sign         true  string  加密串 sign=md5(serialNumber+money+status+t+SERVER_KEY) 是五个变量值拼接后经 md5 后的值，其中SERVER_KEY 在果盘开放平台上获得。
     *                       appid        false  string
     *                       item_id      false  string
     *                       item_price   false  string
     *                       item_count   false  string
     *                       reserved     false  string  扩展参数，SDK 发起支付时有传递，则这里会回传。
     *                       game_uin     true   string  玩家游戏 uid；请务必校验该 game_uin 和下单时对应的角色 game_uin 是否一致，防止“任意充”
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String channelGameId = configMap.get(appId).getString(GuoPanConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(GuoPanConfig.COMMON_KEY);

        //参数
        String trade_no = parameterMap.get("trade_no");
        String serialNumber = parameterMap.get("serialNumber");
        String money = parameterMap.get("money");
        String status = parameterMap.get("status");
        String t = parameterMap.get("t");
        String game_uin = parameterMap.get("game_uin");
        // 加密串
        StringBuilder param = new StringBuilder();
        param.append(serialNumber);
        param.append(money);
        param.append(status);
        param.append(t);

        log.info("param = " + param.toString());

        // 签名验证
        String sign = parameterMap.get("sign");
        String serverSign = sign(param, payKey);

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);


        if (!sign.equals(serverSign)) {
            return false;
        }
        // 渠道订单赋值
        setChannelOrder(channelOrderNo, game_uin, serialNumber, trade_no, money);
        return true;
    }

    @Override
    public String sign(StringBuilder param, String key) {
        String serverSign = super.sign(param, key);
        log.info("channelPayInfo serverSign = " + serverSign);
        return serverSign;
    }
}

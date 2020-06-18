package com.zyh5games.sdk.channel.zhaoshouyou;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.util.MD5Util;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 找游戏
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("25")
public class ZhaoShouYouBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(ZhaoShouYouBaseChannel.class);

    ZhaoShouYouBaseChannel() {
        channelId = ChannelId.H5_ZHAOSHOUYOU;
        channelName = "找手游";
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
        channelData.put("name", "ZhaoShouYouH5");
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
     *                 user_id	        int	    用户id
     *                 user_name	    string	用户名
     *                 game_id	        int	    游戏id
     *                 server_id	    int	    区/服ID，游戏不分区/服则为1
     *                 time	            int	    登录发起时间
     *                 sign	            string	签名
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        String[] mustKey = {"user_id", "username", "game_id", "server_id", "time", "sign"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(ZhaoShouYouConfig.COMMON_KEY);
        String gameId = configMap.get(appId).getString(ZhaoShouYouConfig.GAME_ID);

        String channelUid = map.get("user_id")[0];
        String userName = map.get("username")[0];
        String game_id = map.get("game_id")[0];

        // 加密串
        StringBuilder param = new StringBuilder();
        Arrays.sort(mustKey);
        for (String key : mustKey) {
            if (!key.equals("sign")) {
                param.append(map.get(key)[0]);
            }
        }
        param.append(loginKey);

        log.info("param = " + param.toString());

        // 签名验证
        String sign = map.get("sign")[0];
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelLogin serverSign = " + serverSign);
        log.info("channelLogin sign       = " + sign);

        if (!sign.equals(serverSign) || !gameId.equals(game_id)) {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }

        setUserData(userData, channelUid, userName, String.valueOf(channelId), "");
        return true;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     *                       user_id	    int	        金额
     *                       username	    string	    用户名,urlencoded
     *                       game_id	    int	        游戏id
     *                       server_id	    int	        区/服ID，游戏不分区/服则为0
     *                       money	        smallint	充值金额，不超过1000
     *                       subject	    string	    商品标题
     *                       body	        string	    商品描述
     *                       cp_order	    string	    游戏充值ID
     *                       cp_return	    string	    返回地址，支付成功后跳转到这个地址，为空则跳转到登录接口
     *                       time	        int	        充值发起时间
     *                       extra	        string	    额外信息
     *                       sign	        string	    签名
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(ZhaoShouYouConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ZhaoShouYouConfig.COMMON_KEY);

        long time = System.currentTimeMillis() / 1000;

        String channelUid = orderData.getString("uid");
        String username = orderData.getString("username");
        String serverId = orderData.getString("serverId");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String amount = orderData.getString("amount");
        String subject = orderData.getString("subject");
        String desc = orderData.getString("desc");
        String extrasParams = orderData.getString("extrasParams");


        // 渠道订单数据
        JSONObject data = new JSONObject();
        data.put("user_id", channelUid);
        data.put("username", username);
        data.put("game_id", channelGameId);
        data.put("server_id", serverId);
        data.put("money", amount);
        data.put("subject", subject);
        data.put("body", desc);
        data.put("cp_order", cpOrderNo);
        data.put("cp_return", "");
        data.put("time", time);
        data.put("extra", "");

        String[] signKey = {"user_id", "username", "game_id", "server_id", "money", "subject", "body", "cp_order",
                "cp_return", "time", "extra"};
        Arrays.sort(signKey);
        // 加密串

        StringBuilder param = new StringBuilder();
        for (String key : signKey) {
            param.append(data.getString(key));
        }
        param.append(payKey);
        log.info("param = " + param.toString());

        // 签名验证
        String serverSign = MD5Util.md5(param.toString());
        log.info("channelPayInfo serverSign = " + serverSign);

        data.put("sign", serverSign);
        try {
            data.replace("username", URLEncoder.encode(username, String.valueOf(StandardCharsets.UTF_8)));
            data.replace("subject", URLEncoder.encode(subject, String.valueOf(StandardCharsets.UTF_8)));
            data.replace("body", URLEncoder.encode(desc, String.valueOf(StandardCharsets.UTF_8)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info("channelPayInfo data: " + data);

        boolean first = true;
        StringBuilder orderParam = new StringBuilder();
        for (String key : signKey) {
            if (first) {
                first = false;
                super.addParam(orderParam, key, data.getString(key));
            } else {
                super.addParamAnd(orderParam, key, data.getString(key));
            }
        }
        super.addParamAnd(orderParam, "sign", data.getString("sign"));

        log.info("channelPayInfo data: " + orderParam);

        channelOrderNo.put("data", orderParam.toString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       user_id   Int	        是	付费用户id
     *                       username  String	    是	付费用户名
     *                       to_uid    Int	        是	充入用户id
     *                       to_user   String	    是	充入用户名
     *                       pay_id    string	    是	支付id
     *                       money     Smallint	    是	充值金额
     *                       game_id   Int	        是	游戏id
     *                       server_id Int	        是	区/服 id
     *                       cp_order  String	    是	游戏充值(订单号)
     *                       time      Int	        是	充值发起时间
     *                       extra     String	    否	额外信息good_id
     *                       sign      string	    是	签名f
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String channelGameId = configMap.get(appId).getString(ZhaoShouYouConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ZhaoShouYouConfig.COMMON_KEY);

        String[] signKey = {"user_id", "username", "to_uid", "to_user", "pay_id", "money", "game_id", "server_id", "cp_order",
                "time", "extra"};
        // 加密串
        StringBuilder param = super.signMapNoKey(signKey, parameterMap);

        log.info("param = " + param.toString());

        // 签名验证
        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString() + payKey);

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);

        if (!sign.equals(serverSign)) {
            return false;
        }
        String cpOrderId = parameterMap.get("cp_order");
        String money = parameterMap.get("money");
        // 渠道订单赋值
        setChannelOrder(channelOrderNo, "", cpOrderId, "", money);
        return true;
    }
}

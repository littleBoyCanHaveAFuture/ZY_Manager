package com.zyh5games.sdk.channel.qiyou;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 奇游
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("19")
public class QiYouBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(QiYouBaseChannel.class);

    @Autowired
    HttpService httpService;

    QiYouBaseChannel() {
        channelId = ChannelId.H5_QIYOU;
        channelName = "奇游";
        configMap = new ConcurrentHashMap<>();
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
//        libUrl.add("http://cdn.akwan.cn/js/sdk.js" + "?" + System.currentTimeMillis());
        libUrl.add("https://zyh5games.com/sdk/channel/sdk(qiyou).js" + "?" + System.currentTimeMillis());
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
        channelData.put("name", "QiYouH5");
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
        String[] mustKey = {"zyqyUserId", "zyqyAccount", "zyqyToken"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(QiYouConfig.COMMON_KEY);
        String loginUrl = QiYouConfig.LOGIN_URL;

        String channelUid = map.get("zyqyUserId")[0];
        String account = map.get("zyqyAccount")[0];
        String token = map.get("zyqyToken")[0];

        String url = loginUrl + "?" + "token=" + token;

        /*
         * code	:状态	1 - 成功|0 - 失败
         * data :帐号信息
         * {
         * 	    uid : 用户ID，与登录时返回user_id一致,请注意核对返回的uid和登录时返回的user_id是否一致
         * }
         * */

        JSONObject rsp = httpService.httpGetJson(url);
        if (!rsp.containsKey("code") || rsp.getInteger("code") == -1) {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }
        JSONObject data = rsp.getJSONObject("data");
        String rspUid = data.getString("uid");
        if (!channelUid.equals(rspUid)) {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }
        setUserData(userData, channelUid, account, String.valueOf(channelId), "");
        return true;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     *                       cp_order	        游戏订单号	        是
     *                       product_id	        商品ID	            是（int）
     *                       product_name	    商品名称	            是
     *                       product_desc	    商品描述	            是
     *                       server_id	        服务器ID	            是（int）
     *                       server_name	    服务器名称	        是
     *                       role_id	        角色ID	            是（int）
     *                       role_name	        角色名称	            是
     *                       role_level	        角色等级	            是（int）
     *                       price	            金额，单位（元）	    是
     *                       extension	        扩展数据，如果设置，在发货接口会原样返回该数据	    可选
     * @param channelOrderNo 渠道订单返回参数
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(QiYouConfig.GAME_ID);

        String userRoleId = orderData.getString("userRoleId");
        String userRoleName = orderData.getString("userRoleName");

        String serverId = orderData.getString("serverId");
        String userServer = orderData.getString("userServer");
        String userLevel = orderData.getString("userLevel");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String amount = orderData.getString("amount");

        String subject = orderData.getString("subject");
        String desc = orderData.getString("desc");

        String extrasParams = orderData.getString("extrasParams");
        String goodsId = orderData.getString("goodsId");


        // 渠道订单数据
        JSONObject data = new JSONObject();
        data.put("cp_order", cpOrderNo);
        data.put("product_id", goodsId);
        data.put("product_name", subject);
        data.put("product_desc", desc);
        data.put("server_id", serverId);
        data.put("server_name", userServer);
        data.put("role_id", userRoleId);
        data.put("role_name", userRoleName);
        data.put("role_level", userLevel);
        data.put("price", amount);
        data.put("extension", extrasParams);

        log.info("channelPayInfo data: " + data);
        channelOrderNo.put("data", data.toJSONString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       order_no	        平台支付订单	                            是	201912181137114223
     *                       cp_order	        游戏订单号	                            是
     *                       user_id	        平台用户ID，和登录注册时返回的user_id一致	是
     *                       product_id	        游戏商品ID	                            是
     *                       price	            充值金额（元）	                        是
     *                       role_id	        角色ID	                                是
     *                       server_id	        服务器ID	                                是
     *                       ext	            扩展参数，透传下单时提供的extension	        否	默认空字符串
     *                       time	            Unix时间戳	                            是
     *                       sign	            加密串	                                是	详见下面sign生成算法
     *                       <p>
     *                       sign = md5(order_no + cp_order + user_id + product_id + price + role_id + server_id + ext + time + secret_key)
     *                       其中+为字符串链接符号，secret_key由平台方提供，md5取32位小写字符
     *                       php 例子 $sign = md5($order_no .‘+’. $ cp_order …);
     *                       <p>
     *                       （CP方需要自行校验充值金额和游戏商品金额是否一致）
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String channelGameId = configMap.get(appId).getString(QiYouConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(QiYouConfig.COMMON_KEY);

        String order_no = parameterMap.get("order_no");
        String cp_order = parameterMap.get("cp_order");
        String user_id = parameterMap.get("user_id");
        String product_id = parameterMap.get("product_id");
        String price = parameterMap.get("price");
        String role_id = parameterMap.get("role_id");
        String server_id = parameterMap.get("server_id");
        String ext = parameterMap.get("ext");
        String time = parameterMap.get("time");

        // 加密串
        StringBuilder param = new StringBuilder();
        param.append(order_no).append("+");
        param.append(cp_order).append("+");
        param.append(user_id).append("+");
        param.append(product_id).append("+");
        param.append(price).append("+");
        param.append(role_id).append("+");
        param.append(server_id).append("+");
        param.append(ext).append("+");
        param.append(time).append("+");

        param.append(payKey);
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
        setChannelOrder(channelOrderNo, "", cp_order, order_no, price);
        return true;
    }
}

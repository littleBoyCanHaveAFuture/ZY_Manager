package com.zyh5games.sdk.channel.game1758;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.util.FeeUtils;
import com.zyh5games.util.MD5Util;
import com.zyh5games.util.RandomUtil;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1758
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("16")
public class Game1758BaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(Game1758BaseChannel.class);

    @Autowired
    HttpService httpService;

    static Map<String, String> hlmyGwMap;

    Game1758BaseChannel() {
        channelId = ChannelId.H5_1758;
        configMap = new ConcurrentHashMap<>();
        hlmyGwMap = new ConcurrentHashMap<>();
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("https://res.1758.com/sdk/js/1758sdk.js" + "?" + System.currentTimeMillis());
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
        channelData.put("name", "Game1758H5");

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
     *                 参数字段	    类型	    说明	            是否参与签名
     *                 appKey	    string	游戏的appKey	        是
     *                 hlmy_gw	    string	1758平台自定义参数	是
     *                 userToken	string	用户凭证	            是
     *                 nonce	    string	随机串，不长于32位	是
     *                 timestamp	long	当前时间戳（秒）	    是
     *                 sign	        string	参数签名
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        String[] mustKey = {"appKey", "hlmy_gw", "timestamp", "nonce", "userToken"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        String appKey = map.get("appKey")[0];
        String hlmy_gw = map.get("hlmy_gw")[0];
        String timestamp = map.get("timestamp")[0];
        String nonce = map.get("nonce")[0];
        String userToken = map.get("userToken")[0];

        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(Game1758Config.GAME_SECRET);

        // 加密串
        StringBuilder param = new StringBuilder();
        super.addParam(param, "appKey", appKey);
        super.addParamAnd(param, "hlmy_gw", hlmy_gw);
        super.addParamAnd(param, "nonce", nonce);
        super.addParamAnd(param, "timestamp", timestamp);
        super.addParamAnd(param, "userToken", userToken);

        log.info("param = " + param.toString());

        // 签名验证
        String serverSign = MD5Util.md5(param.toString() + loginKey);

        log.info("channelLogin serverSign = " + serverSign);


        /*
            x-www-form-urlencoded方式
            参数字段	                    类型	        说明
            result	                    int	        响应结果，1成功、0失败
            errorcode	                int	        错误码
            data	                    object	    业务数据
            data.userInfo	            object	    用户资料对象
            data.userInfo.gid	        string	    用户唯一识别id
            data.userInfo.avatar	    string	    用户头像地址
            data.userInfo.nickName	    string	    用户昵称
            data.userInfo.sex	        int	        性别，1男、2女、0未知
        */

        JSONObject reqData = new JSONObject();
        reqData.put("appKey", appKey);
        reqData.put("hlmy_gw", hlmy_gw);
        reqData.put("userToken", userToken);
        reqData.put("nonce", nonce);
        reqData.put("timestamp", timestamp);
        reqData.put("sign", serverSign);
        String loginUrl = Game1758Config.LOGIN_URL;
        JSONObject rsp = httpService.httpPostXwwFormUrlEncoded(loginUrl, reqData);
        if (rsp.containsKey("result")) {
            if (rsp.getInteger("result") == 1 && rsp.containsKey("data")) {
                JSONObject data = rsp.getJSONObject("data");
                JSONObject userInfo = data.getJSONObject("userInfo");
                String channelUid = userInfo.getString("gid");
                String userName = userInfo.getString("nickName");

                if (hlmyGwMap.containsKey(channelUid)) {
                    hlmyGwMap.replace(channelUid, hlmy_gw);
                } else {
                    hlmyGwMap.put(channelUid, hlmy_gw);
                }
                setUserData(userData, channelUid, userName, String.valueOf(channelId), "");
                return true;
            }
        }

        log.info("登录失败");
        setUserData(userData, "", "", String.valueOf(channelId), "");
        return false;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     *                       HTTP POST， x-www-form-urlencoded方式
     *                       参数字段	        类型	        说明	                  是否可空	示例	                            是否参与签名
     *                       appKey	            string	    游戏的appKey             否	    cp从开放平台获取的值	                是
     *                       gid	            string	    1758用户的gid            否	    8284893998245a9bd7c9c73698facf6d	是
     *                       hlmy_gw	        string	    1758的自定义参数          否	    登录透传值，例如0_0__	                是
     *                       totalFee	        int	        支付金额，单位分	        否	    100	                                是
     *                       productDesc	    string	    商品描述	                否	    月卡	                                是
     *                       txId	            string	    cp订单号	                否	    10001	                            是
     *                       state	            string	    自定义参数，              是	    {"roleId":1}	                    是
     *                       支付通知接口会透传该参数
     *                       nonce	            string	    随机串，不长于32位	    否	    xyz001	                            是
     *                       timestamp	        long	    当前时间戳（毫秒）	        否	    1553756241427	                    是
     *                       serverId	        int	        区服ID	                否	    1	                                否
     *                       serverName	        string	    区服名称	                否	    1区	                                否
     *                       roleId	            int	        游戏内角色ID	            否	    1	                                否
     *                       roleName	        string	    游戏角色名称	            否	    龙傲天	                            否
     *                       roleCoins	        int	        角色游戏内货币余额	        否	    0	                                否
     *                       roleLevel	        int	        角色等级	                否	    1	                                否
     *                       vipLevel	        int	        角色VIP等级	            否	    0	                                否
     *                       gameRolePower	    int	        角色战力值	            否	    用户游戏中的动态值，例如经验值	        否
     *                       ext	            string，json格式	额外信息，预留参数，暂未启用	是		                            否
     * @param channelOrderNo 渠道订单返回参数
     *                       {
     *                       "result": 1,
     *                       "errorcode": 0,
     *                       "data": {
     *                       "paySafeCode":"yyyy" //支付安全码
     *                       }
     *                       }
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String payKey = configMap.get(appId).getString(Game1758Config.GAME_KEY);
        String secretKey = configMap.get(appId).getString(Game1758Config.GAME_SECRET);
        String payUrl = Game1758Config.PAY_URL;

        String channelUid = orderData.getString("uid");
        String hlmy_gw = hlmyGwMap.get(channelUid);
        String amount = orderData.getString("amount");
        String desc = orderData.getString("desc");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String extrasParams = orderData.getString("extrasParams");
        String serverId = orderData.getString("serverId");
        String userServer = orderData.getString("userServer");
        String userRoleId = orderData.getString("userRoleId");
        String userRoleName = orderData.getString("userRoleName");
        String userLevel = orderData.getString("userLevel");

        long timestamep = System.currentTimeMillis();
        String random = RandomUtil.rndStr(8, true);
        JSONObject reqData = new JSONObject();
        reqData.put("appKey", payKey);
        reqData.put("gid", channelUid);
        reqData.put("hlmy_gw", hlmy_gw);
        reqData.put("totalFee", FeeUtils.yuanToFen(amount));
        reqData.put("productDesc", desc);
        reqData.put("txId", cpOrderNo);
        reqData.put("state", extrasParams);
        reqData.put("nonce", random);
        reqData.put("timestamp", String.valueOf(timestamep));
        reqData.put("serverId", serverId);
        reqData.put("serverName", userServer);
        reqData.put("roleId", userRoleId);
        reqData.put("roleName", userRoleName);
        reqData.put("roleCoins", "0");
        reqData.put("roleLevel", userLevel);
        reqData.put("vipLevel", "0");
        reqData.put("gameRolePower", "0");
        reqData.put("ext", "");


        StringBuilder param = new StringBuilder();
        String[] signKey = {"appKey", "gid", "hlmy_gw", "totalFee", "productDesc", "txId", "state", "nonce", "timestamp"};
        Arrays.sort(signKey);
        boolean first = true;
        for (String key : signKey) {
            if (first) {
                super.addParam(param, key, reqData.getString(key));
                first = false;
            } else {
                super.addParamAnd(param, key, reqData.getString(key));
            }
        }

        log.info("param = " + param);

        String sign = MD5Util.md5(param.toString() + secretKey);

        reqData.put("sign", sign);

        log.info("data = " + reqData);

        JSONObject rsp = httpService.httpPostXwwFormUrlEncoded(payUrl, reqData);
        if (rsp.containsKey("result") && rsp.getInteger("result") == 1) {
            JSONObject data = rsp.getJSONObject("data");
            String paySafeCode = data.getString("paySafeCode");
            log.info("data = " + paySafeCode);
            channelOrderNo.put("data", data.toJSONString());
            return true;
        } else {
            log.info(rsp.getString("message"));
            return false;
        }
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       appKey      string	    游戏的appKey	                            否	是
     *                       gid         string	    用户的gid	                            否	是
     *                       orderId     string	    1758订单号	                            否	是
     *                       txId        string	    cp订单号	                                否	是
     *                       productDesc string	    商品描述	                                否	是
     *                       totalFee    int	    支付金额，单位分	                        否	是
     *                       status      int	    订单状态	                                否	是
     *                       state       string	    cp自定义参数	                            是	是
     *                       ext         string	    订单其他信息，json结构，预留参数，目前未启用	是	否
     *                       sign        string	    参数签名	                                否	签名方法 , 签名工具
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String secretKey = configMap.get(appId).getString(Game1758Config.GAME_SECRET);

        String money = FeeUtils.fenToYuan(parameterMap.get("totalFee"));
        // 加密串
        StringBuilder param = new StringBuilder();
        String[] signKey = {"appKey", "gid", "orderId", "txId", "productDesc", "totalFee", "status", "state"};
        Arrays.sort(signKey);
        boolean first = true;
        for (String key : signKey) {
            if (key.equals("sign")) {
                continue;
            }
            if (first) {
                super.addParam(param, key, parameterMap.get(key));
                first = false;
            } else {
                super.addParamAnd(param, key, parameterMap.get(key));
            }
        }

        log.info("param = " + param.toString());

        // 签名验证
        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString() + secretKey);

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);


        if (!sign.equals(serverSign)) {
            return false;
        }
        // 渠道订单赋值
        setChannelOrder(channelOrderNo, "", parameterMap.get("txId"), parameterMap.get("orderId"), money);
        return true;
    }
}

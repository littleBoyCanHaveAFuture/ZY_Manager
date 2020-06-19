package com.zyh5games.sdk.channel.shengyu;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.util.Base64;
import com.zyh5games.util.FeeUtils;
import com.zyh5games.util.MD5Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 盛娱
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("30")
public class ShengYuChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(ShengYuChannel.class);

    @Autowired
    HttpService httpService;

    ShengYuChannel() {
        channelId = ChannelId.H5_ShengYu;
        channelName = "盛娱";
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
        channelData.put("name", "ShengYuH5");
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
     *                 channel
     *                 game_id
     *                 code
     *                 state
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        String[] mustKey = {"code"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String clientId = configMap.get(appId).getString(ShengYuConfig.GAME_ID);
        String clientSecret = configMap.get(appId).getString(ShengYuConfig.GAME_KEY);

        HttpHeaders headers = getHeader(clientId, clientSecret);
        String code = map.get("code")[0];

        /*
            {
                'access_token':'xxxxx',
                'expires_in':3600,
                'refresh_token':'xxxxx',
                'token_type':'Bearer'
            }
            {
                'code': 0,
                'data': {
                    'openid': '用户与该游戏绑定的唯一ID',
                    'ip': '用户此次登陆时的IP地址',
                    'nickname': '用户昵称',
                    'avatar': '用户头像',
                    'authentication': false,
                    'game_id': '游戏id',
                    'server_id': '服务器id',
                    'adult_at': '用户年满18周岁的具体时间, 在 authentication 为 true 时再用此时间判断是否年满18岁，unix 时间戳, 秒',
                    'channel': ''
                },
                'message': '操作成功'
            }
        **/
        JSONObject req = new JSONObject();
        req.put("code", code);
        req.put("grant_type", "authorization_code");

        JSONObject rsp = httpService.httpPostXwwFormUrlEncodedExtraHeader(ShengYuConfig.TokenUrl, req, headers);
        if (rsp.containsKey("access_token")) {
            String accessToken = rsp.getString("access_token");
            String userUrl = ShengYuConfig.UserInfoUrl + "code=" + accessToken;
            JSONObject rspUserInfo = httpService.httpGetJsonExtraHeader(userUrl, headers);
            JSONObject data = rspUserInfo.getJSONObject("data");
            if (rspUserInfo.containsKey("code") && rspUserInfo.containsKey("data")) {

                String channelUid = data.getString("openid");
                String userName = data.getString("nickname");

                setUserData(userData, channelUid, userName, String.valueOf(channelId), "");
                return true;
            }
        }

        setUserData(userData, "", "", String.valueOf(channelId), "");
        return false;

    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     *                       'order_no': '订单号',
     *                       'money': '充值金额',
     *                       'game_id': '游戏ID',
     *                       'game_area': '区服ID',
     *                       'product_name': '充值商品名',
     *                       'extra_data': '额外数据, 由游戏服务器自己定义, 这些数据将会在通知时原封不同返回,为 json 字符串',
     *                       'sign': '签名字符串'
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(ShengYuConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ShengYuConfig.COMMON_KEY);


        String serverId = orderData.getString("serverId");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String amount = orderData.getString("amount");
        String subject = orderData.getString("subject");
        String extrasParams = orderData.getString("extrasParams");


        /**
         *'order_no': '订单号',
         * 'money': '充值金额',
         * 'game_id': '游戏ID',
         * 'game_area': '区服ID',
         * 'product_name': '充值商品名',
         * 'extra_data': '额外数据, 由游戏服务器自己定义, 这些数据将会在通知时原封不同返回,为 json 字符串',
         * 'sign': '签名字符串'
         * */

        // 渠道订单数据
        JSONObject data = new JSONObject();
        data.put("order_no", cpOrderNo);
        data.put("money", FeeUtils.yuanToFen(amount));
        data.put("game_id", channelGameId);
        data.put("game_area", serverId);
        data.put("product_name", subject);
        data.put("extra_data", extrasParams);
        data.put("sign", "sign");

        // 加密串
        boolean first = true;
        String[] signKey = {"order_no", "money", "game_id", "game_area", "product_name", "extra_data"};
        Arrays.sort(signKey);
        StringBuilder param = new StringBuilder();
        for (String key : signKey) {
            String value = data.getString(key);
            if (!value.isEmpty()) {
                if (first) {
                    first = false;
                    super.addParam(param, key, value);
                } else {
                    super.addParamAnd(param, key, value);
                }
            }
        }
        super.addParamAnd(param, "key", payKey);
        log.info("param = " + param.toString());

        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayInfo serverSign = " + serverSign);
        //签名大写
        data.replace("sign", serverSign.toUpperCase());
        log.info("channelPayInfo data: " + data);

        channelOrderNo.put("data", data.toJSONString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       origin_order_no  原始订单号
     *                       order_id         平台订单号
     *                       type             充值类型（direct 平台直充 order 订单充值）
     *                       money            充值金额(单位分)
     *                       game_id          游戏ID
     *                       game_area        区服ID
     *                       pay_channel      支付渠道     wechat     alipay    coin
     *                       status           订单状态 1 表示成功，只有成功才会发通知
     *                       complete_at      成功时间戳
     *                       extra_data       透传参数
     *                       sign             签名字符串
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String channelGameId = configMap.get(appId).getString(ShengYuConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ShengYuConfig.COMMON_KEY);

        // 加密串
        Set<String> keySet = parameterMap.keySet();
        Set<String> sortSet = new TreeSet<>(Comparator.naturalOrder());
        sortSet.addAll(keySet);

        StringBuilder param = new StringBuilder();
        boolean first = false;
        for (String key : sortSet) {
            if (key.equals("sign")) {
                continue;
            }
            String value = parameterMap.get(key);
            if (!first) {
                super.addParam(param, key, value);
                first = true;
            } else {
                super.addParamAnd(param, key, value);
            }
        }
        super.addParamAnd(param, "key", payKey);
        log.info("param = " + param.toString());

        // 签名验证 签名大写
        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString()).toUpperCase();

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);

        if (!sign.equals(serverSign)) {
            return false;
        }
        String money = parameterMap.get("money");
        String cpOrderId = parameterMap.get("origin_order_no");
        String channelOrderId = parameterMap.get("order_id");

        String yuan = FeeUtils.fenToYuan(money);
        // 渠道订单赋值
        setChannelOrder(channelOrderNo, "", cpOrderId, channelOrderId, yuan);
        return true;
    }

    /*渠道需要设置 headers**/
    public HttpHeaders getHeader(String clientId, String clientSecret) {
        String param = clientId + ":" + clientSecret;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic" + " " + Base64.encode(param.getBytes()));
        return headers;
    }
}

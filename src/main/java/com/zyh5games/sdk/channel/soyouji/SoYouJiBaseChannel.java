package com.zyh5games.sdk.channel.soyouji;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.sdk.channel.example.ExampleConfig;
import com.zyh5games.util.FeeUtils;
import com.zyh5games.util.MD5Util;
import com.zyh5games.util.RandomUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * so游记
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("24")
public class SoYouJiBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(SoYouJiBaseChannel.class);
    static Map<String, String> extMap;
    @Autowired
    HttpService httpService;

    SoYouJiBaseChannel() {
        channelId = ChannelId.H5_SOYOUJI;
        configMap = new ConcurrentHashMap<>();
        extMap = new ConcurrentHashMap<>();

    }

    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @return boolean
     */
    @Override
    public JSONObject channelLib(Integer appId) {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "SoYouJiH5");
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
     *                 uid      用户在汇米网络的用户ID                                 # length <= 20
     *                 ext      透传参数，在发起支付与发送事件时需要使用到此值            # length = 128
     *                 nonce    随机字符串，可为空                                   # length <= 64
     *                 time     操作发生时的UNIX时间戳，精确到秒                      # length = 10
     *                 sign     签名，用于请求合法性校验
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        String[] mustKey = {"uid", "ext", "nonce", "time", "sign"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(SoYouJiConfig.COMMON_KEY);

        String uid = map.get("uid")[0];
        String ext = map.get("ext")[0];

        // 加密串
        StringBuilder param = new StringBuilder();
        Arrays.sort(mustKey);
        boolean isFirst = true;
        for (String key : mustKey) {
            if (key.equals("sign")) {
                continue;
            }
            String value = map.get(key)[0];
            if (isFirst) {
                isFirst = false;
                super.addParam(param, key, value);
            } else {
                super.addParamAnd(param, key, value);
            }
        }
        param.append(loginKey);

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
        if (extMap.containsKey(uid)) {
            extMap.replace(uid, ext);
        } else {
            extMap.put(uid, ext);
        }
        setUserData(userData, uid, uid, String.valueOf(channelId), "");
        return true;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     *                       uid            用户在汇米网络的用户ID                         # length <= 20
     *                       ext            透传参数，用户在登录时传入的ext参数              # length = 128
     *                       nonce          随机字符串，可为空                             # length <= 64
     *                       time           操作发生时的UNIX时间戳，精确到秒                 # length = 10
     *                       serverid       游戏区服ID                                    # length <= 20
     *                       server_name    游戏区服名                                    # length <= 64
     *                       roleid         用户在游戏中的角色ID                           # length <= 20
     *                       role_name      游戏角色名                                    # length <= 64
     *                       money          订单金额，币种人民币，单位分，此值必须为正整数     # length <= 20
     *                       propsname      游戏商品名                                    # length <= 128
     *                       order          由游戏开发商产生的唯一订单号                      # length <= 128
     *                       token          校验token，此值会在通知支付结果的请求中完整的传回给游戏开发商，可为空 # length <= 128
     *                       sign           签名，用于请求合法性校验
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(SoYouJiConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(SoYouJiConfig.COMMON_KEY);

        String channelUid = orderData.getString("uid");
        String userRoleId = orderData.getString("userRoleId");
        String userRoleName = orderData.getString("userRoleName");
        String serverId = orderData.getString("serverId");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String amount = orderData.getString("amount");
        String subject = orderData.getString("subject");

        String ext = extMap.getOrDefault(channelUid, "");
        if (ext.isEmpty()) {
            return false;
        }

        // 渠道订单数据
        JSONObject data = new JSONObject();
        data.put("uid", channelUid);
        data.put("ext", ext);
        data.put("nonce", "nonce");
        data.put("time", System.currentTimeMillis() / 1000);
        data.put("serverid", serverId);
        data.put("server_name", "");
        data.put("roleid", userRoleId);
        data.put("role_name", userRoleName);
        data.put("money", FeeUtils.yuanToFen(amount));
        data.put("propsname", subject);
        data.put("order", cpOrderNo);
        data.put("token", "");

        String[] signKey = {"uid", "ext", "nonce", "time", "serverid", "server_name", "roleid",
                "role_name", "money", "propsname", "order", "token"};
        // 加密串
        StringBuilder param = new StringBuilder();
        super.signJson(param, signKey, data);

        log.info("param = " + param.toString());

        // 签名验证
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayInfo serverSign = " + serverSign);

        data.put("sign", "sign");
        super.addParamAnd(param, "sign", serverSign);
        log.info("channelPayInfo data: " + data);
        channelOrderNo.put("data", param.toString());

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
        String channelGameId = configMap.get(appId).getString(ExampleConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ExampleConfig.PAY_KEY);

        // 加密串
        String[] signKey = {"uid", "nonce", "time", "money", "propsname", "order",
                "token", "pay_code"};

        StringBuilder param = super.signMap(signKey, parameterMap);

        // 签名验证
        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString() + payKey);

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);

        if (!sign.equals(serverSign)) {
            return false;
        }

        String money = parameterMap.get("money");
        String cpOrderId = parameterMap.get("order");
        String nonce = parameterMap.get("nonce");
        String time = parameterMap.get("time");
        String order = parameterMap.get("order");
        String reqParam = "nonce=" + nonce + "&time=" + time + "&order=" + order;
        String reqSign = MD5Util.md5(reqParam + payKey);
        // 验证订单查询 todo
        // # 查询订单；"nothing" 表示订单不存在 、"unpaid" 表示未支付、"paid" 表示已支付但游戏开发商尚未做出正确反馈、"succeed" 表示订单已完成、"failed" 表示用户已支付但物品发放失败
        //参数 #
        //nonce  随机字符串，可为空 # length <= 64
        //time  操作发生时的UNIX时间戳，精确到秒 # length = 10
        //order  订单号
        //sign  签名，用于请求合法性校验
        String payUrl = SoYouJiConfig.PAY_URL;
        String url = payUrl + reqParam + "&sign=" + reqSign;

        String rsp = httpService.httpGetString(url);
        log.info(rsp);
        if (rsp.isEmpty() || rsp.equals("nothing") || rsp.equals("unpaid")) {
            return false;
        }
        if (rsp.equals("succeed") || rsp.equals("failed")) {
            // 渠道订单赋值
            setChannelOrder(channelOrderNo, "", cpOrderId, "", FeeUtils.fenToYuan(money));
            return true;
        }
        return false;
    }

    @Override
    public JSONObject ajaxGetSignature(Integer appId, JSONObject requestInfo, JSONObject result) {
        String[] signKey = {"uid", "nonce", "time", "serverid", "server_name", "roleid", "role_name", "level"};
        for (String index : signKey) {
            if (!requestInfo.containsKey(index)) {
                result.put("message", "缺失参数：" + index);
                result.put("status", false);
                return null;
            }
        }
        requestInfo.replace("nonce", RandomUtil.rnd100());
        requestInfo.replace("time", System.currentTimeMillis() / 1000);
        String roleKey = configMap.get(appId).getString(SoYouJiConfig.COMMON_KEY);
        String gameId = configMap.get(appId).getString(SoYouJiConfig.GAME_ID);

        // 加密字符串
        StringBuilder param = new StringBuilder();
        super.signJson(param, signKey, requestInfo);

        // 签名验证
        String sign = MD5Util.md5(param.toString() + roleKey);
        log.info("serverSign = " + sign);
        super.addParamAnd(param, "sign", sign);

        JSONObject userData = new JSONObject();
        userData.put("param", param.toString());
        return userData;

    }
}

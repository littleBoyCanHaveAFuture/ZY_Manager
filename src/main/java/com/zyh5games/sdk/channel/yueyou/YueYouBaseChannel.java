package com.zyh5games.sdk.channel.yueyou;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.example.ExampleConfig;
import com.zyh5games.util.FeeUtils;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 百家
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("15")
public class YueYouBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(YueYouBaseChannel.class);

    Map<String, String> loginExtMap;
    Map<String, String> loginModelMap;

    YueYouBaseChannel() {
        channelId = ChannelId.H5_YUEYOU;
        loginExtMap = new HashMap<>();
        loginModelMap = new HashMap<>();
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("http://www.0713yy.com/Public/static/xigusdk/xgh5sdk.js?" + System.currentTimeMillis());
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
        channelData.put("name", "YueYouH5");
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
     *                 channelExt               透传信息，在支付跳转时原样返回
     *                 email                    CP 方分配给运营方的账号（没有则游戏 ID）
     *                 game_appid               游戏编号----运营方为游戏分配的唯一编号
     *                 new_time                 当前时间戳
     *                 loginplatform2cp         用于 CP 要求平台特别传输其他参数，默认是访问 ip
     *                 user_id                  用户唯一 ID
     *                 sdklogindomain           调起登录和支付 sdk 的域名
     *                 sdkloginmodel            调起登录和支付 sdk 的模块,，不是固定值,拉起登录和支付时需原样返回（运营方特殊要求）
     *                 sign                     按照上方签名机制进行签名
     *                 icon                     用户头像，不参与加密
     *                 nickname                 用户昵称，不参与加密
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        String[] mustKey = {"channelExt", "email", "game_appid", "new_time", "loginplatform2cp", "user_id",
                "sdklogindomain", "sdkloginmodel", "icon", "nickname"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(YueYouConfig.COMMON_KEY);
        Arrays.sort(mustKey);
        // 加密串
        StringBuilder param = new StringBuilder();

        boolean first = false;
        for (String key : mustKey) {
            String value = map.get(key).length > 0 ? map.get(key)[0] : "";
            if (!first) {
                super.addParam(param, key, value);
                first = true;
            } else {
                super.addParamAnd(param, key, value);
            }
        }

        System.out.println("param = " + param.toString());

        // 签名验证
        String sign = map.get("sign")[0];
        String serverSign = MD5Util.md5(param.toString() + loginKey);

        log.info("channelLogin serverSign = " + serverSign);
        log.info("channelLogin sign       = " + sign);

        System.out.println("channelLogin serverSign = " + serverSign);
        System.out.println("channelLogin sign       = " + sign);

        if (!sign.equals(serverSign)) {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }
        String channelExt = map.get("channelExt")[0];
        String sdkloginmodel = map.get("sdkloginmodel")[0];
        String channelUid = map.get("user_id")[0];
        loginExtMap.put(channelUid, channelExt);
        loginModelMap.put(channelUid, sdkloginmodel);
        setUserData(userData, "channelUid", "userName", String.valueOf(channelId), "openid");
        return true;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     *                       amount                 金额，单位为分
     *                       channelExt             原样返回登陆时透传的信息
     *                       game_appid             游戏编号----运营方为游戏分配的唯一编号
     *                       props_name             道具名称
     *                       trade_no               游戏透传参数（默认为游戏订单号，回调时候原样返回）
     *                       user_id                运营方用户 ID
     *                       sdkloginmodel          登录时的传递的参数
     *                       sign                   按照上方签名机制进行签名
     *                       server_id              区服 id（不参与加密）
     *                       server_name            区服名称（不参与加密）
     *                       role_id                角色 id（不参与加密）
     *                       role_name              角色名（不参与加密）
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(ExampleConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ExampleConfig.PAY_KEY);

        long time = System.currentTimeMillis() / 1000;

        // 加密串
        StringBuilder param = new StringBuilder();
        String[] signKey = {"amount", "channelExt", "game_appid", "props_name", "trade_no", "sdkloginmodel"};
        Arrays.sort(signKey);

        boolean first = false;
        for (String key : signKey) {
            String value = orderData.getString(key);
            if (!first) {
                super.addParam(param, key, value);
                first = true;
            } else {
                super.addParamAnd(param, key, value);
            }
        }

        System.out.println("param = " + param.toString());


        // 签名验证
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayInfo serverSign = " + serverSign);

        System.out.println("channelPayInfo serverSign = " + serverSign);


        String channelUid = orderData.getString("uid");
        String money = FeeUtils.yuanToFen(orderData.getString("amount"));
        // 渠道订单数据
        JSONObject data = new JSONObject();
        data.put("amount", money);
        data.put("channelExt", loginExtMap.get(channelUid));
        data.put("game_appid", channelGameId);
        data.put("props_name", orderData.getString("subject"));
        data.put("trade_no", orderData.getString("extrasParams"));
        data.put("user_id", channelUid);
        data.put("sdkloginmodel", loginModelMap.get(channelUid));
        data.put("sign", serverSign);
        data.put("server_id", orderData.getString("serverId"));
        data.put("server_name", orderData.getString("userServer"));
        data.put("role_id", orderData.getString("userRoleId"));
        data.put("role_name", orderData.getString("userRoleName"));

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
        String channelGameId = configMap.get(appId).getString(ExampleConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ExampleConfig.PAY_KEY);

        // 加密串
        StringBuilder param = new StringBuilder();
        super.addParam(param, "", "");
        super.addParamAnd(param, "", "");

        System.out.println("param = " + param.toString());

        // 签名验证
        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);

        System.out.println("channelPayCallback serverSign = " + serverSign);
        System.out.println("channelPayCallback sign       = " + sign);

        if (!sign.equals(serverSign)) {
            return false;
        }
        // 渠道订单赋值
        setChannelOrder(channelOrderNo, "", "cpOrderId", "channelOrderId", "money");
        return true;
    }
}

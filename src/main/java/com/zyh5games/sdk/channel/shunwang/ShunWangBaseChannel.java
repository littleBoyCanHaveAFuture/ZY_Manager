package com.zyh5games.sdk.channel.shunwang;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.service.AccountService;
import com.zyh5games.util.Base64;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 顺网
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("12")
public class ShunWangBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(ShunWangBaseChannel.class);
    @Autowired
    HttpService httpService;
    @Autowired
    AccountService accountService;

    ShunWangBaseChannel() {
        configMap = new HashMap<>();
        channelId = ChannelId.H5_SHUNWANG;
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("https://reslaosiji.swjoy.com/pay/h5_pay.js");
        return libUrl;

    }

    /**
     * 1.渠道初始化 加载渠道js文件
     */
    @Override
    public JSONObject channelLib(Integer appId) {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "ShunWangH5");
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
        return "";
    }

    /**
     * 3.渠道登录<p>
     * 3.1 向渠道校验 获取用户数据 <p>
     * 3.2 设置token<p>
     *
     * @param map      渠道传入参数
     *                 guid	            string	是	顺网平台游戏帐号
     *                 fcm	            int	    是	防沉迷。1:未成年，需要沉迷， 0,已成年
     *                 card_state	    int	    否	实名认证 0,未实名;1,已实名
     *                 play_type	    string	是	默认 web,盒子 box,端 pc 等
     *                 server_idx	    string	否	页游顺网平台区服标识
     *                 server_code	    string	否	分区服的游戏必传,游戏方的区服编号,如s1,s2
     *                 platform	        string	是	固定值:swjoy
     *                 time	            int	    是	unix时间戳
     *                 sign	            string	是	签名规则
     *                 _back_url	    string	否	验证失败跳转地址(不参与签 名)
     *                 sw_tag	        string	否	sw_tag参数,页游必传
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);
        String loginKey = configMap.get(appId).getString(ShunWangConfig.LOGIN_KEY);


        String[] mustKey = {"guid", "fcm", "play_type", "platform", "time", "sign"};
        for (String key : mustKey) {
            if (!map.containsKey(key)) {
                return false;
            }
        }
        String channelUid = map.get("guid")[0];

        String[] signKey = {"guid", "fcm", "play_type", "platform", "time", "card_state", "server_idx", "server_code", "sw_tag"};
        Arrays.sort(signKey);

        String sign = map.get("sign")[0];

        //  签名原串: 0|0|6450_1590389480629_59367345|swjoy|web|1|1|1590400382|7y6GEXkYay4e2vP9d75ocGz22N7HhVba
        //  sign=2b93142a5d4473b5d98fa4002a7a79c5
        //升序排列
        StringBuilder param = new StringBuilder();
        try {
            for (String s : signKey) {
                if (!map.containsKey(s)) {
                    continue;
                }
                String[] arrValue = map.get(s);
                if (arrValue.length <= 0) {
                    continue;
                }
                String value = arrValue[0];
                String urlDecodeValue = URLDecoder.decode(value, String.valueOf(StandardCharsets.UTF_8));

                param.append(urlDecodeValue).append("|");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        param.append(loginKey);

        log.info("channelLogin param = " + param);
        String serverSign = MD5Util.md5(param.toString());
        log.info("channelLogin serverSign = " + serverSign);
        log.info("channelLogin sign       = " + sign);

        if (sign.equals(serverSign)) {
            setUserData(userData, channelUid, "", String.valueOf(channelId), "");
            return true;
        } else {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }

    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     *                       guid         string    是    顺网平台游戏帐号
     *                       orderNo      string    是    游戏方订单号
     *                       rmb          string    是    金额,单位元
     *                       idx          int       否    顺网区服编号(如果充值通知接口需要验证server_code，则必需传idx)
     *                       time         int       是    unix时间戳,长度10位
     *                       size         int       否    二维码大小(默认 140*140), 最大不能超过 512
     *                       otherData    string    否    透传参数,充值通知接口原样返回，顺网充值中心的游戏不要传
     *                       swTag        string    否    swTag参数
     * @param channelOrderNo 渠道订单返回参数
     *                       gameId     渠道游戏id
     *                       data       url数据
     *                       sign       签名
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String qrCodeKey = configMap.get(appId).getString(ShunWangConfig.QRCODE_KEY);
        String channelGameId = configMap.get(appId).getString(ShunWangConfig.GAME_ID);

        JSONObject data = new JSONObject();
        data.put("guid", orderData.getString("uid"));
        data.put("orderNo", orderData.getString("cpOrderNo"));
        data.put("size", 140);
        data.put("rmb", orderData.getString("amount"));
        data.put("time", System.currentTimeMillis() / 1000);
        data.put("otherData", orderData.getString("extrasParams"));

        log.info("data = " + data.toJSONString());

        String base64Data = Base64.encode(data.toString(), String.valueOf(StandardCharsets.UTF_8));
        String sign = MD5Util.md5(base64Data + qrCodeKey);
        String urlEncodeData = "";
        try {
            assert sign != null;
            urlEncodeData = URLEncoder.encode(base64Data, String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        channelOrderNo.put("gameId", channelGameId);
        channelOrderNo.put("data", urlEncodeData);
        channelOrderNo.put("sign", sign);
        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       参数名          类型    是否必传    说明
     *                       order_no	    string	是       顺网平台订单号
     *                       guid	        string	是       顺网平台游戏帐号
     *                       server_code	string	否       分区服的游戏必传,游戏方的区服编号,如s1,s2
     *                       money	        string	是       充值金额,人民币(单位元)
     *                       coin	        string	否       游戏币数量,页游必传
     *                       role_id	    string	否       角色ID(充值到角色的游戏必传)
     *                       out_order_no	string	否       游戏方订单号(H5,端游游戏必传)
     *                       other_data	    string	否       其他数据(透传，原样返回)
     *                       platform	    string	是       固定值:swjoy
     *                       time	        int	    是	     unix时间戳
     *                       sign	        string	是	     签名规则
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String payKey = configMap.get(appId).getString(ShunWangConfig.PAY_KEY);

        String[] mustKey = {"order_no", "guid", "money", "platform", "time", "sign"};
        for (String key : mustKey) {
            if (!parameterMap.containsKey(key)) {
                log.info("channelPayCallback 缺少key：" + key);
                return false;
            }
        }

        String[] signKey = {"order_no", "guid", "server_code", "money", "coin",
                "role_id", "out_order_no", "other_data", "platform", "time"};
        Arrays.sort(signKey);

        // 1|6450_1590389480629_59367345|1|Test_LSJ_6450_20200525194126_709204|211|2121|swjoy|1|1|1590407707|1FudvEHJrAKQ8MFiPMWLIE3OvxjzfrHl
        //  sign=fdb912fb0af6c488be7f9ef05e1504d5,
        //升序排列
        StringBuilder pp = new StringBuilder();
        StringBuilder param = new StringBuilder();
        try {
            for (String s : signKey) {
                pp.append("&").append(s).append("=").append(parameterMap.get(s));
                if (!parameterMap.containsKey(s) || parameterMap.get(s).isEmpty()) {
                    continue;
                }
                String value = parameterMap.get(s);
                String urlDecodeValue = URLDecoder.decode(value, String.valueOf(StandardCharsets.UTF_8));

                param.append(urlDecodeValue).append("|");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        param.append(payKey);

        log.info("channelPayCallback param = " + param);

        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);

        return sign.equals(serverSign);
    }


}

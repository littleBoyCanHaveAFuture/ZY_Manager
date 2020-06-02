package com.zyh5games.sdk.channel.zhangmeng;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.util.FeeUtils;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 掌盟
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("17")
public class ZhangMengBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(ZhangMengBaseChannel.class);

    ZhangMengBaseChannel() {
        channelId = ChannelId.H5_ZHANGMENG;
        configMap = new HashMap<>();
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
//        libUrl.add("https://cn.soeasysdk.com/soeasysr/zm_engine_v2.js" + "?" + System.currentTimeMillis());
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
        channelData.put("name", "ZhangMengH5");
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
     * 掌盟因为js加载顺序问题 会失败请求一次 待js初始化完成后 回调方法主动调起 cp登录方法，再次完成登录
     *
     * @param map      渠道传入参数
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);
        String secretKey = configMap.get(appId).getString(ZhangMengConfig.COMMON_KEY);

        String[] mustKey = {"zysyTime", "zysyUid", "zysyName", "zysySign"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        String time = map.get("zysyTime")[0];
        String uid = map.get("zysyUid")[0];
        String name = map.get("zysyName")[0];
        String sign = map.get("zysySign")[0];

        StringBuilder param = new StringBuilder();
        super.addParam(param, "secret_key", secretKey);
        super.addParamAnd(param, "t", time);
        super.addParamAnd(param, "uid", uid);

        String serverSign = MD5Util.md5(param.toString());
        log.info("channelLogin sign = " + sign);
        log.info("channelLogin serverSign = " + serverSign);

        if (!sign.equals(serverSign)) {
            setUserData(userData, "", name, String.valueOf(channelId), "");
            return false;
        }
        setUserData(userData, uid, name, String.valueOf(channelId), "");
        return true;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     *                       check          md5(fee+feeid+seceret_key),
     *                       feeid          必填      cp 方自定义的计费 id,
     *                       fee            必填      金额 分,
     *                       feename        必填      商品名称,
     *                       extradata                  透传参数 支付回调通知时会带给cp,
     *                       serverid       游戏分区号,
     *                       rolename       角色 名称,
     *                       roleid         角色 ID ,
     *                       servername     分区服名称
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        log.info(orderData.toJSONString());
        log.info(configMap.toString());

        Integer appId = orderData.getInteger("appId");

        String payKey = configMap.get(appId).getString(ZhangMengConfig.COMMON_KEY);

        String fee = FeeUtils.yuanToFen(orderData.getString("amount"));
        log.info("fee = " + fee.toString());
        String feeid = orderData.getString("goodsId");
        String feename = orderData.getString("subject");
        String extradata = orderData.getString("extrasParams");
        String serverid = orderData.getString("serverId");
        String rolename = orderData.getString("userRoleName");
        String roleid = orderData.getString("userRoleId");
        String servername = orderData.getString("userServer");

        // 加密串
        StringBuilder param = new StringBuilder();
//        super.addParam(param, "fee", fee);
//        super.addParamAnd(param, "feeid", feeid);
        param.append(fee);
        param.append(feeid);
        param.append(payKey);
        log.info("param = " + param.toString());

        // 签名验证
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayInfo serverSign = " + serverSign);

        log.info("channelPayInfo serverSign = " + serverSign);


        // 渠道订单数据
        JSONObject data = new JSONObject();
        data.put("check", serverSign);
        data.put("feeid", feeid);
        data.put("fee", fee);
        data.put("feename", feename);
        data.put("extradata", extradata);
        data.put("serverid", serverid);
        data.put("rolename", rolename);
        data.put("roleid", roleid);
        data.put("servername", servername);


        log.info("channelPayInfo data: " + data);
        channelOrderNo.put("data", data.toJSONString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       字段        类型        是否必填    备注
     *                       appid       string      Y           同APPID
     *                       sdkindx     string      Y           平台定义
     *                       uid         string      Y           用户的唯一标示
     *                       feeid       string      N           计费点ID
     *                       feemoney    string      Y           实际扣费金额（分）
     *                       orderid     string      Y           支付在速易服务器上订单号
     *                       extradata   string      N           Cp自定义参数，响应时透传返回（如游戏服务的订单号）
     *                       paytime     string      Y           下单时间
     *                       prover      string      Y           协议版本号初始为1
     *                       paystatus   string      Y           支付状态1为成功，2沙盒测试，其他均为失败signstringY算法见下文
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String channelGameId = configMap.get(appId).getString(ZhangMengConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ZhangMengConfig.COMMON_KEY);

        String cpOrderId = parameterMap.get("extradata");
        String channelOrderId = parameterMap.get("orderid");
        String money = parameterMap.get("feemoney");
        String time = parameterMap.get("paytime");
        String[] signKey = {"appid", "sdkindx", "uid", "feeid", "feemoney", "orderid", "extradata", "paytime", "prover", "paystatus"};
        Arrays.sort(signKey);
        // 加密串
        boolean first = true;
        StringBuilder param = new StringBuilder();
        for (String s : signKey) {
            if (parameterMap.get(s) == null || parameterMap.get(s).isEmpty()) {
                continue;
            }
            if ("sign".equals(s)) {
                continue;
            }
            if (first) {
                super.addParam(param, s, parameterMap.get(s));
                first = false;
            } else {
                super.addParamAnd(param, s, parameterMap.get(s));
            }
        }
        System.out.println(time);
        System.out.println(param.toString());
        log.info("param = " + param.toString());
        //appid=1052&extradata=20170307135213SkfBjDM&feeid=1&feemoney=100&orderid=3151703071404286&paystatus=1&paytime=2017-03-0713:52:14&prover=1&sdkindx=315&uid=f734d3f81b6e21e952b4ca3074d90a30
        String serverSignOne = MD5Util.md5(param.toString());
        String serverSignTwo = MD5Util.md5(serverSignOne + payKey);


        // 签名验证
        String sign = parameterMap.get("sign");
        String serverSign = serverSignTwo;

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);

        if (!sign.equals(serverSign)) {
            return false;
        }
        // 渠道订单赋值
        setChannelOrder(channelOrderNo, "", cpOrderId, channelOrderId, money);
        return true;
    }

//    @Override
//    public JSONObject ajaxGetSignature(Integer appId, JSONObject requestInfo, JSONObject result) {
//        String time = requestInfo.getString("time");
//        String uid = requestInfo.getString("uid");
//    }
}

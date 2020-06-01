package com.zyh5games.sdk.channel.zhangmeng;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.util.FeeUtils;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

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
        System.out.println("channelLogin sign = " + sign);
        System.out.println("channelLogin serverSign = " + serverSign);

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
        System.out.println(orderData.toJSONString());
        System.out.println(configMap.toString());

        Integer appId = orderData.getInteger("appId");

        String payKey = configMap.get(appId).getString(ZhangMengConfig.COMMON_KEY);

        String fee = FeeUtils.yuanToFen(orderData.getString("amount"));
        System.out.println("fee = " + fee.toString());
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
        System.out.println("param = " + param.toString());

        // 签名验证
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayInfo serverSign = " + serverSign);

        System.out.println("channelPayInfo serverSign = " + serverSign);


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
        String channelGameId = configMap.get(appId).getString(ZhangMengConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ZhangMengConfig.PAY_KEY);

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

//    @Override
//    public JSONObject ajaxGetSignature(Integer appId, JSONObject requestInfo, JSONObject result) {
//        String time = requestInfo.getString("time");
//        String uid = requestInfo.getString("uid");
//    }
}

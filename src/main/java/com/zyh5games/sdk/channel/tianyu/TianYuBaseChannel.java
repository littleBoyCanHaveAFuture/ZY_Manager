package com.zyh5games.sdk.channel.tianyu;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.UOrder;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 悦游
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("41")
public class TianYuBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(TianYuBaseChannel.class);

    @Resource
    HttpService httpService;

    TianYuBaseChannel() {
        channelId = ChannelId.H5_TianYu;
        channelName = "天宇";
        configMap = new ConcurrentHashMap<>();
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("https://release.tianyuyou.cn/static/h5sdk/mbH5sdk.js" + "?" + System.currentTimeMillis());
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
        channelData.put("name", "TianYuH5");

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
     *                 参数	        说明
     *                 mbGameId
     *                 mbUserId
     *                 mbToken
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        String[] mustKey = {"mbGameId", "mbUserId", "mbToken"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(TianYuConfig.COMMON_KEY);
        Arrays.sort(mustKey);

        String mbGameId = map.get("mbGameId")[0];
        String mbUserId = map.get("mbUserId")[0];
        String mbToken = map.get("mbToken")[0];

        // 加密串
        // 签名方式:md5 appkey(对接群获取)
        //Md5(cp_game_id=".$cp_game_id."&release_token=".$release_token."&release_user_id=".$release_user_id."&app_key=".$appkey)
        StringBuilder param = new StringBuilder();
        super.addParam(param, "cp_game_id", mbGameId);
        super.addParamAnd(param, "release_token", mbToken);
        super.addParamAnd(param, "release_user_id", mbUserId);
        super.addParamAnd(param, "app_key", loginKey);

        log.info("param = " + param.toString());

        // 签名验证
        String serverSign = MD5Util.md5(param.toString());

        //cp_game_id:传给 游戏方的 mbGameId
        //release_user_id:用户id(登录后sdk传入) mbUserId
        //release_token:登录token（登录后sdk传入）mbToken
        //sign:签名
        JSONObject data = new JSONObject();
        data.put("cp_game_id", mbGameId);
        data.put("release_token", mbToken);
        data.put("release_user_id", mbUserId);
        data.put("sign", serverSign);
        log.info("channelLogin serverSign = " + serverSign);
        log.info("channelLogin data = " + data);
        JSONObject rsp = httpService.httpPostXwwFormUrlEncoded(TianYuConfig.Login_URL, data);

        //       返回:状态status:1成功 （其他失败）
        //        {
        //            "status ": "1",
        //            "msg": "用户已登录",
        //            "userinfo": {
        //                      "isAuthenticated ": 1,
        //                     "birthday": 19900101
        //             },
        //            "userinfo1": {
        //                  "age ": 0,
        //                  "birthday": 0,          //生日
        //                  "gender": 0,            //性别
        //                  "oversea": 'false',     //是否海外用户
        //                  "id_type": 0,           //证件类型 1身份证
        //                  "Id": '',               //身份证MD5
        //                  "verify_status": 1      //是否官方验证 1 否
        //              }
        //        }
        //}

        if (rsp.containsKey("status") && rsp.getInteger("status") == 1) {
            log.info(rsp.getString("msg"));
            if (rsp.containsKey("userinfo1")) {
                JSONObject userInfo = rsp.getJSONObject("userinfo1");

                setUserData(userData, mbUserId, "", String.valueOf(channelId), "");
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
     *                       参数名	    是否必填	    参数类型	        参数值
     *                       uid	    必填	        Integer	        用户登录ID
     *                       gkey	    必填	        String（120）	游戏名字(拼音字母缩写)
     *                       skey	    必填	        Integer	        传用户角色所在的游戏区服id
     *                       order_id	必填	        String（100）	订单号(游戏方自己的订单号)
     *                       money	    必填	        Integer	        人民币数量(单位:元) 只能为正整数
     *                       time	    必填	        Integer	        时间戳
     *                       sign	    必填      	String	        双方协定的密钥(生成规则为：md5($uid.$gkey.$skey.$time.$order_id.$money.'#'.$pkey) (pkey为充值密钥)
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String payKey = configMap.get(appId).getString(TianYuConfig.COMMON_KEY);

        long time = System.currentTimeMillis();

        String channelUid = orderData.getString("uid");
        String amount = orderData.getString("amount");
        String subject = orderData.getString("subject");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String extrasParams = orderData.getString("extrasParams");


        // 渠道订单数据
        // data = {
        //      amount:'1',
        //      cporder_sn:'123456',//游戏方订单号
        //      product_name:'测试订单',//商品名称,
        //      attach:'',透传参数
        //};
        JSONObject data = new JSONObject();
        data.put("amount", amount);
        data.put("cporder_sn", cpOrderNo);
        data.put("product_name", subject);
        data.put("attach", extrasParams);

        log.info("channelPayInfo data: " + data);

        channelOrderNo.put("data", data.toJSONString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       out_order_no cp订单编号
     *                       order_no     订单编号
     *                       amount       支付金额，单位元
     *                       role_id      玩家角色id
     *                       pay_time     支付时间(YY-mm-dd HH:ii:ss) 2017-12-31 12:22:22
     *                       cp_game_id   游戏id(对接群获取)
     *                       sign         签名
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String gameId = configMap.get(appId).getString(TianYuConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(TianYuConfig.COMMON_KEY);

        String outOrderNo = parameterMap.get("out_order_no");
        String orderNo = parameterMap.get("order_no");
        String amount = parameterMap.get("amount");
        String roleId = parameterMap.get("role_id");
        String payTime = parameterMap.get("pay_time");

        // 加密串
        StringBuilder param = new StringBuilder();
        super.addParam(param, "amount", amount);
        super.addParamAnd(param, "cp_game_id", gameId);
        super.addParamAnd(param, "order_no", orderNo);
        super.addParamAnd(param, "out_order_no", outOrderNo);
        super.addParamAnd(param, "pay_time", payTime);
        super.addParamAnd(param, "role_id", roleId);
        super.addParamAnd(param, "app_key", payKey);

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
        setChannelOrder(channelOrderNo, "", outOrderNo, orderNo, amount);
        return true;
    }

    @Override
    public boolean checkOrderMoney(String channelMoney, UOrder order) {
        Integer orderMoney = order.getMoney();
        int iYuan = orderMoney / 100;
        log.info("iYuan = " + iYuan);
        int channnelYuan = Integer.parseInt(channelMoney);
        log.info("channnelYuan = " + channnelYuan);
        return channnelYuan == iYuan;
    }
}

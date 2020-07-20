package com.zyh5games.sdk.channel.zixia;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.UOrder;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.util.FeeUtils;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 悦游
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("40")
public class ZiXiaBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(ZiXiaBaseChannel.class);

    ZiXiaBaseChannel() {
        channelId = ChannelId.H5_ZiXia;
        channelName = "紫霞";
        configMap = new ConcurrentHashMap<>();
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("https://game.zixia.com/js/dianjipay.js" + "?" + System.currentTimeMillis());
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
        channelData.put("name", "ZiXiaH5");

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
     *                 uid	        用户登录ID
     *                 platfrom	    联营方标识ID，供游戏方区分联营平台(默认为zixia)
     *                 gkey	        游戏名字(拼音字母缩写)
     *                 skey	        用户角色所在的游戏区服id， 合作默认接入 1
     *                 time	        时间戳
     *                 is_adult	    防沉迷状态(1为18岁以上，2为未满18，0为未填写,如果需要验证实名认证，请跳转到：https://h.zixia.com/u/?action=realname )
     *                 back_url	    登录失败跳转url
     *                 type	        登陆类型，web端type=web, 微端type=pc
     *                 sign	        双方协定的密钥(生成规则为：md5($uid.$platfrom.$gkey.$skey.$time.$is_adult.'#'.$lkey) (lkey为登陆密钥，注：uid为urldecode之后的值))
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        String[] mustKey = {"uid", "platfrom", "gkey", "skey", "time", "is_adult", "back_url", "type", "sign"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(ZiXiaConfig.LOGIN_KEY);
        Arrays.sort(mustKey);


        // 加密串 md5($uid.$platfrom.$gkey.$skey.$time.$is_adult.'#'.$lkey) (lkey为登陆密钥，注：uid为urldecode之后的值)
        StringBuilder param = new StringBuilder();
        String uid = "";
        try {
            uid = map.get("uid")[0];
            String urlUid = URLDecoder.decode(uid, String.valueOf(StandardCharsets.UTF_8));
            param.append(urlUid);
        } catch (Exception e) {
            return false;
        }
        param.append(map.get("platfrom")[0]);
        param.append(map.get("gkey")[0]);
        param.append(map.get("skey")[0]);
        param.append(map.get("time")[0]);
        param.append(map.get("is_adult")[0]);
        param.append("#").append(loginKey);

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

        setUserData(userData, uid, "", String.valueOf(channelId), "");
        return true;
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
        String gameKey = configMap.get(appId).getString(ZiXiaConfig.GAME_KEY);
        String payKey = configMap.get(appId).getString(ZiXiaConfig.PAY_KEY);

        long time = System.currentTimeMillis();

        String channelUid = orderData.getString("uid");
        String fen = FeeUtils.yuanToFen(orderData.getString("amount"));
        String props_name = orderData.getString("subject");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String skey = orderData.getString("serverId");

        Integer iFen = Integer.parseInt(fen);
        Integer iYuan = iFen / 100;
        log.info("iYuan = " + iYuan);

        // 加密串 md5($uid.$gkey.$skey.$time.$order_id.$money.'#'.$pkey
        StringBuilder param = new StringBuilder();
        param.append(channelUid);
        param.append(gameKey);
        param.append(skey);
        param.append(time);
        param.append(cpOrderNo);
        param.append(iYuan);
        param.append("#").append(payKey);

        log.info("param = " + param.toString());

        // 签名
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayInfo serverSign = " + serverSign);

        // 渠道订单数据       {"uid", "gkey", "skey", "order_id", "money", "time", "sign"};
        JSONObject data = new JSONObject();
        data.put("uid", channelUid);
        data.put("gkey", gameKey);
        data.put("skey", skey);
        data.put("order_id", cpOrderNo);
        data.put("money", iYuan);
        data.put("time", time);
        data.put("sign", serverSign);

        log.info("channelPayInfo data: " + data);

        channelOrderNo.put("data", data.toJSONString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       uid	    用户登录ID
     *                       gkey	    游戏名字(拼音字母缩写)
     *                       skey	    传用户角色所在的游戏区服id
     *                       order_id	订单号(游戏方自己的订单号)
     *                       money	    人民币数量(单位:元) 只能为正整数(需要厂家与原始订单进行校验比对)
     *                       time	    时间戳
     *                       sign	    双方协定的密钥(生成规则为：md5($uid.$gkey.$skey.$time.$order_id.$money.'#'.$pkey) (pkey为充值密钥)
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String gamekey = configMap.get(appId).getString(ZiXiaConfig.GAME_KEY);
        String payKey = configMap.get(appId).getString(ZiXiaConfig.PAY_KEY);

        String uid = parameterMap.get("uid");
        String gkey = parameterMap.get("gkey");
        String skey = parameterMap.get("skey");
        String order_id = parameterMap.get("order_id");
        String money = parameterMap.get("money");
        String time = parameterMap.get("time");

        if (!gamekey.equals(gkey)) {
            log.info("channelPayCallback 渠道参数 游戏名称错误 gamekey = " + gamekey + "gkey = " + gkey);
            return false;
        }


        // 加密串- md5($uid.$gkey.$skey.$time.$order_id.$money.'#'.$pkey)
        StringBuilder param = new StringBuilder();
        param.append(uid);
        param.append(gkey);
        param.append(skey);
        param.append(time);
        param.append(order_id);
        param.append(money);
        param.append("#").append(payKey);

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
        setChannelOrder(channelOrderNo, "", order_id, "", money);
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

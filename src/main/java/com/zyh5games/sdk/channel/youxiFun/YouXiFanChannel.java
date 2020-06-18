package com.zyh5games.sdk.channel.youxiFun;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.sdk.channel.example.ExampleConfig;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模板
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("26")
public class YouXiFanChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(YouXiFanChannel.class);

    @Autowired
    HttpService httpService;

    YouXiFanChannel() {
        channelId = ChannelId.H5_YOUXIFAN;
        configMap = new ConcurrentHashMap<>();
        channelName = "游戏Fan";
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("https://cdn.anjiu.cn/h5-sdk/js/yxfSDK.js" + "?" + System.currentTimeMillis());
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
        channelData.put("name", "YouXiFanH5");
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
        String[] mustKey = {"token", "username"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginUrl = configMap.get(appId).getString(YouXiFunConfig.Login_URL);
        String loginKey = configMap.get(appId).getString(ExampleConfig.LOGIN_KEY);

        // 参数
        String token = map.get("token")[0];
        String username = map.get("username")[0];

        // 加密串
        JSONObject req = new JSONObject();
        req.put("token", token);

        /**参数	        类型	        描述
         * code	        int	        返回码(0成功 1失败)
         * message	    string	    返回消息
         * data	        json	    返回数据
         * data >> userInfo	                json	    用户游戏账号信息
         * data >> userInfo >> userId	    string	    用户id
         * data >> userInfo >> username	    string	    用户名
         * data >> idInfo	                json	    防沉迷用户实名信息
         * data >> idInfo >> verifyStatus	int	        实名验证状态 1：未验证 2：验证通过 3：验证失败
         * data >> idInfo >> id	            string	    用户证件号码(已加密，24位)
         * data >> idInfo >> idType	        int	        证件类型 0：身份证 1：中国护照 2：海外护照 3：其他
         * data >> idInfo >> age	        int	        0：未实名认证，无法判断年龄；其他取值：用户实际年龄
         * data >> idInfo >> birthday	    string	    出生日期("20190101")
         * data >> idInfo >> oversea	    bool	    是否是海外用户true：海外用户 false：非海外用户
         * */
        JSONObject rsp = httpService.httpPostJson(loginUrl, req);
        if (rsp.containsKey("code") || rsp.getInteger("code") == 0) {
            if (rsp.containsKey("data")) {
                JSONObject data = rsp.getJSONObject("data");
                JSONObject userInfo = data.getJSONObject("userInfo");
                JSONObject idInfo = data.getJSONObject("idInfo");

                String channelUid = userInfo.getString("userId");
                String userName = userInfo.getString("username");

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
     *                       参数名字	数据类型	是否必须	参数来源
     *                       uId	String	是	uId为游戏侧用户ID，如果游戏侧使用登录返回的userId作为用户ID，则uId的值就填userId，如使用登录返回的username作为用户ID，则uId为username
     *                       roleId	String	是	游戏角色id
     *                       money	Integer	是	成功充值金额(单位：元)
     *                       serverId	String	是	服务器分区Id
     *                       attach	String	是	扩展字段，填游戏订单号
     *                       appKey	String	是	对接提供数据的
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(ExampleConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(ExampleConfig.PAY_KEY);

        long time = System.currentTimeMillis() / 1000;

        String channelId = orderData.getString("channelId");
        String gameKey = orderData.getString("gameKey");
        String channelUid = orderData.getString("uid");
        String username = orderData.getString("username");
        String userRoleId = orderData.getString("userRoleId");
        String userRoleName = orderData.getString("userRoleName");

        String serverId = orderData.getString("serverId");
        String userServer = orderData.getString("userServer");
        String userLevel = orderData.getString("userLevel");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String amount = orderData.getString("amount");

        String count = orderData.getString("count");
        String quantifier = orderData.getString("quantifier");
        String subject = orderData.getString("subject");
        String desc = orderData.getString("desc");
        String callbackUrl = orderData.getString("callbackUrl");

        String extrasParams = orderData.getString("extrasParams");
        String goodsId = orderData.getString("goodsId");
        String channelToken = orderData.getString("channelToken");

        /*
         * 加密串
         * 参数名字	数据类型	是否必须	参数来源
         * uId	    String	是	    uId为游戏侧用户ID，如果游戏侧使用登录返回的userId作为用户ID，则uId的值就填userId，如使用登录返回的username作为用户ID，则uId为username
         * roleId	String	是	    游戏角色id
         * money	Integer	是	    成功充值金额(单位：元)
         * serverId	String	是	    服务器分区Id
         * attach	String	是	    扩展字段，填游戏订单号
         * appKey	String	是	    对接提供数据的
         * attach=353535&money=100&roleId=111111&serverId=123&uId=2441629-1_29&43f9370bcfe40ee330ee1dc38943e471
         * */
        StringBuilder param = new StringBuilder();
        super.addParam(param, "attach", cpOrderNo);
        super.addParamAnd(param, "money", amount);
        super.addParamAnd(param, "roleId", userRoleId);
        super.addParamAnd(param, "serverId", serverId);
        super.addParamAnd(param, "uId", channelUid);
        super.addParamAnd(param, "", payKey);

        log.info("param = " + param.toString());

        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayInfo serverSign = " + serverSign);
        /*
         * var params = {
         *     amount: 1,                        //金额(元，最低一元)
         *     roleid: 1,                        //游戏角色id
         *     serverid: 1,                      //充值服务器id
         *     productname: '充值的游戏名称',       //充值的游戏名称
         *     attach: 'xxxx',                   //游戏方扩展参数
         *     productdesc: '商品描述',          //充值的商品描述
         *     payCpSign: 'xxxx',                // 充值加签处理 详情请看下面充值加签处理 md5加密
         *     cpUid: 'xxxx'                     // 游戏帐号ID
         *   }
         */
        // 渠道订单数据
        JSONObject data = new JSONObject();
        data.put("amount", amount);
        data.put("roleid", userRoleId);
        data.put("serverid", serverId);
        data.put("productname", subject);
        data.put("attach", cpOrderNo);
        data.put("productdesc", desc);
        data.put("payCpSign", serverSign);
        data.put("cpUid", channelUid);


        log.info("channelPayInfo data: " + data);
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
        String channelGameId = configMap.get(appId).getString(YouXiFunConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(YouXiFunConfig.COMMON_KEY);

        // 加密串
        Set<String> keySet = new LinkedHashSet<>(parameterMap.keySet());
        keySet.remove("sign");
        keySet.remove("userId");

        StringBuilder param = new StringBuilder();
        boolean first = false;
        for (String key : keySet) {
            String value = parameterMap.get(key);
            if (!first) {
                super.addParam(param, key, value);
                first = true;
            } else {
                super.addParamAnd(param, key, value);
            }
        }
        super.addParamAnd(param, "", payKey);

        log.info("param = " + param.toString());

        // 签名验证
        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);


        if (!sign.equals(serverSign)) {
            return false;
        }
        String cpOrderNo = parameterMap.get("attach");
        String channelOrder = parameterMap.get("orderid");
        String amount = parameterMap.get("amount");

        // 渠道订单赋值
        setChannelOrder(channelOrderNo, "", cpOrderNo, channelOrder, amount);
        return true;
    }
}

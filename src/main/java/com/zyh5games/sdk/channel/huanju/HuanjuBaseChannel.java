package com.zyh5games.sdk.channel.huanju;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 欢聚
 * 未接的 todo
 * 1.游戏礼包领取接口
 * 2.游戏登录被顶
 * 3.游戏服务器列表查询
 * @author song minghua
 * @date 2020/5/21
 */
@Component("14")
public class HuanjuBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(HuanjuBaseChannel.class);

    HuanjuBaseChannel() {
        channelId = ChannelId.H5_HUANJU;
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("https://issue.hjygame.com/sdk/cy.sdk.js");
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
        channelData.put("name", "HuanJuH5");
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
     *                 接收参数(CGI) 类型         必选  参于加密      说明
     *                 gameId	    int		    是	是	        产品合作ID
     *                 uid	        string		是	是	        用户UID (唯一) 我方用户的UID
     *                 userName	    string		是	是	        用户名（urlencode）
     *                 time	        int		    是	是	        当前时间unix时间戳(服务端会判断时间是否超过配置时间)
     *                 avatar	    String		否	否	        用户头像
     *                 userSex	    String		否	否	        玩家性别[no 末设置 male 男 famale 女]
     *                 fromUid	    string		否	否	        来自分享者的UID
     *                 isAdult	    string		是	否	        玩家是否成年[no未成年，yes成年]
     *                 sign	        string		是	否	        加密串
     *                 signType	    string		是	否	        固定md5
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String[] mustKey = {"gameId", "uid", "userName", "time", "isAdult", "sign", "signType"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        //  id=1&avatar=http%3A%2F%2Fh5.6816.com%2Fstatic%2Fattachment%2Fuser%2F20160816%2F1471334322441376.png&gameId=113&signType=md5&time=1475042060&uid=29923&userName=dreamfly_1981&userSex=male&sign=6a3f16124a0c641082c17a438d1323a8
        //  Md5(gameId=113&time=1475042196&uid=29923&userName=dreamfly_1981&key=testkey)
        String commonKey = configMap.get(appId).getString(HuanJuConfig.COMMON_KEY);

        String avatar = map.get("avatar")[0];
        String fromUid = map.containsKey("fromUid") ? map.get("fromUid")[0] : "";
        String gameId = map.get("gameId")[0];
        String isAdult = map.containsKey("isAdult") ? map.get("isAdult")[0] : "";
        String sign = map.get("sign")[0];
        String signType = map.get("signType")[0];
        String time = map.get("time")[0];
        String uid = map.get("uid")[0];
        String userName = map.get("userName")[0];
        String userSex = map.containsKey("userSex") ? map.get("userSex")[0] : "";

        //可能要urldecode
        StringBuilder param = new StringBuilder();
        super.addParam(param, "gameId", gameId);
        super.addParamAnd(param, "time", time);
        super.addParamAnd(param, "userName", userName);
        super.addParamAnd(param, "uid", uid);
        super.addParamAnd(param, "key", commonKey);

        System.out.println("param = " + param.toString());

        String serverSign = MD5Util.md5(param.toString());
        log.info("channelLogin serverSign = " + serverSign);
        log.info("channelLogin sign = " + sign);

        // todo 参数校验有问题 周一重发参数
        System.out.println("channelLogin serverSign = " + serverSign);
        System.out.println("channelLogin sign = " + sign);
        if (!sign.equals(serverSign)) {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        } else {
            setUserData(userData, uid, userName, String.valueOf(channelId), "");
            return true;
        }
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(HuanJuConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(HuanJuConfig.PAY_KEY);

        String cpOrderNo = orderData.getString("cpOrderNo");
        String channelUid = orderData.getString("uid");
        String serverId = orderData.getString("serverId");
        String userRoleId = orderData.getString("userRoleId");
        String goodsId = orderData.getString("goodsId");
        String goodsName = orderData.getString("subject");
        String money = orderData.getString("amount");
        String ext = orderData.getString("extrasParams");

        String time = String.valueOf(System.currentTimeMillis() / 1000);

        /*
            接收参数(CGI)   类型  必选  参于加密    说明
            gameId		int	    是	是	        产品合作ID
            uid		    string	是	是	        用户UID (唯一) 我方用户的UID
            time		int	    是	是	        当前时间unix时间戳(服务端会判断时间是否超过配置时间)
            server		string	是	是	        支付时的游戏区服
            role		string	是	是	        支付时角色信息
            goodsId		string	是	是	        商品ID（ 没有的话。可以写1，但是不能为空）
            goodsName	string	是	是	        商品名,如：游戏币
            money		decimal	是	是	        商品价格(元) 例：1.00
            cpOrderId	string	是	是	        游戏订单号(回传时原样返回)
            ext		    string	否	否	        额外透传参数(原样返回)
            sign		string	是	否	        签名（小写）
            signType	string	是	否	        固定md5

            Md5(cpOrderId=1475049097&gameId=113&goodsId=1&goodsName=测试商品&money=1&role=1&server=1
            &time=1475049097&uid=6298253&key=testpaykey)
            按照参数名从小到大排序(PHP语言中可用ksort进行排序),Key为后台的支付签名.
        */
        StringBuilder param = new StringBuilder();
        super.addParam(param, "cpOrderId", cpOrderNo);
        super.addParamAnd(param, "gameId", channelGameId);
        super.addParamAnd(param, "goodsId", goodsId);
        super.addParamAnd(param, "goodsName", goodsName);
        super.addParamAnd(param, "money", money);
        super.addParamAnd(param, "role", userRoleId);
        super.addParamAnd(param, "server", serverId);
        super.addParamAnd(param, "time", time);
        super.addParamAnd(param, "uid", channelUid);
        super.addParamAnd(param, "key", payKey);

        String sign = MD5Util.md5(param.toString());

        JSONObject data = new JSONObject();
        data.put("gameId", channelGameId);
        data.put("uid", channelUid);
        data.put("time", time);
        data.put("server", serverId);
        data.put("role", userRoleId);
        data.put("goodsId", goodsId);
        data.put("goodsName", goodsName);
        data.put("money", money);
        data.put("cpOrderId", cpOrderNo);
        data.put("ext", ext);
        data.put("signType", "md5");
        data.put("sign", sign);

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
        String payKey = configMap.get(appId).getString(HuanJuConfig.PAY_KEY);

        String[] mustKey = {"status", "cpOrderId", "orderId", "uid", "userName", "money", "gameId", "goodsId",
                "goodsName", "server", "role", "time", "sign", "signType"};

        /*
            接收参数(CGI)	类型	        必选	参于加密	说明
            status		    String	    是	是	    订单状态。“success”为支付成功
            cpOrderId		String	    是	是	    cp游戏订单号。
            orderId		    String	    是	是	    欢聚游微游戏订单号
            uid		        string	    是	是	    欢聚游微游戏用户的uid
            userName		string	    是	是	    欢聚游微游戏的用户名
            money		    decimal	    是	是	    支付钱数(元),保留2位小数
            gameId		    String	    是	是	    游戏的id
            goodsId		    String	    是	是	    商品ID
            goodsName		String	    是	是	    商品名
            server		    String	    是	是	    支付的游戏服
            role		    String	    是	是	    支付时角色信息,
            time		    int	        是  是	    当前时间unix时间戳
            ext		        String(200)	否	否	    额外透传参数(原样返回)
            sign		    string	    是	否	    加密串
            signType		string	    是	否	    固定md5

            Md5(cpOrderId=1475049097&gameId=11&goodsId=1&goodsName=测试商品&money=1.00&orderId=201705231751455104
            &role=1&server=1&status=success&time=1475049098&uid=6298253&userName=yx6298253&key=testpaykey)
            按照参数名从小到大排序(PHP语言中可用ksort进行排序),Key为后台的支付签名.
        */

        StringBuilder param = new StringBuilder();
        super.addParam(param, "cpOrderId", parameterMap.get("cpOrderId"));
        super.addParamAnd(param, "gameId", parameterMap.get("gameId"));
        super.addParamAnd(param, "goodsId", parameterMap.get("goodsId"));
        super.addParamAnd(param, "goodsName", parameterMap.get("goodsName"));
        super.addParamAnd(param, "money", parameterMap.get("money"));
        super.addParamAnd(param, "orderId", parameterMap.get("orderId"));
        super.addParamAnd(param, "role", parameterMap.get("role"));
        super.addParamAnd(param, "server", parameterMap.get("server"));
        super.addParamAnd(param, "status", parameterMap.get("status"));
        super.addParamAnd(param, "time", parameterMap.get("time"));
        super.addParamAnd(param, "uid", parameterMap.get("uid"));
        super.addParamAnd(param, "userName", parameterMap.get("userName"));
        super.addParamAnd(param, "key", payKey);

        System.out.println("channelPayCallback : " + param.toString());

        String serverSign = MD5Util.md5(param.toString());
        String sign = parameterMap.get("sign");

        System.out.println("channelPayCallback sign: " + serverSign);
        System.out.println("channelPayCallback sign: " + sign);

        if (sign.equals(serverSign)) {
            setChannelOrder(channelOrderNo, "",
                    parameterMap.get("cpOrderId"), parameterMap.get("orderId"), parameterMap.get("money"));
            return true;
        }

        return false;
    }
}

package com.ssm.promotion.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.common.Result;
import com.ssm.promotion.core.common.ResultGenerator;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.jedis.JedisRechargeCache;
import com.ssm.promotion.core.sdk.AccountWorker;
import com.ssm.promotion.core.sdk.LoginToken;
import com.ssm.promotion.core.sdk.LoginWorker;
import com.ssm.promotion.core.util.ResponseUtil;
import com.ssm.promotion.core.util.StringUtils;
import com.ssm.promotion.core.util.UtilG;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 用来做假数据
 * 1.注册
 * 2.登录
 * 3.支付
 *
 * @author song minghua
 * @date 2019/12/3
 */
@Controller
@RequestMapping("/ttt")
public class TtController {
    private static final Logger log = Logger.getLogger(TtController.class);
    @Autowired
    JedisRechargeCache cache;
    @Resource
    LoginWorker loginWorker;
    @Resource
    AccountWorker accountWorker;
    @Autowired
    private HttpServletRequest request;


    /**
     * 注册账号
     * SDK 登录接口
     *
     * @param map username      指悦账户名
     *            pwd           指悦账号密码
     *            phone         手机号
     *            deviceCode
     *            imei
     *            channelId     渠道id
     *            channelUid    渠道账号id
     *            channelUname  渠道账号名称
     *            channelUnick  渠道账号昵称
     *            addparm       额外参数
     *            appId         游戏id
     *            auto          是否渠道自动注册
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result sdkRegister(@RequestBody Map<String, String> map) throws Exception {
        System.out.println("register:" + map.toString());

        //参数校验
        for (String key : map.keySet()) {
            if (map.get(key) == null) {
                if (!key.equals("phone") || !key.equals("deviceCode") || !key.equals("imei")) {
                    JSONObject result = new JSONObject();
                    result.put("err", "参数非法");
                    return ResultGenerator.genSuccessResult(result);
                }
            }
        }
        //获取ip
        map.put("ip", UtilG.getIpAddress(request));
        //注册账号
        JSONObject result = accountWorker.reqRegister(map);
        if (result.get("status").equals("1")) {
            //注册成功
            //存到redis
            cache.register(map);
        }
        return ResultGenerator.genSuccessResult(result);
    }

    /**
     * 客户端先请求
     * SDK 登录接口
     *
     * @param map appId             指悦平台创建的游戏ID，appId      请使用URLEncoder编码
     *            isChannel
     *            name
     *            pwd
     *            channelId         平台标示的渠道SDK ID       请使用URLEncoder编码
     *            channelUserId     渠道SDK标示的用户ID
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public void sdkLogin(@RequestBody Map<String, Object> map,
                         HttpServletResponse response) throws Exception {

        int appid = Integer.parseInt(map.get("appId").toString());
        int channelid = Integer.parseInt(map.get("channelId").toString());
        boolean isChannel = Boolean.parseBoolean(map.get("isChannel").toString());

        JSONObject result = new JSONObject();

        do {
            Account account = accountWorker.getAccount(map);
            if (account == null) {
                if (isChannel) {
                    //渠道账号自动注册并登录
                    result.put("err", "指悦账号不存在，请前往注册！");
                    result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
                    break;
                }
            }
            int accountId = account.getId();
            if (!loginWorker.isWhiteCanLogin(accountId, "")) {

            }

            if (!loginWorker.isSpCanLogin(appid, channelid)) {

            }
            //获取账号成功 发送token
            String loginToken = loginWorker.getGameInfo(accountId, appid);
            String sign = StringUtils.getBASE64(appid + loginToken + accountId);

            result.put("appid", appid);
            result.put("token", loginToken);
            result.put("uid", accountId);
            result.put("sign", sign);
            result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        } while (false);

        ResponseUtil.write(response, result);

        System.out.println("request: ttt/login , map: " + result.toString());

    }

    /**
     * 游戏登录服务器
     * 验证账号信息
     *
     * @param appId 指悦平台创建的游戏ID，appId
     *              请使用URLEncoder编码
     * @param token 随机字符串
     *              请使用URLEncoder编码
     * @param uid   玩家指悦账号id
     *              请使用URLEncoder编码
     * @param sign  签名数据：md5 (appId+token+uid)
     *              请使用URLEncoder编码
     * @return 接口返回：表示用户已登录，其他表示未登陆。
     * 0 验证通过
     * 1 token错误
     * 2签名错误
     */
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    @ResponseBody
    public void sdkLoginCheck(String appId,
                              String token,
                              String uid,
                              String sign,
                              HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();
        do {
            System.out.println(appId);
            System.out.println(token);
            System.out.println(uid);
            System.out.println(sign);


            if (appId == null || token == null || uid == null || sign == null) {
                result.put("status", Constants.SDK_PARAM);
                break;
            }
            int accountId = Integer.parseInt(uid);

            if (!LoginToken.check(accountId, token)) {
                //token 非法
                result.put("status", Constants.SDK_LOGIN_FAIL_TOKEN);
                break;
            }
            String tmpSign = StringUtils.getBASE64(appId + token + accountId);
            if (!tmpSign.equals(sign)) {
                result.put("status", Constants.SDK_LOGIN_FAIL_SIGN);
                break;
            }
            result.put("status", Constants.SDK_LOGIN_SUCCESS);
            result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        } while (false);

        ResponseUtil.write(response, result);
    }

    /**
     * 设置角色基本数据
     *
     * @param context   上下文Activity
     * @param roleId    角色唯一标识
     * @param roleName  角色名
     * @param roleLevel 角色的等级
     * @param zoneId    角色所在区域唯一标识
     * @param zoneName  角色所在区域名称
     */
    @RequestMapping(value = "/updateroledata", method = RequestMethod.GET)
    public void sdkUpdateRoledata(String context,
                                  String roleId,
                                  String roleName,
                                  String roleLevel,
                                  String zoneId,
                                  String zoneName) {


    }

    /**
     * 设置角色基本数据
     * 1.创角打点
     * 2.新手指引打点
     */
    @RequestMapping(value = "/setdata", method = RequestMethod.GET)
    public void sdkSetData() {

        JSONObject roleInfo = new JSONObject();
        //当前登录的玩家角色ID，必须为数字
        roleInfo.put("roleId", "1");
        //当前登录的玩家角色名，不能为空，不能为null
        roleInfo.put("roleName", "猎人");
        //当前登录的玩家角色等级，必须为数字，且不能为0，若无，传入1
        roleInfo.put("roleLevel", "100");
        //当前登录的游戏区服ID，必须为数字，且不能为0，若无，传入1
        roleInfo.put("zoneId", "1");
        //当前登录的游戏区服名称，不能为空，不能为null
        roleInfo.put("zoneName", "阿狸一区");
        //用户游戏币余额，必须为数字，若无，传入0
        roleInfo.put("balance", "0");
        //当前用户VIP等级，必须为数字，若无，传入1
        roleInfo.put("vip", "1");
        //当前角色所属帮派，不能为空，不能为null，若无，传入“无帮派”
        roleInfo.put("partyName", "无帮派");
        //单位为秒，创建角色的时间
        roleInfo.put("roleCTime", "21322222");
        //单位为秒，角色等级变化时间
        roleInfo.put("roleLevelMTime", "54456556");
        //createrole 创建新角色时调用
        //levelup 玩家升级角色时调用
        //enterServer 选择服务器进入时调用
        String key;

    }


    /**
     * 退出接口
     * 指悦账号
     * ：退出游戏
     *
     * @param appId     游戏id
     * @param serverId  区服id
     * @param channelId 渠道id
     * @param roleId    角色id
     */
    @RequestMapping(value = "/exit", method = RequestMethod.GET)
    public void sdkExit(String appId,
                        String serverId,
                        String channelId,
                        String roleId) {
        //查询redis
        //查询数据库

    }

    /**
     * 定额计费接口
     *
     * @param context           上下文Activity
     * @param unitPrice         游戏道具价格，单位为人民币分
     * @param itemName          虚拟货币名称(商品名称)
     *                          注意：虚拟币名称在游戏内一定要确保唯一性！！！！不能出现多个虚拟币名称相同。
     * @param count             用户选择购买道具界面的默认道具数量。（总价为 count*unitPrice）
     * @param callBackInfo      由游戏开发者定义传入的字符串，会与支付结果一同发送给游戏服务器，游戏服务器可通过该字段判断交易的详细内容（金额角色等）
     * @param callBackUrl       将支付结果通知给游戏服务器时的通知地址url，交易结束后，系统会向该url发送http请求，通知交易的结果金额callbackInfo等信息
     *                          注意：这里的回调地址可以填也可以为空字串，如果填了则以这里的回调地址为主，如果为空则以易接开发者中心设置的回调地址为准。
     * @param payResultListener 支付回调接口
     */
    @RequestMapping(value = "/pay", method = RequestMethod.GET)
    public void sdkPay(String context,
                       String unitPrice,
                       String itemName,
                       String count,
                       String callBackInfo,
                       String callBackUrl,
                       String payResultListener) {

    }

    /**
     * 充值校验
     */
    @RequestMapping(value = "/paycheck", method = RequestMethod.GET)
    public void sdkPayCheck(String context,
                            String unitPrice,
                            String itemName,
                            String count,
                            String callBackInfo,
                            String callBackUrl,
                            String payResultListener) {

    }


}

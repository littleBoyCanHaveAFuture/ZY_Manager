package com.ssm.promotion.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.jedis.JedisRechargeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

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
@RequestMapping("/test")
public class testController {
    @Autowired
    JedisRechargeCache cache;
    @Autowired
    HttpServletRequest request;

    /**
     * 注册账号
     * SDK 登录接口
     *
     * @param appId     易接平台创建的游戏ID，appId
     *                  请使用URLEncoder编码
     * @param channelId 易接平台标示的渠道SDK ID
     *                  请使用URLEncoder编码
     * @param userId    渠道SDK标示的用户ID
     *                  请使用URLEncoder编码
     * @param token     渠道SDK登录完成后的Session ID。
     *                  请使用URLEncoder编码
     */
    @RequestMapping(value = "register", method = RequestMethod.GET)
    public void sdkRegister(String appId,
                            String channelId,
                            String userId,
                            String token) {


    }

    /**
     * 客户端先请求
     * SDK 登录接口
     *
     * @param appId     易接平台创建的游戏ID，appId
     *                  请使用URLEncoder编码
     * @param channelId 易接平台标示的渠道SDK ID
     *                  请使用URLEncoder编码
     * @param userId    渠道SDK标示的用户ID
     *                  请使用URLEncoder编码
     * @param token     渠道SDK登录完成后的Session ID。
     *                  请使用URLEncoder编码
     */
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public void sdkLogin(String appId,
                         String channelId,
                         String userId,
                         String token) {


    }

    /**
     * 游戏登录服务器
     * 验证账号信息
     */
    @RequestMapping(value = "check", method = RequestMethod.GET)
    public void sdkLoginCheck() {


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
    @RequestMapping(value = "updateroledata", method = RequestMethod.GET)
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
    @RequestMapping(value = "setdata", method = RequestMethod.GET)
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
        // enterServer 选择服务器进入时调用
        String key;

    }


    /**
     * 退出接口
     */
    @RequestMapping(value = "updateroledata", method = RequestMethod.GET)

    public void sdkExit(String context,
                        String roleId,
                        String roleName,
                        String roleLevel,
                        String zoneId,
                        String zoneName) {

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
    @RequestMapping(value = "updateroledata", method = RequestMethod.GET)
    public void sdkPay(String context,
                       String unitPrice,
                       String itemName,
                       String count,
                       String callBackInfo,
                       String callBackUrl,
                       String payResultListener) {

    }
}

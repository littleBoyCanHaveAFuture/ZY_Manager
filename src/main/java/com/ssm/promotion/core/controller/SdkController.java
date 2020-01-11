package com.ssm.promotion.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.ssm.promotion.core.entity.GameName;
import com.ssm.promotion.core.entity.GameSp;
import com.ssm.promotion.core.jedis.JedisRechargeCache;
import com.ssm.promotion.core.sdk.AccountWorker;
import com.ssm.promotion.core.sdk.GameRoleWorker;
import com.ssm.promotion.core.sdk.LoginWorker;
import com.ssm.promotion.core.sdk.UOrderManager;
import com.ssm.promotion.core.service.*;
import com.ssm.promotion.core.util.ResponseUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/11/27
 */
@Controller
@RequestMapping("/webGame")
public class SdkController {
    @Autowired
    JedisRechargeCache cache;
    @Resource
    LoginWorker loginWorker;
    @Resource
    AccountWorker accountWorker;
    @Resource
    GameRoleWorker gameRoleWorker;
    @Resource
    private ServerListService serverService;
    @Resource
    private GameNameService gameService;
    @Resource
    private GameSpService gameSpService;
    @Resource
    private SpService spService;
    @Resource
    private UserService userService;
    @Autowired
    private HttpServletRequest request;
    @Resource
    private UOrderManager orderManager;

    /**
     * 初始化游戏 、
     * 1.获取 需要的js文件
     * 2.获取 渠道秘钥
     *
     * @param GameId  游戏id
     * @param GameKey 游戏秘钥
     * @param SpId    渠道id
     */
    @RequestMapping(value = "/initApi", method = RequestMethod.GET)
    @ResponseBody
    public void initApi(Integer GameId,
                        String GameKey,
                        Integer SpId,
                        HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();

        Map<String, Object> map = new HashMap<>();
        map.put("gameId", GameId);
        map.put("spId", SpId);

        do {
            List<GameSp> gameSpList = gameSpService.selectGameSp(map, -1);
            if (gameSpList == null || gameSpList.size() != 1) {
                System.out.println("GameSp err");
                result.put("state", false);
                result.put("message", "游戏渠道不存在！");
                break;
            }
            List<GameName> gameList = gameService.getGameList(map, -1);
            if (gameList == null || gameList.size() != 1) {
                System.out.println("GameName err");
                result.put("state", false);
                result.put("message", "游戏不存在！");
                break;
            }

            GameSp gameSp = gameSpList.get(0);
            GameName sp = gameList.get(0);
            if (!GameKey.equals(sp.getSecertKey())) {
                System.out.println("GameName err");
                result.put("state", false);
                result.put("message", "游戏不存在！");
                break;
            }

            JSONObject channelParams = new JSONObject();
            JSONObject channelPlatform = new JSONObject();
            JSONArray libUrl = new JSONArray();

            channelParams.put("login_key", gameSp.getLoginKey());
            channelParams.put("pay_key", gameSp.getPayKey());
            channelParams.put("send_key", gameSp.getSendKey());

            libUrl.add("http://111.231.244.198:8080/try/login/md5.js");
            libUrl.add("http://111.231.244.198:8080/try/login/jquery-3.4.1.min.js");

            channelPlatform.put("libUrl", libUrl);
            channelPlatform.put("playUrl", "");

            result.put("channelParams", channelParams);
            result.put("channelPlatform", channelPlatform);
        } while (false);

        ResponseUtil.write(response, result);
    }
}

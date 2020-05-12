package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.entity.ChannelConfig;
import com.ssm.promotion.core.entity.GameNew;
import com.ssm.promotion.core.entity.PageBean;
import com.ssm.promotion.core.entity.Sp;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.service.ChannelConfigService;
import com.ssm.promotion.core.service.GameNewService;
import com.ssm.promotion.core.service.SpService;
import com.ssm.promotion.core.util.RandomUtil;
import com.ssm.promotion.core.util.ResponseUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Controller
@RequestMapping("/game")
public class GameController {
    private static final Logger log = Logger.getLogger(GameController.class);
    @Autowired
    jedisRechargeCache cache;
    @Resource
    private GameNewService gameNewService;
    @Resource
    private ChannelConfigService channelConfigService;
    @Resource
    private SpService spService;
    @Autowired
    private HttpServletRequest request;

    private Integer getUserId() {
        //可以设置缓存 redis 登录时设置过期时间 24小时 todo
        HttpSession session = request.getSession();
        return (Integer) session.getAttribute("userId");
    }

    /**
     * 查询游戏列表
     */
    @RequestMapping(value = "/getGameList", method = RequestMethod.GET)
    public void getGameList(@RequestParam(value = "page", required = false) String page,
                            @RequestParam(value = "rows", required = false) String rows,
                            Integer gameId,
                            String name,
                            HttpServletResponse response) throws Exception {
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        JSONObject result = new JSONObject();

        Map<String, Object> map = new HashMap<>(5);
        if (page != null && rows != null) {
            PageBean pageBean = new PageBean(Integer.parseInt(page),
                    Integer.parseInt(rows));
            map.put("start", pageBean.getStart());
            map.put("size", pageBean.getPageSize());
        }
        map.put("ownerId", userId);
        if (gameId != null && gameId != -1) {
            map.put("appId", gameId);
        }

        map.put("appName", name);

        //查询满足条件的游戏
        List<GameNew> gameList = gameNewService.getGameList(map, userId);
        Integer nums = gameNewService.getCountGame(map, userId);
        //满足条件的总数
        if (gameList != null && gameList.size() > 0) {
            JSONArray jsonArray = JSONArray.fromObject(gameList);
            result.put("rows", jsonArray);
        }

        result.put("total", nums);
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);

        log.info("request: server/getGameList , map: " + result.toString());
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/addGame", method = RequestMethod.POST)
    @ResponseBody
    public void addServer(@RequestBody GameNew game, HttpServletResponse response) throws Exception {
        log.info("start: /game/addGame " + game.toString());
        Integer userId = getUserId();

        JSONObject result = new JSONObject();
        do {
            if (userId == null) {
                result.put("resultCode", Constants.RESULT_CODE_SERVER_RELOGIN);
                break;
            }
            if (game.getAppName() == null || game.getType() == null || game.getGenres() == null || game.getIpType() == null || game.getDirection() == null) {
                result.put("reason", "添加失败 参数为空");
                break;
            }
            if (game.getType() < 0 || game.getType() > 4) {
                result.put("reason", "添加失败 游戏类型非法");
                break;
            }
            if (game.getGenres() < 0 || game.getGenres() > 19) {
                result.put("reason", "添加失败 游戏类别非法");
                break;
            }
            if (game.getTheme() < 0 || game.getTheme() > 11) {
                result.put("reason", "添加失败 题材类别非法");
                break;
            }
            if (game.getDirection() < 0 || game.getDirection() > 1) {
                result.put("reason", "添加失败 屏幕方向非法");
                break;
            }
            if (game.getDescription().length() > 500) {
                result.put("reason", "添加失败 游戏描述过长");
                break;
            }
            game.setOwnerId(userId);
            game.setSecertKey(RandomUtil.rndSecertKey());
            int res = gameNewService.addGame(game, userId);

            log.info("request: game/save , " + game.toString());
            System.out.println("request: game/save , " + game.toString());

            if (res > 0) {
                result.put("appid", game.getAppId());
            }
        } while (false);

        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/updateGame", method = RequestMethod.POST)
    @ResponseBody
    public void updateServer(@RequestBody GameNew game, HttpServletResponse response) throws Exception {
        log.info("start: /game/addGame " + game.toString());
        Integer userId = getUserId();

        JSONObject result = new JSONObject();
        do {
            if (userId == null) {
                result.put("resultCode", Constants.RESULT_CODE_SERVER_RELOGIN);
                break;
            }
            if (game.getAppName() == null || game.getType() == null || game.getGenres() == null || game.getIpType() == null || game.getDirection() == null) {
                result.put("reason", "添加失败 参数为空");
                break;
            }
            if (game.getType() < 0 || game.getType() > 4) {
                result.put("reason", "添加失败 游戏类型非法");
                break;
            }
            if (game.getGenres() < 0 || game.getGenres() > 19) {
                result.put("reason", "添加失败 游戏类别非法");
                break;
            }
            if (game.getTheme() < 0 || game.getTheme() > 11) {
                result.put("reason", "添加失败 题材类别非法");
                break;
            }
            if (game.getDirection() < 0 || game.getDirection() > 1) {
                result.put("reason", "添加失败 屏幕方向非法");
                break;
            }
            if (game.getDescription().length() > 500) {
                result.put("reason", "添加失败 游戏描述过长");
                break;
            }
            game.setOwnerId(-1);
            int res = gameNewService.updateGame(game, userId);

            log.info("request: game/updateGame , " + game.toString());
            System.out.println("request: game/updateGame , " + game.toString());

            if (res > 0) {
                result.put("appid", game.getAppId());
            }
        } while (false);

        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);
    }

    /**
     * 获取 游戏渠道配置
     */
    @RequestMapping(value = "/channelConfig", method = RequestMethod.POST)
    @ResponseBody
    public void loadGameChannelConfig(@RequestParam(value = "appId", required = false) Integer appId,
                                      @RequestParam(value = "channelId", required = false) Integer channelId,
                                      HttpServletResponse response) throws Exception {
        log.info("start: /game/channelConfig " + channelId);
        Integer userId = getUserId();
        JSONObject result = new JSONObject();
        if (userId == null) {
            result.put("resultCode", Constants.RESULT_CODE_SERVER_RELOGIN);
            ResponseUtil.write(response, result);
            return;
        }
        ChannelConfig config = channelConfigService.selectConfig(appId, channelId, -1);
        Sp sp = spService.getSp(channelId, -1);
        if (sp == null) {
            result.put("result", "fail");
            ResponseUtil.write(response, result);
            return;
        }
        if (sp.getConfig() == null || sp.getConfig().isEmpty()) {
            result.put("result", "fail");
            ResponseUtil.write(response, result);
            return;
        }

        JSONArray ccArray = JSONArray.fromObject(sp.getConfig());

        if (config == null) {
            config = new ChannelConfig();

            config.setAppId(appId);
            config.setChannelId(channelId);

            config.setChannelCallbackUrl("http://cn.soeasysdk.com/ret/{channel_code}/{sdkindex}/{appid}");
            config.setH5Url("https://source.huojianos.com/g1/game/index_zy_suyi.html");

            config.setChannelSdkName(sp.getName());
            config.setChannelSdkCode(sp.getCode());
            config.setChannelConfigKey(sp.getConfig());

            JSONObject jsonObject = new JSONObject();

            for (int i = 0; i < ccArray.size(); i++) {
                //3、把里面的对象转化为JSONObject
                JSONObject job = ccArray.getJSONObject(i);
                // 4、把里面想要的参数一个个用.属性名的方式获取到
                String name = job.getString("name");
                jsonObject.put(name, "");
            }
            config.setConfigKey(jsonObject.toString());

            int index = channelConfigService.insertConfig(config, -1);

            String url = "http://cn.soeasysdk.com/ret/" + sp.getCode() + "/" + index + "/" + appId;
            config.setChannelCallbackUrl(url);
            channelConfigService.updateConfig(config, -1);
        }
        config.setChannelSdkName(sp.getName());
        config.setChannelSdkCode(sp.getCode());
        config.setChannelConfigKey(sp.getConfig());

        result.put("sdkindex", config.getId());
        result.put("app_id", config.getAppId());
        result.put("channel_id", config.getChannelId());
        result.put("config_key", config.getConfigKey());
        result.put("channel_sdk_name", config.getChannelSdkName());
        result.put("channel_sdk_code", config.getChannelSdkCode());
        result.put("channel_config_key", config.getChannelConfigKey());
        result.put("channel_callback_url", config.getChannelCallbackUrl());
        result.put("h5_url", config.getH5Url());

        ResponseUtil.write(response, result);
    }

    /**
     * 获取 游戏渠道配置
     */
    @RequestMapping(value = "/updateChannelConfig", method = RequestMethod.POST)
    @ResponseBody
    public void updateChannelConfig(@RequestParam(value = "app_id", required = false) Integer appId,
                                    @RequestParam(value = "channel_id", required = false) Integer channelId,
                                    @RequestParam(value = "config_key", required = false) String configKey,
                                    @RequestParam(value = "h5_url", required = false) String h5Url,
                                    HttpServletResponse response) throws Exception {
        log.info("start: /game/setConfig " + channelId);
        Integer userId = getUserId();
        JSONObject result = new JSONObject();
        if (userId == null) {
            result.put("resultCode", Constants.RESULT_CODE_SERVER_RELOGIN);
            ResponseUtil.write(response, result);
            return;
        }
        ChannelConfig config = channelConfigService.selectConfig(appId, channelId, -1);
        Sp sp = spService.getSp(channelId, -1);

        if (sp == null || config == null) {
            result.put("result", "fail");
            ResponseUtil.write(response, result);
            return;
        }
        JSONObject jsonObject = JSONObject.fromObject(configKey);
        if (jsonObject.isEmpty()) {
            result.put("result", "fail");
            ResponseUtil.write(response, result);
            return;
        }
        JSONArray ccArray = JSONArray.fromObject(sp.getConfig());
        for (int i = 0; i < ccArray.size(); i++) {
            JSONObject job = ccArray.getJSONObject(i);
            String name = job.getString("name");
            if (jsonObject.containsKey(name)) {
                break;
            }
        }

        config.setConfigKey(configKey);
        config.setH5Url(h5Url);
        channelConfigService.updateConfig(config, -1);

        ResponseUtil.write(response, result);
    }

}

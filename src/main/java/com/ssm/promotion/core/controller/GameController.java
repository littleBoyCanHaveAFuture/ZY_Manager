package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.entity.GameNew;
import com.ssm.promotion.core.entity.PageBean;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.service.GameNewService;
import com.ssm.promotion.core.service.ServerListService;
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
    private ServerListService serverService;
    @Resource
    private GameNewService gameNewService;
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
     * 添加
     */
    @RequestMapping(value = "/channelConfig", method = RequestMethod.POST)
    @ResponseBody
    public void loadGameChannelConfig(@RequestBody GameNew game, HttpServletResponse response) throws Exception {
        log.info("start: /game/addGame " + game.toString());
        Integer userId = getUserId();

        JSONObject result = new JSONObject();
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);
    }
}
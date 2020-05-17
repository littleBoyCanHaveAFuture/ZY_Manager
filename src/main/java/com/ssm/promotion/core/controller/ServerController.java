package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.common.Result;
import com.ssm.promotion.core.common.ResultGenerator;
import com.ssm.promotion.core.entity.*;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.service.*;
import com.ssm.promotion.core.util.ResponseUtil;
import com.ssm.promotion.core.util.enums.ManagerType;
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
import java.util.*;

/**
 * @author Administrator
 */
@Controller
@RequestMapping("/server")
public class ServerController {
    private static final Logger log = Logger.getLogger(ServerController.class);
    @Autowired
    jedisRechargeCache cache;
    @Resource
    private ServerListService serverService;
    @Resource
    private GameNewService gameNewService;
    @Resource
    private GameSpService gameSpService;
    @Resource
    private SpService spService;
    @Resource
    private UserService userService;
    @Resource
    private GameDiscountService gameDiscountService;
    @Autowired
    private HttpServletRequest request;

    private Integer getUserId() {
        //可以设置缓存 redis 登录时设置过期时间 24小时 todo
        HttpSession session = request.getSession();
        return (Integer) session.getAttribute("userId");
    }

    /**
     * 查询服务器列表
     */
    @RequestMapping(value = "/getServerList", method = RequestMethod.POST)
    public void getServerList(@RequestParam(value = "page", required = false) String page,
                              @RequestParam(value = "rows", required = false) String rows,
                              Integer gameId,
                              Integer serverId,
                              Integer spId,
                              HttpServletResponse response) throws Exception {
        log.info("getServerList:");
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }

        Map<String, Object> map = new HashMap<>(5);
        if (page != null && rows != null) {
            PageBean pageBean = new PageBean(Integer.parseInt(page),
                    Integer.parseInt(rows));
            map.put("start", pageBean.getStart());
            map.put("size", pageBean.getPageSize());
        }

        map.put("gameId", gameId);
        map.put("serverId", serverId);
        map.put("spId", spId);

        List<ServerInfo> serverInfos = serverService.getServerList(map, userId);
        Long total = serverService.getTotalServers(map, userId);

        JSONObject result = new JSONObject();
        result.put("rows", JSONArray.fromObject(serverInfos));
        result.put("total", total);
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);

        log.info("request: serverInfo/getServerList , map: " + map.toString());
    }

    /**
     * 修改
     */
    @RequestMapping(value = "updateServer", method = RequestMethod.PUT)
    @ResponseBody
    public Result update(@RequestBody ServerInfo serverInfo) {
        Integer userId = getUserId();
        if (userId == null) {
            return ResultGenerator.genRelogin();
        }
        if (serverInfo.getSpId() == null || serverInfo.getGameId() == null || serverInfo.getServerId() == null) {
            return ResultGenerator.genFailResult("添加失败");
        }

        int resultTotal = serverService.updateServer(serverInfo, userId);

        log.info("request: server/updateServer , " + serverInfo.toString());

        if (resultTotal > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result delete(@PathVariable("ids") String ids) {
        Integer userId = getUserId();
        if (userId == null) {
            return ResultGenerator.genRelogin();
        }
        if (ids.length() > 20) {
            return ResultGenerator.genFailResult("ERROR");
        }
        String[] idsStr = ids.split(",");
        for (String s : idsStr) {
            serverService.delServer(Integer.parseInt(s), userId);
        }

        log.info("request: server/delete , ids: " + ids);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 增加、修改、删除 游戏的渠道信息
     *
     * @param id       已添加渠道的主键id zy_game_sp
     * @param gameId   平台游戏id
     * @param spId     平台渠道id
     * @param loginUrl 渠道登录地址
     * @param appId    渠道游戏id
     * @param appName  渠道游戏名称
     * @param loginKey 登录秘钥
     * @param payKey   支付秘钥
     * @param sendKey  发货秘钥
     * @param type     1 删除
     *                 2 配置
     *                 3 修改
     *                 4.添加
     */
    @RequestMapping(value = "/changeGameSp", method = RequestMethod.POST)
    @ResponseBody
    public void changeGameSp(Integer id,
                             Integer gameId,
                             Integer spId,
                             String loginUrl,
                             Integer appId,
                             String appName,
                             String loginKey,
                             String payKey,
                             String sendKey,
                             Integer type,
                             HttpServletResponse response) throws Exception {
        log.info("changeGameSp");
        log.info("type:" + type);
        log.info("gameId:" + gameId);
        log.info("spId:" + spId);
        log.info("loginUrl:" + loginUrl);
        log.info("appId:" + appId);
        log.info("appName:" + appName);
        log.info("loginKey:" + loginKey);
        log.info("payKey:" + payKey);
        log.info("sendKey:" + sendKey);

        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        if (gameId == null || spId == null) {
            return;
        }
        //目前只能查询自己的
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("gameId", gameId);
        map.put("spId", spId);

        switch (type) {
            case 1:
            case 2:
            case 3: {
                //zy_game_sp 已有数据 需要校验
                boolean isOpt = false;

                //判断与数据库是否相符
                GameSp gameSp = gameSpService.selectGameSp(gameId, spId, -1);
                if (gameSp == null) {
                    log.error("游戏渠道 不存在 GameId=" + gameId + " channelId=" + spId);
                    return;
                }
                int gameSpGid = gameSp.getGameId();
                int gameSpSpId = gameSp.getSpId();
                int gameSpUid = gameSp.getUid();
                int gameSpStatus = gameSp.getStatus();

                if (gameSpGid != gameId || gameSpSpId != spId) {
                    log.info("changeGameSp err1");
                    return;
                }
                //操作权限判断
                log.info("id=" + userId + "\tuid=" + gameSpUid);
                if (userId == gameSpUid) {
                    //创建者可以直接操作
                    isOpt = true;
                } else {
                    //超级管理员也阔以直接操作
                    User currUser = userService.getUserById(userId, userId);
                    if (currUser == null) {
                        log.info("changeGameSp err2");
                        return;
                    }
                    if (currUser.getManagerLv() == ManagerType.SuperManager.getId()) {
                        isOpt = true;
                    }
                }
                if (!isOpt) {
                    log.info("changeGameSp err3");
                    return;
                }
                map.clear();
                //开始mysql事务
                if (type == 1) {
                    //删除渠道信息
                    gameSpService.deleteGameSp(id, userId);
                    cache.delSPIDInfo(String.valueOf(gameId), String.valueOf(spId));
                } else if (type == 2) {
                    //配置渠道信息
                    if (gameSpStatus != 0) {
                        log.info("changeGameSp err4\t" + gameSpStatus);
                        return;
                    }

                    map.put("appId", appId);
                    map.put("appName", appName);
                    map.put("loginKey", loginKey);
                    map.put("payKey", payKey);
                    map.put("sendKey", sendKey);
                    map.put("status", 1);
                    gameSpService.updateGameSp(map, userId);
                } else {
                    //修改渠道信息
                    if (gameSpStatus != 1) {
                        log.info("changeGameSp err5\t" + gameSpStatus);
                        return;
                    }
                    map.put("appId", appId);
                    map.put("appName", appName);
                    map.put("loginKey", loginKey);
                    map.put("payKey", payKey);
                    map.put("sendKey", sendKey);
                    gameSpService.updateGameSp(map, userId);
                }
                //2-3修改内容不一致 需要根据实际情况来 todo
            }
            break;
            case 4: {
                //添加游戏渠道
                GameSp sp = new GameSp();
                sp.setGameId(gameId);
                sp.setSpId(spId);
                sp.setUid(userId);
                sp.setStatus(0);
                sp.setAppId(appId);
                sp.setAppName(appName);
                sp.setLoginUrl(loginUrl);
                sp.setPaybackUrl("");
                sp.setLoginKey(loginKey);
                sp.setPayKey(payKey);
                sp.setSendKey(sendKey);
                gameSpService.insertGameSp(sp, userId);
                //游戏渠道信息
                cache.setSPIDInfo(String.valueOf(gameId), String.valueOf(spId));
            }
            break;
            default:
                break;
        }


        JSONObject result = new JSONObject();
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);
        log.info("request: changeSp ");
    }

    /**
     * 增加、修改、删除 游戏的折扣信息
     *
     * @param id        已添加渠道的主键id
     * @param gameId    平台游戏id
     * @param channelId 平台渠道id
     * @param discount  折扣信息
     * @param type      1 删除
     *                  2 修改
     *                  3.添加
     */
    @RequestMapping(value = "/changeGameDiscount", method = RequestMethod.POST)
    @ResponseBody
    public void changeGameDiscount(Integer id,
                                   Integer gameId,
                                   Integer channelId,
                                   Integer discount,
                                   Integer type,
                                   HttpServletResponse response) throws Exception {
        log.info("changeGameDiscount");
        log.info("gameId:" + gameId);
        log.info("channelId:" + channelId);
        log.info("discount:" + discount);
        log.info("type:" + type);

        JSONObject result = new JSONObject();

        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        if (gameId == null || channelId == null) {
            result.put("state", false);
            result.put("message", "游戏id 或 渠道id 为空");
            ResponseUtil.write(response, result);
            return;
        }
        if (channelId < 0) {
            result.put("state", false);
            result.put("message", "渠道id 非法");
            ResponseUtil.write(response, result);
            return;
        }
        if (type == 2 || type == 3) {
            //检查折扣信息
            if (discount <= 0 || discount > 100) {
                result.put("state", false);
                result.put("message", "请填入正确的折扣数值");
                ResponseUtil.write(response, result);
                return;
            } else {
                //只能取10的倍数
                if (discount % 10 != 0) {
                    System.out.println("discount =" + discount);
                    result.put("state", false);
                    result.put("message", "折扣数值 只能取10的倍数");
                    ResponseUtil.write(response, result);
                    return;
                }
            }
        }
        //检查游戏秘钥
        GameNew gameNew = gameNewService.selectGame(gameId, -1);
        if (gameNew == null) {
            log.error("游戏不存在 GameId=" + gameId);
            result.put("state", false);
            result.put("message", "游戏 非法");
            ResponseUtil.write(response, result);
            return;
        }

        GameSp gameSp = gameSpService.selectGameSp(gameId, channelId, userId);
        if (gameSp == null) {
            result.put("state", false);
            result.put("message", "渠道 非法");
            ResponseUtil.write(response, result);
            return;
        }
        GameDiscount gameDiscount = gameDiscountService.selectGameDiscount(gameId, channelId, userId);
        switch (type) {
            case 1:
            case 2:
                if (gameDiscount == null) {
                    log.error("游戏折扣 不存在 GameId=" + gameId + " channelId=" + channelId);
                    result.put("state", false);
                    result.put("message", "游戏折扣信息不存在");
                    ResponseUtil.write(response, result);
                    return;
                }
                if (type == 1) {
                    gameDiscountService.deleteGameDiscount(gameId, channelId, userId);
                }
                if (type == 2) {
                    gameDiscount = new GameDiscount(id, gameId, "");
                    gameDiscount.setDisCount(discount);
                    gameDiscount.setUid(userId);
                    gameDiscountService.updateGameDiscount(gameDiscount, userId);
                }
                break;
            case 3: {
                //判断与数据库是否相符
                if (gameDiscount != null) {
                    log.error("游戏渠道 已存在 GameId=" + gameId + " channelId=" + channelId);
                    result.put("state", false);
                    result.put("message", "游戏折扣信息已存在");
                    ResponseUtil.write(response, result);
                    return;
                }
                //操作权限判断
                String name = gameNew.getAppName();
                gameDiscount = new GameDiscount();
                gameDiscount.setGameId(gameId);
                gameDiscount.setName(name);
                gameDiscount.setChannelId(channelId);
                gameDiscount.setDisCount(discount);
                gameDiscount.setUid(userId);
                gameDiscountService.insertGameDiscount(gameDiscount, userId);

            }
            break;
            default:
                break;
        }

        result.put("state", true);
        result.put("message", "设置成功");
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);
    }

    /**
     * 查询所有折扣信息
     */
    @RequestMapping(value = "/getAllGameGameDiscount", method = RequestMethod.POST)
    public void getAllGameDiscount(@RequestParam(value = "page", required = false) String page,
                                   @RequestParam(value = "rows", required = false) String rows,
                                   Integer gameId,
                                   String name,
                                   HttpServletResponse response) throws Exception {
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        if (gameId == null || page == null || rows == null) {
            return;
        }

        JSONObject result = new JSONObject();

        Map<String, Object> map = new HashMap<>(5);
        map.put("gameId", gameId);
        map.put("name", name);

        long size = gameDiscountService.getCountGameDiscount(map, userId);

        PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
        map.put("start", pageBean.getStart());
        map.put("size", pageBean.getPageSize());


        List<GameDiscount> discountList = gameDiscountService.selectGameDiscountList(map, userId);


        result.put("rows", discountList);
        result.put("total", size);
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);

        log.info("request: server/getAllGameGameDiscount , map: " + result.toString());
    }

    /**
     * 查询所有折扣信息
     */
    @RequestMapping(value = "/getGameGameDiscount", method = RequestMethod.POST)
    public void getAllGameDiscount(Integer gameId,
                                   Integer channelId,
                                   HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();
        if (gameId == null || channelId == null) {
            result.put("state", false);
            result.put("message", "游戏id 或 渠道id 为空");
            ResponseUtil.write(response, result);
            return;
        }

        GameDiscount gameDiscount = gameDiscountService.selectGameDiscount(gameId, channelId, -1);
        if (gameDiscount == null) {
            result.put("state", false);
            result.put("message", "折扣信息不存在");
            ResponseUtil.write(response, result);
            return;
        } else {
            Integer discount = gameDiscount.getDisCount();
            if (discount < 0 || discount > 100 || discount % 10 != 0) {
                result.put("message", 100);
            } else {
                result.put("message", discount);
            }

            result.put("state", true);
            ResponseUtil.write(response, result);
        }


        log.info("request: server/geGameGameDiscount , map: " + result.toString());
    }


    /**
     * 查询服务器列表
     */
    @RequestMapping(value = "/getGameList", method = RequestMethod.GET)
    public void getGameList(@RequestParam(value = "page", required = false) String page,
                            @RequestParam(value = "rows", required = false) String rows,
                            Integer gameId,
                            String name,
                            Integer spId,
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
        map.put("gameId", gameId);
        map.put("name", name);

        List<GameNew> serverInfos = gameNewService.getGameList(map, userId);

        if (spId != null) {
            List<GameNew> res = new ArrayList<>();

            List<Integer> gameIdList = serverService.getDistinctServerInfo(map, 1, userId);

            if (gameIdList != null && gameIdList.size() > 0) {
                for (GameNew game : serverInfos) {
                    if (gameIdList.contains(game.getId())) {
                        res.add(game);
                    }
                }
                JSONArray jsonArray = JSONArray.fromObject(res);
                result.put("rows", jsonArray);
            }
        } else {
            JSONArray jsonArray = JSONArray.fromObject(serverInfos);
            result.put("rows", jsonArray);
        }

        result.put("total", serverInfos.size());
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);

        log.info("request: server/getGameList , map: " + result.toString());
    }


    /**
     * 查询服务器列表
     */
    @RequestMapping(value = "/getSpList", method = RequestMethod.GET)
    public void getSpList(@RequestParam(value = "page", required = false) String page,
                          @RequestParam(value = "rows", required = false) String rows,
                          Integer spId,
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
        map.put("name", name);
        map.put("spId", spId);
        List<Sp> spInfos = spService.getAllSpByPage(map, userId);

        long size = spService.getTotalSp(userId);
        result.put("rows", spInfos);
        result.put("total", size);
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);

        log.info("request: server/getSpList , map: " + result.toString());
    }

    /**
     * 增加、修改、删除 渠道信息
     *
     * @param type 1 删除
     *             2 更新
     *             3 增加
     */
    @RequestMapping(value = "/changeSp", method = RequestMethod.GET)
    @ResponseBody
    public void changeSp(Integer spId,
                         String name,
                         Integer parent,
                         Integer state,
                         String shareLinkUrl,
                         String iconUrl,
                         String version,
                         String code,
                         Integer type, HttpServletResponse response) throws Exception {
        log.info("changeSp");
        log.info("spId:" + spId);
        log.info("name:" + name);
        log.info("parent:" + parent);
        log.info("state:" + state);
        log.info("shareLinkUrl:" + shareLinkUrl);
        log.info("iconUrl:" + iconUrl);
        log.info("version:" + version);
        log.info("code:" + code);

        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        if (spId == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("spId", spId);
        map.put("name", name);
        map.put("parent", parent);
        map.put("state", state);
        map.put("shareLinkUrl", shareLinkUrl);
        map.put("iconUrl", iconUrl);
        map.put("version", version);

        if (type == 1) {
            spService.delSp(spId, userId);
        } else if (type == 2) {
            spService.updateSp(map, userId);
        } else if (type == 3) {
            if (code != null || !code.isEmpty()) {
                map.put("code", code);
            }
            spService.addSp(map, userId);
        }


        JSONObject result = new JSONObject();
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);
        log.info("request: changeSp ");
    }

    /**
     * 查询用户-该游戏的所有渠道
     */
    @RequestMapping(value = "/getGameSpList", method = RequestMethod.POST)
    public void getGameSpList(@RequestParam(value = "page", required = false) String page,
                              @RequestParam(value = "rows", required = false) String rows,
                              Integer gameId,
                              String name,
                              HttpServletResponse response) throws Exception {
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        if (gameId == null) {
            return;
        }

        JSONObject result = new JSONObject();

        Map<String, Object> map = new HashMap<>(5);
        if (page != null && rows != null) {
            PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
            map.put("start", pageBean.getStart());
            map.put("size", pageBean.getPageSize());
        }

        map.put("gameId", gameId);
        map.put("name", name);
        map.put("uid", userId);

        long size = 0;
        Map<Integer, WebGameSp> webGameSpMap = new HashMap<>();
        do {
            //查询该账号-该游戏的所有渠道
            List<GameSp> gameSpList = gameSpService.selectGameSpList(map, userId);
            if (gameSpList == null || gameSpList.size() == 0) {
                break;
            }
//            List<ChannelConfig> configs =channelConfigService.

            size = gameSpService.getCountGameSp(map, userId);
            Set<Integer> spIds = new HashSet<>();
            for (GameSp sp : gameSpList) {
                spIds.add(sp.getSpId());
            }
            log.info(spIds.toString());

            //查询详细的渠道信息
            map.clear();
            map.put("spIdList", spIds);
            List<Sp> spList = spService.selectSpByIds(false, map, userId);

            for (GameSp gameSp : gameSpList) {
                Integer spId = gameSp.getSpId();
                WebGameSp webGameSp = new WebGameSp();
                webGameSp.setId(gameSp.getId());
                webGameSp.setAppid(gameId);
                webGameSp.setUid(gameSp.getUid());
                webGameSp.setChannelid(spId);
                //设置配置状态
                webGameSp.setConfigStatus(gameSp.getStatus());

                webGameSpMap.put(spId, webGameSp);
            }
            for (Sp sp : spList) {
                Integer spId = sp.getSpId();
                if (webGameSpMap.containsKey(spId)) {
                    WebGameSp webGameSp = webGameSpMap.get(spId);
                    webGameSp.setIcon(sp.getIconUrl());
                    webGameSp.setName(sp.getName());
                    webGameSp.setStatus(1);
                    webGameSp.setVersion(sp.getVersion());

                }
            }

            if (!name.isEmpty()) {
                Iterator<Map.Entry<Integer, WebGameSp>> it = webGameSpMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, WebGameSp> entry = it.next();
                    WebGameSp webGameSp = entry.getValue();
                    if (!webGameSp.getName().contains(name)) {
                        it.remove();
                    }
                }
                size = webGameSpMap.size();
            }


        } while (false);


        result.put("rows", webGameSpMap.values());
        result.put("total", size);
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);

        log.info("request: server/getGameSpList , map: " + result.toString());
    }

    /**
     * 查询所有渠道
     */
    @RequestMapping(value = "/getAllGameSpList", method = RequestMethod.POST)
    public void getAllGameSpList(@RequestParam(value = "page", required = false) String page,
                                 @RequestParam(value = "rows", required = false) String rows,
                                 Integer gameId,
                                 String name,
                                 HttpServletResponse response) throws Exception {
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        if (gameId == null || page == null || rows == null) {
            return;
        }

        long size = spService.getTotalSp(userId);
        JSONObject result = new JSONObject();

        List<WebGameSp> webGameSpList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(5);
        //需要将已经存在的放前面
        Map<Integer, WebGameSp> webGameSpMap = new HashMap<>();

        PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
        map.put("start", pageBean.getStart());
        map.put("size", pageBean.getPageSize());

        do {
            //已添加的游戏渠道
            Set<Integer> hasGameSpId = new LinkedHashSet<>();

            map.put("gameId", gameId);
            map.put("name", name);
            map.put("uid", userId);

            //查询该账号-该游戏的所有渠道
            List<GameSp> gameSpList = gameSpService.selectGameSpList(map, userId);
            if (gameSpList == null) {
                break;
            }

            //数据条数
            int gameSpSize = gameSpList.size();
            //数据最大条数
            long gameSpMaxSize = gameSpService.getCountGameSp(map, userId);
            //计算最大页数
            long gameSpMaxPage = gameSpMaxSize == 0 ? 0 : gameSpMaxSize / pageBean.getPageSize() + 1;

            long noGameSpStart = 0;
            long noGameSpPageSize = 0;

            if (gameSpMaxPage > pageBean.getPage()) {
                //当前页均是已添加的渠道
                //直接查询
                for (GameSp sp : gameSpList) {
                    hasGameSpId.add(sp.getSpId());
                }
            } else if (gameSpMaxPage == pageBean.getPage()) {
                //当前页=已添加的渠道+未添加的渠道
                for (GameSp sp : gameSpList) {
                    hasGameSpId.add(sp.getSpId());
                }
                noGameSpStart = 0;
                noGameSpPageSize = pageBean.getStart() + pageBean.getPageSize() - gameSpSize;
            } else {
                //当前页均是未添加的渠道
                noGameSpStart = pageBean.getStart() - gameSpMaxSize + 1;
                noGameSpPageSize = pageBean.getPageSize();
            }

            log.info("noGameSpStart\t" + noGameSpStart);
            log.info("noGameSpPageSize\t" + noGameSpPageSize);
            log.info("spIds\t" + hasGameSpId.toString());

            //查询详细的渠道信息
            map.remove("gameId");
            map.remove("name");
            map.remove("uid");

            //对应游戏已添加渠道的详细信息
            if (hasGameSpId.size() != 0) {
                map.put("spIdList", hasGameSpId);
                List<Sp> hasSpList = spService.selectSpByIds(false, map, userId);

                log.info(hasGameSpId.toString());

                for (GameSp gameSp : gameSpList) {
                    Integer spId = gameSp.getSpId();
                    WebGameSp webGameSp = new WebGameSp();
                    webGameSp.setId(gameSp.getId());
                    webGameSp.setAppid(gameId);
                    webGameSp.setUid(gameSp.getUid());
                    webGameSp.setChannelid(spId);
                    //设置配置状态
                    webGameSp.setConfigStatus(gameSp.getStatus());
                    webGameSpMap.put(spId, webGameSp);
                    webGameSpList.add(webGameSp);
                }
                for (Sp sp : hasSpList) {
                    Integer spId = sp.getSpId();
                    if (webGameSpMap.containsKey(spId)) {
                        WebGameSp webGameSp = webGameSpMap.get(spId);
                        webGameSp.setIcon(sp.getIconUrl());
                        webGameSp.setName(sp.getName());
                        webGameSp.setStatus(1);
                        webGameSp.setVersion(sp.getVersion());
                    }
                }
            }
            //对应游戏未添加渠道的详细信息
            if (hasGameSpId.size() < pageBean.getPageSize()) {
                List<Sp> noSpList = spService.selectSpByIds(true, map, userId);
                for (Sp sp : noSpList) {
                    Integer spId = sp.getSpId();
                    WebGameSp webGameSp = new WebGameSp();
                    webGameSp.setAppid(gameId);
                    webGameSp.setIcon(sp.getIconUrl());
                    webGameSp.setName(sp.getName());
                    webGameSp.setChannelid(spId);
                    webGameSp.setStatus(0);
                    webGameSp.setVersion(sp.getVersion());
                    //设置配置状态
                    webGameSp.setConfigStatus(0);
                    webGameSpMap.put(spId, webGameSp);
                    webGameSpList.add(webGameSp);
                }
            }
        } while (false);


        result.put("rows", webGameSpList);
        result.put("total", size);
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);

        log.info("request: server/getAllGameSpList , map: " + result.toString());
    }
}

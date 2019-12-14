package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.common.Result;
import com.ssm.promotion.core.common.ResultGenerator;
import com.ssm.promotion.core.entity.GameName;
import com.ssm.promotion.core.entity.PageBean;
import com.ssm.promotion.core.entity.ServerInfo;
import com.ssm.promotion.core.service.GameNameService;
import com.ssm.promotion.core.service.ServerListService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Controller
@RequestMapping("/server")
public class ServerController {
    private static final Logger log = Logger.getLogger(ServerController.class);
    @Resource
    private ServerListService serverService;
    @Resource
    private GameNameService gameService;
    @Autowired
    private HttpServletRequest request;

    private Integer getUserId() {
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
        System.out.println("getServerList:");
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

        System.out.println("request: serverInfo/getServerList , map: " + result.toString());
        log.info("request: serverInfo/getServerList , map: " + map.toString());
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/addServer", method = RequestMethod.POST)
    @ResponseBody
    public Result addServer(@RequestBody ServerInfo serverInfo)
            throws Exception {
        Integer userId = getUserId();
        if (userId == null) {
            return ResultGenerator.genRelogin();
        }
        if (serverInfo.getSpId() == null || serverInfo.getGameId() == null || serverInfo.getServerId() == null) {
            return ResultGenerator.genFailResult("添加失败");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("gameId", serverInfo.getGameId());
        map.put("serverId", serverInfo.getServerId());
        map.put("spId", serverInfo.getSpId());

        if (serverService.existSGS(map, userId)) {
            return ResultGenerator.genFailResult("添加失败");
        }
        List<GameName> gameNames = gameService.getGameList(map, userId);
        if (gameNames == null || gameNames.size() == 0) {
            return ResultGenerator.genFailResult("添加失败");
        }
        //todo 渠道 游戏 区服 不能一样
        serverInfo.setGamename(gameNames.get(0).getName());
        int resultTotal = serverService.addServer(serverInfo, userId);

        log.info("request: article/save , " + serverInfo.toString());

        if (resultTotal > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("添加失败");
        }
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
     * 删除
     */
    @RequestMapping(value = "/gamedata", method = RequestMethod.GET)
    @ResponseBody
    public void deletegame(Integer gameId,
                           String name,
                           Integer type, HttpServletResponse response) throws Exception {
        System.out.println("deletegame:");
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        System.out.println("gameId:" + gameId);
        System.out.println("name:" + name);
        System.out.println("type:" + type);
        if (type == 1) {
            gameService.deleteGame(gameId, userId);
        } else if (type == 2) {
            gameService.updateGame(gameId, name, userId);
        } else if (type == 3) {
            gameService.addGame(gameId, name, userId);
        }

        log.info("request: game/gamedata , gameId: " + gameId);

        JSONObject result = new JSONObject();
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);
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
        System.out.println("getGameList:");

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

        List<GameName> serverInfos = gameService.getGameList(map, userId);

        if (spId != null) {
            List<GameName> res = new ArrayList<>();

            List<Integer> gameIdList = serverService.getDistinctServerInfo(map, 1, userId);

            if (gameIdList != null || gameIdList.size() > 0) {
                for (GameName gameName : serverInfos) {
                    if (gameIdList.contains(gameName.getGameId())) {
                        res.add(gameName);
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
     * 查询不同的区服和渠道
     */
    @RequestMapping(value = "/getDistinctServerInfo", method = RequestMethod.POST)
    public void getDistinctServerInfo(Integer spId, Integer gameId, Integer serverId,
                                      Integer type,
                                      HttpServletResponse response) throws Exception {
        System.out.println("getDistinctServerInfo:");

        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        Map<String, Object> map = new HashMap<>(6);
        map.put("spId", spId);
        map.put("gameId", gameId);
        map.put("serverId", serverId);

        List<Integer> serverInfos = serverService.getDistinctServerInfo(map, type, userId);
        serverInfos = serverInfos.stream().sorted(Integer::compareTo).collect(Collectors.toList());

        JSONArray rows;
        if (type == 2) {
            List<GameName> gameNameList = gameService.getGameList(map, userId);
            rows = JSONArray.fromObject(gameNameList);
        } else {
            rows = JSONArray.fromObject(serverInfos);
        }

        JSONObject result = new JSONObject();
        result.put("rows", rows.toString());
        result.put("total", serverInfos.size());
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);

        System.out.println("request: server/getDistinctServerInfo , map: " + result.toString());
        log.info("request: server/getDistinctServerInfo , map: " + result.toString());
    }
}

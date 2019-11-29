package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.common.Result;
import com.ssm.promotion.core.common.ResultGenerator;
import com.ssm.promotion.core.entity.PageBean;
import com.ssm.promotion.core.entity.ServerInfo;
import com.ssm.promotion.core.entity.Servername;
import com.ssm.promotion.core.service.ServerListService;
import com.ssm.promotion.core.service.ServerNameService;
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
@RequestMapping("/server")
public class ServerController {
    private static final Logger log = Logger.getLogger(ServerController.class);
    @Resource
    private ServerListService serverService;
    @Resource
    private ServerNameService gameService;

    @Autowired
    private HttpServletRequest request;

    private Integer getUserId() {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        return userId;
    }

    /**
     * 查询服务器列表
     */
    @RequestMapping(value = "/getServerList", method = RequestMethod.POST)
    public void getServerList(@RequestParam(value = "page", required = false) String page,
                              @RequestParam(value = "rows", required = false) String rows,
                              Integer gameId, Integer serverId, String spId,
                              HttpServletResponse response) throws Exception {
        System.out.println("getServerList:");
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        Map<String, Object> map = new HashMap<>(6);
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
        JSONArray jsonArray = JSONArray.fromObject(serverInfos);
        result.put("rows", jsonArray);
        result.put("total", total);
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);
        System.out.println("request: server/getServerList , map: " + result.toString());
        log.info("request: server/getServerList , map: " + map.toString());
    }

    /**
     * 添加
     */
    @RequestMapping(value = "addServer", method = RequestMethod.POST)
    @ResponseBody
    public Result addServer(@RequestBody ServerInfo server)
            throws Exception {
        Integer userId = getUserId();
        if (userId == null) {
            return ResultGenerator.genRelogin();
        }
        int resultTotal = 0;

        resultTotal = serverService.addServer(server, userId);

        log.info("request: article/save , " + server.toString());

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
    public Result update(@RequestBody ServerInfo serverInfo)
            throws Exception {

        Integer userId = getUserId();
        if (userId == null) {
            return ResultGenerator.genRelogin();
        }
        int resultTotal = 0;
        resultTotal = serverService.updateServer(serverInfo, userId);

        log.info("request: article/update , " + serverInfo.toString());

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
    public Result delete(@PathVariable("ids") String ids) throws Exception {
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

        log.info("request: article/delete , ids: " + ids);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 查询服务器列表
     */
    @RequestMapping(value = "/getGameList", method = RequestMethod.GET)
    public void getGameList(HttpServletResponse response) throws Exception {
        System.out.println("getGameList:");

        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }

        List<Servername> serverInfos = gameService.getGameList(userId);

        JSONObject result = new JSONObject();
        JSONArray jsonArray = JSONArray.fromObject(serverInfos);
        result.put("rows", jsonArray);
        result.put("total", serverInfos.size());
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);

        log.info("request: server/getGameList , map: " + result.toString());
    }

    /**
     * 查询不同的区服和渠道
     */
    @RequestMapping(value = "/getDistinctServerInfo", method = RequestMethod.POST)
    public void getDistinctServerInfo(Integer gameId, Integer serverId,
                                      String spId, Integer type,
                                      HttpServletResponse response) throws Exception {
        System.out.println("getDistinctServerInfo:");

        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        Map<String, Object> map = new HashMap<>(6);
        map.put("gameId", gameId);
        map.put("serverId", serverId);
        map.put("spId", spId);

        List<String> serverInfos = serverService.getDistinctServerInfo(map, type, userId);

        JSONObject result = new JSONObject();
        JSONArray jsonArray = JSONArray.fromObject(serverInfos);
        result.put("rows", jsonArray);
        result.put("total", serverInfos.size());
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);
        System.out.println("request: server/getGameList , map: " + result.toString());
        log.info("request: server/getGameList , map: " + result.toString());
    }
}

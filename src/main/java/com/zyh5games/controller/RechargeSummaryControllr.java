package com.zyh5games.controller;

import com.zyh5games.common.Constants;
import com.zyh5games.entity.GameInfo;
import com.zyh5games.entity.PageBean;
import com.zyh5games.entity.RechargeSummary;
import com.zyh5games.jedis.JedisRechargeCache;
import com.zyh5games.service.RechargeSummaryService;
import com.zyh5games.util.DateUtil;
import com.zyh5games.util.ResponseUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/11/26
 */
@Controller
@RequestMapping("/rechargeSummary")
public class RechargeSummaryControllr {
    private static final Logger log = Logger.getLogger(RechargeSummaryControllr.class);
    @Autowired
    JedisRechargeCache cache;
    @Resource
    private RechargeSummaryService rsService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 获取当前用户 session
     */
    private Integer getUserId() {
        HttpSession session = request.getSession();
        return (Integer) session.getAttribute("userId");
    }

    /**
     * @param type      1/2/3 全服-时间排序/全区服-区服排序/全渠道-渠道排序
     * @param gameId    游戏id 不为空
     * @param serverId  区服id 查询对应游戏的区服 可为空,则查询数据库该游戏所有区服
     * @param channelId 渠道id 查询对应游戏渠道 可为空
     * @param startTime 开始时间 jsp内格式：yyyy-MM-dd HH:mm
     * @param endTime   结束时间 jsp内格式：yyyy-MM-dd HH:mm
     */
    @RequestMapping(value = "/getRS", method = RequestMethod.POST)
    @ResponseBody
    public void getRechargeSummary(Integer type,
                                   Integer gameId, Integer channelId, Integer serverId,
                                   String startTime, String endTime,
                                   String page, String rows,
                                   HttpServletResponse response) throws Exception {
        log.info("getRS");

        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        //可能用到的页数
        int pageStart = 0;
        int pageSize = 10;
        if (page != null && rows != null) {
            PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
            pageStart = pageBean.getStart();
            pageSize = pageBean.getPageSize();
        }
        long start = System.currentTimeMillis();

        JSONObject result = new JSONObject();
        do {
            if (gameId == null || gameId == -1) {
                result.put("message", "游戏id为空或未选择");
                result.put("state", false);
                break;
            }
            if (startTime == null || endTime == null) {
                result.put("message", "日期为空");
                result.put("state", false);
                break;
            }
            //查询的日期天数
            List<String> timeList = DateUtil.transTimes(startTime, endTime, DateUtil.FORMAT_YYYY_MMDD_HHmm);
            System.out.println(timeList);
            //需要查询的游戏、渠道、区服信息
            Map<String, GameInfo> gameInfoMap = rsService.getGameInfo(gameId, channelId, serverId);
            if (gameInfoMap == null) {
                result.put("message", "无此游戏、渠道或区服");
                result.put("state", false);
                break;
            }
            GameInfo gameInfo = gameInfoMap.get(String.valueOf(gameId));
            //查询结果
            List<RechargeSummary> res = null;
            switch (type) {
                case 1:
                    res = rsService.getDayResult(gameInfoMap, timeList);
                    break;
                case 2:
                    if (channelId == null) {
                        result.put("message", "渠道id为空");
                        result.put("state", false);
                        break;
                    }
                    res = rsService.getChannelResult(gameInfo, timeList);
                    break;
                case 3:
                    //剑秋：不同渠道在同一游戏服务器里游戏
                    if (serverId == null) {
                        result.put("message", "区服id为空");
                        result.put("state", false);
                        break;
                    }
                    res = rsService.getServerResult(gameInfo, timeList);
                    break;

                default:
                    break;
            }

            if (res != null) {
                result.put("rows", JSONArray.fromObject(res));
                result.put("total", res.size());
                result.put("state", true);
                result.put("message", "查询成功");
            }

        } while (false);

        result.put("time", new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - start) / 1000));
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);
    }

}


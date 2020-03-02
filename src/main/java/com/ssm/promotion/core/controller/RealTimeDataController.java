package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.entity.PageBean;
import com.ssm.promotion.core.entity.UOrder;
import com.ssm.promotion.core.entity.User;
import com.ssm.promotion.core.jedis.RedisKeyNew;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.sdk.UOrderManager;
import com.ssm.promotion.core.service.impl.UserServiceImpl;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.ResponseUtil;
import com.ssm.promotion.core.util.ServerInfoUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Administrator
 */
@Controller
@RequestMapping("/realtime")
public class RealTimeDataController {
    private static final Logger log = Logger.getLogger(RealTimeDataController.class);
    @Autowired
    jedisRechargeCache cache;
    @Resource
    private UOrderManager orderManager;
    @Autowired
    private HttpServletRequest request;

    private Integer getUserId() {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        return userId;
    }

    /**
     * 支付记录
     */
    @RequestMapping(value = "/getPayRecord", method = RequestMethod.POST)
    @ResponseBody
    public void getPayRecord(@RequestBody Map<String, Object> param,
                             HttpServletResponse response) throws Exception {
        log.info("getPayRecord:" + param.toString());

        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        String page = param.get("page").toString();
        String rows = param.get("rows").toString();
        if (page != null && rows != null) {
            PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
            param.put("start", pageBean.getStart());
            param.put("size", pageBean.getPageSize());
        }

        User currUser = UserServiceImpl.getUser(userId);

        param.put("spId", ServerInfoUtil.spiltStr(currUser.getSpId()));

        long start = System.currentTimeMillis();

        List<UOrder> orderList = orderManager.getUOrderList(param);
        orderList.forEach(UOrder::setJsOrder);
        Long total = orderManager.getTotalUorders(param);

        long end = System.currentTimeMillis();

        JSONObject result = new JSONObject();
        result.put("rows", JSONArray.fromObject(orderList));
        result.put("total", total);
        result.put("time", new DecimalFormat("0.00").format((double) (end - start) / 1000));
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);

        log.info("request: realtime/getPayRecord , map: " + param.toString());
    }

    /**
     * 实时数据
     */
    @RequestMapping(value = "/realtimedata", method = RequestMethod.POST)
    @ResponseBody
    public void getRealTimeData(Integer type,
                                String spId, Integer gameId, Integer serverId,
                                String startTime, String endTime,
                                HttpServletResponse response) throws Exception {
        log.info("realtimedata:");

        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        JSONObject result = new JSONObject();
        do {
            if (spId == null || gameId == null || serverId == null) {
                result.put("err", "参数非法");
                result.put("resultCode", Constants.SDK_PARAM);
                break;
            }
            if (spId.equals("-1") || gameId == -1) {
                result.put("err", "请选择区服");
                result.put("resultCode", Constants.SDK_PARAM);
                break;
            }
            if (startTime == null || endTime == null) {
                startTime = DateUtil.formatDate(new Date(System.currentTimeMillis() - DateUtil.HOUR_MILLIS), DateUtil.FORMAT_YYYY_MMDD_HHmmSS);
                endTime = DateUtil.getCurrentDateStr();
            }

            List<String> timeDaylist = DateUtil.transTimes(startTime, endTime, DateUtil.FORMAT_YYYY_MMDD_HHmm);
            if (timeDaylist.size() != 1) {
                result.put("err", "请选择同一天的数据");
                result.put("resultCode", Constants.SDK_PARAM);
                break;
            }

            List<String> timeMinlist = DateUtil.getDateMinStr(startTime, endTime);

            if (serverId == -1) {
                Set<String> serverIdInfo = cache.getServerInfo(String.valueOf(gameId), spId);
                if (type == 2) {
                    List<Integer> money = new ArrayList<>();
                    cache.getRealtimeData(type, spId, gameId, timeDaylist.get(0), timeMinlist, money);
                    result.put("money", money);
                } else if (type == 3) {
                    List<Integer> onlines = new ArrayList<>();
                    cache.getRealtimeData(type, spId, gameId, timeDaylist.get(0), timeMinlist, onlines);
                    result.put("onlines", onlines);

                    List<Integer> newadd = new ArrayList<>();
                    type = 1;
                    cache.getRealtimeData(type, spId, gameId, timeDaylist.get(0), timeMinlist, newadd);
                    result.put("newadd", newadd);
                }
            } else {
                if (type == 2) {
                    List<Integer> money = new ArrayList<>();
                    cache.getRealtimeData(type, spId, gameId, serverId, timeDaylist.get(0), timeMinlist, money);
                    result.put("money", money);
                } else if (type == 3) {
                    List<Integer> onlines = new ArrayList<>();
                    cache.getRealtimeData(type, spId, gameId, serverId, timeDaylist.get(0), timeMinlist, onlines);
                    result.put("onlines", onlines);

                    List<Integer> newadd = new ArrayList<>();
                    type = 1;
                    cache.getRealtimeData(type, spId, gameId, serverId, timeDaylist.get(0), timeMinlist, newadd);
                    result.put("newadd", newadd);
                }
            }

            result.put("times", timeMinlist);
            result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        } while (false);

        ResponseUtil.write(response, result);

        log.info("request: realtime/realtimedata , map: " + result.toString());
    }


}

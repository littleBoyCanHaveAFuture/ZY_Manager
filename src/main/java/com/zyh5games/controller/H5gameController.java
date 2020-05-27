package com.zyh5games.controller;

import com.zyh5games.common.Constants;
import com.zyh5games.entity.HuoguoExchange;
import com.zyh5games.entity.HuoguoExchangeRecord;
import com.zyh5games.entity.PageBean;
import com.zyh5games.jedis.JedisRechargeCache;
import com.zyh5games.service.HuoguoExchangeService;
import com.zyh5games.util.DateUtil;
import com.zyh5games.util.ResponseUtil;
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
@RequestMapping("/h5")
public class H5gameController {
    private static final Logger log = Logger.getLogger(H5gameController.class);
    @Autowired
    JedisRechargeCache cache;
    @Autowired
    private HttpServletRequest request;
    @Resource
    private HuoguoExchangeService exchangeService;

    /**
     * 查询商品兑换列表
     */
    @RequestMapping(value = "/getExchangeStatus", method = RequestMethod.GET)
    public void getExchangeStatus(HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();
        String key = "huoguo#exchange";
        boolean status = false;
        boolean hasKey = cache.exists(key);
        if (!hasKey) {
            cache.setString(key, "false");
            result.put("status", false);
        } else {
            String sstatus = cache.getString(key);
            status = "true".equals(sstatus);
            result.put("status", status);
        }

        ResponseUtil.write(response, result);
    }

    /**
     * 查询商品兑换列表
     */
    @RequestMapping(value = "/setExchangeStatus", method = RequestMethod.GET)
    public void setExchangeStatus(HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();
        String key = "huoguo#exchange";
        boolean status = false;
        boolean hasKey = cache.exists(key);
        if (!hasKey) {
            cache.setString(key, "false");
            result.put("status", false);
        } else {
            String sstatus = cache.getString(key);
            status = "true".equals(sstatus);
            if (status) {
                cache.setString(key, "false");
            } else {
                cache.setString(key, "true");
            }
            result.put("status", !status);
        }

        ResponseUtil.write(response, result);
    }

    /**
     * 查询商品兑换列表
     */
    @RequestMapping(value = "/getExchangeList", method = RequestMethod.GET)
    public void getExchange(HttpServletResponse response) throws Exception {
        List<HuoguoExchange> exchangeList = exchangeService.getAll(-1);

        JSONObject result = new JSONObject();
        int num = 0;
        if (exchangeList != null && exchangeList.size() > 0) {
            JSONArray jsonArray = JSONArray.fromObject(exchangeList);
            result.put("rows", jsonArray);
            num = exchangeList.size();
        }
        result.put("num", num);

        ResponseUtil.write(response, result);

        log.info("request: h5/getExchangeList , map:");
    }

    /**
     * 查询玩家申请的兑换列表
     *
     * @param openid 玩家id
     */
    @RequestMapping(value = "/getExRecordList", method = RequestMethod.GET)
    public void getExchangeRecord(@RequestParam(value = "page", required = false) String page,
                                  @RequestParam(value = "rows", required = false) String rows,
                                  String openid, HttpServletResponse response) throws Exception {
        if (openid == null) {
            return;
        }

        Map<String, Object> map = new HashMap<>(5);
        if (page != null && rows != null) {
            PageBean pageBean = new PageBean(Integer.parseInt(page),
                    Integer.parseInt(rows));
            map.put("start", pageBean.getStart());
            map.put("size", pageBean.getPageSize());
        }
        map.put("openid", openid);
        JSONObject result = new JSONObject();

        List<HuoguoExchangeRecord> exchangeRecordList = exchangeService.getRecord(map, -1);
        long num = exchangeService.getCount(openid, -1);
        if (exchangeRecordList != null && exchangeRecordList.size() > 0) {
            JSONArray jsonArray = JSONArray.fromObject(exchangeRecordList);
            result.put("rows", jsonArray);
        }
        result.put("num", num);

        ResponseUtil.write(response, result);

        log.info("request: h5/getExRecordList end");
    }

    /**
     * 玩家申请兑换
     */
    @RequestMapping(value = "/addExRecord", method = RequestMethod.POST)
    @ResponseBody
    public void addRecord(@RequestBody HuoguoExchangeRecord record, HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();
        do {
            if (record.getOpenId() == null) {
                result.put("result", "fail");
                break;
            }

            HuoguoExchange exchange = exchangeService.getById(record.getItemId(), -1);
            if (exchange == null) {
                result.put("result", "fail");
                break;
            }

            record.setStatus(0);
            record.setExchangeTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
            record.setMessage("");
            record.setFinishedTime("");
            int res = exchangeService.addRecord(record, -1);

            if (res > 0) {
                result.put("result", "success");
            } else {
                result.put("result", "fail");
            }
        } while (false);

        ResponseUtil.write(response, result);
        log.info("request: h5/addExRecord , map: " + result.toString());
    }

    /**
     * 后台确认兑换
     */
    @RequestMapping(value = "/confirmExRecord", method = RequestMethod.POST)
    @ResponseBody
    public void confirmRecord(Integer id, Integer status, HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();
        do {
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                result.put("result", "fail");
                result.put("resultCode", Constants.RESULT_CODE_SERVER_RELOGIN);
                break;
            }

            HuoguoExchangeRecord record = exchangeService.getRecord(id, -1);
            if (record == null) {
                result.put("result", "fail");
                break;
            }
            HuoguoExchange exchange = exchangeService.getById(record.getItemId(), -1);
            if (exchange == null) {
                result.put("result", "fail");
                break;
            }

            if (record.getStatus() < 1 && record.getStatus() > 3) {
                result.put("result", "fail");
                break;
            }
            record.setFinishedTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
            record.setStatus(status);
            int res = exchangeService.updateRecord(record, -1);
            if (res > 0) {
                result.put("result", "success");
            } else {
                result.put("result", "fail");
            }
        } while (false);

        ResponseUtil.write(response, result);
        log.info("request: h5/addExRecord , map: " + result.toString());
    }


}


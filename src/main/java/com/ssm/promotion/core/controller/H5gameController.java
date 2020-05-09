package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.entity.HuoguoExchange;
import com.ssm.promotion.core.entity.HuoguoExchangeRecord;
import com.ssm.promotion.core.entity.PageBean;
import com.ssm.promotion.core.service.HuoguoExchangeService;
import com.ssm.promotion.core.util.DateUtil;
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
@RequestMapping("/h5")
public class H5gameController {
    private static final Logger log = Logger.getLogger(H5gameController.class);

    @Autowired
    private HttpServletRequest request;
    @Resource
    private HuoguoExchangeService exchangeService;

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

        log.info("request: h5/getExchangeList , map: " + result.toString());
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

        log.info("request: h5/getExRecordList , map: " + result.toString());
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


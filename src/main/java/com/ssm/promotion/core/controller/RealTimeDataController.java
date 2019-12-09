package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.entity.*;
import com.ssm.promotion.core.sdk.UOrderManager;
import com.ssm.promotion.core.service.PayRecordService;
import com.ssm.promotion.core.service.impl.UserServiceImpl;
import com.ssm.promotion.core.util.EncryptUtils;
import com.ssm.promotion.core.util.ResponseUtil;
import com.ssm.promotion.core.util.ServerInfoUtil;
import com.ssm.promotion.core.util.enums.StateCode;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Controller
@RequestMapping("/realtime")
public class RealTimeDataController {
    private static final Logger log = Logger.getLogger(RealTimeDataController.class);
    @Resource
    private PayRecordService payRecordService;
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
    public void getServerList(
            @RequestBody Map<String, Object> param,
            HttpServletResponse response) throws Exception {
        System.out.println("getPayRecord:" + param.toString());

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

        List<Integer> spIdlist = new ArrayList<>();
        param.put("spId", ServerInfoUtil.spiltStr(currUser.getSpId()));

//        List<PayRecord> orderList = payRecordService.getPayOrderList(param, userId);
//        Long total = payRecordService.getTotalPayRecords(param, userId);

        List<UOrder> orderList = orderManager.getUOrderList(param);
        Long total = payRecordService.getTotalPayRecords(param, userId);

        JSONObject result = new JSONObject();
        JSONArray jsonArray = JSONArray.fromObject(orderList);
        result.put("rows", jsonArray);
        result.put("total", total);
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);


        System.out.println("request: realtime/getPayRecord , map: " + result.toString());
        log.info("request: realtime/getPayRecord , map: " + param.toString());
    }



}

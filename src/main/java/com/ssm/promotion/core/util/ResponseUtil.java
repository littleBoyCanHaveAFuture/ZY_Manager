package com.ssm.promotion.core.util;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.common.Constants;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
public class ResponseUtil {

    public static void write(HttpServletResponse response, Object o) throws Exception {
        response.setContentType("text/html;charset=utf-8");
/*        tomcat 已经处理
        response.addHeader("Access-Control-Allow-Origin", "*");*/
        PrintWriter out = response.getWriter();
        out.println(o.toString());
        out.flush();
        out.close();
    }

    public static void writeRelogin(HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        response.addHeader("Access-Control-Allow-Origin", "*");

        JSONObject result = new JSONObject();
        result.put("resultCode", Constants.RESULT_CODE_SERVER_RELOGIN);

        PrintWriter out = response.getWriter();
        out.println(result.toString());
        out.flush();
        out.close();
    }
}

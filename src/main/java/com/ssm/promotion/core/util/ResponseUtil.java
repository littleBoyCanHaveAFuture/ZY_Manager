package com.ssm.promotion.core.util;

import com.ssm.promotion.core.common.Constants;
import net.sf.json.JSONObject;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
public class ResponseUtil {

    public static void write(HttpServletResponse response, Object o) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        //tomcat 已经处理
//        response.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        out.println(o.toString());
        out.flush();
        out.close();
    }

    public static void writeRelogin(HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();
        result.put("resultCode", Constants.RESULT_CODE_SERVER_RELOGIN);
        response.setContentType("text/html;charset=utf-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        out.println(result.toString());
        out.flush();
        out.close();
    }
}

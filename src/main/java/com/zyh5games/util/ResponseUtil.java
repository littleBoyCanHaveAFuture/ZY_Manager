package com.zyh5games.util;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.common.Constants;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
//        System.out.println(o.toString());
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

    public static void callBack(HttpServletResponse response, Object o) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(o.toString());
        out.flush();
        out.close();
    }

}

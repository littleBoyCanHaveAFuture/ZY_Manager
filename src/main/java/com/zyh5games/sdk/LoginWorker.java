package com.zyh5games.sdk;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/4
 */
@Component
public class LoginWorker {
    private static final Logger log = Logger.getLogger(LoginWorker.class);

    public void getLoginParams(Map<String, String[]> map) {
        //遍历
        for (Map.Entry<String, String[]> stringEntry : map.entrySet()) {
            //key值
            Object strKey = stringEntry.getKey();
            //value,数组形式
            String[] value = stringEntry.getValue();

            System.out.println(strKey.toString() + "=" + value[0]);
        }
    }


}

package com.ssm.promotion.core.util;

import java.util.LinkedList;
import java.util.List;

/**
 * @author song minghua
 * @date 2019/12/3
 */
public class ServerInfoUtil {
    /**
     * 区服渠道排重
     */
    public static List<String> spiltStrList(List<String> serverInfos) {
        List<String> spIdStrList = new LinkedList<>();
        for (String spIdList : serverInfos) {
            if (spIdList.contains(",")) {
                String[] spilt = spIdList.split(",");
                for (String spId : spilt) {
                    if (!spIdStrList.contains(spId)) {
                        spIdStrList.add(spId);
                    }
                }
            } else {
                if (!spIdStrList.contains(spIdList)) {
                    spIdStrList.add(spIdList);
                }
            }
        }
        return spIdStrList;
    }
}

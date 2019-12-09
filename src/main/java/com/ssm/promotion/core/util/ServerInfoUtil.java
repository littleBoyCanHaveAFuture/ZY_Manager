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
        if (serverInfos == null) {
            return null;
        }
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

    public static List<String> spiltStr(String spList) {
        if (spList == null) {
            return null;
        }
        List<String> spIdStrList = new LinkedList<>();
        if (spList.contains(",")) {
            String[] spilt = spList.split(",");
            for (String spId : spilt) {
                if (!spIdStrList.contains(spId)) {
                    spIdStrList.add(spId);
                }
            }
        } else {
            if (!spList.isEmpty()) {
                spIdStrList.add(spList);
            }
        }

        return spIdStrList;
    }
}

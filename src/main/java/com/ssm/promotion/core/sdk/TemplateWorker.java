package com.ssm.promotion.core.sdk;

import com.ssm.promotion.core.dao.TemplateDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author song minghua
 * @date 2019/12/4
 */

public class TemplateWorker {
    private static final Logger log = Logger.getLogger(TemplateWorker.class);
    /**
     * 敏感字
     */
    public static HashSet<String> BadWordList;
    /**
     * 禁止登陆的ip
     */
    public static Set<String> BanIpList;
    @Autowired
    private TemplateDao dao;

    public static boolean hasBad(String org) {
        String s = org.replace(" ", "").replace("　", "");
        Iterator<String> var2 = BadWordList.iterator();

        String badWord;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            badWord = var2.next();
        } while (!s.contains(badWord));

        return true;
    }

    public static boolean hasBanIp(String ip) {
        return false;
//        if (BanIpList.contains(ip)) {
//            return true;
//        }
    }

    public static void addBanIp(String ip) {
        BanIpList.add(ip);
    }

    public void init() {
        log.info("------------------TemplateWorker init start-------------------");
        BadWordList = new HashSet<String>();
        List<String> cacheMapList = dao.selectall();
        BadWordList.addAll(cacheMapList);
        log.info("------------------TemplateWorker init finished-------------------");
    }
}

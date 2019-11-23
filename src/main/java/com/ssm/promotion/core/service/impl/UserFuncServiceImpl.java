package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.UserFuncDao;
import com.ssm.promotion.core.entity.UserFunc;
import com.ssm.promotion.core.service.UserFuncService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


/**
 * @author Administrator
 */
@Service("UserFuncService")
public class UserFuncServiceImpl implements UserFuncService {
    @Resource
    UserFuncDao dao;


    @Override
    public List<UserFunc> getFuncById(Map<String, Object> map, Integer userId) {
        if (map.get("id") != null) {
            String id = map.get("id").toString();
            Integer id2 = Integer.parseInt(id);
            return dao.getFuncById(id2);
        }

        return null;
    }

    @Override
    public List<UserFunc> getFuncList(Integer userId) {

        List<Map<String, Object>> treeData = new ArrayList<>();
        List<UserFunc> allFunc = this.dao.getFuncList();

        for (UserFunc funcVo : allFunc) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", funcVo.getId());
            map.put("pId", funcVo.getParent());
            map.put("name", funcVo.getName());
            map.put("open", true);
            treeData.add(map);
        }

        Iterator<UserFunc> iterator = allFunc.iterator();
        while (iterator.hasNext()) {
            UserFunc funcVo = iterator.next();
            if (funcVo.getName().isEmpty()) {
                iterator.remove();//使用迭代器的删除方法删除
            } else {
                funcVo.toString();
            }
        }
        return allFunc;
    }
}

package com.zyh5games.service;


import com.zyh5games.entity.UserFunc;

import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 */
public interface UserFuncService {

    public List<UserFunc> getFuncById(Map<String, Object> map,Integer userId);

    public List<UserFunc> getFuncList(Integer userId);

}

package com.ssm.promotion.core.service;


import com.ssm.promotion.core.entity.UserFunc;

import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 */
public interface UserFuncService {

    public List<UserFunc> getFuncById(Map<String, Object> map,Integer userId);

    public List<UserFunc> getFuncList(Integer userId);

}

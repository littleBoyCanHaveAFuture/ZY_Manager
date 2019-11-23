package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.UserFunc;

import java.util.List;

/**
 * @author song minghua
 * @date 2019/11/23
 */
public interface UserFuncDao {

    public List<UserFunc> getFuncById(Integer id);

    public List<UserFunc> getFuncList();


}

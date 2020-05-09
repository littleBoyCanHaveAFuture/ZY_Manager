package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.HuoguoExchange;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
@Repository
public interface Huoguo_ExchangeDao {

    List<HuoguoExchange> getAll();

    HuoguoExchange getById(@Param("id") Integer id);
}

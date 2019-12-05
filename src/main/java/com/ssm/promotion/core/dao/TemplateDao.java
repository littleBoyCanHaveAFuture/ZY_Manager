package com.ssm.promotion.core.dao;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
@Repository
public interface TemplateDao {

    List<String> selectall();

}

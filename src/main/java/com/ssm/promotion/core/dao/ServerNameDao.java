package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.Servername;

import java.util.List;

/**
 * @author Administrator
 */
public interface ServerNameDao {
    List<Servername> selectAll();

//    int deleteByPrimaryKey(Integer gameid);
//
//    int insert(Servername record);
//
//    int insertSelective(Servername record);
//
//    Servername selectByPrimaryKey(Integer gameid);
//
//    int updateByPrimaryKeySelective(Servername record);
//
//    int updateByPrimaryKey(Servername record);
}
package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.Servername;

import java.util.List;

/**
 * @author Administrator
 */
public interface ServerNameService {
    public List<Servername> getGameList(Integer userid);

}
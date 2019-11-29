package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.ServerNameDao;
import com.ssm.promotion.core.entity.Servername;
import com.ssm.promotion.core.service.ServerNameService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author song minghua
 * @date 2019/11/26
 */
@Service("ServerNameService")
public class ServerNameServiceImpl implements ServerNameService {
    @Resource
    ServerNameDao dao;

    @Override
    public List<Servername> getGameList(Integer userid) {
        return dao.selectAll();
    }
}

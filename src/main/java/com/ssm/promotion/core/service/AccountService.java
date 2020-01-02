package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.Account;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/4
 */
public interface AccountService {
    /**
     * 创建账号
     *
     * @param Account
     * @return
     */
    public void createAccount(Account Account) throws DataAccessException;

    /**
     * 查询相同设备码账号条数
     */
    int getTotalSameDeviceCode(String deviceCode, Integer channelId);

    int readMaxAccountId(int maxSpid);

    /**
     * 查找账号
     *
     * @param map
     * @return
     */
    public List<Account> findUser(Map<String, String> map);

    /**
     * 更新数据
     */
    public void updateAccount(Map<String, Object> map);

    /**
     * 查询相同渠道号
     */
    public int exist(Map<String, String> map);

    /**
     * 更新数据
     */
    public void updateAccountUid(Map<String, Object> map);

    public Account findAccountById(int id);
}

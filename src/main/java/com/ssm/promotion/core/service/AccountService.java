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
     * @param account
     */
    void createAccount(Account account) throws DataAccessException;

    /**
     * 查询相同设备码账号条数
     */
    int getTotalSameDeviceCode(String deviceCode, Integer channelId);

    Integer readMaxAccountId(int maxSpid);

    /**
     * 查找账号
     *
     * @param map
     * @return List<Account>
     */
    List<Account> findUser(Map<String, Object> map);

    /**
     * @param id
     */
    Account findAccountById(int id);

    /**
     * 查找账号
     *
     * @return List<Account>
     */
    Account findUserBychannelUid(String channelId, String channelUid);

    /**
     * 查找账号
     *
     * @return List<Account>
     */
    Account findAccountByname(String username);

    /**
     * 更新数据
     *
     * @param map
     */
    void updateAccount(Map<String, Object> map);

    /**
     * 查询相同渠道号
     *
     * @param map
     */
    int exist(Map<String, Object> map);

    /**
     * 更新数据
     *
     * @param map
     */
    void updateAccountUid(Map<String, Object> map);

}

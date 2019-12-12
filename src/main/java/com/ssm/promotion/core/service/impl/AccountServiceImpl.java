package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.AccountDao;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.sdk.AccountWorker;
import com.ssm.promotion.core.service.AccountService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/4
 */
@Service("AccountService")
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountDao accountDao;

    @Override
    public void createAccount(Account account) {
        int id = AccountWorker.lastUserId.get();
        do {
            //若有多个login服，则lastUserId的值异步；主键循环增长，直至插入成功为止
            account.setId(++id);

            try {
                int res = accountDao.create(account);
                System.out.println("success:" + res);
                break;
            } catch (DataAccessException e) {
                String err = e.getMessage();
                //仅使主键重复异常被忽略
                if (err.contains("SQLIntegrityConstraintViolationException") && err.contains("for key 'PRIMARY'")) {
                    System.out.println("err1");
                    continue;
                } else if (err.contains("for key 'name_unique'")) {
                    System.out.println("err2");
                    account.setId(-2);
                    return;
                } else {
                    account.setId(-1);
                    System.out.println("err3:" + err);
                    return;
                }
            } catch (Exception e) {
                System.out.println("err4:" + e.getMessage());
                return;
            }
        } while (true);

        AccountWorker.lastUserId.set(id);

        System.out.println(AccountWorker.lastUserId.get());
    }

    @Override
    public int getTotalSameDeviceCode(String deviceCode, Integer channelId) {

        return accountDao.getTotalSameDeviceCode(deviceCode, channelId);
    }

    @Override
    public int readMaxAccountId(int maxSpid) {
        Integer count = accountDao.readMaxAccountId(maxSpid);
        if (count == null) {
            count = 0;
        }
        return count;
    }

    @Override
    public List<Account> findUser(Map<String, String> map) {
        boolean isChannel = Boolean.parseBoolean(map.get("isChannel"));
        if (isChannel) {
            if (map.get("channelId").isEmpty() || map.get("channelUid").isEmpty()) {
                return null;
            }
            return accountDao.findAccountSp(map);
        } else {
            if (map.get("name").isEmpty() || map.get("pwd").isEmpty()) {
                return null;
            }
            return accountDao.findAccountOF(map);
        }
    }

    @Override
    public void updateAccount(Map<String, Object> map) {
        accountDao.updateAccount(map);
    }

    @Override
    public int exist(Map<String, String> map) {
        return accountDao.exist(map).size();
    }
}

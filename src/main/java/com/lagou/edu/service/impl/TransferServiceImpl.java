package com.lagou.edu.service.impl;

import com.lagou.edu.anno.WzwAutowired;
import com.lagou.edu.anno.WzwService;
import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.pojo.Account;
import com.lagou.edu.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author 应癫
 */
@WzwService("transferService")
@Transactional
public class TransferServiceImpl implements TransferService {


    @WzwAutowired
    private AccountDao accountDao;




    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

            Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney()-money);
            to.setMoney(to.getMoney()+money);

            accountDao.updateAccountByCardNo(to);
            accountDao.updateAccountByCardNo(from);
    }
}

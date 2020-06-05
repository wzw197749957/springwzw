package com.lagou.edu.service.impl;

import com.lagou.edu.anno.WzwAutowired;
import com.lagou.edu.anno.WzwService;
import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.pojo.Account;
import com.lagou.edu.service.TransferService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;


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
        Resource resource = new ClassPathResource("jdbc.properties");
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
        Connection conn = DriverManager.getConnection(properties.getProperty("jdbc.url")
                , properties.getProperty("jdbc.username")
                , properties.getProperty("jdbc.password"));
        Account from = accountDao.queryAccountByCardNo(conn, fromCardNo);
        Account to = accountDao.queryAccountByCardNo(conn, toCardNo);

        from.setMoney(from.getMoney() - money);
        to.setMoney(to.getMoney() + money);

        accountDao.updateAccountByCardNo(conn,to);
        accountDao.updateAccountByCardNo(conn,from);
        conn.close();
    }
}

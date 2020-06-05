package com.lagou.edu.dao.impl;

import com.lagou.edu.anno.WzwService;
import com.lagou.edu.anno.WzwTransactional;
import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.pojo.Account;
import com.mysql.jdbc.Driver;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author 应癫
 */
@WzwService
public class JdbcTemplateDaoImpl implements AccountDao {


    @Override
    public Account queryAccountByCardNo(Connection conn, String cardNo) throws Exception {
        Account account = new Account();
        String sql = "select * from account where cardNo=?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, cardNo);
        ResultSet res = preparedStatement.executeQuery();
        while (res.next()) {
            account.setName(res.getString("name"));
            account.setCardNo(res.getString("cardNo"));
            account.setMoney(res.getInt("money"));
        }
        return account;
    }

    @Override
    @WzwTransactional
    public void updateAccountByCardNo(Connection conn, Account account) throws Exception {
        Statement statement = conn.createStatement();
        String sql = "update account set money=? where cardNo=?";
        statement.executeUpdate(sql);
    }

    private DataSource getDataSource() {
        Resource resource = new ClassPathResource("jdbc.properties");
        Properties properties = null;
        try {
            properties = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SimpleDriverDataSource(
                BeanUtils.instantiateClass(Driver.class),
                properties.getProperty("jdbc.url"),
                properties.getProperty("jdbc.username"),
                properties.getProperty("jdbc.password"));
    }
}

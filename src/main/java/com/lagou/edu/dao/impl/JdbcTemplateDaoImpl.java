package com.lagou.edu.dao.impl;

import com.lagou.edu.anno.WzwAutowired;
import com.lagou.edu.anno.WzwService;
import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.pojo.Account;
import com.mysql.jdbc.Driver;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author 应癫
 */
@WzwService
public class JdbcTemplateDaoImpl implements AccountDao {


    @Override
    public Account queryAccountByCardNo(String cardNo) throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
        String sql = "select * from account where cardNo=?";
        return jdbcTemplate.queryForObject(sql, new RowMapper<Account>() {
            @Override
            public Account mapRow(ResultSet resultSet, int i) throws SQLException {
                Account account = new Account();
                account.setName(resultSet.getString("name"));
                account.setCardNo(resultSet.getString("cardNo"));
                account.setMoney(resultSet.getInt("money"));
                return account;
            }
        }, cardNo);
    }

    @Override
    public int updateAccountByCardNo(Account account) throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
        String sql = "update account set money=? where cardNo=?";
        return jdbcTemplate.update(sql, account.getMoney(), account.getCardNo());
    }

    private DataSource getDataSource(){
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

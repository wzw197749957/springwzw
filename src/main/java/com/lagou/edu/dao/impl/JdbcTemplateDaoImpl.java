package com.lagou.edu.dao.impl;

import com.lagou.edu.anno.WzwAutowired;
import com.lagou.edu.anno.WzwService;
import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.pojo.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author 应癫
 */
@WzwService
public class JdbcTemplateDaoImpl implements AccountDao {

    @Override
    public Account queryAccountByCardNo(String cardNo) throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
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
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        String sql = "update account set money=? where cardNo=?";
        return jdbcTemplate.update(sql, account.getMoney(), account.getCardNo());
    }
}

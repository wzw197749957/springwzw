package com.lagou.edu.dao;

import com.lagou.edu.pojo.Account;

import java.sql.Connection;

/**
 * @author 应癫
 */
public interface AccountDao {

    Account queryAccountByCardNo(Connection conn, String cardNo) throws Exception;

    void updateAccountByCardNo(Connection conn, Account account) throws Exception;
}

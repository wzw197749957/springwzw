package com.lagou.edu.service;

import com.lagou.edu.anno.WzwAutowired;
import com.lagou.edu.anno.WzwService;

@WzwService
public class HomeworkService {
    @WzwAutowired
    private TransferService transferService;

    public void transfer() throws Exception {
        transferService.transfer("123456","654321",100);
    }
}

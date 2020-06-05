package com.lagou.edu;

import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.ioc.WzwApplicationContext;
import com.lagou.edu.service.HomeworkService;
import org.junit.Test;

public class IocTest {


    @Test
    public void testAnno() throws Exception {
        // 通过读取classpath下的xml文件来启动容器（xml模式SE应用下推荐）
        WzwApplicationContext applicationContext = new WzwApplicationContext();

        HomeworkService homeworkService = (HomeworkService) applicationContext.getBean("homeworkService");

        homeworkService.transfer();
    }


}

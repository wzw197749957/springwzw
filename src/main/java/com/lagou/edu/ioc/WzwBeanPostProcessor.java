package com.lagou.edu.ioc;

import com.lagou.edu.anno.WzwTransactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

public class WzwBeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        if (beanName.equals("jdbcTemplateDaoImpl")) {
            Class clazz = bean.getClass();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(WzwTransactional.class)) {
                    bean = Proxy.newProxyInstance(
                            clazz.getClassLoader(),
                            clazz.getInterfaces(),
                            new InvocationHandler() {
                                @Override
                                public Object invoke(Object proxy, Method method1, Object[] args) throws Throwable {
                                    Connection conn = (Connection) args[0];
                                    conn.setAutoCommit(false);
                                    try {
                                        Object proxyObject = method1.invoke(clazz, args);
                                        conn.commit();
                                        return proxyObject;
                                    } catch (Exception e) {
                                        conn.rollback();
                                    }
                                    return null;
                                }
                            });
                }
            }
        }
        return bean;
    }
}

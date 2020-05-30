package com.lagou.edu.core;

public interface WzwBeanFactory {
    Object getBean(String beanName) throws Exception;
    public Object getBean(Class<?> beanClass) throws Exception;
}

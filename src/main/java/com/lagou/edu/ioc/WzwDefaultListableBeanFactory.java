package com.lagou.edu.ioc;

import com.google.common.collect.Maps;
import com.lagou.edu.beans.WzwBeanDefinition;

import java.util.Map;

public class WzwDefaultListableBeanFactory extends WzwAbstractApplicationContext {
    protected final Map<String, WzwBeanDefinition> beanDefinitionMap = Maps.newConcurrentMap();
}

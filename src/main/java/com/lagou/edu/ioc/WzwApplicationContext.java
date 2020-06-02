package com.lagou.edu.ioc;

import com.google.common.collect.Lists;
import com.lagou.edu.anno.WzwAutowired;
import com.lagou.edu.anno.WzwService;
import com.lagou.edu.beans.WzwBeanDefinition;
import com.lagou.edu.beans.WzwBeanWrapper;
import com.lagou.edu.core.WzwBeanFactory;
import com.lagou.edu.service.TransferService;
import com.sun.istack.internal.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WzwApplicationContext extends WzwDefaultListableBeanFactory implements WzwBeanFactory {
    private String[] configLocations;
    private WzwBeanDefinitionReader reader;

    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();
    private Map<String, WzwBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, WzwBeanWrapper>();

    public WzwApplicationContext() {
//        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        reader = new WzwBeanDefinitionReader();
        List<WzwBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        doRegisterBeanDefinition(beanDefinitions);
        doAutowired();
    }

    private void doAutowired() {
        for (Map.Entry<String, WzwBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<WzwBeanDefinition> beanDefinitions) throws Exception {
        for (WzwBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The" + beanDefinition.getFactoryBeanName() + "is exists!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        WzwBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        try {
            WzwBeanPostProcessor beanPostProcessor = new WzwBeanPostProcessor();
            Object instance = instantiateBean(beanDefinition);
            if (null == instance) {
                return null;
            }
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            WzwBeanWrapper beanWrapper = new WzwBeanWrapper(instance);
            this.factoryBeanInstanceCache.put(beanName, beanWrapper);
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            polulateBean(beanName, instance);
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void polulateBean(String beanName, Object instance) {
        Class clazz = instance.getClass();
        if (!clazz.isAnnotationPresent(WzwService.class)) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(WzwAutowired.class)) {
                continue;
            }
            WzwAutowired autowired = field.getAnnotation(WzwAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                Class<?> target=Class.forName(autowiredBeanName);
                if(target.isInterface()){
                    field.set(instance, new WzwBeanWrapper(getInterfaceImpls(target).get(0).newInstance()));
                }else {
                    field.set(instance, new WzwBeanWrapper(Class.forName(autowiredBeanName).newInstance()));
                }
//                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private Class<?> getInterfaceImpls(Class<?> target){
        if(target.){

        }
    }

    private Object instantiateBean(WzwBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            if (this.factoryBeanObjectCache.containsKey(className)) {
                instance = this.factoryBeanObjectCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}

package com.lagou.edu.ioc;

import com.google.common.collect.Lists;
import com.lagou.edu.anno.WzwAutowired;
import com.lagou.edu.anno.WzwService;
import com.lagou.edu.beans.WzwBeanDefinition;
import com.lagou.edu.beans.WzwBeanWrapper;
import com.lagou.edu.core.WzwBeanFactory;
import com.lagou.edu.utils.SpringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class WzwApplicationContext extends WzwDefaultListableBeanFactory implements WzwBeanFactory {
    private WzwBeanDefinitionReader reader;

    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();
    private Map<String, WzwBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, WzwBeanWrapper>();

    public WzwApplicationContext() {
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
            if (!super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            }
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        WzwBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
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
    }


    private void polulateBean(String beanName, Object instance) throws Exception {
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
            Class<?> reflectClass = Class.forName(autowiredBeanName);
            if (null == this.factoryBeanInstanceCache.get(autowiredBeanName)) {
                if (reflectClass.isInterface()) {
                    List<String> implClasses = Lists.newArrayList();
                    findImplClass(reflectClass, "com.lagou.edu", implClasses);//多继承实现需要通过设定name值或Qulifier实现，此处暂不处理
                    Class<?> implClass = Class.forName(implClasses.get(0));
                    getBean(implClass.getName());//此处循环依赖暂不处理
                    getBean(SpringUtils.toLowerFirstCase(implClass.getSimpleName()));
                    autowiredBeanName = SpringUtils.toLowerFirstCase(implClass.getSimpleName());
                } else {
                    Class<?> implClass = Class.forName(autowiredBeanName);
                    getBean(implClass.getName());//此处循环依赖暂不处理
                    getBean(SpringUtils.toLowerFirstCase(implClass.getSimpleName()));
                    autowiredBeanName = SpringUtils.toLowerFirstCase(implClass.getSimpleName());
                }
            }
            field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
        }
    }

    private void findImplClass(Class<?> reflectClass, String basePackage, List<String> implClasses) {
        URL url = reflectClass.getClass().getResource("/" + basePackage.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        //遍历包下面所有文件
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                //递归扫描
                findImplClass(reflectClass, basePackage + "." + file.getName(), implClasses);
            } else {
                String className = basePackage + "." + file.getName().replace(".class", "");
                try {
                    Class<?>[] interfaces = Class.forName(className).getInterfaces();
                    for (Integer interCount = 0; interCount < interfaces.length; interCount++) {
                        if (interfaces[interCount].isAssignableFrom(reflectClass)) {
                            implClasses.add(className);
                            break;
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Object instantiateBean(WzwBeanDefinition beanDefinition) {
        if (null == beanDefinition) {
            return null;
        }
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

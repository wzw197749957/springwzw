package com.lagou.edu.ioc;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.common.collect.Lists;
import com.lagou.edu.anno.WzwService;
import com.lagou.edu.beans.WzwBeanDefinition;
import com.lagou.edu.utils.SpringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class WzwBeanDefinitionReader {
    private List<String> registyBeanClasses = new ArrayList<String>();
    private Properties config = new Properties();
    private final String SCAN_PACKAGE = "scanPackage";
    List<String> classPaths = new ArrayList<String>();

    public WzwBeanDefinitionReader() {
        doScanner("com.lagou.edu");
    }

    private void doScanner(String basePackage) {
        if(basePackage.equals("com.lagou.edu.ioc")){
            return;
        }
        URL url = this.getClass().getResource("/" + basePackage.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        //遍历包下面所有文件
        for(File file: dir.listFiles()) {
            if(file.isDirectory()){
                //递归扫描
                doScanner(basePackage + "." + file.getName());
            } else {
                String className = basePackage + "." + file.getName().replace(".class", "");
                registyBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return this.config;
    }

    public List<WzwBeanDefinition> loadBeanDefinitions() {
        List<WzwBeanDefinition> result = new ArrayList<WzwBeanDefinition>();
        try {
            for (String className : registyBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) {
                    continue;
                }
                result.add(doCreateBeanDefinition(SpringUtils.toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private WzwBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        WzwBeanDefinition beanDefinition = new WzwBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }


}

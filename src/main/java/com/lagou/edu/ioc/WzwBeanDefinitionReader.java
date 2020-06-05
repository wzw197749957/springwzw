package com.lagou.edu.ioc;

import com.lagou.edu.beans.WzwBeanDefinition;
import com.lagou.edu.utils.SpringUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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
        if (basePackage.equals("com.lagou.edu.ioc")
                || basePackage.equals("com.lagou.edu.aop")
                || basePackage.equals("com.lagou.edu.core")
                || basePackage.equals("com.lagou.edu.beans")) {
            return;
        }
        URL url = this.getClass().getResource("/" + basePackage.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        //遍历包下面所有文件
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                //递归扫描
                doScanner(basePackage + "." + file.getName());
            } else {
                String className = trimClassName(basePackage + "." + file.getName().replace(".class", ""));
                if(!registyBeanClasses.contains(className)){
                    registyBeanClasses.add(className);
                }
            }
        }
    }

    private String trimClassName(String className){
        Integer loc = className.indexOf("$");
        if(loc>0){
            return className.substring(0,loc);
        }
        return className;
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

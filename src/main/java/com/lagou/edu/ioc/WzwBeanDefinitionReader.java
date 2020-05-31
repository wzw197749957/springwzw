package com.lagou.edu.ioc;

import com.google.common.collect.Lists;
import com.lagou.edu.anno.WzwService;
import com.lagou.edu.beans.WzwBeanDefinition;

import java.io.File;
import java.lang.reflect.Field;
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
        doScanner();
    }

    private void doScanner() {
//        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
//        File classPath = new File(url.getFile());
//        for (File file : classPath.listFiles()) {
//            if (file.isDirectory()) {
//                doScanner();
//            } else {
//                if (!file.getName().endsWith(".class")) {
//                    continue;
//                }
//                String className = (scanPackage + "." + file.getName().replace(".class", ""));
//                registyBeanClasses.add(className);
//            }
//        }
        List<String> classNames = Lists.newArrayList();
        try {
            classNames = searchClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String className : classNames) {
            registyBeanClasses.add(className);
        }
    }

    private List<String> searchClass() throws ClassNotFoundException {
        List<String> classNames = Lists.newArrayList();
//        String basePack = "com.baibin";
//        String classpath = WzwBeanDefinitionReader.class.getResource("/").getPath();
//        basePack = basePack.replace(".", File.separator);
//        String searchPath = classpath + basePack;
//        String searchPath="D:\\springwzw\\src\\main\\java\\com\\lagou\\edu\\service\\HomeworkService.java";
//        doPath(new File(searchPath));
//        for (String s : classPaths) {
//            s = s.replace(classpath.replace("/", "\\").replaceFirst("\\\\", ""), "").replace("\\", ".").replace(".class", "");
//            Class cls = Class.forName(s);
//            if (havaAnnotation(cls)) {
//                classNames.add(s);
//            }
//        }
        classNames.add("com.lagou.edu.service.HomeworkService");
        return classNames;
    }

    private boolean havaAnnotation(Class<?> targetClass) {
        List<Field> fields = Arrays.asList(targetClass.getDeclaredFields());
        for (Field field : fields) {
            if (field.isAnnotationPresent(WzwService.class)) {
                return true;
            }
        }
        return false;
    }

    private void doPath(File file) {
        if (file.isDirectory()) {//文件夹
            //文件夹我们就递归
            File[] files = file.listFiles();
            for (File f1 : files) {
                doPath(f1);
            }
        } else {//标准文件
            //标准文件我们就判断是否是class文件
            if (file.getName().endsWith(".class")) {
                //如果是class文件我们就放入我们的集合中。
                classPaths.add(file.getPath());
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
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
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

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}

package com.my_framework.www.beans;

import com.my_framework.www.annotation.Component;
import com.my_framework.www.utils.ClassUtil;
import com.my_framework.www.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.*;


/**
 * 读取配置文件，扫描相关的类解析成BeanDefinition
 * @author 14629
 */
public class BeanDefinitionReader {
    private final Properties config = new Properties();

    /**
     * 配置文件中指定需要扫描的包名
     */
    private final String SCAN_PACKAGE = "scanPackage";


    public Properties getConfig() {
        return config;
    }

    private Set<Class<?>> registerBeanClasses = new HashSet<>();

    public BeanDefinitionReader(String location) {
        try (
                //1.定位，通过URL定位找到配置文件，然后转换为文件流
             InputStream is = this.getClass().getClassLoader().
                     getResourceAsStream(location)) {
            //2.加载，保存为properties
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //3.扫描，扫描资源文件(class)，并保存到集合中
        registerBeanClasses = ClassUtil.getClassSet(config.getProperty(SCAN_PACKAGE));
    }
    /**
     * 把配置文件中扫描到的所有的配置信息转换为BeanDefinition对象
     */
    public List<BeanDefinition> loadBeanDefinitions() {
        List<BeanDefinition> result = new ArrayList<>();
        try {
            for (Class<?> beanClass : registerBeanClasses) {
                //如果是一个接口，是不能实例化的，不需要封装
                if (beanClass.isInterface()) {
                    continue;
                }
                Annotation[] annotations = beanClass.getAnnotations();
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    //只考虑被@Component注解的class
                    if (annotationType.isAnnotationPresent(Component.class)||annotation instanceof Component) {
                        //beanName有三种情况:
                        //1、默认是类名首字母小写
                        //2、自定义名字（这里暂不考虑）
                        //3、接口注入
                        result.add(doCreateBeanDefinition(StringUtil.toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
//                        Class<?>[] interfaces = beanClass.getInterfaces();
//                        for (Class<?> i : interfaces) {
//                            //接口和实现类之间的关系也需要封装，作用是可以用接口名获取实现类
//                            result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
//                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 相关属性封装到BeanDefinition
     */
    private BeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }


}

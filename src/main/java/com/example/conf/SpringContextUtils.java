package com.example.conf;

import org.springframework.context.ApplicationContext;

public class SpringContextUtils   {

    private static ApplicationContext applicationContext;

    public static  ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    public static void  setApplicationContext(ApplicationContext applicationContext){
        SpringContextUtils.applicationContext =applicationContext;
    }

    public static Object getBean(String beanName){
        return applicationContext.getBean(beanName);
    }

    public static Object getBean(Class<?> aClass){
        return  applicationContext.getBean(aClass);
    }
}

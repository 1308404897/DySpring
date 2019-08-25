package com.duanya.spring.framework.core.load;

import com.duanya.spring.commont.util.StringUtils;
import com.duanya.spring.framework.context.exception.DyContextException;
import com.duanya.spring.framework.context.spring.DySpringApplicationContent;
import com.duanya.spring.framework.core.annotation.DyComponent;
import com.duanya.spring.framework.core.annotation.DyConfiguration;
import com.duanya.spring.framework.core.annotation.DyService;
import com.duanya.spring.framework.core.bean.factory.DyBeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author zheng.liming
 * @date 2019/8/19
 * @description
 */

public class DyIocLoader {

    private static DySpringApplicationContent applicationContent;

    public synchronized static void load() throws ClassNotFoundException, InstantiationException, DyContextException, IllegalAccessException, InvocationTargetException {
       if (null==applicationContent) {
           applicationContent = new DySpringApplicationContent();
       }
        loadBean();
        initConfigurationBean();
        doAutowirteAll();
    }

    private static void loadBean() throws ClassNotFoundException, InstantiationException, IllegalAccessException, DyContextException {

        List<Class> clazzs=DyClassLoader.getClassContainer();

        for (Class beanClass:clazzs){

            if (beanClass.isAnnotationPresent(DyService.class)
                    ||beanClass.isAnnotationPresent(DyComponent.class)||beanClass.isAnnotationPresent(DyConfiguration.class)){

                Object bean = DyBeanFactory.createNewBean(beanClass);

                DyBeanFactory.doFields(bean,DyConfigurationLoader.getEvn());

                String beanName=getAnnotationValue(beanClass);

                if (StringUtils.isEmptyPlus(beanName)){
                    Class[] interfaceClass =  beanClass.getInterfaces();
                  if (interfaceClass.length==1){
                      beanName= interfaceClass[0].getSimpleName();
                  }else {
                      beanName=beanClass.getSimpleName();
                  }
                    beanName=StringUtils.toLowerCaseFirstName(beanName);
                }
                DyBeanFactory.doFields(bean,DyConfigurationLoader.getEvn());
                applicationContent.setBean(beanName,bean);

            }
        }
    }

    private static void initConfigurationBean() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, DyContextException {
        Map<String,Object> context=applicationContent.getContext();
        Iterator<String> iterator=context.keySet().iterator();
        List<Map<String,Object>> list=new ArrayList< Map<String,Object>>();
        while (iterator.hasNext()){
            String key=iterator.next();
                //调用bean的方法创建实例
                Map<String,Object> beans=DyBeanFactory.doMethodsInitialBean(context.get(key));
                list.add(beans);
        }
        //将结果放入dyspring的上下文
       for (Map<String,Object> itme:list){
           applicationContent.setBeanMap(itme);
       }
    }

    private static void doAutowirteAll() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Map<String,Object> context=applicationContent.getContext();
        Iterator iterator=context.keySet().iterator();
        while (iterator.hasNext()){
            String key=(String)iterator.next();
            DyBeanFactory.doAutowired(context.get(key));
        }
    }

    private static String getAnnotationValue(Class c){
        String result=null;
        if (c.isAnnotationPresent(DyService.class)){
            DyService dyService= (DyService) c.getAnnotation(DyService.class);
            result=dyService.value();
        }else if (c.isAnnotationPresent(DyComponent.class)){
            DyComponent dyComponent= (DyComponent) c.getAnnotation(DyComponent.class);
            result=dyComponent.value();
        }
        return result;
    }


}

package com.edison.bigdatakafka.testSpringLife;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2) //指定CommandLineRunner顺序
public class Bean1 implements BeanFactoryAware, ApplicationContextAware, InitializingBean, BeanPostProcessor, CommandLineRunner {
    Logger logger= LoggerFactory.getLogger(Bean1.class);
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        logger.info("Bean1： 初始化前处理：BeanPostProcessor.postProcessBeforeInitialization()被调用，bean={}",bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        logger.info("Bean1： 初始化后处理：BeanPostProcessor.postProcessAfterInitialization()被调用，bean={}",bean);
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        logger.info("Bean1： 工厂级处理：BeanFactoryAware.setBeanFactory()被调用");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Bean1： 属性赋值后处理：InitializingBean.afterPropertiesSet()被调用");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("Bean1： 应用上下文级处理：ApplicationContextAware.setApplicationContext()被调用");
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Bean1： CommandLineRunner.run()被调用");
    }
}

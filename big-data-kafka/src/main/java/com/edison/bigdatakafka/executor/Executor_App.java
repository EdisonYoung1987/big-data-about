package com.edison.bigdatakafka.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**应用处理的线程池，暂时和producer的线程池共用同一套参数*/
@Configuration
public class Executor_App {
    @Value("${app.executor.core-size}")
    private int coreSize;
    @Value("${app.executor.max-size}")
    private int maxSize;
    @Value("${app.executor.queue-size}")
    private int queueSize;

    @Bean(name="exectorApp")
    public ThreadPoolExecutor Executor(){
        ThreadPoolExecutor executor= new ThreadPoolExecutor(coreSize,maxSize,300, TimeUnit.SECONDS,new LinkedBlockingQueue<>(queueSize));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}

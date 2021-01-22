package com.edison.bigdatakafka.tasks;

import com.edison.bigdatakafka.kafka.KafkaSenderUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.ArrayList;
import java.util.List;

/**模拟应用处理的任务*/
public class ApplicationTask implements Runnable {
    private static Logger logger= LoggerFactory.getLogger(ApplicationTask.class);
    private List<ConsumerRecord<String, String>> records;


    public ApplicationTask(List<ConsumerRecord<String, String>> records){
        this.records=records;
    }



    @Override
    public void run() {
        logger.info("当前线程[{}]处理[{}]条数据",Thread.currentThread().getId(),records.size());
        for(ConsumerRecord<String,String> record:records){
            logger.info("主题:{},offset:{},msg={}",record.topic(),record.offset(),record.value());
        }
    }
}

package com.edison.bigdatakafka.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class KafkaSender {
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate; //这个会创建KafkaProducer

    public boolean sendMsg(String topic,String msg){
        //topic-partition 如指定key，则按key的hash%计算，否则轮询-timestamp 时间戳,默认系统时间milli-key 指定分区用-value
        ListenableFuture<SendResult<String, String>> future=kafkaTemplate.send(topic,null,null,null,msg);
        return true;
    }
}

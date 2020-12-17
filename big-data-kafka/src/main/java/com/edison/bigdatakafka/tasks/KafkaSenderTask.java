package com.edison.bigdatakafka.tasks;

import com.edison.bigdatakafka.kafka.KafkaSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**发送kafka数据的任务*/
public class KafkaSenderTask implements Runnable {
    private static Logger logger= LoggerFactory.getLogger(KafkaSenderTask.class);
    private KafkaSender kafkaSender;
    private String topic;
    private List<String> msgs;

    public KafkaSenderTask(KafkaSender kafkaSender,String topic,List<String> msgs){
        this.kafkaSender=kafkaSender;
        this.topic=topic;
        this.msgs=msgs;
    }

    @Override
    public void run() {
        for(String msg:msgs) {
            logger.info("{} 发送 消息{}", Thread.currentThread().getName(), msg);
            kafkaSender.sendMsg(topic, msg);
        }
    }
}

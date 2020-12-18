package com.edison.bigdatakafka.tasks;

import com.edison.bigdatakafka.kafka.KafkaSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.ArrayList;
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

    /*@Override
    public void run() {
        int size=msgs.size();
        logger.info("{} 共需发送消息{}条", Thread.currentThread().getName(), msgs.size());
        List<ListenableFuture<SendResult<String, String>>>  res=new ArrayList<>();
        for(String msg:msgs) {
            logger.info("{} 发送 消息{}", Thread.currentThread().getName(), msg);
            try {
                ListenableFuture<SendResult<String, String>> future = kafkaSender.sendMsgAsync(topic, msg);//如果网络不通，这里会抛异常
                res.add(future);
            }catch (Exception e){
                logger.error("发送kafka异常:",e);
                //针对消息重发
            }
        }
        logger.info("开始获取结果...");
        for(ListenableFuture<SendResult<String, String>> future:res){
            try {
                SendResult<String, String> result = future.get();
                logger.info("结果：{}",result.toString());
            }catch (Exception e){
                logger.error("获取结果异常:",e);
            }
        }
    }*/

    @Override
    public void run() {
        int size=msgs.size();
        logger.info("{} 共需发送消息{}条", Thread.currentThread().getName(), msgs.size());
        List<ListenableFuture<SendResult<String, String>>>  res=new ArrayList<>();
        for(String msg:msgs) {
            logger.info("{} 发送 消息{}", Thread.currentThread().getName(), msg);
            try {
                kafkaSender.sendMsgAsync(topic, msg, new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onFailure(Throwable throwable) { //这个throwable捕获的是get()方法的异常，
                        logger.error("消息发送失败:" + msg, throwable);
                    }

                    @Override
                    public void onSuccess(SendResult<String, String> stringStringSendResult) {
                        logger.info("消息发送成功:{}", msg);
                    }
                });//如果网络不通，这里会抛异常
            }catch (Exception e){
                logger.error("消息发送异常:"+msg,e);
            }
        }
    }
}

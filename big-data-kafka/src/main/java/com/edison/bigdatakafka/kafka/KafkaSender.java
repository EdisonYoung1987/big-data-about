package com.edison.bigdatakafka.kafka;

import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class KafkaSender {
    private static Logger logger= LoggerFactory.getLogger(KafkaSender.class);
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate; //这个会创建KafkaProducer

    /**同步发送:
     * 异常包括：bootservers网络不通、发送buffer满阻塞超时、消息过大、topic不存在且不能创建等等*/
    public boolean sendMsgSync(String topic,String msg) {
        try {
            //topic-partition 如指定key，则按key的hash%计算，否则轮询-timestamp 时间戳,默认系统时间milli-key 指定分区用-value
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, null, null, null, msg);
            SendResult<String, String> res = future.get();
            if (res != null && res.getRecordMetadata() != null) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            logger.error("发送kafka异常：",e);
            return false;
        }
    }

    /**异步发送1:需要自己对future.get()*/
    public ListenableFuture<SendResult<String, String>> sendMsgAsync(String topic,String msg) throws Exception{
        //topic-partition 如指定key，则按key的hash%计算，否则轮询-timestamp 时间戳,默认系统时间milli-key 指定分区用-value
        ListenableFuture<SendResult<String, String>> future=kafkaTemplate.send(topic,null,null,null,msg);
        return future;
    }

    /**异步发送2：指定回调方法onSuccess,onFailure*/
    public void sendMsgAsync(String topic, String msg, ListenableFutureCallback<SendResult<String, String>> callback) throws  Exception {
          kafkaTemplate.send(topic,null,null,null,msg).addCallback(callback);
    }
}

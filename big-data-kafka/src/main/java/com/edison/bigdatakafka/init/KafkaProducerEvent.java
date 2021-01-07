package com.edison.bigdatakafka.init;

import com.alibaba.fastjson.JSON;
import com.edison.bigdatakafka.entity.PassCar;
import com.edison.bigdatakafka.kafka.KafkaSender;
import com.edison.bigdatakafka.tasks.KafkaSenderTask;
import com.edison.bigdatakafka.utils.RandomPasscarGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**应用启动时启动，每隔一定时间生成一些模拟数据交由executor执行，发送到kafka*/
@Component
public class KafkaProducerEvent implements ApplicationListener<ApplicationReadyEvent> {
    private static Logger logger= LoggerFactory.getLogger(KafkaProducerEvent.class);
    private static final int LISTCAPACITY=20;

    @Autowired
    KafkaSender kafkaSender;
    @Autowired
    ThreadPoolExecutor executor;
    @Value("${app.topicName.passCarTopic}")
    private String passCarTopic;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        logger.info("KafkaProducerEvent启动...");
        PassCar passCar=new PassCar();
        while(true){
            try{
                List<String> msgs=new ArrayList<>(LISTCAPACITY);
                for(int i=0;i<LISTCAPACITY;i++){
                    passCar.setPassTime(System.currentTimeMillis());
                    passCar.setPlateNum(RandomPasscarGenerator.genPlateNum());
                    passCar.setDeviceId(RandomPasscarGenerator.genRandomNums(20));
                    msgs.add(JSON.toJSONString(passCar));
                }

                KafkaSenderTask kafkaSenderTask=new KafkaSenderTask(kafkaSender,passCarTopic,msgs);
                executor.execute(kafkaSenderTask);
                Thread.currentThread().sleep(1000);
                break;
            }catch(Exception e){//这个异常一般是发送前的异常，如序列化错误、缓冲区满、连接broker失败等等。
                e.printStackTrace();
            }
        }
    }
}

package com.edison.bigdatakafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BigDataKafkaApplication {
    private static Logger logger= LoggerFactory.getLogger(BigDataKafkaApplication.class);

    public static void main(String[] args) {
        logger.info("启动big-data-kafka");
        SpringApplication.run(BigDataKafkaApplication.class, args);
        logger.info("启动成功");
    }

}

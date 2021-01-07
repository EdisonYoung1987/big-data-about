package com.edison.bigdataredis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BigDataRdisApplication {
    private static Logger logger= LoggerFactory.getLogger(BigDataRdisApplication.class);

    public static void main(String[] args) {
        logger.info("启动big-data-redis");
        SpringApplication.run(BigDataRdisApplication.class, args);
        logger.info("启动成功");
    }

}

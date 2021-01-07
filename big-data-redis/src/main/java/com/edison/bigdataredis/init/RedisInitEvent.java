package com.edison.bigdataredis.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;


/**应用启动时启动，每隔一定时间生成一些模拟数据交由executor执行，发送到kafka*/
@Component
public class RedisInitEvent implements ApplicationListener<ApplicationReadyEvent> {
    private static Logger logger= LoggerFactory.getLogger(RedisInitEvent.class);
    private static final int LISTCAPACITY=20;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        logger.info("RedisInitEvent...");
        Set<String> keys=redisTemplate.keys("bkhp:*");
        logger.info("个数:{}",keys.size());
        for(String key:keys){
            logger.info(key);
            logger.info(redisTemplate.opsForValue().get(key));
            redisTemplate.opsForSet().add("bk_hp_set",key);
        }
logger.info("-------------------------");
        keys=redisTemplate.opsForSet().members("bk_hp_set");
        for(String key:keys){
            logger.info(key);
            logger.info(redisTemplate.opsForValue().get(key));
        }
    }
}

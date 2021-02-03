package com.edison.bigdatakafka.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author User
 */
@Configuration
@EnableKafka
public class KafkaConsumerAutoCommitConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;

    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private Integer autoCommitInterval;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer maxPollRecords;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.listener.concurrency}")
    private Integer concurrency;


    /**
     * 消费者配置信息-自动提交--在spring-kafka调用kafkaClient.poll()时由kafkaClient自动提交
     */
    public Map<String, Object> autoCommitConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //kafka0.9之前消费者的offset存于zk，之后版本存在一个单独的topic，__consumer_offsets，如果这个数据丢失，则消费者需要重置offset：
        // 如果存在已经提交的offest时(手动或自动提交的),不管设置为earliest 或者latest 都会从已经提交的offest处开始消费
        // 如果不存在已经提交的offest时,earliest 表示从头开始消费,latest 表示从最新的数据消费,也就是新产生的数据.
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);//针对offset的重置：默认latest
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);//broker实例地址
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);//每次拉取消息最大数量，默认500
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,300000);//拉取消息最大间隔毫秒，默认300秒
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 120000);//session会话超时，默认10秒
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 180000);//请求超时30000
//        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,) //心跳间隔
        //ENABLE_AUTO_COMMIT_CONFIG //自动提交最好显示设置，否则取值为null，最终会被设置为false
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);
//        //AUTO_COMMIT_INTERVAL_MS_CONFIG 自动提交间隔
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,1000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); //key反序列化
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);//value反序列化
        return props;
    }

    /**
     * 消费者并发批量消费配置
     */
    @Bean("autoCommitContainerFactory")
    public KafkaListenerContainerFactory<?> autoCommitContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(autoCommitConsumerConfigs()));
        //设置并发线程数
        //这个表示会创建多少个消费者，如果服务是多节点，可以设置为1，如果是单节点，可以考虑设置N(CPU核数)+1
        //但是不能超过topic的分区数量，不然多余的消费者并没有意义
        factory.setConcurrency(concurrency);
//        factory.setConcurrency(Runtime.getRuntime().availableProcessors());

        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        //BatchMessagingMessageListenerAdapter，否则创建的是RecordMessagingMessageListenerAdapter
        factory.setBatchListener(true);
        //自动提交时这个参数无意义
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

        return factory;

    }
}
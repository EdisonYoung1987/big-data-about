package com.edison.bigdatakafka.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
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

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author User
 */
@Configuration
@EnableKafka
public class KafkaConfiguration {

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

    @Value("${spring.kafka.producer.retries}")
    private Integer retries;

    @Value("${spring.kafka.producer.batch-size}")
    private Integer batchSize;

    @Value("${spring.kafka.producer.buffer-memory}")
    private Integer bufferMemory;

    @Value("${spring.kafka.listener.concurrency}")
    private Integer concurrency;

    /**     如果使用spring-boot-autoconfigure自带的KafkaAutoConfiguration，他会根据spring.kafka.*实例化配置KafkaProperties，
     * 然后用new  DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties())生成生产者工厂，然后再用
     * new KafkaTemplate<>(kafkaProducerFactory)生成kafkaTemplate实例用来操作生成者；
     *      而消费者方面，则是通过new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties())生成消费者工厂，
     * 再用来创建kafkaListenerContainerFactory，然后@KafkaListener会指定该工厂进行初次配置消费者，然后KafkaListenerAnnotationBeanPostProcessor
     * 再次配置消费者，比如自动生成groupId；
     * */

    /**
     * 生产者配置信息
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.ACKS_CONFIG, "-1");//等同all，需leader和isr中所有的followers都保存成功，0-不管是否成功，1-只leader成功即可
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);//服务器地址
        props.put(ProducerConfig.RETRIES_CONFIG, retries); //重试次数
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG,100);//重试间隔，100ms
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true");//开启冪等检查，防止消息重复发送
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize); //每个批次的最大数据量 默认16k=16384字节
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5); //每个批次处理的最大间隔毫秒
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,120 * 1000);//每个消息从发送到
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory); //发送缓冲区大小
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,30*1000);
        return props;
    }

    /**
     * 生产者工厂
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * 生产者模板
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * 消费者配置信息
     */
    @Bean
    public Map<String, Object> consumerConfigs() {
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
        //ENABLE_AUTO_COMMIT_CONFIG
        //AUTO_COMMIT_INTERVAL_MS_CONFIG 自动提交间隔
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); //key反序列化
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);//value反序列化
        return props;
    }

    /**
     * 消费者并发批量消费配置
     */
    @Bean
    public KafkaListenerContainerFactory<?> batchFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfigs()));
        //设置并发线程数
//        factory.setConcurrency(concurrency);
        factory.setConcurrency(Runtime.getRuntime().availableProcessors());//这个表示会创建多少个消费者，如果服务是多节点，可以设置为1，如果是单节点，可以考虑设置N(CPU核数)+1

        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.setBatchListener(true);
        return factory;

    }
}
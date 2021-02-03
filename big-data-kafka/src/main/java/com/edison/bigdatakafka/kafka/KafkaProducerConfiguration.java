package com.edison.bigdatakafka.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author User
 * kafka生产者配置
 */
@Configuration
@EnableKafka
public class KafkaProducerConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.retries}")
    private Integer retries;

    @Value("${spring.kafka.producer.batch-size}")
    private Integer batchSize;

    @Value("${spring.kafka.producer.buffer-memory}")
    private Integer bufferMemory;

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

}
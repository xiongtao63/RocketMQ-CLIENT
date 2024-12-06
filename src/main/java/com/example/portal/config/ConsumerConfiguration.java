package com.example.portal.config;


import com.example.portal.dto.Chapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class ConsumerConfiguration {

    @Bean
    public MQConsumer mqConsumer(){
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("cg-portal");
        try {
            ObjectMapper mapper = new ObjectMapper();
            consumer.setNamesrvAddr("localhost:9876");
            consumer.subscribe("PORTAL","*");
            consumer.setMessageModel(MessageModel.CLUSTERING); //集群模式
            consumer.setMessageModel(MessageModel.BROADCASTING); //广播模式

            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                    try {
                        for(MessageExt msg:list){
                            log.info("接收到新章节数据："+msg.getMsgId() + "===>"+new String(msg.getBody()));
                            Chapter chapter = mapper.readValue(msg.getBody(), Chapter.class);
                            log.info("["+chapter.getSeriesName()+"]系列新内容["+chapter.getChapterTitle()+"]已在门户网站更新");
                        }
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } catch (Exception e) {
                        log.info("接收章节异常",e);
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;//稍后重试
                    }
                }
            });
            consumer.start();
            log.info("PortalConsumer启动成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return consumer;
    }
}

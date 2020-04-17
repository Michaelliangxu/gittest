package com.example.send.config;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.send.entity.ConfirmLog;
import com.example.send.mapper.ConfirmLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @description:
 * @author: xuliang
 * @createDate: 2020/4/9
 * @version: 1.0
 */
@Configuration
@Slf4j
public class RabbitConfig {
    @Autowired
    private ConfirmLogMapper confirmLogMapper;

    // 消息发送到交换器Exchange后触发回调
    private final RabbitTemplate.ConfirmCallback confirmCallback =
            new RabbitTemplate.ConfirmCallback() {
                @Override
                public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                    //查重试次数
                    QueryWrapper<ConfirmLog> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("message_id", correlationData.getId());
                    ConfirmLog confirmLog1 = confirmLogMapper.selectOne(queryWrapper);
                    if (null!=confirmLog1) {
                        //更新状态，重试次数+1
                        if (ack) {
                            ConfirmLog confirmLog = new ConfirmLog();
                            confirmLog.setMessage((StringUtils.isEmpty(cause))?"none":cause);
                            //更新状态
                            confirmLog.setStatus("1");
                            confirmLog.setUpdateTime(new Date());
                            confirmLog.setNextRetry(DateUtil.offset(new Date(), DateField.MINUTE, 5));
                            confirmLog.setTryCount(confirmLog1.getTryCount()+1);

                            QueryWrapper<ConfirmLog> queryWrapper1 = new QueryWrapper<>();
                            queryWrapper1.eq("message_id", correlationData.getId());
                            confirmLogMapper.update(confirmLog,queryWrapper1);
                            System.out.println("消息可达 ==== ");
                        }
                    }
                }
            };

    //消息不可达queue
    private final RabbitTemplate.ReturnCallback returnCallback=
            new RabbitTemplate.ReturnCallback(){
                @Override
                public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                    System.out.println("message = " + message);
                    System.out.println("replyCode = " + replyCode);
                    System.out.println("replyText = " + replyText);
                    System.out.println("exchange = " + exchange);
                    System.out.println("routingKey = " + routingKey);
                }
            };
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //rabbitTemplate.setConfirmCallback(confirmCallback);
        //rabbitTemplate.setReturnCallback(returnCallback);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }




}

//package com.example.send.jianshu;
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.DirectExchange;
//import org.springframework.amqp.core.Exchange;
//import org.springframework.amqp.core.FanoutExchange;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by shuai on 2019/5/16.
// */
//@Configuration
//public class RabbitMQCreateConfig {
//
//    @Resource(name = "publicRabbitAdmin")
//    private RabbitAdmin publicRabbitAdmin;
//
////    @Resource(name = "someOtherRabbitAdmin")
////    private RabbitAdmin iqianzhanRabbitAdmin;
//
//    @PostConstruct
//    public void RabbitInit() {
//
//
///*
//        publicRabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));
//        publicRabbitAdmin.declareExchange(new TopicExchange("test.topic", false, false));
//        publicRabbitAdmin.declareExchange(new FanoutExchange("test.fanout", false, false));
//
//        publicRabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
//        publicRabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
//        publicRabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));
//
//        publicRabbitAdmin.declareBinding(new Binding("test.direct.queue",
//                Binding.DestinationType.QUEUE,
//                "test.direct", "direct", new HashMap<>()));
//
//        publicRabbitAdmin.declareBinding(
//                BindingBuilder
//                        .bind(new Queue("test.topic.queue", false))        //直接创建队列
//                        .to(new TopicExchange("test.topic", false, false))    //直接创建交换机 建立关联关系
//                        .with("user.#"));    //指定路由Key
//
//        publicRabbitAdmin.declareBinding(
//                BindingBuilder
//                        .bind(new Queue("test.fanout.queue", false))
//                        .to(new FanoutExchange("test.fanout", false, false)));
//
//*/
//    }
//}
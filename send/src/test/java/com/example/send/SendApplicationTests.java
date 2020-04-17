package com.example.send;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.send.entity.ConfirmLog;
import com.example.send.entity.Order;
import com.example.send.entity.User;
import com.example.send.mapper.ConfirmLogMapper;
import com.example.send.mapper.OrderMapper;
import com.example.send.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SendApplication.class)
@Slf4j
public class SendApplicationTests {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ConfirmLogMapper confirmLogMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    //@Transactional
    public void send() {
        //插入业务
        String messageId = IdUtil.getSnowflake(1, 1).nextIdStr();
        Order order = new Order();
        order.setMessageId(messageId);
        order.setName("confirm..msg");
        orderMapper.insert(order);

        //插入消息log
        ConfirmLog confirmLog = new ConfirmLog();
        confirmLog.setMessageId(messageId);
        confirmLog.setMessage("none");
        confirmLog.setUpdateTime(new Date());
        confirmLog.setCreateTime(new Date());
        confirmLog.setNextRetry(DateUtil.offset(new Date(), DateField.MINUTE, 5));
        confirmLog.setStatus("0");
        confirmLog.setTryCount(1);
        confirmLogMapper.insert(confirmLog);

        //发送消息
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
//        Message message = new Message(JSON.toJSONString(order).getBytes(), messageProperties);
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(messageId);
        rabbitTemplate.convertAndSend("exchange-dead", "dead.tt", order, correlationData);
    }


    @Test
    public void redis() {
        QueryWrapper<ConfirmLog> queryConfirmLog = new QueryWrapper<>();
        queryConfirmLog.eq("status", 0).le("try_count", 3);
        List<ConfirmLog> confirmLogs = confirmLogMapper.selectList(queryConfirmLog);
//        stringRedisTemplate.opsForValue().append("test:kv1", "test1");
//        stringRedisTemplate.opsForValue().append("test:kv2", "test2");
        System.out.println("confirmLogs = " + confirmLogs);
    }

    @Test
    public void sendDead() throws InterruptedException {
        User user = new User(1, "xuliang", "123456");
        MessageProperties mps = new MessageProperties();
        mps.setCorrelationId(IdUtil.getSnowflake(1, 1).nextIdStr());
        mps.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        Message message = new Message(JSON.toJSONString(user).getBytes(), mps);
        for (int i = 0; i < 1; i++) {
            CorrelationData correlationData = new CorrelationData();
            correlationData.setId(i + "");
            rabbitTemplate.convertAndSend("exchange-order", "order.ttl", message, correlationData);
        }

    }

    @Test
    public void sendObj() {
        User user = new User(1, "xuliang", "123456");
        rabbitTemplate.convertAndSend("exchange-dead", "dead.tt", user);
    }


    @Test
    public void sendMap() throws InterruptedException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "xu");
        map.put("age", 20);
        rabbitTemplate.convertAndSend("exchange-dead", "dead.tt", map);
    }

    @Test
    public void sendStr() throws InterruptedException {
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId("1234545451515");
        rabbitTemplate.convertAndSend("exchange-dead", "dead.tt", "message", correlationData);
    }

    @Test
    public void log() throws InterruptedException {
        try {
            User user = new User(1, "xuliang", "123456");
            log.error("This bean is [{}]", user);
            log.warn("Time is:[{}]", DateUtil.date());
            int i = 1 / 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rabbitAdmin() {
        rabbitAdmin.purgeQueue("queue003");
    }


}

package com.example.send.jobhandler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.send.entity.ConfirmLog;
import com.example.send.entity.Order;
import com.example.send.mapper.ConfirmLogMapper;
import com.example.send.mapper.OrderMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、在Spring Bean实例中，开发Job方法，方式格式要求为 "public ReturnT<String> execute(String param)"
 * 2、为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
//@Component
public class CheckACKStatusXxlJob {
    private static Logger logger = LoggerFactory.getLogger(CheckACKStatusXxlJob.class);

    @Autowired
    private ConfirmLogMapper confirmLogMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @XxlJob("checkACK")
    public ReturnT<String> checkACKStatus(String param) throws Exception {
        QueryWrapper<ConfirmLog> queryConfirmLog = new QueryWrapper<>();
        queryConfirmLog.eq("status", 0).le("try_count", 3);
        List<ConfirmLog> confirmLogs = confirmLogMapper.selectList(queryConfirmLog);
        if (!CollectionUtils.isEmpty(confirmLogs)) {
            for (ConfirmLog c : confirmLogs) {
                QueryWrapper<Order> queryOrder = new QueryWrapper<>();
                queryOrder.eq("message_id", c.getMessageId());
                Order order = orderMapper.selectOne(queryOrder);

                Message message = new Message(JSON.toJSONString(order).getBytes(), null);
                CorrelationData correlationData = new CorrelationData(order.getMessageId());
                rabbitTemplate.convertAndSend("exchange-dead", "dead.test", message, correlationData);
            }
        }
        DateTime date = DateUtil.date();
        System.out.println("System.currentTimeMillis() = " + date.toString());
        return ReturnT.SUCCESS;
    }

    /**
     * 5、生命周期任务示例：任务初始化与销毁时，支持自定义相关逻辑；
     */
    @com.xxl.job.core.handler.annotation.XxlJob(value = "demoJobHandler2", init = "init", destroy = "destroy")
    public ReturnT<String> demoJobHandler2(String param) throws Exception {
        XxlJobLogger.log("XXL-JOB, Hello World.");
        return ReturnT.SUCCESS;
    }

    public void init() {
        logger.info("init");
    }

    public void destroy() {
        logger.info("destory");
    }


}

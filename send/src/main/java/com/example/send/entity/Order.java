package com.example.send.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description:
 * @author: xuliang
 * @createDate: 2020/4/13
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_order")
public class Order implements Serializable {
    private static final long serialVersionUID = -8893044706813878717L;

    @TableId(type=IdType.ID_WORKER_STR)
    private String id;

    private String name;

    private String messageId;
}

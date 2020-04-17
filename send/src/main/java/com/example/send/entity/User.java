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
 * @createDate: 2020/4/11
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@TableName("t_user")
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    private int id;

    private String username;

    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

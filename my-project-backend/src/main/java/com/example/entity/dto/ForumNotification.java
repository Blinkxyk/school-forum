package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("db_forum_notification")
public class ForumNotification {
    @TableId(type = IdType.AUTO)
    Integer id;
    String content;
}

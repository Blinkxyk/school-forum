package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    @Select("""
            select content from db_forum_notification
            """)
    String getForumNotification();

    @Update("""
            UPDATE `school-forum`.`db_forum_notification` SET `content` = #{content}
            """
    )
    void updateForumNotification(String content);
}

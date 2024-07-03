package com.example.entity.dto;

import lombok.Data;
import java.util.Date;

@Data
public class TopicWithUserInfo {
    private Integer id;
    private String title;
    private String content;
    private Integer type;
    private Date time;
    private Integer uid;
    private String username;
    private String avatar;
    private Integer likeCount;
    private Integer collectCount;
}

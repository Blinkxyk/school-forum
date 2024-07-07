package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Interact;

import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicWithUserInfo;

import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.*;
import com.example.service.NotificationService;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import com.example.utils.Const;
import com.example.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Resource
    WeatherService service;

    @Resource
    TopicService topicService;

    @Resource
    NotificationService notificationService;

    @Resource
    ControllerUtils utils;

    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude){
        WeatherVO vo = service.fetchWeather(longitude, latitude);
        return vo == null ?
                RestBean.failure(400, "获取地理位置信息与天气失败，请联系管理员！") : RestBean.success(vo);
    }

    @GetMapping("/types")
    public RestBean<List<TopicTypeVO>> listTypes(){
        return RestBean.success(topicService
                .listTypes()
                .stream()
                .map(type -> type.asViewObject(TopicTypeVO.class))
                .toList());
    }

    @PostMapping("/create-topic")
    public RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int id) {
        return utils.messageHandle(() -> topicService.createTopic(id, vo));
    }

    @GetMapping("/list-topic")
    public RestBean<List<TopicPreviewVO>> listTopic(@RequestParam @Min(0) int page,
                                                    @RequestParam @Min(0) int type) {
        return RestBean.success(topicService.listTopicByPage(page + 1, type));
    }

    @GetMapping("/top-topic")
    public RestBean<List<TopicTopVO>> topTopic(){
        return RestBean.success(topicService.listTopTopics());
    }

    @GetMapping("/topic")
    public RestBean<TopicDetailVO> topic(@RequestParam @Min(0) int tid,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id){
        return RestBean.success(topicService.getTopic(tid, id));
    }

    @GetMapping("/interact")
    public RestBean<Void> interact(@RequestParam @Min(0) int tid,
                                   @RequestParam @Pattern(regexp = "(like|collect)") String type,
                                   @RequestParam boolean state,
                                   @RequestAttribute(Const.ATTR_USER_ID) int id) {
        topicService.interact(new Interact(tid, id, new Date(), type), state);
        return RestBean.success();
    }

    @GetMapping("/collects")
    public RestBean<List<TopicPreviewVO>> collects(@RequestAttribute(Const.ATTR_USER_ID) int id){
        return RestBean.success(topicService.listTopicCollects(id));
    }

    @PostMapping("/update-topic")
    public RestBean<Void> updateTopic(@Valid @RequestBody TopicUpdateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int id){
        return utils.messageHandle(() -> topicService.updateTopic(id, vo));
    }

    @PostMapping("/add-comment")
    public RestBean<Void> addComment(@Valid @RequestBody AddCommentVO vo,
                                     @RequestAttribute(Const.ATTR_USER_ID) int id){
        return utils.messageHandle(() -> topicService.createComment(id, vo));
    }

    @GetMapping("/comments")
    public RestBean<List<CommentVO>> comments(@RequestParam @Min(0) int tid,
                                              @RequestParam @Min(0) int page){
        return RestBean.success(topicService.comments(tid, page + 1));
    }

    @GetMapping("/delete-comment")
    public RestBean<Void> deleteComment(@RequestParam @Min(0) int id,
                                        @RequestAttribute(Const.ATTR_USER_ID) int uid){
        topicService.deleteComment(id, uid);
        return RestBean.success();
    }
    // 新添加的通过标题搜索Topic的方法
    @GetMapping("/search-topic")
    public RestBean<List<TopicPreviewVO>> searchTopicsByTitle(@RequestParam String title) {

        List<TopicWithUserInfo> topics = topicService.searchTopicsByTitle(title);

        if (topics.isEmpty()) {
            return RestBean.success(Collections.emptyList());
        }

        List<TopicPreviewVO> previewVOs = topics.stream()
                .map(this::convertToTopicPreviewVO)
                .collect(Collectors.toList());
        return RestBean.success(previewVOs);
    }

    @GetMapping("/my-topic")
    public RestBean<List<TopicPreviewVO>> searchMyTopic(@RequestParam int uid) {
        List<TopicWithUserInfo > topics = topicService.searchMyTopic(uid);
        if (topics.isEmpty()) {
            return RestBean.success(Collections.emptyList());
        }

        List<TopicPreviewVO> previewVOs = topics.stream()
                .map(this::convertToTopicPreviewVO)
                .collect(Collectors.toList());
        return RestBean.success(previewVOs);
    }

    private TopicPreviewVO convertToTopicPreviewVO(Topic topic) {
        TopicPreviewVO vo = new TopicPreviewVO();
        BeanUtils.copyProperties(topic, vo);
        // 如果TopicPreviewVO需要额外的数据处理，可以在这里添加
        // 例如，vo.setSomeField(computeSomeValue(topic));
        return vo;
    }

    private TopicPreviewVO convertToTopicPreviewVO(TopicWithUserInfo topic) {
        TopicPreviewVO vo = new TopicPreviewVO();
        BeanUtils.copyProperties(topic, vo);
        vo.setUsername(topic.getUsername());
        vo.setAvatar(topic.getAvatar());
        vo.setLike(topic.getLikeCount());
        vo.setCollect(topic.getCollectCount());
        return vo;
    }

    //删除帖子
    @GetMapping("/delete-topic")
    public RestBean<Void> deleteTopic(@RequestParam int id) {
        topicService.deleteTopic(id);
        return RestBean.success();
    }

    @GetMapping("/set-top")
    public RestBean<Void> setTop(@RequestParam int id) {
            topicService.setTop(id);
        return RestBean.success();
    }

    @GetMapping("/get-forum-notification")
    public RestBean<String> getNotification() {
        String notification = notificationService.getForumNotification();
        return RestBean.success(notification);
    }

}

package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Interact;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicWithUserInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
    @Insert("""
            <script>
                insert ignore into db_topic_interact_${type} values
                <foreach collection ="interacts" item="item" separator =",">
                    (#{item.tid}, #{item.uid}, #{item.time})
                </foreach>
            </script>
            """)
    void addInteract(List<Interact> interacts, String type);

    @Delete("""
            <script>
                delete from db_topic_interact_${type} where
                <foreach collection="interacts" item="item" separator=" or ">
                    (tid = #{item.tid} and uid = #{item.uid})
                </foreach>
            </script>
            """)
    int deleteInteract(List<Interact> interacts, String type);

    @Select("""
            select count(*) from db_topic_interact_${type} where tid = #{tid}
            """)
    int interactCount(int tid, String type);

    @Select("""
            select count(*) from db_topic_interact_${type} where tid = #{tid} and uid = #{uid}
            """)
    int userInteractCount(int tid, int uid, String type);

    @Select("""
            select * from db_topic_interact_collect left join db_topic on tid = db_topic.id
             where db_topic_interact_collect.uid = #{uid}
            """)
    List<Topic> collectTopics(int uid);

    @Select("""
            select t.*, u.username, u.avatar,
                   (select count(*) from db_topic_interact_like where tid = t.id) as like_count,
                   (select count(*) from db_topic_interact_collect where tid = t.id) as collect_count
            from db_topic t
            left join db_account u on t.uid = u.id
            where t.title like CONCAT('%',#{title},'%')
            """)
    List<TopicWithUserInfo> searchTopicsByTitle(@Param("title") String title);

    @Delete("DELETE FROM db_topic WHERE id = #{id}")
    void deleteTopic( @Param("id") int id);

    @Select("""
            select t.*, u.username, u.avatar,
                   (select count(*) from db_topic_interact_like where tid = t.id) as like_count,
                   (select count(*) from db_topic_interact_collect where tid = t.id) as collect_count
            from db_topic t
            left join db_account u on t.uid = u.id
            where t.uid = #{uid}
            """)
    List<TopicWithUserInfo> searchMyTopic(int uid);

    @Update("""
            UPDATE db_topic
            SET top = CASE WHEN top = 1 THEN 0 ELSE 1 END
            WHERE id = #{id}
            """)
    void setTop(@Param("id") int id);

}

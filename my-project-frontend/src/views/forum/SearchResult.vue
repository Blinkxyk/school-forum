<script setup>
import {onMounted} from "vue";
import {useStore} from "@/store";
import router from "@/router";
import LightCard from "@/components/LightCard.vue";
import {ElMessage} from "element-plus";
import {
    Clock, CircleCheck, Star
} from "@element-plus/icons-vue";
import TopicTag from "@/components/TopicTag.vue";

const store = useStore();

onMounted(() => {
    if (store.searchResults.length === 0) {
        ElMessage.info('未找到相关帖子');
    }
});
</script>

<template>
    <div style="display: flex;margin: 20px auto;gap: 20px;max-width: 900px">
        <div style="flex: 1">
            <transition name="el-fade-in" mode="out-in">
                <div v-if="store.searchResults.length">
                    <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
                        <light-card v-for="item in store.searchResults" :key="item.id" class="topic-card"
                                    @click="router.push('/index/topic-detail/'+item.id)">
                            <div style="display: flex">
                                <div>
                                    <el-avatar :size="30" :src="store.avatarUserUrl(item.avatar)"/>
                                </div>
                                <div style="margin-left: 7px;transform: translateY(-2px)">
                                    <div style="font-size: 13px;font-weight: bold">{{item.username}}</div>
                                    <div style="font-size: 12px;color: grey">
                                        <el-icon><Clock/></el-icon>
                                        <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">
                                            {{new Date(item.time).toLocaleString()}}
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div style="margin-top: 5px">
                                <topic-tag :type="item.type"/>
                                <span style="font-weight: bold;margin-left: 7px">{{item.title}}</span>
                            </div>
                            <div class="topic-content">{{item.text}}</div>
                            <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                                <el-image class="topic-image" v-for="img in item.images" :key="img" :src="img" fit="cover"></el-image>
                            </div>
                            <div style="display: flex;gap: 20px;font-size: 13px;margin-top: 10px;opacity: 0.8">
                                <div>
                                    <el-icon style="vertical-align: middle"><CircleCheck/></el-icon> {{item.like}}点赞
                                </div>
                                <div>
                                    <el-icon style="vertical-align: middle"><Star/></el-icon> {{item.collect}}收藏
                                </div>
                            </div>
                        </light-card>
                    </div>
                </div>
            </transition>
        </div>
    </div>
</template>

<style lang="less" scoped>
.topic-card {
    padding: 15px;
    transition: scale .3s;

    &:hover {
        scale: 1.015;
        cursor: pointer;
    }

    .topic-content {
        font-size: 13px;
        color: grey;
        margin: 5px 0;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 3;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .topic-image {
        width: 100%;
        height: 100%;
        max-height: 110px;
        border-radius: 5px;
    }
}
</style>

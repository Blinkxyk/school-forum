<script setup>
import {get, logout} from '@/net'
import router from "@/router";
import {useStore} from "@/store";
import {onMounted, reactive, ref, watch} from "vue";
import {
    Back,
    Bell,
    ChatDotSquare, Check, Collection, DataLine,
    Document, Files,
    Location, Lock, Message, Monitor,
    Notification, Operation,
    Position,
    School, Search,
    Umbrella, User
} from "@element-plus/icons-vue";
import LightCard from "@/components/LightCard.vue";
import {ElMessage} from "element-plus";
import {useDark, useToggle} from "@vueuse/core/index";

const store = useStore()
const loading = ref(true)
const topics = reactive({
    list: [],
    page: 0,
    end: false,
});

const searchInput = reactive({
    type: '1',
    text: ''
})
const notification = ref([])

get('/api/user/info', (data) => {
    store.user = data
    loading.value = false
})
const loadNotification =
        () => get('/api/notification/list', data => notification.value = data)
loadNotification()

function userLogout() {
    logout(() => router.push("/"))
}

function confirmNotification(id, url) {
    get(`/api/notification/delete?id=${id}`, () => {
        loadNotification()
        window.open(url)
    })
}

function deleteAllNotification() {
    get(`/api/notification/delete-all`, loadNotification)
}

// 搜索帖子的函数
/*function searchTopics() {
    store.searchResults = null; // 清空搜索结果;
    if (!searchInput.text.trim()) {
        ElMessage.warning('请输入搜索关键词');
        return;
    }
    const url = `/api/forum/search-topic?title=${encodeURIComponent(searchInput.text)}`;
    get(url, (data) => {
        if (data && data.length > 0) {
            store.searchResults = data; // 假设你在store中定义了searchResults来存储搜索结果
            router.push('/index/search-results/'+searchInput.text); // 假设你有一个显示搜索结果的路由
        } else {
            ElMessage.info('未找到相关帖子');
        }
    });
}*/
let isSearching = ref(false);
const searchTopics = () => {
    if (!searchInput.text.trim()) {
        ElMessage.warning('请输入搜索关键词');
        return;
    }
    isSearching=true;
    const url = `/api/forum/search-topic?title=${encodeURIComponent(searchInput.text)}`;
    get(url, (data) => {
        if (data && data.length > 0) {
            topics.list = data;

            store.searchResults = data; // 更新 store 中的搜索结果
            router.push('/index/search-results/'+searchInput.text);
        } else {
            topics.list = [];
            ElMessage.info('未找到相关帖子');
        }
    });
    isSearching=false;
}

/*watch(() => store.searchResults, () => {
    loadSearchResults();
}, { immediate: true });*/

const loadSearchResults = () => {
    topics.page = 0;
    topics.end = false;
    topics.list = store.searchResults;
}

onMounted(() => {
    if (store.searchResults.length === 0&&isSearching.value) {
        ElMessage.info('未找到相关帖子');
    } else {
        loadSearchResults();
    }
});
function handleCommand(command) {
    router.push(command);
}
const isDark = useDark()
const toggleDarkMode = useToggle(isDark)
</script>

<template>
    <div class="main-content" v-loading="loading" element-loading-text="正在进入，请稍后...">
        <el-container style="height: 100%" v-if="!loading">
            <el-header class="main-content-header">
                <el-image class="logo" src="https://www.cse.cqu.edu.cn/img/20231025121046.png"/>
                <div style="flex: 1;padding: 0 20px;text-align: center">
                    <el-input v-model="searchInput.text" placeholder="搜索论坛相关内容..."
                              @keyup.enter="searchTopics">
                    style="width: 100%; max-width: 500px">
                    <template #prefix>
                        <el-icon @click="searchTopics"><Search/></el-icon>
                    </template>
                    <template #append>
                        <el-select style="width: 120px" v-model="searchInput.type">
                            <el-option value="1" label="帖子广场"/>
                            <el-option value="2" label="校园活动"/>
                            <el-option value="3" label="表白墙"/>
                            <el-option value="4" label="教务通知"/>
                        </el-select>
                    </template>
                    </el-input>

                </div>
                <div class="user-info">
                    <el-button @click="toggleDarkMode">
                        切换到 {{ isDark ? '浅色' : '深色' }} 模式
                    </el-button>
                    <el-popover placement="bottom" :width="350" trigger="click">
                        <template #reference>
                            <el-badge style="margin-right: 15px" is-dot :hidden="!notification.length">
                                <div class="notification">
                                    <el-icon><Bell/></el-icon>
                                    <div style="font-size: 10px">消息</div>
                                </div>
                            </el-badge>
                        </template>
                        <el-empty :image-size="80" description="暂时没有未读消息哦~" v-if="!notification.length"/>
                        <el-scrollbar :max-height="500" v-else>
                            <light-card v-for="item in notification" class="notification-item"
                                        @click="confirmNotification(item.id, item.url)">
                                <div>
                                    <el-tag size="small" :type="item.type">消息</el-tag>&nbsp;
                                    <span style="font-weight: bold">{{item.title}}</span>
                                </div>
                                <el-divider style="margin: 7px 0 3px 0"/>
                                <div style="font-size: 13px;color: grey">
                                    {{item.content}}
                                </div>
                            </light-card>
                        </el-scrollbar>
                        <div style="margin-top: 10px">
                            <el-button size="small" type="info" :icon="Check" @click="deleteAllNotification"
                                       style="width: 100%" plain>清除全部未读消息</el-button>
                        </div>
                    </el-popover>
                    <div class="profile">
                        <div>{{ store.user.username }}</div>
                        <div>{{ store.user.email }}</div>
                    </div>
                    <el-dropdown @command="handleCommand">
                        <el-avatar :src="store.avatarUrl"/>
                        <template #dropdown>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item command="/index/user-setting">
                                    <el-icon>
                                        <Operation/>
                                    </el-icon>
                                    个人设置
                                </el-dropdown-item>
                                <el-dropdown-item command="/index/privacy-setting">
                                    <el-icon>
                                        <Message/>
                                    </el-icon>
                                    安全设置
                                </el-dropdown-item>
                                <el-dropdown-item @click="userLogout" divided>
                                    <el-icon>
                                        <Back/>
                                    </el-icon>
                                    退出登录
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </el-header>
            <el-container>
                <el-aside width="230px">
                    <el-scrollbar style="height: calc(100vh - 55px)">
                        <el-menu
                                router
                                :default-active="$route.path"
                                :default-openeds="['1', '2', '3']"
                                style="min-height: calc(100vh - 55px)">
                            <el-sub-menu index="1">
                                <template #title>
                                    <el-icon>
                                        <Location/>
                                    </el-icon>
                                    <span><b>校园论坛</b></span>
                                </template>
                                <el-menu-item index="/index">
                                    <template #title>
                                        <el-icon>
                                            <ChatDotSquare/>
                                        </el-icon>
                                        帖子广场
                                    </template>
                                </el-menu-item>
                                <el-menu-item>
                                    <template #title>
                                        <el-icon>
                                            <Bell/>
                                        </el-icon>
                                        失物招领
                                    </template>
                                </el-menu-item>
                                <el-menu-item>
                                    <template #title>
                                        <el-icon>
                                            <Umbrella/>
                                        </el-icon>
                                        表白墙
                                    </template>
                                </el-menu-item>
                            </el-sub-menu>
                            <el-sub-menu index="2">
                                <template #title>
                                    <el-icon>
                                        <Position/>
                                    </el-icon>
                                    <span><b>探索与发现</b></span>
                                </template>
                                <el-menu-item>
                                    <template #title>
                                        <el-icon>
                                            <Monitor/>
                                        </el-icon>
                                        教务系统
                                    </template>
                                </el-menu-item>
                                <el-menu-item>
                                    <template #title>
                                        <el-icon>
                                            <Collection/>
                                        </el-icon>
                                        在线图书馆
                                    </template>
                                </el-menu-item>
                            </el-sub-menu>
                            <el-sub-menu index="3">
                                <template #title>
                                    <el-icon>
                                        <Operation/>
                                    </el-icon>
                                    <span><b>个人设置</b></span>
                                </template>
                                <el-menu-item index="/index/user-setting">
                                    <template #title>
                                        <el-icon>
                                            <User/>
                                        </el-icon>
                                        个人信息设置
                                    </template>
                                </el-menu-item>
                                <el-menu-item index="/index/privacy-setting">
                                    <template #title>
                                        <el-icon>
                                            <Lock/>
                                        </el-icon>
                                        账号安全设置
                                    </template>
                                </el-menu-item>
                            </el-sub-menu>
                        </el-menu>
                    </el-scrollbar>
                </el-aside>
                <el-main class="main-content-page">
                    <el-scrollbar style="height: calc(100vh - 55px)">
                        <router-view v-slot="{ Component }">
                            <transition name="el-fade-in-linear" mode="out-in">
                                <component :is="Component" style="height: 100%"/>
                            </transition>
                        </router-view>
                    </el-scrollbar>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<style lang="less" scoped>
.notification-item {
    transition: .3s;
    &:hover {
        cursor: pointer;
        opacity: 0.7;
    }
}

.notification {
    font-size: 22px;
    line-height: 14px;
    text-align: center;
    transition: color .3s;

    &:hover {
        color: grey;
        cursor: pointer;
    }
}

.main-content-page {
    padding: 0;
    background-color: #f7f8fa;
}

.dark .main-content-page {
    background-color: #212225;
}

.main-content {
    height: 100vh;
    width: 100vw;
}

.main-content-header {
    border-bottom: solid 1px var(--el-border-color);
    height: 55px;
    display: flex;
    align-items: center;
    box-sizing: border-box;

    .logo {
        height: 32px;
    }

    .user-info {
        display: flex;
        justify-content: flex-end;
        align-items: center;

        .el-avatar:hover {
            cursor: pointer;
        }

        .profile {
            text-align: right;
            margin-right: 20px;

            :first-child {
                font-size: 18px;
                font-weight: bold;
                line-height: 20px;
            }

            :last-child {
                font-size: 10px;
                color: grey;
            }
        }
    }
}
</style>

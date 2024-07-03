<!--<script setup>-->
<!--import { useDark, useToggle } from '@vueuse/core'-->

<!--useDark({-->
<!--  selector: 'html',-->
<!--  attribute: 'class',-->
<!--  valueDark: 'dark',-->
<!--  valueLight: 'light'-->
<!--})-->

<!--useDark({-->
<!--  onChanged(dark) { useToggle(dark) }-->
<!--})-->

<!--</script>-->

<!--<template>-->
<!--  <header>-->
<!--    <div class="wrapper">-->
<!--      <router-view/>-->
<!--    </div>-->
<!--  </header>-->
<!--</template>-->

<!--<style scoped>-->
<!--header {-->
<!--  line-height: 1.5;-->
<!--}-->
<!--</style>-->

<script setup>
import { ref, watch } from 'vue'
import { useDark, useToggle } from '@vueuse/core'

const isDark = ref(false)  // 定义一个响应式的变量来存储当前的主题状态

// 定义函数来切换主题
function toggleTheme(dark) {
  const themeClass = dark ? 'dark' : 'light';
  document.documentElement.className = themeClass;
}

// 监听 isDark 的变化并调用 toggleTheme 函数
watch(isDark, (newVal) => {
  toggleTheme(newVal);
})

// 监听子组件事件来更新 isDark
function handleToggleDarkMode(dark) {
  isDark.value = dark;
}

</script>

<template>
  <header @toggle-dark-mode="handleToggleDarkMode">
    <div class="wrapper">
      <router-view/>
    </div>
  </header>
</template>

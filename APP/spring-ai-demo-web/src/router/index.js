import {createRouter, createWebHistory } from 'vue-router';

// 本地静态路由
export const constantRoutes = [
  {
    path: '/talk',
    component: () => import('@/views/ai-communicate/talk.vue'),
    meta: {
      isParentView: true,
    },
  },
  {
    path: '/test',
    component: () => import('@/views/test/test.vue'),
    meta: {
      isParentView: true,
    },
  },
  
  {
    // path: '/404',
    path: '/:pathMatch(.*)*', // 防止浏览器刷新时路由未找到警告提示: vue-router.mjs:35 [Vue Router warn]: No match found for location with path "/xxx"
    component: () => import('@/views/error-page/404.vue'),
  },
];

// 创建路由
const router = createRouter({
  history: createWebHistory (),
  routes: constantRoutes,
});

export default router;
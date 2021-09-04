import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'

Vue.use(Router)

export default new Router({
  routes: [
    // {
    //   path: '/',
    //   name: 'HelloWorld',
    //   component: ()=>import('../pages/test.vue'),
    //   meta:{title:'测试页面'}
    // },
    {
      path: '/',
      name: 'ChartRoom',
      component: ()=>import('../pages/ChatRoom/index.vue'),
      meta:{title:'聊天室'}
    }
  ]
})

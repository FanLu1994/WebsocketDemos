// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import VueRouter from "vue-router";
import ElementUI, { MessageBox } from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import "./assets/css/tailwind.css"

Vue.config.productionTip = false

Vue.use(ElementUI)
Vue.use(VueRouter)

// 给路由定义前置的全局守卫
router.beforeEach((to, from, next) => {
  // console.log("从"+from.path+'跳转到：'+to.path)
  document.title = `${to.meta.title} | 主页`;
  // next()
  // console.log(token)
  next()
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})

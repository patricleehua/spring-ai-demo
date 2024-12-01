import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
let app = createApp(App);
// 路由
import router from '@/router';
app.use(router);

// 配置全局api
import api from '@/api'
app.config.globalProperties.$api = api

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css' // Ensure you are using css-loader
const vuetify = createVuetify({
    components,
    directives,
    icons: {
        defaultSet: 'mdi', // This is already the default value - only for display purposes
      },
  })
app.use(vuetify);

//引入tailWindCss 
import '@/styles/tailwindcss.css'

app.mount('#app')

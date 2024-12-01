import axios from 'axios';

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 50000, // 请求超时时间：50s
  headers: { 'Content-Type': 'application/json;charset=utf-8' },
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    if (!config.headers) {
        console.log(`Expected 'config' and 'config.headers' not to be undefined`);
    }

    //处理逻辑
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response;
    const { status, data } = res;
    if (status === 200) {
      return res;
    } else {
        //处理逻辑
        console.log("返回状态异常:")
    }
  },
  (error) => {
    console.log("axios捕捉异常:"+error)
  },
);


// 导出实例
export default service;
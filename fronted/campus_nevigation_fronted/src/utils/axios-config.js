import axios from 'axios'

const instance = axios.create({
  baseURL: 'http://127.0.0.1:8080/api', // 使用后端的端口
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json' //发送的请求体为json格式
  }
})

export default instance
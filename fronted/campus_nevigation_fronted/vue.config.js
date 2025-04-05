const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: { //配置开发服务器
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080', // 指向后端服务
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    }
  }
})
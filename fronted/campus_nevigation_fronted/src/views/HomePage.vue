<template>
  <div class="home-container">
    <top-navigation
    @locate-building="handleLocateBuilding"
    @calculate-route="handleCalculateRoute" /> <!--监听事件-->
    <div v-if="loading" class="loading-tip">地图加载中...</div>
    <div v-if="error" class="error-tip">{{ error }}</div>
    <div id="map-container" class="map-container"></div> <!--渲染地图-->
    <div id="route-panel" class="route-panel"></div>
     <!--定义路线信息面板-->
     <div v-if="currentRoute" class="route-panel">
      <div class="panel-header">
        <h3>路线详情</h3>
        <el-button
          type="danger"
          size="small"
          @click="clearRoute"
          circle
        ><!--隐藏面板-->>
          <i class="el-icon-close"></i>
        </el-button>
      </div>
      <!--汇总路线-->>
      <div class="route-summary">
        <div class="route-point">
          <span class="point-label">起点:</span>
          <span class="point-value">{{ currentRoute.start.name }}</span>
        </div>
        <div class="route-point">
          <span class="point-label">终点:</span>
          <span class="point-value">{{ currentRoute.end.name }}</span>
        </div>
        <div class="route-stat">
          <span class="stat-label">距离:</span>
          <span class="stat-value">{{ formatDistance(currentRoute.distance) }}</span>
        </div>
        <div class="route-stat">
          <span class="stat-label">预计时间:</span>
          <span class="stat-value">{{ formatDuration(currentRoute.duration) }}</span>
        </div>
      </div>

      <div class="route-steps">
        <h4>步行指引</h4>
        <div
          v-for="(step, index) in currentRoute.steps"
          :key="index"
          class="route-step"
        >
          <div class="step-index">{{ index + 1 }}</div>
          <div class="step-content" v-html="step.instruction"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomePage'
}
</script>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import TopNavigation from '../components/TopNavigation.vue'

const loading = ref(true)
const error = ref('')
const currentRoute = ref(null) // 存储当前路线信息
let map = null
let markers = [] // 存储所有标记点
let routePlanner = null
let routeLine = null // 保存路线对象用于清除
 //初始化地图
const initMap = async () => {
  try {
    window._AMapSecurityConfig = {
      securityJsCode: "62a456cf51dfa8d69710c51740a6646c",
    };
    const AMap = await AMapLoader.load({
      key: 'a8139019fabb76636d2683223fcdaafe',
      version: '2.0',
      plugins: [
        'AMap.ToolBar',
        'AMap.Scale',
        'AMap.HawkEye',
        'AMap.MapType',
        'AMap.Geolocation',
        'AMap.Walking'
      ],
    })
    //设立中心点
    map = new AMap.Map('map-container', {
      center: [119.520585, 39.9174],
      zoom: 17,
      viewMode: '3D'
    })

    map.on('complete', () => {
      loading.value = false
    })

  } catch (e) {
    error.value = `地图加载失败：${e.message}`
    loading.value = false
  }
}
//定位建筑物
const handleLocateBuilding = ({ latitude, longitude }) => {
  if (map) {
    map.setCenter([longitude, latitude])//改变地图中心点
    map.setZoom(18)

    clearMarkers()// 清除地图上已有的标记点

    const marker = new AMap.Marker({
      position: [longitude, latitude],
      animation: 'AMAP_ANIMATION_DROP'// 标记点的动画效果
    })
    map.add(marker)
    markers.push(marker)
  }
}
//计算路线
const handleCalculateRoute = ({ start, end }) => {
  if (!map || !start || !end) return

  // 清除旧路线
  clearRoute()

  // 初始化路线规划服务
  routePlanner = new AMap.Walking({
    map: map
  })

  // 规划路径
  routePlanner.search(
    [start.longitude, start.latitude],
    [end.longitude, end.latitude],
    (status, result) => {
      if (status === 'complete') {
        console.log('路线规划成功:', result)

        // 存储路线信息
        const route = result.routes?.[0] //可选链操作符，用于安全访问
        if (route) {
          currentRoute.value = {
            start,
            end,
            distance: route.distance,
            duration: route.time,
            steps: route.steps.map(step => ({
              instruction: step.instruction.replace(/<[^>]+>/g, ''), //去除HTML标签
              distance: step.distance,
              duration: step.time,
              path: step.path
            }))
          }

          // 标记路线
          routeLine = route.polyline

          // 标记起点终点
          const startMarker = new AMap.Marker({
            position: [start.longitude, start.latitude],
            icon: 'https://webapi.amap.com/theme/v1.3/markers/n/start.png',
            map: map,
          })
          const endMarker = new AMap.Marker({
            position: [end.longitude, end.latitude],
            icon: 'https://webapi.amap.com/theme/v1.3/markers/n/end.png',
            map: map,
          })
          markers.push(startMarker, endMarker)
        }
      } else {
        console.error('路线规划失败：', result)
        ElMessage.error('路线规划失败，请重试')
      }
    }
  )
}
//清空所有标记点
const clearMarkers = () => {
  markers.forEach(marker => marker.setMap(null))
  markers = []
}
//清空路线
const clearRoute = () => {
  if (routeLine) {
    map.remove(routeLine)
    routeLine = null
  }
  clearMarkers()
  currentRoute.value = null
}
//转换为公里
const formatDistance = (meters) => {
  if (meters < 1000) {
    return `${Math.round(meters)}米`
  } else {
    return `${(meters / 1000).toFixed(1)}公里`
  }
}
//时间换算
const formatDuration = (seconds) => {
  const minutes = Math.round(seconds / 60)
  if (minutes < 60) {
    return `${minutes}分钟`
  } else {
    const hours = Math.floor(minutes / 60)
    const remainingMinutes = minutes % 60
    return `${hours}小时${remainingMinutes}分钟`
  }
}
//调用初始化
onMounted(() => {
  initMap()
})
//销毁地图
onUnmounted(() => {
  if (map) {
    map.destroy()
  }
})
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh;
  position: relative;
}

.map-container {
  width: 100%;
  height: 100%;
  margin-top: 60px; /* 为顶部导航栏留出空间 */
}

.loading-tip, .error-tip {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
  padding: 10px 20px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  border-radius: 4px;
}

.error-tip {
  background: rgba(255, 0, 0, 0.7);
}

.route-panel {
  position: fixed;
  top: 60px;
  right: 20px;
  width: 300px;
  max-height: calc(100vh - 80px);
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 1000;
  padding: 15px;
  overflow-y: auto;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.route-summary {
  margin-bottom: 15px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.route-point, .route-stat {
  display: flex;
  margin-bottom: 8px;
}

.point-label, .stat-label {
  font-weight: bold;
  width: 70px;
  color: #606266;
}

.point-value, .stat-value {
  flex: 1;
}

.route-steps {
  margin-top: 15px;
}

.route-step {
  display: flex;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #eee;
}

.step-index {
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background: #409eff;
  color: white;
  border-radius: 50%;
  margin-right: 10px;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
  font-size: 14px;
  line-height: 1.5;
}
</style>
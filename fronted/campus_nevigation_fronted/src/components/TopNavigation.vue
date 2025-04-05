<template>
  <div class="top-nav-container">
    <div class="top-nav">
      <el-menu
        mode="horizontal"
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-sub-menu
          v-for="category in categories"
          :key="category.id"
          :index="category.id.toString()"
        >
          <template #title>{{ category.name }}</template>
          <el-menu-item
            v-for="building in buildingsByCategory[category.id]"
            :key="building.id"
            @click="handleBuildingClick(building)"
            :class="{
              'is-start': building.id === routeStart?.id,
              'is-end': building.id === routeEnd?.id
            }"
          ><!--动态绑定类名-->
            <span class="building-name">{{ building.name }}</span>
            <span v-if="building.id === routeStart?.id" class="route-tag start-tag">起点</span>
            <span v-if="building.id === routeEnd?.id" class="route-tag end-tag">终点</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>

    <!-- 路线规划控制面板 - 现在位于左侧 -->
    <div v-if="routeStart || routeEnd" class="route-control-panel">
      <div class="route-info">
        <div v-if="routeStart" class="route-point">
          <span class="label">起点:</span>
          <span class="value">{{ routeStart.name }}</span>
          <el-button
            size="small"
            type="text"
            @click="clearRoutePoint('start')"
            icon="el-icon-close"
          ></el-button>
        </div>
        <div v-if="routeEnd" class="route-point">
          <span class="label">终点:</span>
          <span class="value">{{ routeEnd.name }}</span>
          <el-button
            size="small"
            type="text"
            @click="clearRoutePoint('end')"
            icon="el-icon-close"
          ></el-button>
        </div>
      </div>
      <div class="route-actions">
        <el-button
          type="primary"
          @click="calculateRoute"
          :disabled="!routeStart || !routeEnd"
          size="small"
        >
          <i class="el-icon-map-location"></i> 计算路线
        </el-button>
        <el-button
          type="info"
          @click="clearRoutePoints"
          size="small"
        >
          <i class="el-icon-delete"></i> 清空
        </el-button>
      </div>
    </div>

    <!-- 建筑物详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedBuilding?.name"
      width="30%"
    >
      <div class="building-actions">
        <el-button type="primary" @click="locateOnMap">在地图中定位</el-button>
        <el-button
          v-if="selectedBuilding?.websiteUrl"
          type="success"
          @click="goToWebsite"
        >
          访问官网
        </el-button>
        <el-button
          type="warning"
          @click="setAsRoutePoint('start')"
          :disabled="selectedBuilding?.id === routeStart?.id"
        >
          {{ selectedBuilding?.id === routeStart?.id ? '已是起点' : '设为起点' }}
        </el-button>
        <el-button
          type="danger"
          @click="setAsRoutePoint('end')"
          :disabled="selectedBuilding?.id === routeEnd?.id"
        >
          {{ selectedBuilding?.id === routeEnd?.id ? '已是终点' : '设为终点' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios-config'

const categories = ref([]) //存储建筑物类别
const buildingsByCategory = ref({}) //按类别分组的建筑物对象
const dialogVisible = ref(false) //建筑物详情对话框的显示状态标记
const selectedBuilding = ref(null) //存储当前选中建筑物
const routeStart = ref(null) //起点
const routeEnd = ref(null) //终点
//获取建筑物类别
const fetchCategories = async () => {
  try {
    const { data } = await axios.get('/buildings/categories') //后端获取建筑物类别
    categories.value = data
    await fetchAllBuildingsData()
  } catch (error) {
    console.error('获取类别失败:', error.message)
    ElMessage.error('获取建筑物类别失败')
  }
}
//获取具体建筑物
const fetchAllBuildingsData = async () => {
  for (const category of categories.value) {
    try {
      const { data } = await axios.get(`/buildings/category/${category.id}`)
      buildingsByCategory.value[category.id] = data
    } catch (error) {
      console.error(`获取建筑物失败 (类别 ${category.id}):`, error.message)
    }
  }
}
//处理建筑物选中事件
const handleBuildingClick = (building) => {
  selectedBuilding.value = building
  dialogVisible.value = true
}
//地图中定位
const locateOnMap = () => {
  if (selectedBuilding.value) {
    emit('locate-building', {
      latitude: selectedBuilding.value.latitude,
      longitude: selectedBuilding.value.longitude,
      zoom: 17,
      viewMode: '3D'
    })
    dialogVisible.value = false
  }
}
//访问官网
const goToWebsite = () => {
  if (selectedBuilding.value?.websiteUrl) {
    window.open(selectedBuilding.value.websiteUrl, '_blank')
    dialogVisible.value = false
  }
}
//设置路线点
const setAsRoutePoint = (type) => {
  if (!selectedBuilding.value) return

  if (type === 'start') {
    routeStart.value = selectedBuilding.value
    ElMessage.success(`已设置 ${selectedBuilding.value.name} 为起点`)
  } else {
    routeEnd.value = selectedBuilding.value
    ElMessage.success(`已设置 ${selectedBuilding.value.name} 为终点`)
  }

  dialogVisible.value = false
}
//计算和清除路线
const calculateRoute = () => {
  if (routeStart.value && routeEnd.value) {
    emit('calculate-route', {
      start: {
        id: routeStart.value.id,
        name: routeStart.value.name,
        latitude: routeStart.value.latitude,
        longitude: routeStart.value.longitude
      },
      end: {
        id: routeEnd.value.id,
        name: routeEnd.value.name,
        latitude: routeEnd.value.latitude,
        longitude: routeEnd.value.longitude
      }
    })
  }
}

const clearRoutePoint = (type) => {
  if (type === 'start') {
    routeStart.value = null
    ElMessage.info('已清除起点')
  } else {
    routeEnd.value = null
    ElMessage.info('已清除终点')
  }
}

const clearRoutePoints = () => {
  routeStart.value = null
  routeEnd.value = null
  ElMessage.info('已清除所有路线点')
}

const emit = defineEmits(['locate-building', 'calculate-route'])

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.top-nav-container {
  position: relative;
}

.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.building-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  gap: 10px;
  padding: 20px 0;
}

.route-control-panel {
  position: fixed;
  top: 60px;
  left: 20px;
  background: rgba(255, 255, 255, 0.95);
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.15);
  z-index: 1001;
  max-width: 280px;
  backdrop-filter: blur(5px);
  border: 1px solid #ebeef5;
  animation: fadeIn 0.3s ease;
}

/* 添加小箭头指向导航栏 */
.route-control-panel::before {
  content: '';
  position: absolute;
  top: -10px;
  left: 20px;
  border-width: 0 10px 10px;
  border-style: solid;
  border-color: transparent transparent rgba(255, 255, 255, 0.95);
}

.route-info {
  margin-bottom: 10px;
}

.route-point {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.route-point .label {
  font-weight: bold;
  margin-right: 8px;
  color: #606266;
}

.route-point .value {
  flex: 1;
  margin-right: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.route-actions {
  display: flex;
  justify-content: space-between;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 建筑物菜单项样式 */
:deep(.el-menu-item) {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.building-name {
  flex-grow: 1;
}

.route-tag {
  font-size: 12px;
  padding: 2px 5px;
  border-radius: 4px;
  margin-left: 10px;
}

.start-tag {
  background-color: #fdf6ec;
  color: #e6a23c;
  border: 1px solid #f5dab1;
}

.end-tag {
  background-color: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}

/* 高亮显示起点和终点 */
:deep(.is-start) {
  background-color: #fdf6ec !important;
}

:deep(.is-end) {
  background-color: #fef0f0 !important;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
}

:deep(.el-sub-menu__title) {
  font-size: 16px;
  font-weight: bold;
}
</style>
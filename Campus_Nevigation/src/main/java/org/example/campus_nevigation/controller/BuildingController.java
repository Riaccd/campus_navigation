package org.example.campus_nevigation.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.campus_nevigation.dto.BuildingDTO;
import org.example.campus_nevigation.entity.BuildingCategory;
import org.example.campus_nevigation.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j //日志记录器
@RestController //转换成json
@RequestMapping("/api/buildings") //定义请求的映射路径
public class BuildingController {

    @Autowired
    private BuildingService buildingService; //依赖注入

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            log.info("Fetching all categories");
            List<BuildingCategory> categories = buildingService.getAllCategories(); //调用服务层的方法获取建筑物类别
            log.info("Found {} categories", categories.size());
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Error fetching categories", e);
            return ResponseEntity.status(500).body("获取类别失败：" + e.getMessage());
        }
    }

    //根据类别id（类型）来获取建筑物
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getBuildingsByCategory(@PathVariable Long categoryId) {
        try {
            log.info("Fetching buildings for category id {}", categoryId);
            List<BuildingDTO> buildings = buildingService.getBuildingsByCategory(categoryId);
            log.info("Found {} buildings for category id {}", buildings.size(), categoryId);
            return ResponseEntity.ok(buildings);
        } catch (Exception e) {
            log.error("Error fetching buildings for category id {}", categoryId, e);
            return ResponseEntity.status(500).body("获取建筑物失败：" + e.getMessage());
        }
    }
}
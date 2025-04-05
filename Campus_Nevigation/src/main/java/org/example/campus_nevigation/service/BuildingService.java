package org.example.campus_nevigation.service;

import org.example.campus_nevigation.dto.BuildingDTO;
import org.example.campus_nevigation.entity.BuildingCategory;

import java.util.List;

public interface BuildingService {
    List<BuildingCategory> getAllCategories();//返回类别
    List<BuildingDTO> getBuildingsByCategory(Long categoryId);//返回对应类别的详细建筑物
    BuildingDTO getBuildingById(Long id);
}
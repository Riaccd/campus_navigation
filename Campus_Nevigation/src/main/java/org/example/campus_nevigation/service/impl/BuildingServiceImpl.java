package org.example.campus_nevigation.service.impl;

import org.example.campus_nevigation.dto.BuildingDTO;
import org.example.campus_nevigation.entity.Building;
import org.example.campus_nevigation.entity.BuildingCategory;
import org.example.campus_nevigation.repository.BuildingCategoryRepository;
import org.example.campus_nevigation.repository.BuildingRepository;
import org.example.campus_nevigation.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuildingServiceImpl implements BuildingService {

    @Autowired
    private BuildingCategoryRepository buildingCategoryRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Override
    public List<BuildingCategory> getAllCategories() {
        return buildingCategoryRepository.findAll();
    }

    @Override //重写父类service方法
    public List<BuildingDTO> getBuildingsByCategory(Long categoryId) {
        List<Building> buildings = buildingRepository.findByCategoryId(categoryId);
        return buildings.stream().map(this::convertToDTO).collect(Collectors.toList());
    }//将buildings集合中的每个对象转换为DTO对象，并返回一个包含所有转换后对象的列表。

    @Override
    public BuildingDTO getBuildingById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found with id: " + id));
        return convertToDTO(building);
    }

    private BuildingDTO convertToDTO(Building building) { //实体转化为DTO
        BuildingDTO dto = new BuildingDTO();
        dto.setId(building.getId());
        dto.setName(building.getName());
        dto.setLatitude(building.getLatitude());
        dto.setLongitude(building.getLongitude());
        dto.setWebsiteUrl(building.getWebsiteUrl());
        dto.setDescription(building.getDescription());
        dto.setFloorCount(building.getFloorCount());
        dto.setPictureUrl(building.getPictureUrl());
        dto.setCreatedTime(building.getCreatedTime());
        dto.setUpdatedTime(building.getUpdatedTime());
        return dto;
    }
}
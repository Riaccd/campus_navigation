package org.example.campus_nevigation.repository;

import org.example.campus_nevigation.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findByCategoryId(Long categoryId); //JPA自动实现
}
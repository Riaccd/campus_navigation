package org.example.campus_nevigation.repository;

import org.example.campus_nevigation.entity.BuildingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingCategoryRepository extends JpaRepository<BuildingCategory, Long> {
}
//继承了常用的 CRUD
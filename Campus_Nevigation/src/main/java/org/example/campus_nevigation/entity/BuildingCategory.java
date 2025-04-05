package org.example.campus_nevigation.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Data
@Entity //JPA实体类
@Table(name = "building_category") //对应的数据库表
public class BuildingCategory {

    @Id //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //自增
    private Long id;

    @Column(nullable = false) //不能为空
    private String name;

    @Column
    private String icon;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @JsonIgnore
    @OneToMany(mappedBy = "category") //一对多
    private List<Building> buildings;
}
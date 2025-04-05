package org.example.campus_nevigation.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BuildingDTO {
    private Long id;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String websiteUrl;
    private String description;
    private Integer floorCount;
    private String pictureUrl;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
package org.example.campus_nevigation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("org.example.campus_nevigation.entity")//标注实体类位置
@EnableJpaRepositories("org.example.campus_nevigation.repository")//标注接口类位置
public class CampusNevigationApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusNevigationApplication.class, args);
    }
}
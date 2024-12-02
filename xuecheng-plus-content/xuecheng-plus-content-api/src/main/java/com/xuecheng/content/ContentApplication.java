package com.xuecheng.content;

import com.xuecheng.base.config.CorsConfig;
import com.xuecheng.base.config.LocalDateTimeConfig;
import com.xuecheng.base.config.MybatisPlusConfig;
import com.xuecheng.base.handler.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-30 11:29
 **/
@OpenAPIDefinition(
        info = @Info(
                title = "学成在线教育管理系统",
                version = "1.0",
                description = "内容管理系统Api文档"
        )
)
@SpringBootApplication
@MapperScan("com.xuecheng.content.mapper")
@Import({MybatisPlusConfig.class,
        CorsConfig.class,
        LocalDateTimeConfig.class,
        GlobalExceptionHandler.class})
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}

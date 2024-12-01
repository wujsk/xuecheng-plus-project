package com.xuecheng.system;

import com.xuecheng.base.config.CorsConfig;
import com.xuecheng.base.config.MybatisPlusConfig;
import com.xuecheng.base.config.RedisConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-30 19:04
 **/
@OpenAPIDefinition(
        info = @Info(
                title = "学成在线教育管理系统",
                version = "1.0",
                description = "字典管理系统Api文档"
        )
)
@SpringBootApplication
@Import({RedisConfig.class, MybatisPlusConfig.class, CorsConfig.class})
public class DictionaryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DictionaryApplication.class, args);
    }
}

package com.xuecheng.base.config;

import com.xuecheng.base.properties.MinioProperty;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio 配置信息
 *
 * @author ruoyi
 */
@Configuration
@Data
public class MinioConfig
{

    @Resource
    private MinioProperty minIOProperty;

    @Bean
    public MinioClient minioClient()
    {
        return MinioClient.builder()
                .endpoint(minIOProperty.getEndpoint())
                .credentials(minIOProperty.getAccessKey(), minIOProperty.getSecretKey())
                .build();
    }
}

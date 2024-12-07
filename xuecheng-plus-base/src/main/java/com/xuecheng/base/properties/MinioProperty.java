package com.xuecheng.base.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-12-02 22:50
 **/
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperty {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    @Value("${minio.bucket.files}")
    private String files;

    @Value("${minio.bucket.videofiles}")
    private String videofiles;

}

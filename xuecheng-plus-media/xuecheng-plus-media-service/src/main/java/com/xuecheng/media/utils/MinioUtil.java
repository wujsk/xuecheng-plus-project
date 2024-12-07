package com.xuecheng.media.utils;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Minio 文件存储工具类
 *
 * @author ruoyi
 */
@Component
public class MinioUtil
{
    /**
     * 上传文件
     *
     * @param bucketName 桶名称
     * @param fileName
     * @throws IOException
     */
    public static String uploadFile(String bucketName, String fileName, MultipartFile multipartFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String url = "";
        MinioClient minioClient = SpringUtils.getBean(MinioClient.class);
        //判断桶是否存在
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
        if(!bucketExists){
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            //给同赋予权限
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(createBucketPolicyConfig(bucketName))
                            .build()
            );
        }
        try (InputStream inputStream = multipartFile.getInputStream())
        {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, multipartFile.getSize(), -1)
                    .contentType(multipartFile.getContentType()).build());
            url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .method(Method.GET)
                    .build());
            url = url.substring(0, url.indexOf('?'));
            return URLDecoder.decode(url, "UTF-8");
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }

    }

    public static byte[] downFile(String bucketName, String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = SpringUtils.getBean(MinioClient.class);
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            throw new RuntimeException("文件下载失败");
        };
        try (InputStream in = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build())) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            return out.toByteArray();
        }
    }

    private static String createBucketPolicyConfig(String bucketName) {
        return "{\n" +
                "  \"Statement\" : [ {\n" +
                "    \"Action\" : \"s3:GetObject\",\n" +
                "    \"Effect\" : \"Allow\",\n" +
                "    \"Principal\" : \"*\",\n" +
                "    \"Resource\" : \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                "  } ],\n" +
                "  \"Version\" : \"2012-10-17\"\n" +
                "}";
    }
}


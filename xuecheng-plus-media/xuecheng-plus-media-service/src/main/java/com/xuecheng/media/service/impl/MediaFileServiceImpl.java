package com.xuecheng.media.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.properties.MinioProperty;
import com.xuecheng.base.response.XueChengPlusException;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @description
 * @author Mr.M
 * @date 2022/9/10 8:58
 * @version 1.0
 */
@Service
@Slf4j
public class MediaFileServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFileService {

     @Resource
     private MediaFilesMapper mediaFilesMapper;

     @Resource
     private MinioClient minioClient;

     @Resource
     private MinioProperty minioProperty;

     @Resource
     private MediaFilesMapper mediaFileMapper;

     @Resource
     private ApplicationContext applicationContext;

     private final String[] ALLOW_SUFFIX = {"jpg", "jpeg", "png", "mp4"};

     @Override
     public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {
         //构建查询条件对象
         LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
         //分页对象
         Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
         // 查询数据内容获得结果
         Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
         // 获取数据列表
         List<MediaFiles> list = pageResult.getRecords();
         // 获取数据总数
         long total = pageResult.getTotal();
         // 构建结果集
         PageResult<MediaFiles> mediaListResult = new PageResult<>(pageParams.getPageNo(), pageParams.getPageSize(), total, list);
         return mediaListResult;
     }

    /**
     * 根据文件后缀获取mineType
     * @param extension
     * @return
     */
     public String getFileMineType(String extension) {
         ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
         String mineType = MediaType.MULTIPART_FORM_DATA_VALUE;
         if (extensionMatch != null) {
             mineType = extensionMatch.getMimeType();
         }
         return mineType;
     }

    /**
     * 获取文件MD5
     * @param file
     * @return
     */
     public String getFileMd5(File file) {
         try (InputStream is = new FileInputStream(file)) {
             return DigestUtils.md5Hex(is);
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
     }

    /**
     * 获取文件默认存储目录路径 年/月/日
     * @return
     */
     public String getDestFolderPath() {
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
         return simpleDateFormat.format(new Date()).replace("-", "/") + "/";
     }

     public String getBucket(String extension) {
         boolean contains = Arrays.asList(ALLOW_SUFFIX).contains(extension);
         if (contains) {
             if ("mp4".equals(extension)) {
                 return minioProperty.getVideofiles();
             }
             else {
                 return minioProperty.getFiles();
             }
         } else {
             XueChengPlusException.cast("暂不支持上传该格式文件");
         }
         return null;
     }

    /**
     * 将文件上传到minio中
     * @param bucket
     * @param mineType
     * @param localFilePath
     * @param objectName
     * @return
     */
     public boolean addMediaFileToMinio(String bucket, String mineType, String localFilePath, String objectName) {
         try {
             minioClient.uploadObject(UploadObjectArgs.builder()
                     .bucket(bucket) //桶的名称
                     .contentType(mineType) // 文件类型
                     .filename(localFilePath) // 指定本地文件路径
                     .object(objectName) // minio文件目录
                     .build());
             log.info("上传文件至minio, bucket:{}, objectName:{}", bucket, objectName);
             return true;
         } catch (Exception e) {
            log.info("上传文件失败, bucket:{}, objectName:{}, e:{}", bucket, objectName, e.getMessage());
            XueChengPlusException.cast("上传文件失败");
         }
         return false;
     }

    // 涉及到文件上传，IO流，需要只对数据库控制的加上事务，避免事务时间过长
     @Override
     public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto dto, String localFilePath) {
         String filename = dto.getFilename();
         // 文件后缀
         String extension = filename.substring(filename.lastIndexOf(".") + 1);
         // 文件mineType
         String fileMineType = getFileMineType(extension);
         // 文件MD5
         String fileMd5 = getFileMd5(new File(localFilePath));
         // 文件存储路径
         String filePath = getDestFolderPath() + fileMd5 + "." + extension;
         // 桶名称
         String bucket = getBucket(extension);
         // 上传到minio
         addMediaFileToMinio(bucket,
                 getFileMineType(fileMineType),
                 localFilePath,
                 filePath);
         // 将文件信息保存到数据库中
         // 事务控制是在代理对象执行前加上开启事务和提交事务
         // addMediaFilesToDb(companyId, fileMd5, filename, bucket, filePath, dto);这种是this实例对象，不是代理对象
         // 我们需要获得这个代理对象 避免循环依赖
         // 使用AopContext.currentProxy()需要在启动类上加上@EnableAspectJAutoProxy(exposeProxy = true)
         // 获得通过ApplicationContext.getBean(MediaFileService.class)获得代理对象
         MediaFileService mediaFileService = applicationContext.getBean(MediaFileService.class);
         return mediaFileService.addMediaFilesToDb(companyId, fileMd5, filename, bucket, filePath, dto);
     }

     @Override
     @Transactional
     public UploadFileResultDto addMediaFilesToDb(Long companyId,
                                                   String fileMd5,
                                                   String filename,
                                                   String bucket,
                                                   String filePath,
                                                   UploadFileParamsDto dto) {
         MediaFiles mediaFilesById = mediaFileMapper.selectById(fileMd5);
         if (!Objects.isNull(mediaFilesById)) {
             UploadFileResultDto resultDto = BeanUtil.copyProperties(mediaFilesById, UploadFileResultDto.class);
             return resultDto;
         }
         MediaFiles mediaFiles = BeanUtil.copyProperties(dto, MediaFiles.class);
         mediaFiles.setId(fileMd5)
                 .setCompanyId(companyId)
                 .setFilename(filename)
                 .setBucket(bucket)
                 .setFilePath(filePath)
                 .setFileId(fileMd5)
                 .setUrl("/" + minioProperty.getFiles() + "/" + filePath)
                 .setStatus("1")
                 .setCreateDate(LocalDateTime.now())
                 .setAuditStatus("002003");
         boolean save = save(mediaFiles);
         if (!save) {
             log.info("向数据库保存文件信息失败,bucket:{},objectName:{}", minioProperty.getFiles(), filePath);
             XueChengPlusException.cast("保存媒资信息失败");
         }
         UploadFileResultDto resultDto = BeanUtil.copyProperties(mediaFiles, UploadFileResultDto.class);
         return resultDto;
     }

}

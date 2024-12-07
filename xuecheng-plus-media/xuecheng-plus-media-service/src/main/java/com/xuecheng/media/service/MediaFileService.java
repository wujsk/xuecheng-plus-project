package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

    /**
    * @description 媒资文件查询方法
    * @param pageParams 分页参数
    * @param queryMediaParamsDto 查询条件
    * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
    * @author Mr.M
    * @date 2022/9/10 8:57
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     * @param companyId 机构id
     * @param dto 上传文件信息
     * @param localFilePath 本地路径
     * @return UploadFileResultDto
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto dto, String localFilePath);

    /**
     * 添加媒资文件信息到数据库
     * @param companyId
     * @param fileMd5
     * @param filename
     * @param bucket
     * @param filePath
     * @param dto
     * @return
     */
    public UploadFileResultDto addMediaFilesToDb(Long companyId,
                                                 String fileMd5,
                                                 String filename,
                                                 String bucket,
                                                 String filePath,
                                                 UploadFileParamsDto dto);

}

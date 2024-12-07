package com.xuecheng.media.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.response.XueChengPlusException;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @description 媒资文件管理接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Tag(name = "媒资文件管理接口")
@RestController
public class MediaFilesController {

    @Autowired
    private MediaFileService mediaFileService;

    @Operation(summary = "媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);
    }

    @Operation(summary = "上传图片")
    @PostMapping(value = "/upload/coursefile")
    public UploadFileResultDto upload(MultipartFile filedata) {
        Long companyId = 1232141425L;
        // 接收到了文件
        // 创建一个临时文件
        File tempFile = null;
        UploadFileResultDto dto = null;
        try {
            tempFile = File.createTempFile("minio", ".temp");
            filedata.transferTo(tempFile);
            String localPath = tempFile.getAbsolutePath();
            UploadFileParamsDto paramsDto = new UploadFileParamsDto();
            paramsDto.setFilename(filedata.getOriginalFilename());
            paramsDto.setFileType("001001");
            paramsDto.setFileSize(filedata.getSize());
            // 调用service
            dto = mediaFileService.uploadFile(companyId, paramsDto, localPath);
        } catch (IOException e) {
            e.printStackTrace();
            XueChengPlusException.cast("上传文件失败");
        } finally {
            try {
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dto;
    }

}

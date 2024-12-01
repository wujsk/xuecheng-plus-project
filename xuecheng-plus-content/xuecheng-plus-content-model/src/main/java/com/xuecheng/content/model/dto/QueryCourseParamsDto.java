package com.xuecheng.content.model.dto;

import lombok.Data;

/**
 * @program: xuecheng-plus-project
 * @description: 课程查询条件
 * @author: 酷炫焦少
 * @create: 2024-11-30 11:08
 **/
@Data
public class QueryCourseParamsDto {

    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;

}


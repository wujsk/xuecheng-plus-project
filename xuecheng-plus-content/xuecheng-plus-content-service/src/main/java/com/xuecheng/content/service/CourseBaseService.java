package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.UpdateCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.dto.SaveCourseDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author itcast
 * @since 2024-11-30
 */
public interface CourseBaseService extends IService<CourseBase> {

    PageResult<CourseBase> getPage(PageParams params, QueryCourseParamsDto dto);

    CourseBaseInfoDto insert(Long companyId, SaveCourseDto saveCourseDto);

    /**
     * 查询课程信息
     */
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 修改课程信息
     * @param companyId
     * @param dto
     * @return
     */
    CourseBaseInfoDto update(Long companyId, UpdateCourseDto dto);

}

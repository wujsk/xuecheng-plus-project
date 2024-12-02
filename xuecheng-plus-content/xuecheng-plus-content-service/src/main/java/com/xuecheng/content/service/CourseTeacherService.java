package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author itcast
 * @since 2024-11-30
 */
public interface CourseTeacherService extends IService<CourseTeacher> {

    List<CourseTeacher> getList(Long courseId);

    CourseTeacher insertTeacher(Long companyId, CourseTeacher courseTeacher);

    void deleteTeacherByIdAndTeacherId(Long companyId, Long courseId, Long teacherId);

}

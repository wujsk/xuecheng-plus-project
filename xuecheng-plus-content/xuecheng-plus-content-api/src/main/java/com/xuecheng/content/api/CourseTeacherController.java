package com.xuecheng.content.api;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xuecheng.base.CommonError;
import com.xuecheng.base.response.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Wrapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-12-02 08:04
 **/
@Tag(name = "课程老师相关接口")
@RestController
@RequestMapping("/courseTeacher")
public class CourseTeacherController {

    @Resource
    private CourseTeacherService courseTeacherService;

    @GetMapping("/list/{courseId}")
    @Operation(summary = "查询教师列表")
    public List<CourseTeacher> getList(@PathVariable("courseId") Long courseId) {
        return courseTeacherService.getList(courseId);
    }

    @PostMapping
    @Operation(summary = "新增教师")
    public CourseTeacher insertTeacher(Long companyId,
                                       @RequestBody
                                       @Validated CourseTeacher courseTeacher) {
        return courseTeacherService.insertTeacher(companyId, courseTeacher);
    }

    @DeleteMapping("/course/{courseId}/{teacherId}")
    @Operation(summary = "删除教师")
    public void deleteTeacher (Long companyId,
                                        @PathVariable("courseId") Long courseId,
                                        @PathVariable("teacherId") Long teacherId) {
        courseTeacherService.deleteTeacherByIdAndTeacherId(companyId, courseId, teacherId);
    }

}

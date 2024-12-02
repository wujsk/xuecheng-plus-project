package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.validated.ValidationGroups;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.UpdateCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.dto.SaveCourseDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-30 11:25
 **/
@RestController
@RequestMapping("/course")
@Tag(name = "课程信息编辑接口")
public class ContentBaseInfoController {

    @Resource
    private CourseBaseService courseBaseService;

    @PostMapping("/list")
    @Operation(summary = "课程查询接口")
    public PageResult<CourseBase> list(PageParams params, @RequestBody(required = false) QueryCourseParamsDto dto) {
        return courseBaseService.getPage(params, dto);
    }

    @PostMapping
    @Operation(summary = "新增课程接口")
    public CourseBaseInfoDto insert(@RequestBody
                                    @Validated(value = {ValidationGroups.Insert.class}) SaveCourseDto saveCourseDto) {
        // 机构名称，由于认证系统没上线，硬编码
        Long companyId = 1L;
        return courseBaseService.insert(companyId, saveCourseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询课程信息")
    public CourseBaseInfoDto query(@PathVariable("id") Long id) {
        return courseBaseService.getCourseBaseInfo(id);
    }

    @PutMapping
    @Operation(summary = "修改课程信息")
    public CourseBaseInfoDto update(@RequestBody
                                    @Validated UpdateCourseDto dto) {
        // TODO 机构名称，由于认证系统没上线，硬编码
        Long companyId = 1232141425L;
        return courseBaseService.update(companyId, dto);
    }

    @DeleteMapping("/{courseId}")
    @Operation(summary = "删除课程信息")
    public void deleteTeacher(@PathVariable("courseId") Long courseId) {
        // TODO 机构名称，由于认证系统没上线，硬编码
        Long companyId = 1232141425L;
        courseBaseService.delete(companyId, courseId);
    }

}

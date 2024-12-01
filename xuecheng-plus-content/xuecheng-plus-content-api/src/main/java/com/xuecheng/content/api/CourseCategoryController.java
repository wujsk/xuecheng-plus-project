package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-30 20:44
 **/
@RestController
@RequestMapping("/course-category")
@Tag(name = "课程分类接口")
public class CourseCategoryController {

    @Resource
    private CourseCategoryService courseCategoryService;

    @GetMapping("/tree-nodes")
    @Operation(summary = "查找课程分类")
    public List<CourseCategoryTreeDto> treeNodes() {
        return courseCategoryService.getCategoryTree();
    }

}

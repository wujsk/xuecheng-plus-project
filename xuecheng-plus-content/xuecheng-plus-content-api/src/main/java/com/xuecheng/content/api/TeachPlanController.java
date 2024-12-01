package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachPlanTreeDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-12-01 16:20
 **/
@RestController
@RequestMapping("/teachplan")
@Tag(name = "课程计划相关接口")
public class TeachPlanController {

    @Resource
    private TeachplanService teachplanService;

    @GetMapping("/{courseId}/tree-nodes")
    @Operation(summary = "查询课程计划")
    public List<TeachPlanTreeDto> getTeachPlanTree(@PathVariable("courseId") Long courseId) {
        return teachplanService.getTeachPlanTree(courseId);
    }

    @PostMapping
    @Operation(summary = "新增或修改课程计划")
    public void saveTeachPlan(@RequestBody SaveTeachplanDto dto) {
        teachplanService.saveTeachPlan(dto);
    }

}

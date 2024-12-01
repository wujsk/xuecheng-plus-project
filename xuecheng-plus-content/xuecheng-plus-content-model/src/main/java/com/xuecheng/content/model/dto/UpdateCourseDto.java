package com.xuecheng.content.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-30 22:29
 **/
@Setter
@Getter
@Schema(description = "修改课程")
public class UpdateCourseDto extends SaveCourseDto {

    @Schema(title = "课程id")
    private Long id;

}

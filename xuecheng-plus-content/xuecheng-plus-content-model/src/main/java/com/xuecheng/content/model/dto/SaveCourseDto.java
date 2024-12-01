package com.xuecheng.content.model.dto;

import com.xuecheng.base.validated.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-30 22:29
 **/
@Data
@Schema(description = "新增课程")
public class SaveCourseDto {

    @Schema(title = "教育模式")
    private String teachmode;

    @NotEmpty(message = "新增课程名称不能为空", groups = {ValidationGroups.Insert.class})
    @NotEmpty(message = "修改课程名称不能为空", groups = {ValidationGroups.Update.class})
    @Schema(title = "课程名称")
    private String name;

    @Schema(title = "课程标签")
    private String tags;

    @NotEmpty(message = "课程分类不能为空")
    @Schema(title = "大分类")
    private String mt;

    @NotEmpty(message = "课程分类不能为空")
    @Schema(title = "小分类")
    private String st;

    @Schema(title = "课程等级")
    private String grade;

    @Schema(title = "课程简介")
    private String description;

    @NotEmpty(message = "适用人群不能为空")
    @Size(message = "适用人群内容过少",min = 10)
    @Schema(title = "适用人群")
    private String users;

    @Schema(title = "课程封面")
    private String pic;

    @NotEmpty(message = "收费规则不能为空")
    @Schema(title = "收费类型")
    private String charge;

    @Schema(title = "原价")
    private BigDecimal originalPrice;

    @Schema(title = "现价")
    private BigDecimal price;

    @Schema(title = "咨询qq")
    private String qq;

    @Schema(title = "微信号")
    private String wechat;

    @Schema(title = "电话")
    private String phone;

    @Schema(title = "有效期")
    private Integer validDays;

}

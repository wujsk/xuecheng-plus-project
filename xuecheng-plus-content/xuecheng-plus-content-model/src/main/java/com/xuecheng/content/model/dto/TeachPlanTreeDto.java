package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-12-01 16:24
 **/
@Data
@Schema(description = "课程计划树")
public class TeachPlanTreeDto extends Teachplan implements Serializable {

    private TeachplanMedia teachplanMedia;

    private List<TeachPlanTreeDto> teachPlanTreeNodes;

}

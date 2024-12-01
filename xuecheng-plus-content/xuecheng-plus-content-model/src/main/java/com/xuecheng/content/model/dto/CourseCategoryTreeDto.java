package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-30 20:43
 **/
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    List<CourseCategoryTreeDto> childrenTreeNodes;

}

package com.xuecheng.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @program: xuecheng-plus-project
 * @description: 分页参数
 * @author: 酷炫焦少
 * @create: 2024-11-30 11:03
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "分页参数")
public class PageParams implements Serializable {

    @Schema(title = "当前页码")
    private Integer pageNo;

    @Schema(title = "每页记录数")
    private Integer pageSize;

}

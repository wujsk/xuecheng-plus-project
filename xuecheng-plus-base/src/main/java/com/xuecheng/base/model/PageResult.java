package com.xuecheng.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: xuecheng-plus-project
 * @description: 返回分页数据
 * @author: 酷炫焦少
 * @create: 2024-11-30 11:10
 **/
@Data
@Schema(description = "分页查询结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Schema(title = "当前页码")
    private Integer page;

    /**
     * 每页记录数
     */
    @Schema(title = "每页记录数")
    private Integer pageSize;

    /**
     * 总记录数
     */
    @Schema(title = "总记录数")
    private Long counts;

    /**
     * 数据列表
     */
    @Schema(title = "数据列表")
    private List<T> items;

    public PageResult() {
    }

    public PageResult(Integer page, Integer pageSize, Long counts, List<T> items) {
        this.page = page;
        this.pageSize = pageSize;
        this.counts = counts;
        this.items = items;
    }
}

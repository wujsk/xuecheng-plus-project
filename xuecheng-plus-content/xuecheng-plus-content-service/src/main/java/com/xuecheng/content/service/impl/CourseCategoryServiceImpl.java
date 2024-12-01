package com.xuecheng.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {

    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    /**
     * 递归遍历目录树
     */
    public List<CourseCategoryTreeDto> selectCategoryTree (String treeNode, List<CourseCategoryTreeDto> courseCategoryTreeDtos) {
        return courseCategoryTreeDtos.stream()
                .filter(a -> treeNode.equals(a.getParentid()))
                .peek(b -> {
                    List<CourseCategoryTreeDto> c = selectCategoryTree(b.getId(), courseCategoryTreeDtos);
                    if (!CollectionUtil.isEmpty(c)) {
                        b.setChildrenTreeNodes(selectCategoryTree(b.getId(), courseCategoryTreeDtos));
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseCategoryTreeDto> getCategoryTree() {
        List<CourseCategory> courseCategories = courseCategoryMapper
                .selectList(Wrappers.<CourseCategory>lambdaQuery()
                        .orderByAsc(CourseCategory::getOrderby)
                        .eq(CourseCategory::getIsShow, 1));
        if (courseCategories.isEmpty()) {
            throw new RuntimeException("暂无分类");
        }
        List<CourseCategoryTreeDto> courseCategoryTreeDtoList = new ArrayList<>();
        courseCategories.forEach(courseCategory -> {
            courseCategoryTreeDtoList.add(BeanUtil.copyProperties(courseCategory, CourseCategoryTreeDto.class));
        });
        return selectCategoryTree("1", courseCategoryTreeDtoList);
    }

}

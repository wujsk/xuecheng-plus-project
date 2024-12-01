package com.xuecheng.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.CommonError;
import com.xuecheng.base.response.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachPlanTreeDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachplanService {

    @Resource
    private TeachplanMediaMapper teachplanMediaMapper;

    @Resource
    private TeachplanMapper teachplanMapper;

    private List<TeachPlanTreeDto> selectTeachplanTree(Long treeNode, List<TeachPlanTreeDto> teachplans) {
        return teachplans.stream()
                .filter(teachplan -> treeNode.equals(teachplan.getParentid()))
                .peek(teachplan -> {
                    List<TeachPlanTreeDto> teachPlanTreeDtos = selectTeachplanTree(teachplan.getId(), teachplans);
                    if (CollectionUtils.isNotEmpty(teachPlanTreeDtos)) {
                        teachplan.setTeachPlanTreeNodes(teachPlanTreeDtos);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public List<TeachPlanTreeDto> getTeachPlanTree(Long courseId) {
        List<Teachplan> teachplans = teachplanMapper.selectList(Wrappers.<Teachplan>lambdaQuery()
                .eq(!Objects.isNull(courseId), Teachplan::getCourseId, courseId)
                .orderByAsc(Teachplan::getGrade, Teachplan::getParentid));
        if (CollectionUtils.isEmpty(teachplans)) {
            return Collections.emptyList();
        }
        List<TeachPlanTreeDto> teachPlanTreeDtos = new ArrayList<>();
        teachplans.forEach(teachplan -> {
            teachPlanTreeDtos.add(BeanUtil.copyProperties(teachplan, TeachPlanTreeDto.class));
        });
        Map<Long, TeachplanMedia> mediaMap = teachplanMediaMapper.selectList(Wrappers.<TeachplanMedia>lambdaQuery()
                        .eq(!Objects.isNull(courseId), TeachplanMedia::getCourseId, courseId))
                .stream()
                .collect(Collectors.toMap(TeachplanMedia::getTeachplanId, item -> item));
        teachPlanTreeDtos.forEach(teachPlanTreeDto -> {
            if (mediaMap.containsKey(teachPlanTreeDto.getId())) {
                teachPlanTreeDto.setTeachplanMedia(mediaMap.get(teachPlanTreeDto.getId()));
            }
        });
        List<TeachPlanTreeDto> treeDtos = selectTeachplanTree(0L, teachPlanTreeDtos);
        return treeDtos;
    }

    @Override
    public void saveTeachPlan(SaveTeachplanDto dto) {
        if (Objects.isNull(dto)) {
            XueChengPlusException.cast(CommonError.OBJECT_NULL);
        }
        if (Objects.isNull(dto.getCourseId())) {
            XueChengPlusException.cast("请选择要添加的课程");
        }
        Teachplan teachplan = BeanUtil.copyProperties(dto, Teachplan.class);
        if (Objects.isNull(teachplan.getId())) {
            teachplan.setCreateDate(LocalDateTime.now());
            teachplan.setIsPreview("0");
            Long count = teachplanMapper.selectCount(Wrappers.<Teachplan>lambdaQuery()
                    .eq(Teachplan::getParentid, dto.getParentid())
                    .eq(Teachplan::getCourseId, dto.getCourseId()));
            teachplan.setOrderby(teachplan.getOrderby() + count.intValue());
            teachplanMapper.insert(teachplan);
        } else {
            if (StringUtils.isBlank(dto.getPname())) {
                XueChengPlusException.cast("课程计划名称不能为空");
            }
            teachplan.setChangeDate(LocalDateTime.now());
            teachplanMapper.updateById(teachplan);
        }
    }

}

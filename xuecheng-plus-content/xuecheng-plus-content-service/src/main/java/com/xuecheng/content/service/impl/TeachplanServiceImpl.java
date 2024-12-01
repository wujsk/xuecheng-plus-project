package com.xuecheng.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.xuecheng.base.response.RestErrorResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.CommonError;
import com.xuecheng.base.constant.TeachPlanConstant;
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
                    if (!CollectionUtils.isEmpty(teachPlanTreeDtos)) {
                        teachplan.setTeachPlanTreeNodes(teachPlanTreeDtos);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public List<TeachPlanTreeDto> getTeachPlanTree(Long courseId) {
        List<Teachplan> teachplans = teachplanMapper.selectList(Wrappers.<Teachplan>lambdaQuery()
                .eq(!Objects.isNull(courseId), Teachplan::getCourseId, courseId)
                .orderByAsc(Teachplan::getGrade, Teachplan::getParentid, Teachplan::getOrderby));
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
            teachplan.setOrderby(count.intValue() + 1);
            teachplanMapper.insert(teachplan);
        } else {
            if (StringUtils.isBlank(dto.getPname())) {
                XueChengPlusException.cast("课程计划名称不能为空");
            }
            teachplan.setChangeDate(LocalDateTime.now());
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    @Transactional(rollbackFor = XueChengPlusException.class)
    public RestErrorResponse deleteTeachPlan(Long teachplanId) {
        if (Objects.isNull(teachplanId)) {
            XueChengPlusException.cast(CommonError.PARAMS_ERROR);
        }
        // 查看有没有数据
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        // 查看是不是大章节
        if (Objects.isNull(teachplan)) {{
            XueChengPlusException.cast("无改课程计划");
        }}
        // 如果有大章节下不能有小章节，才能删除
        if (Objects.equals(teachplan.getGrade(), TeachPlanConstant.BIG_GRADE)) {{
            List<Teachplan> teachplans = teachplanMapper.selectList(Wrappers.<Teachplan>lambdaQuery()
                    .eq(Teachplan::getParentid, teachplan.getId()));
            if (!CollectionUtils.isEmpty(teachplans)) {
               return new RestErrorResponse(120409, "课程计划信息还有子级信息，无法操作");
            }
        }}
        int result = teachplanMapper.deleteById(teachplanId);
        if (result < 1) {
            XueChengPlusException.cast("删除失败");
        }
        teachplanMediaMapper.delete(Wrappers.<TeachplanMedia>lambdaQuery()
                .eq(TeachplanMedia::getTeachplanId, teachplanId));
        return new RestErrorResponse(200);
    }

    @Override
    public void move(Long teachplanId, Short moveType) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (Objects.isNull(teachplan)) {
            XueChengPlusException.cast("无该课程计划");
        }
        Integer orderby = teachplan.getOrderby();
        if (Objects.equals(moveType, TeachPlanConstant.UP)) {
            Teachplan teachplan1 = teachplanMapper.selectOne(Wrappers.<Teachplan>lambdaQuery()
                    .eq(Teachplan::getOrderby, orderby - 1)
                    .eq(Teachplan::getParentid, teachplan.getParentid())
                    .eq(Teachplan::getCourseId, teachplan.getCourseId()));
            if (Objects.isNull(teachplan1)) {
                XueChengPlusException.cast("上移失败");
            }
            teachplan.setOrderby(orderby - 1);
            teachplan1.setOrderby(orderby);
            teachplanMapper.updateById(Arrays.asList(teachplan, teachplan1));
        } else if (Objects.equals(moveType, TeachPlanConstant.DOWN)) {
            Teachplan teachplan1 = teachplanMapper.selectOne(Wrappers.<Teachplan>lambdaQuery()
                    .eq(Teachplan::getOrderby, orderby + 1)
                    .eq(Teachplan::getParentid, teachplan.getParentid())
                    .eq(Teachplan::getCourseId, teachplan.getCourseId()));
            if (Objects.isNull(teachplan1)) {
                XueChengPlusException.cast("下移失败");
            }
            teachplan.setOrderby(orderby + 1);
            teachplan1.setOrderby(orderby);
            teachplanMapper.updateById(Arrays.asList(teachplan, teachplan1));
        }
    }

}

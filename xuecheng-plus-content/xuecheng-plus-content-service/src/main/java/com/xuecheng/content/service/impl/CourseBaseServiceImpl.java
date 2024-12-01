package com.xuecheng.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.CommonError;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.response.XueChengPlusException;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.UpdateCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.dto.SaveCourseDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

/**
 * <p>
 * 课程基本信息 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {

    @Resource
    private CourseBaseMapper courseBaseMapper;

    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    @Resource
    private CourseMarketMapper courseMarketMapper;

    private void validateCourseInfo(SaveCourseDto saveCourseDto) {
        //合法性校验
        if (StringUtils.isBlank(saveCourseDto.getName())) {
            throw new XueChengPlusException("课程名称为空");
        }

        if (StringUtils.isBlank(saveCourseDto.getMt())) {
            throw new XueChengPlusException("课程分类为空");
        }

        if (StringUtils.isBlank(saveCourseDto.getSt())) {
            throw new XueChengPlusException("课程分类为空");
        }

        if (StringUtils.isBlank(saveCourseDto.getGrade())) {
            throw new XueChengPlusException("课程等级为空");
        }

        if (StringUtils.isBlank(saveCourseDto.getTeachmode())) {
            throw new XueChengPlusException("教育模式为空");
        }

        if (StringUtils.isBlank(saveCourseDto.getUsers())) {
            throw new XueChengPlusException("适应人群为空");
        }

        if (StringUtils.isBlank(saveCourseDto.getCharge())) {
            throw new XueChengPlusException("收费规则为空");
        }
    }

    @Override
    public PageResult<CourseBase> getPage(PageParams params, QueryCourseParamsDto dto) {
        Integer pageNo = params.getPageNo();
        Integer pageSize = params.getPageSize();
        if (Objects.isNull(pageNo)) {
            pageNo = 1;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }
        Page<CourseBase> pageInfo = new Page<>(pageNo, pageSize);
        if (Objects.isNull(dto)) {
            dto = new QueryCourseParamsDto();
        }
        String auditStatus = dto.getAuditStatus();
        String courseName = dto.getCourseName();
        String publishStatus = dto.getPublishStatus();
        LambdaQueryWrapper<CourseBase> wrapper = Wrappers.<CourseBase>lambdaQuery()
                .eq(StringUtils.isNotBlank(auditStatus), CourseBase::getAuditStatus, auditStatus)
                .like(StringUtils.isNotBlank(courseName), CourseBase::getName, courseName)
                .eq(StringUtils.isNotBlank(publishStatus), CourseBase::getStatus, publishStatus)
                .orderByDesc(CourseBase::getCreateDate);
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(pageInfo, wrapper);
        if (CollectionUtils.isEmpty(courseBasePage.getRecords())) {
            return new PageResult<>(pageNo, pageSize, 0L, Collections.emptyList());
        }
        return new PageResult<>(pageNo, pageSize, courseBasePage.getTotal(), courseBasePage.getRecords());
    }

    @Override
    @Transactional(rollbackFor = XueChengPlusException.class)
    public CourseBaseInfoDto insert(Long companyId, SaveCourseDto saveCourseDto) {
        validateCourseInfo(saveCourseDto);
        CourseBase courseBase = BeanUtil.copyProperties(saveCourseDto, CourseBase.class);
        courseBase.setCompanyId(companyId)
                // 未提交、待发布
                .setAuditStatus("202002").setStatus("203001")
                .setCreateDate(LocalDateTime.now()).setChangeDate(LocalDateTime.now())
                .setCreatePeople("1").setChangePeople("1");
        int insert = courseBaseMapper.insert(courseBase);
        if (insert <= 0) {
            throw new RuntimeException("添加课程失败");
        }
        CourseMarket courseMarket = BeanUtil.copyProperties(saveCourseDto, CourseMarket.class);
        Long courseBaseId = courseBase.getId();
        courseMarket.setId(courseBaseId);
        int result = saveCourseMarket(courseMarket);
        if (result <= 0) {
            throw new RuntimeException("保存信息失败");
        }
        return getCourseBaseInfo(courseBaseId);
    }

    @Override
    @Transactional(rollbackFor = XueChengPlusException.class)
    public CourseBaseInfoDto update(Long companyId, UpdateCourseDto dto) {
        Long courseId = dto.getId();
        if (Objects.isNull(courseId)) {
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR.getErrMessage());
        }
        validateCourseInfo(dto);
        CourseBase course = courseBaseMapper.selectById(courseId);
        if (Objects.isNull(course)) {
            XueChengPlusException.cast("课程不存在");
        }
        if (!companyId.equals(course.getCompanyId())) {
            XueChengPlusException.cast("本机构只能修改本机构的");
        }
        CourseBase courseBase = BeanUtil.copyProperties(dto, CourseBase.class);
        // TODO 待修改
        courseBase.setCompanyId(companyId).setChangePeople("1").setChangeDate(LocalDateTime.now());
        int result = courseBaseMapper.updateById(courseBase);
        if (result <= 0) {
            XueChengPlusException.cast("课程信息更新失败");
        }
        CourseMarket courseMarket = BeanUtil.copyProperties(dto, CourseMarket.class);
        int row = saveCourseMarket(courseMarket);
        if (row <= 0) {
            XueChengPlusException.cast("课程信息更新失败");
        }
        return getCourseBaseInfo(courseId);
    }

    // 查询课程信息
    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        if (Objects.isNull(courseId)) {
            XueChengPlusException.cast("请选择你要查询的课程");
        }
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (Objects.isNull(courseBase)) {
            return null;
        }
        CourseBaseInfoDto dto = BeanUtil.copyProperties(courseBase, CourseBaseInfoDto.class);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        BeanUtil.copyProperties(courseMarket, dto);
        // 查询分类信息
        CourseCategory mtCategory = courseCategoryMapper.selectById(dto.getMt());
        CourseCategory stCategory = courseCategoryMapper.selectById(dto.getSt());
        dto.setMtName(mtCategory.getName());
        dto.setStName(stCategory.getName());
        return dto;
    }

    private int saveCourseMarket(CourseMarket courseMarket) {
        int result = 0;
        String charge = courseMarket.getCharge();
        if (StrUtil.isBlank(charge)) {
            XueChengPlusException.cast("收费规则为空");
        }
        // 如果收费，价格没有填写也要报异常
        if ("201001".equals(charge)) {
            Float price = courseMarket.getPrice();
            if (Objects.isNull(price) || price.floatValue() <= 0) {
                XueChengPlusException.cast("课程价格不能为空并且必须大于0");
            }
        }
        // 如果不收费，现价应为0
        if ("201000".equals(charge)) {
            Float price = courseMarket.getPrice();
            if (!Objects.isNull(price) || price.floatValue() == 0) {
                XueChengPlusException.cast("课程价格不能大于0");
            }
        }
        Long id = courseMarket.getId();
        CourseMarket market = courseMarketMapper.selectById(id);
        if (Objects.isNull(market)) {
            result = courseMarketMapper.insert(courseMarket);
        } else {
            result = courseMarketMapper.updateById(courseMarket);
        }
        return result;
    }

}

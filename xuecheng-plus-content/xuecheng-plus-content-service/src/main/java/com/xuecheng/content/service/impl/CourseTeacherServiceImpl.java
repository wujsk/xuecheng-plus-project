package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.CommonError;
import com.xuecheng.base.response.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Override
    public List<CourseTeacher> getList(Long courseId) {
        return courseTeacherMapper.selectList(Wrappers.<CourseTeacher>lambdaQuery()
                .eq(CourseTeacher::getCourseId, courseId)
                .orderByDesc(CourseTeacher::getCreateDate));
    }

    @Override
    public CourseTeacher insertTeacher(Long companyId, CourseTeacher courseTeacher) {
        if (Objects.isNull(courseTeacher)) {
            XueChengPlusException.cast(CommonError.REQUEST_NULL);
        }
        Long id = courseTeacher.getId();
        Long courseId = courseTeacher.getCourseId();
        CourseBase courseBase = courseBaseMapper.selectOne(Wrappers.<CourseBase>lambdaQuery()
                .eq(CourseBase::getId, courseId));
        if (Objects.isNull(id)) {
            if (Objects.isNull(courseBase)) {
                XueChengPlusException.cast("新增教师失败");
            }
            if (!Objects.equals(courseBase.getCompanyId(), companyId)) {
                XueChengPlusException.cast("新增教师失败");
            }
            courseTeacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(courseTeacher);
            if (insert < 1) {
                XueChengPlusException.cast("新增教师失败");
            }
        } else {
            if (Objects.isNull(courseBase)) {
                XueChengPlusException.cast("更新教师失败");
            }
            if (!Objects.equals(courseBase.getCompanyId(), companyId)) {
                XueChengPlusException.cast("更新教师失败");
            }
            int update = courseTeacherMapper.updateById(courseTeacher);
            if (update < 1) {
                XueChengPlusException.cast("更新教师失败");
            }
        }
        return courseTeacher;
    }

    @Override
    public void deleteTeacherByIdAndTeacherId(Long companyId, Long courseId, Long teacherId) {
        CourseBase courseBase = courseBaseMapper.selectOne(Wrappers.<CourseBase>lambdaQuery()
                .eq(CourseBase::getId, courseId));
        if (Objects.isNull(courseBase)) {
            XueChengPlusException.cast("删除教师失败");
        }
        if (!Objects.equals(courseBase.getCompanyId(), companyId)) {
            XueChengPlusException.cast("删除教师失败");
        }
        CourseTeacher courseTeacher = courseTeacherMapper.selectOne(Wrappers.<CourseTeacher>lambdaQuery()
                .eq(CourseTeacher::getCourseId, courseId)
                .eq(CourseTeacher::getId, teacherId));
        if (Objects.isNull(courseTeacher)) {
            XueChengPlusException.cast("删除教师失败");
        }
        int delete = courseTeacherMapper.deleteById(courseTeacher);
        if (delete < 1) {
            XueChengPlusException.cast("删除教师失败");
        }
    }
}

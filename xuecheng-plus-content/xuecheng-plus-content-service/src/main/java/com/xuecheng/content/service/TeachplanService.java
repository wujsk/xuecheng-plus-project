package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.base.response.RestErrorResponse;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachPlanTreeDto;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author itcast
 * @since 2024-11-30
 */
public interface TeachplanService extends IService<Teachplan> {

    /**
     * 查询课程计划
     * @param courseId
     * @return
     */
    List<TeachPlanTreeDto> getTeachPlanTree(Long courseId);

    void saveTeachPlan(SaveTeachplanDto dto);

    RestErrorResponse deleteTeachPlan(Long teachplanId);

    void move(Long teachplanId, Short moveType);

}

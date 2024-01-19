package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.*;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author yepianer
 * @date 2024/1/13 3:24
 * @project_name xuechengnew
 * @description 课程信息管理接口
 */
public interface CourseBaseInfoService {

    //课程分页查询
    /**
     * @param courseParamsDto 课程查询条件
     * @param pageParams 分页查询参数
     * @return 课程分页信息
     * */
    public PageResult<CourseBaseDto> queryCourseBaseList(Long companyId, PageParams pageParams, QueryCourseParamsDto courseParamsDto);

    /**
     * @param addCourseDto 课程信息
     * @param companyId 机构ID
     * @return 课程详细信息
     * */
    //新增课程
    public CourseBaseInfoDto createCourseBase(Long companyId,AddCourseDto addCourseDto);

    //保存营销信息(存在则更新，不存在则添加)
    public int saveCourseMarket(CourseMarket courseMarketNew);

    /**
     * 根据课程的id查询课程的信息
     * @param courseId 课程id
     * @return 课程详细信息
     * */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 修改课程
     * @param companyId 机构id
     * @param editCourseDto 修改课程的信息
     * @return 课程详细信息
     * */
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);
}

package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author yepianer
 * @date 2023/7/15 0:16
 * @project_name yepianerxuecheng
 * @description
 */
public interface CourseTeacherService {

    /**
     * 查询师资列表
     * */
    public List<CourseTeacher> getTeacherList(Long courseId);

    /**
     * 添加教师/修改教师
     * */
    public void addTeacher(Long companyId, CourseTeacher courseTeacher);

    /**
     * 删除教师
     * */
    public void deleteTeacher(Long companyId, Long courseId, Long id);
}

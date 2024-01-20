package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @author yepianer
 * @date 2024/1/20 23:32
 * @project_name xuechengnew
 * @description 课程计划管理相关的接口
 */
public interface TeachplanService {

    /**
     * 根据课程id查询课程计划
     * @param courseId 课程id
     * */
    public List<TeachplanDto> findTeachplanTree(Long courseId);
}

package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author yepianer
 * @date 2024/1/16 0:51
 * @project_name xuechengnew
 * @description 课程分类接口
 */
public interface CourseCategoryService {
    /**
     * 课程分类属性结构查询
     * */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}

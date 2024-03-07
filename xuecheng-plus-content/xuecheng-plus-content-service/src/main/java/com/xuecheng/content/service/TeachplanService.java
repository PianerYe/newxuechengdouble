package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
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

    /**
     * 新增/修改/保存课程计划
     * */
    public void saveTeachplan(SaveTeachplanDto teachplan);

    /**
     * 大/小章节的上移
     * */
    public void moveupTeachplan(Long id);

    /**
     *大/小章节的下移
     * */
    public void movedownTeachplan(Long id);

    /**
     * 课程计划删除
     * */
    public void deleteTeachplan(Long id);

    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    public void deleteWithAssociationMedia(String teachPlanId, String mediaId);
}

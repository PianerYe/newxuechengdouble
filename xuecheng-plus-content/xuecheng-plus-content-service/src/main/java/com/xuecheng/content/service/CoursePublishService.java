package com.xuecheng.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;

/**
 * @author yepianer
 * @date 2023/9/10 22:10
 * @project_name yepianerxuecheng
 * @description  课程发布相关的接口
 */
public interface CoursePublishService {
    /**
     * @description 获取课程预览信息
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     * @author Mr.M
     * @date 2022/9/16 15:36
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);


    public void commitAudit(Long companyId,Long courseId);

    /**
     * @deprecated 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     * @return void
     * @serialData 2024/3/13
     * */
    public void publish(Long companyId,Long courseId);


    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    public File generateCourseHtml(Long courseId);

    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    public void  uploadCourseHtml(Long courseId,File file);


    CoursePublish getCoursePublish(Long courseId);

    CoursePublish getCoursePublishCache(Long courseId);
}

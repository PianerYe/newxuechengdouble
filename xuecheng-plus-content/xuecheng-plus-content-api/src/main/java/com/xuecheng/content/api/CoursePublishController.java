package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yepianer
 * @date 2023/9/10 0:42
 * @project_name yepianerxuecheng
 * @description
 */
@Api(value = "课程预览发布接口",tags = "课程预览发布接口")
@Controller
public class CoursePublishController {

    @Resource
    CoursePublishService coursePublishService;
    @Resource
    CourseTeacherService courseTeacherService;
    /**
     * 模版接口
     * */
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") long courseId){
        ModelAndView modelAndView = new ModelAndView();
        //查询课程的信息作为模型数据
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        //查询师资信息，添加到模型数据中,一门课程可以有多个老师，也存在没有老师的情况
        List<CourseTeacher> courseTeachers = courseTeacherService.getTeacherList(courseId);
        if (courseTeachers!= null && courseTeachers.size() > 0){
//            courseTeachers = courseTeachers.stream().map(courseTeacher -> {
//                String photograph = courseTeacher.getPhotograph();
//                if (!StringUtils.contains("http://",photograph)){
//                    courseTeacher.setPhotograph("file.51xuecheng.cn" + photograph);
//                }
//                return courseTeacher;
//            }).collect(Collectors.toList());
            coursePreviewInfo.setCourseTeachers(courseTeachers);
        }

        //指定模型
        modelAndView.addObject("model",coursePreviewInfo);
        //指定模板
        modelAndView.setViewName("course_template");//根据视图名称加.ftl找到模板
        return modelAndView;
    }

    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId,courseId);
    }

    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping("/coursepublish/{courseId}")
    public void setCoursepublish(@PathVariable("courseId") Long courseId){
        //获取到用户所属机构的id
        Long companyId = 1232141425L;
        coursePublishService.publish(companyId,courseId);
    }

}

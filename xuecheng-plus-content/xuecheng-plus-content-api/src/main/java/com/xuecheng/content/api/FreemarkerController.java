package com.xuecheng.content.api;

import com.xuecheng.content.service.CoursePublishService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * @author yepianer
 * @date 2023/9/8 23:11
 * @project_name yepianerxuecheng
 * @description Freemarker入门程序
 */

@Controller
public class FreemarkerController {

    @Resource
    CoursePublishService coursePublishService;

    @GetMapping("/testfreemarker")
    public ModelAndView test(){

        ModelAndView modelAndView = new ModelAndView();
        //指定模型
        modelAndView.addObject("name","小明");
        //指定模板
        modelAndView.setViewName("test");//根据视图名称加.ftl找到模板
        return modelAndView;
    }

}

package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yepianer
 * @date 2024/1/15 14:46
 * @project_name xuechengnew
 * @description 课程分类相关接口
 */
@Api(value = "课程类别接口",tags = "课程类别接口")
@RestController
public class CourseCategoryController {

    @Resource
    CourseCategoryService courseCategoryService;
    @ApiOperation("课程类别展示")
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        return courseCategoryTreeDtos;
    }

}

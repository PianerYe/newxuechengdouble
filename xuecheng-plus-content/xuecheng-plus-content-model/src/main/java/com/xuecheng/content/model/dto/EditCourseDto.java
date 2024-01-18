package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yepianer
 * @date 2023/7/7 20:26
 * @project_name yepianerxuecheng
 * @description
 */
@Data
@ApiModel(value = "EditCourseDto",description = "修改课程基本信息")
public class EditCourseDto extends AddCourseDto{

    @ApiModelProperty(value = "课程id",required = true)
    private long Id;
}

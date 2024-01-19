package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "CourseBaseDto",description = "课程基本信息的补充")
public class CourseBaseDto extends CourseBase {
    //任务数，一共含有几个大章节，就是所谓的任务数
    private Integer subsectionNum;
    //是否收费，对应词典。201000 免费  201001 收费
    private String charge;
}

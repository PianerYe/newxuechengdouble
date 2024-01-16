package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;
import io.swagger.annotations.ApiModel;
import lombok.Data;
/**
 * @author yepianer
 * @date 2024/1/15 14:42
 * @project_name xuechengnew
 * @description 课程基本信息模型类
 */
@Data
@ApiModel(value = "CourseBaseInfoDto",description = "课程基本信息")
public class CourseBaseInfoDto extends CourseBase {

    //收费规则，对应数据字典
    private String charge;
    //价格
    private Float price;
    //原价
    private Float originalPrice;
    //咨询qq
    private String qq;
    //微信
    private String wechat;
    //电话
    private String phone;
    //有效天数
    private Integer validDays;
    //大分类名称
    private String mtName;
    //小分类名称
    private String stName;

}

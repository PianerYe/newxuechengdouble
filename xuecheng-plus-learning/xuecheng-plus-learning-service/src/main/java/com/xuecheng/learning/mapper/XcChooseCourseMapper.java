package com.xuecheng.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.learning.model.po.XcChooseCourse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface XcChooseCourseMapper extends BaseMapper<XcChooseCourse> {

    //select * from xc_choose_course where course_id = ? and user_id = ?   selectByCourseId
    //selectListByShardIndex media_process
//    @Select("select * from xc_choose_course where course_id = #{courseId} and user_id = #{userId}")
//    //XcChooseCourse chooseCourse = chooseCourseMapper.selectById(chooseCourseId);
//    XcChooseCourse selectByCourseId(@Param("courseId") long courseId,@Param("userId") long userId);



}

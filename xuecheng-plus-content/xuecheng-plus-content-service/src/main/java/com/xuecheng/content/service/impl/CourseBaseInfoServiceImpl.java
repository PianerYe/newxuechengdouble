package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.*;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yepianer
 * @date 2024/1/13 3:27
 * @project_name xuechengnew
 * @description
 */
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Resource
    CourseBaseMapper courseBaseMapper;
    @Resource
    CourseMarketMapper courseMarketMapper;
    @Resource
    CourseCategoryMapper courseCategoryMapper;
    @Resource
    CourseBaseInfoService proxycourseBaseInfo;
    @Resource
    TeachplanMapper teachplanMapper;

    /**
     * 课程分页查询
     * @param pageParams 分页查询参数
     * @param queryCourseParamsDto 查询条件
     * @return 查询结果
     * */
    @Override
    public PageResult<CourseBaseDto> queryCourseBaseList(Long companyId, PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        // 测试查询接口
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //拼接查询条件
        //根据课程名称模糊查询  name like '%名称%'
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),
                CourseBase::getName,queryCourseParamsDto.getCourseName());
        //根据课程审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        //按课程发布状态查询
        //根据培训机构的id拼装查询条件
//        queryWrapper.eq(CourseBase::getCompanyId,companyId);
        //分页参数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBaseDto> dtoPage = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        //分页查询E page 分页参数, @Param("ew") Wrapper<T> queryWrapper 查询条件
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(page,dtoPage,"records");
        //数据
        List<CourseBase> items = pageResult.getRecords();
        List<CourseBaseDto>list =items.stream().map((item)->{
            CourseBaseDto courseBaseDto = new CourseBaseDto();
            BeanUtils.copyProperties(item,courseBaseDto);
            Long courseBaseDtoId = courseBaseDto.getId();
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Teachplan::getCourseId,courseBaseDtoId)
                    .eq(Teachplan::getGrade,"1");
            //SELECT COUNT(1) FROM teachplan WHERE course_id = 25 AND grade = "1"
            Integer integer = teachplanMapper.selectCount(wrapper);
            courseBaseDto.setSubsectionNum(integer);
            //查询是否收费免费
            //select charge from course_market where id = courseBaseDtoId;
            LambdaQueryWrapper<CourseMarket> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(CourseMarket::getId,courseBaseDtoId);
            CourseMarket courseMarket = courseMarketMapper.selectOne(wrapper2);
            String charge ;
            if (courseMarket == null){
                charge = null;
            }else {
                charge = courseMarket.getCharge();
            }
            courseBaseDto.setCharge(charge);

            return courseBaseDto;
        }).collect(Collectors.toList());
        //总记录数
        long total = pageResult.getTotal();

        //准备返回数据 List<T> items, long counts, long page, long pageSize
        PageResult<CourseBaseDto> courseBasePageResult = new PageResult<>(list, total,pageParams.getPageNo(), pageParams.getPageSize());

        return  courseBasePageResult;
    }

    /**
     * @param dto 课程信息
     * @param companyId 机构ID
     * @return 课程详细信息
     * */
    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

        //参数合法性校验
//        if (StringUtils.isBlank(dto.getName())) {
////            throw new RuntimeException("课程名称为空");
//            throw new XueChengPlusException("课程名称为空");
//        }
//
//        if (StringUtils.isBlank(dto.getMt())) {
//            throw new XueChengPlusException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getSt())) {
//            throw new XueChengPlusException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getGrade())) {
//            throw new XueChengPlusException("课程等级为空");
//        }
//
//        if (StringUtils.isBlank(dto.getTeachmode())) {
//            throw new XueChengPlusException("教育模式为空");
//        }
//
//        if (StringUtils.isBlank(dto.getUsers())) {
//            throw new XueChengPlusException("适应人群为空");
//        }
//
//        if (StringUtils.isBlank(dto.getCharge())) {
//            throw new XueChengPlusException("收费规则为空");
//        }

        //向课程基本信息表course_base写入数据
        CourseBase courseBaseNew = new CourseBase();
        //将传入的页面的参数放到courseBaseNew对象
        //courseBaseNew.setName(dto.getName());
        //courseBaseNew.setDescription(dto.getDescription());
        //上边从原始对象中get拿数据向新对象set，比较复杂
        BeanUtils.copyProperties(dto,courseBaseNew);//主要属性名称一致就可以拷贝
        //向课程营销表course_market写入数据
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //审核状态默认为未提交
        courseBaseNew.setAuditStatus("202002");
        //发布状态默认为未发布
        courseBaseNew.setStatus("203001");
        //插入数据库
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0){
            throw new XueChengPlusException("添加课程失败");
        }
        //向课程营销表course_market写入数据
        CourseMarket courseMarketNew = new CourseMarket();

        //将页面输入的数据拷贝到courseMarketNew
        BeanUtils.copyProperties(dto,courseMarketNew);
        //课程id
        //课程id
        Long id = courseBaseNew.getId();
        courseMarketNew.setId(id);
        //保存营销信息
        proxycourseBaseInfo.saveCourseMarket(courseMarketNew);
//        saveCourseMarket(courseMarketNew);
        //从数据库查出课程的详细信息,包括两部分
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(id);
        return courseBaseInfo;
    }

    @Transactional
    //单独写一个方法保存营销信息，逻辑：存在则更新，不存在则添加
    public int saveCourseMarket(CourseMarket courseMarketNew){
        //参数的合法性校验
        String charge = courseMarketNew.getCharge();
        if (StringUtils.isEmpty(charge)){
            throw new XueChengPlusException("收费规则为空");
        }
        //如果课程收费，价格没有填写也需要抛异常
        if (charge.equals("201001")){
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice() <= 0){
                throw new XueChengPlusException("课程的价格不能为空并且必须大于0");
            }
            if (courseMarketNew.getOriginalPrice() == null || courseMarketNew.getOriginalPrice() <= 0){
                throw new XueChengPlusException("课程的原价不能为空并且必须大于0");
            }
        }
        //从数据库查询营销信息,存在则更新，不存在则添加
        Long id = courseMarketNew.getId();//主键
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        if (courseMarket == null){
            //插入数据库
            int insert = courseMarketMapper.insert(courseMarketNew);
            return insert;
        }else {
            //将courseMarketNew拷贝到courseMarket
            BeanUtils.copyProperties(courseMarketNew,courseMarket);
            courseMarket.setId(courseMarketNew.getId());
            int i = courseMarketMapper.updateById(courseMarket);
            return i;
        }
    }

    //查询课程信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){

        //从课程基本信息表查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase==null){
            return null;
        }
        //从课程营销表查询
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //组装在一起
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if (courseMarket!=null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }else {
            CourseMarket courseMarket1 = new CourseMarket();
            courseMarket1.setId(courseId);
            courseMarket1.setCharge("201000");
            BeanUtils.copyProperties(courseMarket1,courseBaseInfoDto);
        }
        //通过courseCategoryMapper查询分类信息，将分类名称放在courseBaseInfoDto中
        //todo: 课程分类的名称设置到对象中
        String mt = courseBaseInfoDto.getMt();
        CourseCategory courseCategoryMt = courseCategoryMapper.selectById(mt);
        String mTName = courseCategoryMt.getName();
        courseBaseInfoDto.setMtName(mTName);

        String st = courseBaseInfoDto.getSt();
        CourseCategory courseCategorySt = courseCategoryMapper.selectById(st);
        String sTName = courseCategorySt.getName();
        courseBaseInfoDto.setStName(sTName);

        return courseBaseInfoDto;
    }

    /**
     * @param editCourseDto 修改课程信息
     * @param companyId 机构ID
     * @return 课程详细信息
     * */
    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        //拿到课程id
        Long courseId = editCourseDto.getId();
        //查询课程信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null){
            XueChengPlusException.cast("课程不存在");
        }
        //数据合法性校验
        //根据具体的业务逻辑去校验
        //本机构只能修改本机构的课程
        if (!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }
        //封装数据
        BeanUtils.copyProperties(editCourseDto,courseBase);
        //修改时间
        courseBase.setChangeDate(LocalDateTime.now());
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");
        //更新数据库
        int i = courseBaseMapper.updateById(courseBase);
        if (i <= 0){
            XueChengPlusException.cast("修改课程失败");
        }
        //更新营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto,courseMarketNew);
        saveCourseMarket(courseMarketNew);
        //查询课程信息
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);

        //调用es接口来写索引
        return courseBaseInfo;
    }
}

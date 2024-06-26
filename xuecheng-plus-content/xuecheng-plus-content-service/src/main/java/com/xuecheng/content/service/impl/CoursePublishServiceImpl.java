package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.StringUtil;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author yepianer
 * @date 2023/9/10 22:16
 * @project_name yepianerxuecheng
 * @description 课程发布接口相关实现
 */
@Slf4j
@Service
@Transactional
public class CoursePublishServiceImpl implements CoursePublishService {

    @Resource
    CourseBaseInfoService courseBaseInfoService;
    @Resource
    TeachplanService teachplanService;
    @Resource
    CourseMarketMapper courseMarketMapper;
    @Resource
    CoursePublishPreMapper coursePublishPreMapper;
    @Resource
    CourseBaseMapper courseBaseMapper;
    @Resource
    CoursePublishMapper coursePublishMapper;
    @Resource
    MqMessageService mqMessageService;
    @Resource
    MediaServiceClient mediaServiceClient;
    @Resource
    CourseTeacherService courseTeacherService;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    RedissonClient redissonClient;


    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        //课程基本信息，营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfo);
        //课程的计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        coursePreviewDto.setTeachplans(teachplanTree);
        //添加师资信息
        //查询师资信息，添加到模型数据中,一门课程可以有多个老师，也存在没有老师的情况
        List<CourseTeacher> courseTeachers = courseTeacherService.getTeacherList(courseId);
        if (courseTeachers!= null && courseTeachers.size() > 0){
            coursePreviewDto.setCourseTeachers(courseTeachers);
        }
        return coursePreviewDto;
    }

    @Override
    public void commitAudit(Long companyId, Long courseId) {
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        if (courseBaseInfo == null) {
            XueChengPlusException.cast("课程找不到");
        }
        //审核状态
        String auditStatus = courseBaseInfo.getAuditStatus();

        //如果课程的审核状态为已提交，则不允许提交
        if ("202003".equals(auditStatus)) {
            XueChengPlusException.cast("课程已提交请等待审核");
        }
        //本机构只能提交本机构的课程
        //todo
        if (companyId.longValue() != courseBaseInfo.getCompanyId().longValue()) {
            XueChengPlusException.cast("提交课程机构和课程所属机构不相符，审核被拒绝");
        }

        //课程的图片、计划信息没有填写也不允许提交
        String pic = courseBaseInfo.getPic();
        if (StringUtil.isEmpty(pic)) {
            XueChengPlusException.cast("请上传课程图片");
        }
        //查询课程计划
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if (teachplanTree == null || teachplanTree.size() == 0) {
            XueChengPlusException.cast("请编写课程计划");
        }
        //查询到课程的基本信息，营销信息，计划等信息插入到课程计划表
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        //设置机构ID
        coursePublishPre.setCompanyId(companyId);
        //营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //转json
        String courseMarketJson = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(courseMarketJson);
        //计划信息
        String teachplanTreeJson = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(teachplanTreeJson);
        //状态为已提交
        coursePublishPre.setStatus("202003");
        coursePublishPre.setCreateDate(LocalDateTime.now());
        //查询预发布表，如果有记录则更新，没有则插入
        CoursePublishPre coursePublishPreObj = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreObj == null) {
            //插入
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        //更新课程信息表的审核状态为提交
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("202003");//审核状态为已提交

        courseBaseMapper.updateById(courseBase);
    }

    /**
     * @deprecated 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     * @return void
     * @serialData 2024/3/13
     * */
    @Override
    @Transactional
    public void publish(Long companyId, Long courseId) {
        //查询预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengPlusException.cast("课程没有审核记录，无法发布");
        }
        //课程机构
        Long companyIdPre = coursePublishPre.getCompanyId();
        if (companyId.longValue() != companyIdPre.longValue()) {
            XueChengPlusException.cast("提交课程机构和课程所属机构不相符，发布被拒绝");
        }
        //状态
        String status = coursePublishPre.getStatus();
        //课程如果没有审核通过不允许发布
        if (!"202004".equals(status)) {
            XueChengPlusException.cast("课程没有审核通过，不允许发布");
        }
        //向发布表写入数据
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        //先查询课程发布表，有则更新，无则添加
        CoursePublish coursePublishObj = coursePublishMapper.selectById(courseId);
        if (coursePublishObj == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }
        //向消息表写入数据
        //todo
        saveCoursePublishMessage(courseId);
        //将预发布表数据删除
        coursePublishPreMapper.deleteById(courseId);
        //课程信息表发布状态改为已发布
        CourseBase courseBase = new CourseBase();
        CourseBase courseBaseOld = courseBaseMapper.selectById(courseId);
        BeanUtils.copyProperties(courseBaseOld,courseBase);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    @Override
    public File generateCourseHtml(Long courseId) {
        Configuration configuration = new Configuration(Configuration.getVersion());
        //最终的静态文件
        File htmlFile = null;
        try {
            //拿到classpath路径
            String classpath = this.getClass().getResource("/").getPath();
//            String classpath = this.getClass().getResource("/").getPath().substring(1);;
            //指定模板目录
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //更换如下方式
            configuration.setTemplateLoader(new ClassTemplateLoader(this.getClass().getClassLoader(),"/templates"));
            //指定编码
            configuration.setDefaultEncoding("utf-8");
            //得到模板
            Template template = configuration.getTemplate("course_template.ftl");
            //准备数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);
            HashMap<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);
            //Template template 模板,object model 数据
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            //输入流
            InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
            htmlFile = File.createTempFile("coursepublish",".html");
            //输出文件
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            //使用流将html写入文件
            IOUtils.copy(inputStream, outputStream);
        }catch (Exception e){
            log.error("页面静态化出现问题,课程id：{}",courseId,e);
            e.printStackTrace();
        }
        return htmlFile;
    }

    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        try {
            //将file转成MultipartFile
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
            //远程调用得到返回值
            String upload = mediaServiceClient.upload(multipartFile, "course/"+ courseId + ".html");
            if (upload == null){
                log.debug("远程走降级逻辑得到上传结果为空,课程id:{}",courseId);
                XueChengPlusException.cast("上传静态文件过程中存在异常");
            }
        }catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("上传静态文件过程中存在异常");
        }
    }

    /**
     * 根据课程ID查询课程发布信息
     * */
    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        return coursePublish;
    }

    /**
     * 根据课程ID查询课程发布信息，走redis缓存
     * */
//    @Override
//    public CoursePublish getCoursePublishCache(Long courseId) {
//        ////查询布隆过滤器，如果是返回0，直接返回
//        Object jsonObj = redisTemplate.opsForValue().get("course:" + courseId);
//        if ("null".equals(jsonObj)){
//            return null;
//        }
//        if (jsonObj != null){
//            //System.out.println("===========从缓存中查询=========");
//            //缓存中有直接返回数据
//            String jsonString = jsonObj.toString();
//            CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
//            return coursePublish;
//        }else {
//            System.out.println("===========查询数据库=========");
//            CoursePublish coursePublish = getCoursePublish(courseId);
//            //序列化
//            String jsonString = JSON.toJSONString(coursePublish);
//            if (coursePublish == null){
//                redisTemplate.opsForValue().set("course:" + courseId,jsonString,30,TimeUnit.SECONDS);
//            }else {
//                //设置30秒过期时间
//                redisTemplate.opsForValue().set("course:" + courseId,jsonString,3600 + new Random().nextInt(100), TimeUnit.MINUTES);
//            }
//
//            return coursePublish;
//        }
//    }

    //使用同步锁解决缓存击穿

    //避免缓存击穿
//    @Override
//    public CoursePublish getCoursePublishCache(Long courseId) {
//
//        //查询布隆过滤器，如果是返回0，直接返回
//
//        Object jsonObj = redisTemplate.opsForValue().get("course:" + courseId);
//        if (jsonObj != null) {
//            if ("null".equals(jsonObj)){
//                return null;
//            }
////            System.out.println("===========从缓存中查询=========");
//            //缓存中有直接返回数据
//            String jsonString = jsonObj.toString();
//            CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
//            return coursePublish;
//        } else {
//            synchronized (this){
//                //再次查询一下缓存
//                Object jsonObjOne = redisTemplate.opsForValue().get("course:" + courseId);
//                if (jsonObjOne != null) {
//                    if ("null".equals(jsonObj)){
//                        return null;
//                    }
//                    //缓存中有直接返回数据
//                    String jsonString = jsonObjOne.toString();
//                    CoursePublish coursePublishOne = JSON.parseObject(jsonString, CoursePublish.class);
//                    return coursePublishOne;
//                }
//                //从数据库查询
//                System.out.println("===========查询数据库=========");
//                CoursePublish coursePublish = getCoursePublish(courseId);
//                if (coursePublish == null) {
//                    //查询完成再存储到redis
//                    //设置30秒过期时间
//                    redisTemplate.opsForValue().set("course:" + courseId, JSON.toJSONString(coursePublish),30, TimeUnit.SECONDS);
//                }else {
//                    redisTemplate.opsForValue().set("course:" + courseId, JSON.toJSONString(coursePublish),60*24*7+new Random().nextInt(100), TimeUnit.MINUTES);
//                }
//                return coursePublish;
//            }
//
//        }

        //使用redisson实现分布式锁
        @Override
        public CoursePublish getCoursePublishCache(Long courseId) {

            //查询布隆过滤器，如果是返回0，直接返回

            Object jsonObj = redisTemplate.opsForValue().get("course:" + courseId);
            if (jsonObj != null) {
                if ("null".equals(jsonObj)){
                    return null;
                }
//            System.out.println("===========从缓存中查询=========");
                //缓存中有直接返回数据
                String jsonString = jsonObj.toString();
                CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
                return coursePublish;
            } else {
                RLock lock = redissonClient.getLock("coursequerylock:" + courseId);
                //获取分布式锁
                lock.lock();
                try {
                    //再次查询一下缓存
                    Object jsonObjOne = redisTemplate.opsForValue().get("course:" + courseId);
                    if (jsonObjOne != null) {
                        if ("null".equals(jsonObj)){
                            return null;
                        }
                        //缓存中有直接返回数据
                        String jsonString = jsonObjOne.toString();
                        CoursePublish coursePublishOne = JSON.parseObject(jsonString, CoursePublish.class);
                        return coursePublishOne;
                    }
                    //从数据库查询
                    System.out.println("===========查询数据库=========");
                    //测试，部署之后需要注释。测试锁的续期功能
//                    try {
//                        Thread.sleep(60000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
                    CoursePublish coursePublish = getCoursePublish(courseId);
                    if (coursePublish == null) {
                        //查询完成再存储到redis
                        //设置30秒过期时间
                        redisTemplate.opsForValue().set("course:" + courseId, JSON.toJSONString(coursePublish),30, TimeUnit.SECONDS);
                    }else {
                        redisTemplate.opsForValue().set("course:" + courseId, JSON.toJSONString(coursePublish),60*24*7+new Random().nextInt(100), TimeUnit.MINUTES);
                    }
                    return coursePublish;
                } finally {
                    lock.unlock();
                }
            }



    }
    /**
     * @deprecated 保存消息记录
     * @param courseId 课程ID
     * @return void
     * @author yepianer
     * @data 2024/3/13
     * */
    private void saveCoursePublishMessage(Long courseId){
        MqMessage mqMessage = mqMessageService.addMessage("course_publish",
                String.valueOf(courseId), null, null);
        if (mqMessage==null){
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }
    }


}

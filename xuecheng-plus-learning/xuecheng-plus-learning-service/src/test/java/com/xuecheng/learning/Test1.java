package com.xuecheng.learning;

import com.alibaba.fastjson.JSON;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/10/2 10:32
 * @version 1.0
 */
 @SpringBootTest
public class Test1 {

  @Resource
 XcChooseCourseMapper xcChooseCourseMapper;

  @Test
 public void test() {
      //集合中放入36个数
      List<Integer> abc = new ArrayList<>();
      for (int i = 1; i < 37; i++) {
          abc.add(i);
      }
      //对abc进行洗牌
      Collections.shuffle(abc);
      System.out.println(abc);
  }

  @Test
    public void getVideo(){
      String json = "[\n" +
              "{\"courseId\":124,\"grade\":1,\"id\":300,\"orderby\":1,\"parentid\":0,\"pname\":\"前言\",\n" +
              "\"teachPlanTreeNodes\":[\n" +
              "]},\n" +
              "{\"courseId\":124,\"grade\":1,\"id\":301,\"orderby\":2,\"parentid\":0,\"pname\":\"第一章\",\n" +
              "\"teachPlanTreeNodes\":[\n" +
              "{\"courseId\":124,\"grade\":2,\"id\":302,\"isPreview\":\"0\",\"orderby\":1,\"parentid\":301,\"pname\":\"第一节\",\"teachplanMedia\":{\"courseId\":124,\"id\":61,\"mediaFilename\":\"test.avi\",\"mediaId\":\"ed5d4c9584dc27b27ea1b05937241604\",\"teachplanId\":302}\n" +
              "}]\n" +
              "}\n" +
              "]";
      List<Teachplan> teachplanList = JSON.parseArray(json, Teachplan.class);
      System.out.println(teachplanList);
  }

}

package com.xuecheng.checkcode.controller;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.checkcode.model.R;
import com.xuecheng.checkcode.utils.ValidateCodeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.xuecheng.checkcode.utils.ValidateCodeUtils.generateValidateCode;

@SpringBootTest
public class CheckCodeTests {

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Resource
    private JavaMailSender javaMailSender;

    @Test
    void sendEmailTest(){
        String toEmail = "1728934985@qq.com";
        String text  = "重置密码测试";
        String message = "测试邮件是否收到";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        //设置发件邮箱
        simpleMailMessage.setFrom(fromEmail);
        //收件人邮箱
        simpleMailMessage.setTo(toEmail);
        //主题标题
        simpleMailMessage.setSubject(text);
        //信息内容
        simpleMailMessage.setText(message);
        //执行发送
        try {//发送可能失败
            javaMailSender.send(simpleMailMessage);
            //没有异常返回true，表示发送成功
            System.out.println("发送成功");
//            return true;
        } catch (Exception e) {
            //发送失败，返回false
            System.out.println("发送失败");
//            return false;
        }
    }

}

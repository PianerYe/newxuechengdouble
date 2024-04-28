package com.xuecheng.checkcode.controller;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.model.R;
import com.xuecheng.checkcode.service.CheckCodeService;
import com.xuecheng.checkcode.utils.ValidateCodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xuecheng.checkcode.utils.SMSUtils.sendShortMessage;

/**
 * @author Mr.M
 * @version 1.0
 * @description 验证码服务接口
 * @date 2022/9/29 18:39
 */
@Api(value = "验证码服务接口")
@RestController
public class CheckCodeController {

    @Resource(name = "PicCheckCodeService")
    private CheckCodeService picCheckCodeService;
    @Resource
    RedisTemplate redisTemplate;
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Resource
    private JavaMailSender javaMailSender;


    @ApiOperation(value = "生成验证信息", notes = "生成验证信息")
    @PostMapping(value = "/pic")
    public CheckCodeResultDto generatePicCheckCode(CheckCodeParamsDto checkCodeParamsDto) {
        return picCheckCodeService.generate(checkCodeParamsDto);
    }

    @ApiOperation(value = "校验", notes = "校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "业务名称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "key", value = "验证key", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/verify")
    public Boolean verify(String key, String code) {
        Boolean isSuccess = picCheckCodeService.verify(key, code);
        return isSuccess;
    }

    /**
     * http://www.51xuecheng.cn/api/checkcode/phone?param1=aimeinvshen@126.com
     * */
    //通过param1参数发送验证码，邮箱方式发送到邮箱，手机方式发送到手机上
    //验证码是6位数字展示
    //1:随机生成6位验证码，然后放入redis，同时发送到相应的邮箱和电话中

    /**
     * 发送手机短信验证码
     */
    @PostMapping("/phone")
    @ApiOperation("发送手机短信验证码接口")
    public R<String> sendMsg(String phoneOrEmail) {
        //随机生成一个6位数字验证码
        String code = ValidateCodeUtils.generateValidateCode(6).toString();
        //给用户发送验证码
        System.out.println(code);
        //假如是手机,做校验
        if (isChinaPhone(phoneOrEmail)) {
            //发送手机验证码
            sendShortMessage(Integer.parseInt(code), phoneOrEmail);  //为了省钱，不再发了
        } else if (isValidEmail(phoneOrEmail)) {
            //发送邮箱验证码到邮箱
            sendEmail(phoneOrEmail,"重置密码","你的学成账号密码重置中，验证码是:" + code );
        } else {
            //报错
            XueChengPlusException.cast("输入的邮箱及手机号均有误");
        }

        //将生成的验证码缓存到Redis中，并且设置有效期为五分钟
        redisTemplate.opsForValue().set(phoneOrEmail, code, 5, TimeUnit.MINUTES);

        return R.success("手机验证码发送成功");
    }

    //手机号做校验
    public boolean isChinaPhone(String phone) {
        String regExp = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[3,5,6,7,8])|(18[0-9])|(19[8,9]))\\d{8}$";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    //邮箱校验
    public static boolean isValidEmail(String email) {
        if ((email != null) && (!email.isEmpty())) {
            return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", email);
        }
        return false;
    }

    //发送邮箱验证码
    public boolean sendEmail(String toEmail, String text, String message) {
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
            return true;
        } catch (Exception e) {
            //发送失败，返回false
            return false;
        }
    }


}


package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.auth.utils.ValidPhoneAndEmailUtils;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.FindPassowrdDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

/**
 * @author yepianer
 * @date 2023/10/5 23:56
 * @project_name yepianerxuecheng
 * @description 账号名密码方式
 */
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {

    @Resource
    XcUserMapper xcUserMapper;
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    CheckCodeClient checkCodeClient;
    @Resource
    RedisTemplate redisTemplate;
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //账号
        String username = authParamsDto.getUsername();

        //输入的验证码
        String checkcode = authParamsDto.getCheckcode();
        //验证码对应的key
        String checkcodekey = authParamsDto.getCheckcodekey();

        if (StringUtils.isEmpty(checkcode) || StringUtils.isEmpty(checkcodekey)){
            throw new RuntimeException("请输入验证码");
        }

        //远程调用验证码服务接口去校验验证码
        //todo:校验验证码
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (verify == null || !verify){
            throw new RuntimeException("验证码输入错误");
        }


        //账号是否存在
        //根据username账号查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        //查询到用户不存在，要返回null即可，spring security框架提示抛出异常用户不存在
        if (xcUser == null){
            throw new RuntimeException("账号不存在");
        }
        //验证密码是否正确
        //如果查到了用户拿到了正确的密码
        String passwordDb = xcUser.getPassword();
        //拿到用户输入的密码
        String passwordForm = authParamsDto.getPassword();
        //校验密码
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        if (!matches){
            throw new RuntimeException("账号或密码错误");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        return xcUserExt;
    }

    public void findPassword(@RequestBody FindPassowrdDto findPassowrdDto){
        //找回密码需要输入的对象，用于修改用户数据库的密码
        //FindPassowrdDto
        //1:先判断输入的密码是否一致,且密码不能为空
        //本身是前端做的校验
        String password = findPassowrdDto.getPassword();
        if (password == null && "".equals(password)){
            //提醒密码不能为空
            throw new RuntimeException("密码不能为空");
        }
        if (!password.equals(findPassowrdDto.getConfirmpwd())){
            throw new RuntimeException("两次输入的密码不一致");
        }
        //校验手机以及邮箱的格式，只要其中一个满足即可
        if (ValidPhoneAndEmailUtils.isChinaPhone(findPassowrdDto.getCellphone())){
            //校验验证码是否正确
            String checkcode = findPassowrdDto.getCheckcode();
            String checkcodeRedis = (String) redisTemplate.opsForValue().get(findPassowrdDto.getCellphone() + "findpassword");
            if (password == null && "".equals(password)){
                //验证码不能为空
                XueChengPlusException.cast("验证码不能为空");
            }
            //通过手机号查找是否在数据库存在这个账号

        } else if (ValidPhoneAndEmailUtils.isValidEmail(findPassowrdDto.getEmail())) {
            //校验验证码是否正确
            //通过邮箱查找是否在数据库中存在这个账号

        }



    }
}

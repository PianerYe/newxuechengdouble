package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.auth.utils.ValidPhoneAndEmailUtils;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.FindPassowrdDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import com.xuecheng.ucenter.service.PasswordService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

@Service
public class PasswordServiceImpl implements PasswordService {

    @Resource
    RedisTemplate redisTemplate;
    @Resource
    XcUserMapper xcUserMapper;
    @Resource
    PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void findPassword(FindPassowrdDto findPassowrdDto) {

            //找回密码需要输入的对象，用于修改用户数据库的密码
            //FindPassowrdDto
            //1:先判断输入的密码是否一致,且密码不能为空
            //本身是前端做的校验
            String password = findPassowrdDto.getPassword();
            if (password == null && "".equals(password)){
                //提醒密码不能为空
                XueChengPlusException.cast("密码不能为空");
            }
            if (!password.equals(findPassowrdDto.getConfirmpwd())){
                XueChengPlusException.cast("两次输入的密码不一致");
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
                if (password.equals(checkcodeRedis)){
                    //通过手机号查找是否在数据库存在这个账号
                    LambdaQueryWrapper<XcUser> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(XcUser::getCellphone,findPassowrdDto.getCellphone());
                    XcUser xcUser = xcUserMapper.selectOne(queryWrapper);
                    if (xcUser == null && "".equals(xcUser)){
                        XueChengPlusException.cast("账号不存在,请注册账号");
                    }
                    String encode = passwordEncoder.encode(findPassowrdDto.getPassword());
                    xcUser.setPassword(encode);

                    int i = xcUserMapper.updateById(xcUser);
                    if (i <= 0){
                        XueChengPlusException.cast("密码重置失败");
                    }
                }


            } else if (ValidPhoneAndEmailUtils.isValidEmail(findPassowrdDto.getEmail())) {
                //校验验证码是否正确
                String checkcode = findPassowrdDto.getCheckcode();
                String checkcodeRedis = (String) redisTemplate.opsForValue().get(findPassowrdDto.getEmail() + "findpassword");
                //通过邮箱查找是否在数据库中存在这个账号
                LambdaQueryWrapper<XcUser> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(XcUser::getEmail,findPassowrdDto.getEmail());
                XcUser xcUser = xcUserMapper.selectOne(queryWrapper);
                if (xcUser == null && "".equals(xcUser)){
                    XueChengPlusException.cast("账号不存在,请注册账号");
                    return;
                }
                String encode = passwordEncoder.encode(findPassowrdDto.getPassword());
                xcUser.setPassword(encode);
                int i = xcUserMapper.updateById(xcUser);
                if (i <= 0){
                    XueChengPlusException.cast("密码重置失败");
                }

            }
    }
}

package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yepianer
 * @date 2023/10/3 22:42
 * @project_name yepianerxuecheng
 * @description
 */
@Slf4j
@Component
public class UserServiceImpl implements UserDetailsService {

    @Resource
    XcUserMapper xcUserMapper;
    @Resource
    XcMenuMapper xcMenuMapper;

    @Resource
    ApplicationContext applicationContext;

    //传人的认证请求参数就是AuthParamsDto
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
//        //将传入的json转成AuthParamsDto对象
//        AuthParamsDto authParamsDto = null;
//
//        try{
//            authParamsDto = JSON.parseObject(s,AuthParamsDto.class);
//        }catch (Exception e){
//            throw new RuntimeException("请求认证的参数不符合要求");
//        }
//
//        //认证类型,有password,wx...
//        String authType = authParamsDto.getAuthType();
//
//        //根据认证类型从spring容器中取出指定的Bean
//        String beanName = authType + "_authservice";
//        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
//        //调用统一execute方法完成认证
//        XcUserExt xcUserExt = authService.execute(authParamsDto);
//        //封装XcUserExt用户信息为Use'r'Details
//        //根据UserDetails对象生成令牌
//        UserDetails userPrincipal = getUserPrincipal(xcUserExt);
//
//        return userPrincipal;
        //账号
        String username = s;
        //根据username查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        //查询用户名不存在，返回Null即可
        if (xcUser == null){
            return null;
        }
        String password = xcUser.getPassword();
        //权限
        String[] authorities = {"test"};

        xcUser.setPassword(null);
        //将用户信息转json
        String userJson = JSON.toJSONString(xcUser);
        UserDetails userDetails = User.withUsername(userJson).password(password).authorities(authorities).build();

        return userDetails;
    }

    /**
     * @description 查询用户信息
     * @param xcUser  用户id，主键
     * @return com.xuecheng.ucenter.model.po.XcUser 用户信息
     * @author Mr.M
     * @date 2022/9/29 12:19
     */
    public UserDetails getUserPrincipal(XcUserExt xcUser){
        String password = xcUser.getPassword();
        //权限
        String[] authorities = {"test"};
        //根据用户的id查询用户的权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(xcUser.getId());
        if (xcMenus.size()>0){
            List<String> permissions = new ArrayList<>();
            xcMenus.forEach(m->{
                //拿到用户拥有的权限标识符
                permissions.add(m.getCode());
            });
            //将permissions转成数组
            authorities = permissions.toArray(new String[0]);
        }

        xcUser.setPassword(null);
        //将用户的信息转成json
        String userJson = JSON.toJSONString(xcUser);
        UserDetails userDetails = User.withUsername(userJson).password(password).authorities(authorities).build();
        return userDetails;
    }
}

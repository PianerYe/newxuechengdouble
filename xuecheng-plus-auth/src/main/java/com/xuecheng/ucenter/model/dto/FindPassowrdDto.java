package com.xuecheng.ucenter.model.dto;

import lombok.Data;
  /**
   *
   *
   *    "cellphone": "",
   *    "email": "",
   *    "checkcodekey": "",
   *    "checkcode": "",
   *    "confirmpwd": "",
   *    "password": ""
   *
   *
   * */
@Data
public class FindPassowrdDto {

        String cellphone;//手机号码
        String email;//电子邮箱
        String checkcodekey;
        String checkcode;//验证码
        String confirmpwd;//重复输入的密码
        String password;//第一次输入的密码
}


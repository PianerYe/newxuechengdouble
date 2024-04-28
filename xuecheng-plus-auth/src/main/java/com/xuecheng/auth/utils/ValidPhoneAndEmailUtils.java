package com.xuecheng.auth.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidPhoneAndEmailUtils {

    //手机号做校验
    public static boolean isChinaPhone(String phone) {
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
}

package com.paojiao.sdk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Desction:表单判断
 * Author:pengjianbo
 * Date:15/7/21 下午8:13
 */
public class FormVerifyUtils {

    public static boolean checkUserName(String username) {
        if (StringUtils.isEmpty(username) || username.length() < 6 || username.length() > 16) {
            return  false;
        }
        return true;
    }

    public static boolean checkPassword(String password) {
        if (StringUtils.isEmpty(password) || password.length() < 6 || password.length() > 30) {
            return  false;
        }
        return true;
    }

    public static boolean checkMobile(String mobile) {
        try {
            if (mobile == null || mobile.length() != 11) {
                return false;
            } else {
                Pattern p = Pattern.compile("^[1][0-9]{10}$");
                Matcher m = p.matcher(mobile);
                return (m.matches());
            }
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}

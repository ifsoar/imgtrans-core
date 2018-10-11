package com.soar.analysis;

import java.util.UUID;

/**
 * @Author soar
 * @Description //UUID工具类
 * @Date 2018/9/23
 **/

public class UUIDUtil {
    public static String make() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

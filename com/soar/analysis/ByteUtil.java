package com.soar.analysis;

/**
 * @Author soar
 * @Description //TODO
 * @Date 2018/10/11
 **/

public class ByteUtil {
    public static byte[] long2Bytes(long length, int le) {
        byte[] targets = new byte[le];
        for (int i = 0; i < le; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((length >>> offset) & 0xff);
        }
        return targets;
    }
}

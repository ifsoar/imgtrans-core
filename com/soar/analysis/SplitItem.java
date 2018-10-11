package com.soar.analysis;

/**
 * @Author soar
 * @Description //TODO
 * @Date 2018/10/11
 **/

public class SplitItem {
    private String name;
    private String md5;
    private long length;
    private int index;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

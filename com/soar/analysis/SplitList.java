package com.soar.analysis;

import java.util.List;

/**
 * @Author soar
 * @Description //TODO
 * @Date 2018/10/11
 **/

public class SplitList {
    private int splitCount;
    private String sourceName;
    private long sourceLength;
    private String format;
    private String md5;
    private List<SplitItem> itemList;
    private String password;

    public int getSplitCount() {
        return splitCount;
    }

    public void setSplitCount(int splitCount) {
        this.splitCount = splitCount;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public long getSourceLength() {
        return sourceLength;
    }

    public void setSourceLength(long sourceLength) {
        this.sourceLength = sourceLength;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public List<SplitItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<SplitItem> itemList) {
        this.itemList = itemList;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

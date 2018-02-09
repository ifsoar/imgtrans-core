package com.soar.analysis;

import java.util.List;

public class SplitList {
    private int splitCount;
    private String sourceName;
    private long sourceLength;
    private String format;
    private List<SplitItem> itemList;

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

    public List<SplitItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<SplitItem> itemList) {
        this.itemList = itemList;
    }
}

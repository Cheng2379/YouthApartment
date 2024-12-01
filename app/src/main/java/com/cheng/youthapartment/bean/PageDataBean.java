package com.cheng.youthapartment.bean;

import java.util.ArrayList;
import java.util.List;

public class PageDataBean<T> {

    /**
     * records : [...]
     * total : 13
     * size : 5
     * current : 1
     * pages : 3
     */
    private ArrayList<T> records;
    private int total;
    private int size;
    private int current;
    private int pages;

    public PageDataBean() {

    }

    public PageDataBean(ArrayList<T> records, int total, int size, int current, int pages) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = pages;
    }

    public ArrayList<T> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<T> records) {
        this.records = records;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "PageDataBean{" +
                "records=" + records +
                ", total=" + total +
                ", size=" + size +
                ", current=" + current +
                ", pages=" + pages +
                '}';
    }
}

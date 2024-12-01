package com.cheng.youthapartment.bean;

import java.io.Serializable;

public class GraphVoBean implements Serializable {
    /**
     * name : 公寓 (12).jpg
     * url : http://xxx:xxx/lease/20240929/5dad9a85-5118-4c2e-9757-01cc364bc622-公寓 (12).jpg
     */
    private String name;
    private String url;

    public GraphVoBean() {

    }

    public GraphVoBean(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
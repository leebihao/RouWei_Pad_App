package com.scinan.sdk.bean;

import java.io.Serializable;

/**
 * Created by lijunjie on 2017/12/19.
 */

public class APBean implements Serializable {

    private String displayName;
    private String key;
    private Object data;

    public APBean(String name, String key, Object data) {
        this.displayName = name;
        this.key = key;
        this.data = data;
    }

    public APBean(String key) {
        this(null, key, null);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof APBean) {
            APBean b = (APBean) obj;
            return b.getKey().equalsIgnoreCase(getKey());
        }
        return false;
    }
}


package com.scinan.sdk.api.v2.bean;


import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * <pre>
 *     author : kentli
 *     e-mail : kentli@scinan.com
 *     time   : 2020/02/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MultiDevicesBean implements Serializable {
    public String device_id;
    public String title;
    public String type;
    public String model;
    public String product_id;
    public String hardware_version;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getHardware_version() {
        return hardware_version;
    }

    public void setHardware_version(String hardware_version) {
        this.hardware_version = hardware_version;
    }

    public TreeMap<String, String> getAddMultiDeviceTree() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", device_id);
        param.put("title", title);
        param.put("type", type);
        param.put("model", model);
        param.put("product_id", product_id);
        param.put("hardware_version", hardware_version);
        return param;
    }

    @NonNull
    @Override
    public String toString() {
        return "[ " + this.device_id + "   "  + this.type + " ]";
    }
}

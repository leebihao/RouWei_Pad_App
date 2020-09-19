package com.scinan.sdk.bluetooth;

import java.io.Serializable;

/**
 * Created by lijunjie on 17/4/13.
 */

public class ScanFilter implements Serializable {

    private String companyId;
    private String type;

    public ScanFilter() {
    }

    public ScanFilter(String companyId, String type) {
        this.companyId = companyId;
        this.type = type;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

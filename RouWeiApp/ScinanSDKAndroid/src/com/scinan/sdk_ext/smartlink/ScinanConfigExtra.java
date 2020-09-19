package com.scinan.sdk_ext.smartlink;

import java.io.Serializable;

/**
 * Created by lijunjie on 17/6/10.
 */

public class ScinanConfigExtra implements Serializable {

    private String companyId;
    private boolean isLoggable;
    private boolean isTestApi;

    public ScinanConfigExtra(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public boolean isLoggable() {
        return isLoggable;
    }

    public void setLoggable(boolean loggable) {
        isLoggable = loggable;
    }

    public boolean isTestApi() {
        return isTestApi;
    }

    public void setTestApi(boolean testApi) {
        isTestApi = testApi;
    }
}

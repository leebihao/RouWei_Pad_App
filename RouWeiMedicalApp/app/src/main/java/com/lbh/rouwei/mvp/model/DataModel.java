package com.lbh.rouwei.mvp.model;

import com.lbh.rouwei.common.bean.LoginBean;
import com.lbh.rouwei.common.bean.base.BaseObjectBean;
import com.lbh.rouwei.mvp.contract.DataContract;
import com.lbh.rouwei.common.network.RetrofitClient;

import io.reactivex.Observable;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/08/29
 *     desc   :
 * </pre>
 */
public class DataModel implements DataContract.Model {
    @Override
    public Observable<BaseObjectBean<LoginBean>> login(String username, String password) {
        return RetrofitClient.getInstance().getApi().login(username,password);
    }
}

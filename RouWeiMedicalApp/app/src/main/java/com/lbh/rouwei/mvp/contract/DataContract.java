package com.lbh.rouwei.mvp.contract;

import com.lbh.rouwei.common.bean.LoginBean;
import com.lbh.rouwei.common.bean.base.BaseObjectBean;
import com.lbh.rouwei.mvp.base.BaseView;

import io.reactivex.Observable;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/08/29
 *     desc   :
 * </pre>
 */
public interface DataContract {

    interface Model {
        Observable<BaseObjectBean<LoginBean>> login(String username, String password);
    }

    interface View extends BaseView {

        @Override
        void showLoading();

        @Override
        void hideLoading();

        @Override
        void onError(String errMessage);

    }

    interface Presenter {
        /**
         * 登陆
         *
         * @param username
         * @param password
         */
        void login(String username, String password);
    }
}

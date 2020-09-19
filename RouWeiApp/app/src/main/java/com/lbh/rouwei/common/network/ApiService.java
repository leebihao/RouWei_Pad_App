package com.lbh.rouwei.common.network;


import com.lbh.rouwei.common.bean.LoginBean;
import com.lbh.rouwei.common.bean.base.BaseObjectBean;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * @author azheng
 * @date 2018/4/24.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public interface ApiService {

    /**
     * 登陆
     *
     * @param username 账号
     * @param password 密码
     * @return
     */
    @FormUrlEncoded
    @POST("user/login")
    Observable<BaseObjectBean<LoginBean>> login(@Field("username") String username,
                                                @Field("password") String password);

}

package com.lbh.rouwei.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpActivity;
import com.lbh.rouwei.common.adapter.DeviceListAdapter;
import com.lbh.rouwei.common.bean.SocketDevice;
import com.lbh.rouwei.common.constant.Constant;
import com.lbh.rouwei.common.utils.AppUtil;
import com.lbh.rouwei.zmodule.config.ui.activity.AirkissConfigStep1Activity;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.util.LogUtil;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/21
 *     desc   :
 * </pre>
 */
public class DeviceListActivity extends BaseMvpActivity implements DeviceListAdapter.DeviceListCallback {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.deviceListView)
    ListView deviceListView;
    @BindView(R.id.iv_go_add)
    ImageView ivGoAdd;
    private List<SocketDevice> mDeviceList;
    private DeviceListAdapter mDeviceAdapter;

    @Override
    protected void getExtarDataFromPrePage(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_device_list;
    }

    @Override
    public void initView() {
        ivGoAdd.setVisibility(View.VISIBLE);
        mDeviceList = new ArrayList<>();
        mDeviceAdapter = new DeviceListAdapter(context, mDeviceList);
        mDeviceAdapter.setCallback(this);
        deviceListView.setAdapter(mDeviceAdapter);
        mDeviceAgent.getDeviceList();
    }

    @Override
    public void initData() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(String errMessage) {

    }

    @Override
    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
        super.OnFetchDataSuccess(api, responseCode, responseBody);
        switch (api) {
            case RequestHelper.API_DEVICE_LIST:
                try {
                    LogUtil.d("====" + responseBody);
                    List<SocketDevice> devices = new ArrayList<SocketDevice>();
                    JSONArray jsonArray = JSON.parseArray(responseBody);
                    if (jsonArray.size() == 0) {
                        startActivity(new Intent(this, AirkissConfigStep1Activity.class));
                        finish();
                        return;
                    }

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        if (AppUtil.isSupportType(jo.getString("type"))) {
                            List<SocketDevice> typeDevices = JSON.parseArray(jo.getJSONArray("devices").toJSONString(), SocketDevice.class);
                            devices.addAll(typeDevices);
                        }
                    }

                    Collections.sort(devices, new Comparator<SocketDevice>() {
                        @Override
                        public int compare(SocketDevice lhs, SocketDevice rhs) {

                            LogUtil.d("" + lhs.getType().compareTo(rhs.getType()));
                            if (lhs.getType().compareTo(rhs.getType()) > 0) {
                                return 1;
                            }

                            return -1;
                        }
                    });

                    mDeviceList.clear();
                    mDeviceList.addAll(devices);
                    mDeviceAdapter.notifyDataSetChanged();
                    deviceListView.setSelection(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    @Override
    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
        super.OnFetchDataFailed(api, error, responseBody);
        showToast(JsonUtil.parseErrorMsg(responseBody));
    }


    @Override
    public void OnSwitchChanged(int position, boolean checked) {

    }

    @Override
    public void OnLongClickListener(int position) {

    }

    @Override
    public void OnItemClickListener(int position) {
        Intent intent = new Intent(this, MainActivity.class);
        SocketDevice socketDevice = mDeviceList.get(position);
        intent.putExtra(Constant.KEY_DEVICE_ID, socketDevice.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.iv_go_add)
    public void onViewClicked() {
        startActivityForResult(new Intent(this, AirkissConfigStep1Activity.class), 100);
    }

    @OnClick(R.id.iv_back)
    public void onGoBack() {
        AppUtil.goAndroidHome(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            mDeviceAgent.getDeviceList();
        }
    }
}

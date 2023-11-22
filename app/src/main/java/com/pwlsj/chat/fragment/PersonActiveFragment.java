package com.pwlsj.chat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pwlsj.chat.R;
import com.pwlsj.chat.activity.InfoActiveActivity;
import com.pwlsj.chat.adapter.InfoActiveRecyclerAdapter;
import com.pwlsj.chat.base.AppManager;
import com.pwlsj.chat.base.BaseResponse;
import com.pwlsj.chat.base.LazyFragment;
import com.pwlsj.chat.bean.ActiveBean;
import com.pwlsj.chat.bean.ActiveFileBean;
import com.pwlsj.chat.bean.PageBean;
import com.pwlsj.chat.constant.ChatApi;
import com.pwlsj.chat.constant.Constant;
import com.pwlsj.chat.net.AjaxCallback;
import com.pwlsj.chat.net.NetCode;
import com.pwlsj.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料下方动态
 * 作者：
 * 创建时间：2018/6/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PersonActiveFragment extends LazyFragment implements View.OnClickListener {

    public Activity mContext;
    private InfoActiveRecyclerAdapter mAdapter;
    private TextView mSeeMoreTv;
    private int mActorId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_info_active_layout,
                container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mSeeMoreTv = view.findViewById(R.id.see_more_tv);
        mSeeMoreTv.setOnClickListener(this);
        RecyclerView content_rv = view.findViewById(R.id.content_rv);
        content_rv.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        content_rv.setLayoutManager(manager);
        mAdapter = new InfoActiveRecyclerAdapter(mContext);
        content_rv.setAdapter(mAdapter);
        mIsViewPrepared = true;
    }

    @Override
    protected void onFirstVisibleToUser() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActorId = getActivity().getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        getActiveList();
        mIsDataLoadCompleted = true;
    }

    /**
     * 获取动态列表
     */
    private void getActiveList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id + "");
        paramMap.put("coverUserId", String.valueOf(mActorId));
        paramMap.put("page", String.valueOf(1));
        paramMap.put("reqType", String.valueOf(0));//0.公开动态，1.关注动态
        OkHttpUtils.post().url(ChatApi.GET_PRIVATE_DYNAMIC_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<ActiveBean<ActiveFileBean>>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<ActiveBean<ActiveFileBean>>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<ActiveBean<ActiveFileBean>> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<ActiveBean<ActiveFileBean>> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            mAdapter.loadData(focusBeans);
                            if (focusBeans.size() >= 10) {
                                mSeeMoreTv.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.see_more_tv: {//查看更多
                Intent intent = new Intent(mContext, InfoActiveActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}

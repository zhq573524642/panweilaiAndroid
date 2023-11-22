package com.pwlsj.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.pwlsj.chat.R;
import com.pwlsj.chat.activity.PersonInfoActivity;
import com.pwlsj.chat.base.AppManager;
import com.pwlsj.chat.base.BaseFragment;
import com.pwlsj.chat.base.BaseResponse;
import com.pwlsj.chat.bean.FocusBean;
import com.pwlsj.chat.constant.ChatApi;
import com.pwlsj.chat.helper.ImageLoadHelper;
import com.pwlsj.chat.net.FocusRequester;
import com.pwlsj.chat.net.PageRequester;
import com.pwlsj.chat.net.RefreshHandler;
import com.pwlsj.chat.net.RefreshPageListener;
import com.pwlsj.chat.util.BeanParamUtil;
import com.pwlsj.chat.util.DevicesUtil;
import com.pwlsj.chat.util.ToastUtil;
import com.pwlsj.chat.view.recycle.AbsRecycleAdapter;
import com.pwlsj.chat.view.recycle.OnItemClickListener;
import com.pwlsj.chat.view.recycle.ViewHolder;

import java.util.List;

/**
 * 谁喜欢我/我喜欢谁/相互喜欢关注
 */
public class FollowFragment extends BaseFragment {

    final int smallOverWidth = DevicesUtil.dp2px(AppManager.getInstance(), 50);

    private SmartRefreshLayout mRefreshLayout;

    private AbsRecycleAdapter mAdapter;

    private PageRequester<FocusBean> requester;

    @Override
    protected int initLayout() {
        return R.layout.fragment_follow;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);

        requester = new PageRequester<FocusBean>() {
            @Override
            public void onSuccess(List<FocusBean> list, boolean isRefresh) {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                mAdapter.setData(list, isRefresh);
            }
        };

        requester.setApi(ChatApi.GET_COVER_FOLLOW());
        requester.setOnPageDataListener(new RefreshPageListener(mRefreshLayout));

        mRefreshLayout.setOnRefreshListener(new RefreshHandler(requester));
        mRefreshLayout.setOnLoadMoreListener(new RefreshHandler(requester));

        mContentRv.getItemAnimator().setChangeDuration(0);
        mContentRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AbsRecycleAdapter(new AbsRecycleAdapter.Type(R.layout.item_follow, FocusBean.class)) {

            @Override
            public void convert(ViewHolder holder, Object t) {

                FocusBean bean = (FocusBean) t;

                //昵称
                holder.<TextView>getView(R.id.nick_tv).setText(bean.t_nickName);

                //头像
                ImageView imageView = holder.getView(R.id.head_iv);
                if (!TextUtils.isEmpty(bean.t_handImg)) {
                    ImageLoadHelper.glideShowCircleImageWithUrl(
                            mContext,
                            bean.t_handImg,
                            imageView,
                            smallOverWidth,
                            smallOverWidth);
                } else {
                    imageView.setImageResource(R.drawable.default_head_img);
                }

                TextView tvAge = holder.getView(R.id.age_tv);

                //年龄
                tvAge.setText(BeanParamUtil.getAge(bean.t_age));

                //性别
                tvAge.setSelected(bean.t_sex == 0);

                TextView textView = holder.getView(R.id.attention_tv);

                //是否关注
                textView.setSelected(bean.isFollow == 1);
                textView.setText(BeanParamUtil.getFocus(bean.isFollow, bean.isCoverFollow));
            }

            @Override
            public void setViewHolder(final ViewHolder viewHolder) {

                //关注点击事件
                viewHolder.getView(R.id.attention_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final FocusBean bean = (FocusBean) getData().get(viewHolder.getRealPosition());
                        boolean focus = bean.isFollow != 0;
                        new FocusRequester() {
                            @Override
                            public void onSuccess(BaseResponse response, boolean focus) {
                                ToastUtil.showToast(response.m_strMessage);
                                bean.isFollow = focus ? 1 : 0;
                                notifyItemChanged(viewHolder.getRealPosition());
                            }
                        }.focus(bean.t_id, !focus);
                    }
                });
            }

        };

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object object, int position) {
                FocusBean bean = (FocusBean) mAdapter.getData().get(position);
                PersonInfoActivity.start(mContext, bean.t_id);
            }
        });

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                getView().findViewById(R.id.empty_tv).setVisibility(mAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });

        mContentRv.setAdapter(mAdapter);
    }

    @Override
    protected void onFirstVisible() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
         * type     0谁喜欢我    1我喜欢谁     2互相喜欢
         */
        requester.setParam("type", getActivity().getIntent().getIntExtra("type", 0));
        requester.onRefresh();
    }
}
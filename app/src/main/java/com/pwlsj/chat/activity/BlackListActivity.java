package com.pwlsj.chat.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pwlsj.chat.R;
import com.pwlsj.chat.base.AppManager;
import com.pwlsj.chat.base.BasePageActivity;
import com.pwlsj.chat.base.BaseResponse;
import com.pwlsj.chat.bean.BlackBean;
import com.pwlsj.chat.constant.ChatApi;
import com.pwlsj.chat.glide.GlideCircleTransform;
import com.pwlsj.chat.net.BlackRequester;
import com.pwlsj.chat.net.PageRequester;
import com.pwlsj.chat.util.BeanParamUtil;
import com.pwlsj.chat.util.DevicesUtil;
import com.pwlsj.chat.util.TimeUtil;
import com.pwlsj.chat.view.recycle.AbsRecycleAdapter;
import com.pwlsj.chat.view.recycle.OnItemClickListener;
import com.pwlsj.chat.view.recycle.ViewHolder;

import java.util.List;

public class BlackListActivity extends BasePageActivity<BlackBean> {

    final int smallOverWidth = DevicesUtil.dp2px(AppManager.getInstance(), 50);

    @Override
    public PageRequester<BlackBean> createRequester() {
        return new PageRequester<BlackBean>() {
            @Override
            public void onSuccess(List<BlackBean> list, boolean isRefresh) {
                if (isFinishing())
                    return;
                handleList(list, isRefresh);
            }
        };
    }

    @Override
    public RecyclerView.Adapter createAdapter() {
        return new AbsRecycleAdapter(new AbsRecycleAdapter.Type(R.layout.item_black, BlackBean.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {
                BlackBean bean = (BlackBean) t;

                //昵称
                holder.<TextView>getView(R.id.nick_tv).setText(bean.t_nickName);

                //头像
                ImageView imageView = holder.getView(R.id.head_iv);
                Glide.with(mContext)
                        .load(bean.t_handImg)
                        .error(R.drawable.default_head_img)
                        .transform(new GlideCircleTransform(mContext))
                        .into(imageView);

                //关注时间
                holder.<TextView>getView(R.id.time_tv).setText(TimeUtil.getTimeStr(bean.t_create_time));

                TextView tvAge = holder.getView(R.id.age_tv);

                //年龄
                tvAge.setText(BeanParamUtil.getAge(bean.t_age));

            }

            @Override
            public void setViewHolder(final ViewHolder viewHolder) {
                viewHolder.getView(R.id.post_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final BlackBean bean = (BlackBean) getAbsAdapter().getData().get(viewHolder.getRealPosition());
                        new BlackRequester() {
                            @Override
                            public void onSuccess(BaseResponse response, boolean addToBlack) {
                                getAbsAdapter().getData().remove(bean);
                                adapter.notifyDataSetChanged();
                            }
                        }.post(bean.t_id, false);
                    }
                });
            }
        };
    }

    @Override
    protected void onContentAdded() {
        super.onContentAdded();
        setTitle(R.string.black_list);
        refresh();
        getAbsAdapter().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object object, int position) {
                BlackBean bean = (BlackBean) getAbsAdapter().getData().get(position);
                PersonInfoActivity.start(mContext, bean.t_id);
            }
        });
    }

    @Override
    public String getApi() {
        return ChatApi.getBlackUserList();
    }
}

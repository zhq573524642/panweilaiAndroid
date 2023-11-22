package com.pwlsj.chat.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.pwlsj.chat.R;
import com.pwlsj.chat.activity.MainActivity;
import com.pwlsj.chat.base.BaseActivity;
import com.pwlsj.chat.base.BaseFragment;
import com.pwlsj.chat.dialog.RewardRuleDialog;
import com.pwlsj.chat.view.tab.FragmentParamBuilder;
import com.pwlsj.chat.view.tab.RankLabelViewHolder;
import com.pwlsj.chat.view.tab.TabFragmentAdapter;
import com.pwlsj.chat.view.tab.TabPagerLayout;

/**
 * 榜单页面
 */
public class RankGroupFragment extends BaseFragment {

    @Override
    protected int initLayout() {
        return R.layout.fragment_rank_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        Bundle bundle = new Bundle();
        bundle.putString("type", RankFragment.RankType.Goddess.name());

        Bundle bundle1 = new Bundle();
        bundle1.putString("type", RankFragment.RankType.Invitation.name());

        Bundle bundle3 = new Bundle();
        bundle3.putString("type", RankFragment.RankType.Consumption.name());

        Bundle bundle4 = new Bundle();
        bundle4.putString("type", RankFragment.RankType.Guard.name());

        final ViewPager mContentVp = view.findViewById(R.id.content_vp);
        TabPagerLayout tabPagerLayout = view.findViewById(R.id.category_rg);

        TabFragmentAdapter adapter = new TabFragmentAdapter(getChildFragmentManager(), mContentVp);

        adapter.init(

                FragmentParamBuilder.create()
                        .withName("魅力榜")
                        .withBundle(bundle)
                        .withClazz(RankFragment.class)
                        .withViewHolder(new RankLabelViewHolder(tabPagerLayout))
                        .build(),
//                FragmentParamBuilder.create()
//                        .withName("邀请榜")
//                        .withBundle(bundle1)
//                        .withClazz(RankFragment.class)
//                        .withViewHolder(new RankLabelViewHolder(tabPagerLayout))
//                        .build(),

                FragmentParamBuilder.create()
                        .withName("富豪榜")
                        .withBundle(bundle3)
                        .withClazz(RankFragment.class)
                        .withViewHolder(new RankLabelViewHolder(tabPagerLayout))
                        .build()

//                FragmentParamBuilder.create()
//                        .withName("守护榜")
//                        .withBundle(bundle4)
//                        .withClazz(RankFragment.class)
//                        .withViewHolder(new RankLabelViewHolder(tabPagerLayout))
//                        .build()
        );
        tabPagerLayout.init(mContentVp);

        final int[] colors = {0xFFFF3088, 0xFF4C95FF, 0xFFFF3088, 0xFF4C95FF};

        final View changeView = view.findViewById(R.id.bg_view);
        changeView.setBackgroundColor(colors[0]);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mContentVp.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int start = mContentVp.getScrollX() / mContentVp.getWidth();
                    float position = mContentVp.getScrollX() % mContentVp.getWidth() / (float) mContentVp.getWidth();
                    if (colors.length == start + 1) {
                        changeView.setBackgroundColor(colors[start]);
                    } else {
                        changeView.setBackgroundColor(getColorChanges(colors[start], colors[start + 1], position));
                    }
                }
            });
        }
    }

    private int getColorChanges(int cl1, int cl2, float change) {
        int R, G, B;
        R = (int) (Color.red(cl1) + (Color.red(cl2) - Color.red(cl1)) * change);
        G = (int) (Color.green(cl1) + (Color.green(cl2) - Color.green(cl1)) * change);
        B = (int) (Color.blue(cl1) + (Color.blue(cl2) - Color.blue(cl1)) * change);
        return Color.rgb(R, G, B);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //主页隐藏返回按钮
        View finishBtn = findViewById(R.id.finish_btn);
        if (getActivity().getClass() == MainActivity.class) {
            finishBtn.setVisibility(View.GONE);
        } else {
            finishBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }

        findViewById(R.id.introduction_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RewardRuleDialog((BaseActivity) getActivity()).show();
            }
        });
    }
}
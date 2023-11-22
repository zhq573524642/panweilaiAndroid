package com.pwlsj.chat.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pwlsj.chat.R;
import com.pwlsj.chat.activity.AccountBalanceActivity;
import com.pwlsj.chat.activity.ActorVerifyingActivity;
import com.pwlsj.chat.activity.ApplyCompanyActivity;
import com.pwlsj.chat.activity.ApplyVerifyHandActivity;
import com.pwlsj.chat.activity.ChargeActivity;
import com.pwlsj.chat.activity.HelpCenterActivity;
import com.pwlsj.chat.activity.InviteActivity;
import com.pwlsj.chat.activity.ModifyUserInfoActivity;
import com.pwlsj.chat.activity.MyActorActivity;
import com.pwlsj.chat.activity.MyFollowActivity;
import com.pwlsj.chat.activity.MyInviteActivity;
import com.pwlsj.chat.activity.PhoneNaviActivity;
import com.pwlsj.chat.activity.PhoneVerifyActivity;
import com.pwlsj.chat.activity.SetBeautyActivity;
import com.pwlsj.chat.activity.SetChargeActivity;
import com.pwlsj.chat.activity.SettingActivity;
import com.pwlsj.chat.activity.UserAlbumListActivity;
import com.pwlsj.chat.activity.UserSelfActiveActivity;
import com.pwlsj.chat.activity.VerifyOptionActivity;
import com.pwlsj.chat.activity.VipCenterActivity;
import com.pwlsj.chat.activity.WhoSawTaActivity;
import com.pwlsj.chat.activity.WithDrawActivity;
import com.pwlsj.chat.base.AppManager;
import com.pwlsj.chat.base.BaseFragment;
import com.pwlsj.chat.base.BaseResponse;
import com.pwlsj.chat.bean.ReceiveRedBean;
import com.pwlsj.chat.bean.RedCountBean;
import com.pwlsj.chat.bean.UserCenterBean;
import com.pwlsj.chat.bean.VerifyBean;
import com.pwlsj.chat.constant.ChatApi;
import com.pwlsj.chat.dialog.CompanyInviteDialog;
import com.pwlsj.chat.glide.GlideCircleTransform;
import com.pwlsj.chat.helper.IMHelper;
import com.pwlsj.chat.helper.SharedPreferenceHelper;
import com.pwlsj.chat.net.AjaxCallback;
import com.pwlsj.chat.net.NetCode;
import com.pwlsj.chat.util.CodeUtil;
import com.pwlsj.chat.util.ParamUtil;
import com.pwlsj.chat.util.ToastUtil;
import com.pwlsj.chat.view.recycle.AbsRecycleAdapter;
import com.pwlsj.chat.view.recycle.OnItemClickListener;
import com.pwlsj.chat.view.recycle.ViewHolder;
import com.robinhood.ticker.TickerView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 我的
 */
public class MineFragment extends BaseFragment {

    private Unbinder unbinder;

    //个人资料
    private UserCenterBean userCenterBean;

    //主播认证状态
    private int actorVerifyState = -1;

    private MineMenu menuApply;

    @Override
    protected int initLayout() {
        return R.layout.fragment_mine;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //男性隐藏“申请主播”按钮
        if (AppManager.getInstance().getUserInfo().isSexMan()) {
            findViewById(R.id.verify_btn).setVisibility(View.GONE);
        }
        setFunction();
        refreshUser();
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * 更新个人数据、更新认证状态、检查是否有红包
         */
        getInfo();
        getVerifyStatus();
        receiveRedPacket();
    }

    @OnClick({
            R.id.modify_btn,
            R.id.verify_btn,
            R.id.company_iv,
            R.id.my_like_btn,
            R.id.each_like_btn,
            R.id.follow_btn,
            R.id.victor_btn,
            R.id.charge_btn,
            R.id.with_draw_btn,
            R.id.gold_ll,
            R.id.get_ll,
            R.id.copy_btn,
            R.id.my_dynamic_btn
    })
    public void onClick(View view) {

        switch (view.getId()) {

            //我的动态
            case R.id.my_dynamic_btn:
                startActivity(new Intent(mContext, UserSelfActiveActivity.class));
                break;

            //复制id
            case R.id.copy_btn:
                if (checkInvalidBean())
                    return;
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("ID", String.valueOf(userCenterBean.t_idcard));
                if (cm != null) {
                    cm.setPrimaryClip(mClipData);
                    ToastUtil.showToast(R.string.copy_success);
                }
                break;

            //邀请的人
            case R.id.get_ll:
                startActivity(new Intent(getActivity(), MyInviteActivity.class));
                break;

            //账单
            case R.id.gold_ll:
                startActivity(new Intent(getActivity(), AccountBalanceActivity.class));
                break;

            //公会
            case R.id.company_iv:
                if (checkInvalidBean())
                    return;
                if (!TextUtils.isEmpty(userCenterBean.guildName)) {
                    ToastUtil.showToast(getContext(), String.format(getString(R.string.belong_company), userCenterBean.guildName));
                }
                break;

            //type 0谁喜欢我 1我喜欢谁 2互相喜欢
            //谁喜欢我
            case R.id.my_like_btn:
                MyFollowActivity.start(getActivity(), "我的粉丝", 0);
                break;

            //我的关注
            case R.id.follow_btn:
                MyFollowActivity.start(getActivity(), "我的关注", 1);
                break;

            //互相喜欢
            case R.id.each_like_btn:
                MyFollowActivity.start(getActivity(), "相互关注", 2);
                break;

            //访客
            case R.id.victor_btn:
                if (checkInvalidBean())
                    return;
                WhoSawTaActivity.start(getActivity());
                break;

            //编辑资料
            case R.id.modify_btn:
                startActivity(new Intent(getActivity(), ModifyUserInfoActivity.class));
                break;

            //申请主播
            case R.id.verify_btn:

                //未开放男性申请主播
                if (AppManager.getInstance().getUserInfo().isSexMan()) {
                    ToastUtil.showToast(getContext(), R.string.male_not);
                    return;
                }

                if (!isGetState) {
                    ToastUtil.showToast("获取数据中");
                    getVerifyStatus();
                    return;
                }

                //申请主播
                if (actorVerifyState == -1 || actorVerifyState == 2) {
                    startActivity(new Intent(getContext(), ApplyVerifyHandActivity.class));
                }

                //审核中
                else if (actorVerifyState == 0) {
                    startActivity(new Intent(getContext(), ActorVerifyingActivity.class));
                }

                //收费设置
                else if (actorVerifyState == 1) {
                    startActivity(new Intent(getContext(), SetChargeActivity.class));
                }

                break;

            //充值
            case R.id.charge_btn:
                startActivity(new Intent(getContext(), ChargeActivity.class));
                break;

            //提现
            case R.id.with_draw_btn:
                if (checkInvalidBean())
                    return;
                if (userCenterBean.phoneIdentity == 0) {
                    new AlertDialog.Builder(mContext)
                            .setMessage("为了你的账户安全，请绑定手机号")
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PhoneVerifyActivity.start(getActivity(), userCenterBean.t_phone);
                                    dialog.dismiss();
                                }
                            }).create().show();
                    return;
                }
                startActivity(new Intent(getContext(), WithDrawActivity.class));
                break;
        }
    }

    /**
     * 设置功能按钮
     */
    private void setFunction() {

        final List<MineMenu> list = Arrays.asList(

                new MineMenu(R.drawable.mine_function_invite, "邀请有奖", InviteActivity.class),
                new MineMenu(R.drawable.mine_funciton_vip, "开通会员", VipCenterActivity.class),
                menuApply = new MineMenu(R.drawable.mine_funciton_apply, "申请主播", VerifyOptionActivity.class),

                new MineMenu(R.drawable.mine_funciton_verify, "我的公会", MyActorActivity.class),
                new MineMenu(R.drawable.mine_funciton_beauty, "美颜设置", SetBeautyActivity.class),
                new MineMenu(R.drawable.mine_funciton_help, "常见问题", HelpCenterActivity.class),

                new MineMenu(R.drawable.mine_funciton_call, "来电提醒", PhoneNaviActivity.class),
                new MineMenu(R.drawable.mine_funciton_sys, "系统设置", SettingActivity.class),
                new MineMenu(R.drawable.mine_funciton_bind, "绑定手机", PhoneVerifyActivity.class),
                new MineMenu(R.drawable.mine_funciton_active, "视频秀", UserAlbumListActivity.class));

        RecyclerView recyclerView = findViewById(R.id.mine_rv);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        final AbsRecycleAdapter adapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_mine_function, MineMenu.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {
                MineMenu bean = (MineMenu) t;
                TextView textView = (TextView) holder.itemView;
                textView.setCompoundDrawablesWithIntrinsicBounds(0, bean.drawId, 0, 0);
                textView.setText(bean.menu);
                if (bean.clazz == PhoneNaviActivity.class) {
                    textView.setTextColor(0xffff0000);
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    textView.setTextColor(0xff868686);
                    textView.setTypeface(Typeface.DEFAULT);
                }
            }
        };
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object obj, int position) {
                MineMenu bean = (MineMenu) adapter.getData().get(position);
                Class clazz = bean.clazz;
                if (clazz == Object.class) {
                    CodeUtil.jumpToQQ(mContext);
                } else if (clazz == VerifyOptionActivity.class) {

                    //未开放男性申请主播
                    if (AppManager.getInstance().getUserInfo().isSexMan()) {
                        ToastUtil.showToast(getContext(), R.string.male_not);
                        return;
                    }

                    if (!isGetState) {
                        ToastUtil.showToast("获取数据中");
                        getVerifyStatus();
                        return;
                    }

                    //申请主播状态
                    if (actorVerifyState == 1) {
                        startActivity(new Intent(getContext(), SetChargeActivity.class));
                    } else {
                        startActivity(new Intent(getContext(), VerifyOptionActivity.class));
                    }

                } else if (clazz == MyActorActivity.class) {
                    if (checkInvalidBean())
                        return;
                    //申请工会状态 0.未申请 1.审核中 2.已通过
                    if (userCenterBean.isGuild == 0) {
                        showCompanyDialog();
                    } else if (userCenterBean.isGuild == 1) {
                        ToastUtil.showToast(mContext, R.string.apply_company_ing_des);
                    } else {
                        //我的公会
                        if (userCenterBean.isGuild == 3) {
                            //已下架
                            ToastUtil.showToast(getContext(), R.string.company_down);
                        } else {
                            Intent intent = new Intent(getContext(), MyActorActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (clazz == PhoneVerifyActivity.class) {
                    if (checkInvalidBean())
                        return;
                    PhoneVerifyActivity.start(getActivity(), userCenterBean.t_phone);
                } else if (clazz == UserAlbumListActivity.class) {
                    UserAlbumListActivity.start(mContext, "视频", 1);
                } else {
                    startActivity(new Intent(mContext, clazz));
                }
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setDatas(list);
    }

    /**
     * 显示公会
     */
    private void showCompanyDialog() {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_connect_qq_layout, null);
        setDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        //取消
        ImageView cancel_iv = view.findViewById(R.id.cancel_iv);
        cancel_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ApplyCompanyActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 获取实名认证状态
     * 男性不调用
     */
    private boolean isGetState;

    private void getVerifyStatus() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.GET_VERIFY_STATUS())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<VerifyBean>>() {
            @Override
            public void onResponse(BaseResponse<VerifyBean> response, int id) {

                if (getActivity() == null || getActivity().isFinishing() || getView() == null)
                    return;

                // （bean == null 未申请) ->state=-1  0.审核中  1.审核成功 2.审核失败
                if (response != null) {

                    int state = -1;
                    if (response.m_object != null) {
                        state = response.m_object.t_certification_type;
                    }

                    if (state != actorVerifyState) {

                        actorVerifyState = state;

                        int[] stringIds = {R.string.apply_actor, R.string.actor_ing, R.string.set_money, R.string.apply_actor};
                        String text = getString(stringIds[++state]);
                        ((TextView) getView().findViewById(R.id.verify_btn)).setText(text);
                        menuApply.menu = text;
                        RecyclerView recyclerView = findViewById(R.id.mine_rv);
                        if (recyclerView.getAdapter() != null) {
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    }

                }

                isGetState = true;
            }
        });
    }

    /**
     * 更新个人数据
     */
    private void refreshUser() {

        if (getView() == null)
            return;

        //昵称
        ((TextView) getView().findViewById(R.id.nick_name_tv))
                .setText(AppManager.getInstance().getUserInfo().t_nickName);

        //Vip/SVip
        boolean isVip = AppManager.getInstance().getUserInfo().isVip();
        getView().findViewById(R.id.v_iv).setVisibility(isVip ? View.VISIBLE : View.GONE);

        TextView ageTv = getView().findViewById(R.id.age_tv);

        //性别
        ageTv.setSelected(AppManager.getInstance().getUserInfo().isSexMan());

        //头像
        Glide.with(mContext)
                .load(AppManager.getInstance().getUserInfo().headUrl)
                .error(R.drawable.default_head)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) getView().findViewById(R.id.head_iv));

        if (userCenterBean != null) {

            //保存角色状态  1 = 主播
            if (AppManager.getInstance().getUserInfo().t_role != userCenterBean.t_role) {
                AppManager.getInstance().getUserInfo().t_role = userCenterBean.t_role;
                SharedPreferenceHelper.saveRoleInfo(mContext, userCenterBean.t_role);
            }

            //年龄
            ageTv.setText(String.valueOf(userCenterBean.t_age));

            //个性签名
            String sign = TextUtils.isEmpty(userCenterBean.t_autograph) ?
                    getString(R.string.lazy) : userCenterBean.t_autograph;
            ((TextView) getView().findViewById(R.id.sign_tv)).setText(sign);

            //ID
            ((TextView) getView().findViewById(R.id.id_tv))
                    .setText(String.format("盼未来号: %s", userCenterBean.t_idcard));

            //我的粉丝
            ((TextView) getView().findViewById(R.id.fan_tv)).setText(String.valueOf(userCenterBean.likeMeCount));

            //相互喜欢
            ((TextView) getView().findViewById(R.id.each_like_tv)).setText(String.valueOf(userCenterBean.eachLikeCount));

            //我的关注
            ((TextView) getView().findViewById(R.id.follow_tv)).setText(String.valueOf(userCenterBean.myLikeCount));

            //访客数量
            ((TextView) getView().findViewById(R.id.victor_tv)).setText(String.valueOf(userCenterBean.browerCount));

            //我的动态
            ((TextView) getView().findViewById(R.id.my_dynamic_tv)).setText(String.valueOf(userCenterBean.dynamCount));

            //余额
            setTicker((TickerView) getView().findViewById(R.id.gold_tv), userCenterBean.amount);

            //提现
            setTicker((TickerView) getView().findViewById(R.id.gold_get_tv), userCenterBean.extractGold);

            //检查TIM昵称
            IMHelper.checkTIMInfo(userCenterBean.nickName, userCenterBean.handImg);

            //开关状态
            refreshSwitch();
        }
    }

    /**
     * 设置开关
     */
    private void refreshSwitch() {

        View switchVideo = findViewById(R.id.video_chat_iv);
        View switchText = findViewById(R.id.im_chat_iv);
        View switchFloat = findViewById(R.id.disable_slide_iv);
        View switchRank = findViewById(R.id.disable_rank_iv);

        TextView videoTv = findViewById(R.id.video_chat_tv);
        TextView textTv = findViewById(R.id.im_chat_tv);
        TextView floatTv = findViewById(R.id.disable_slide_tv);
        TextView rankTv = findViewById(R.id.disable_rank_tv);

        videoTv.setText(userCenterBean.t_is_not_disturb == 1 ? "已开启视频聊天" : "已关闭视频聊天");
        textTv.setText(userCenterBean.t_text_switch == 1 ? "已开启文字聊天" : "已关闭文字聊天");
        floatTv.setText(userCenterBean.t_float_switch == 1 ? "已开启屏蔽飘窗" : "已关闭屏蔽飘窗");
        rankTv.setText(userCenterBean.t_rank_switch == 1 ? "已开启屏蔽榜单" : "已关闭屏蔽榜单");

        switchVideo.setSelected(userCenterBean.t_is_not_disturb == 1);
        switchText.setSelected(userCenterBean.t_text_switch == 1);
        switchFloat.setSelected(userCenterBean.t_float_switch == 1);
        switchRank.setSelected(userCenterBean.t_rank_switch == 1);

        if (switchVideo.getTag() != null) {
            return;
        }

        //chatType 1视频 2 语音 3 文字   4:排行榜隐身  5:飘屏隐身
        switchVideo.setTag(1);
        switchText.setTag(3);
        switchFloat.setTag(5);
        switchRank.setTag(4);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final int type = Integer.parseInt(view.getTag().toString());
                final boolean isSelected = !view.isSelected();
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("userId", mContext.getUserId());
                paramMap.put("chatType", type);
                paramMap.put("switchType", isSelected ? 1 : 0);
                OkHttpUtils.post().url(ChatApi.setUpChatSwitch())
                        .addParams("param", ParamUtil.getParam(paramMap))
                        .build().execute(
                        new AjaxCallback<BaseResponse<String>>() {

                            @Override
                            public void onResponse(BaseResponse response, int id) {
                                if (getActivity() == null || getActivity().isFinishing())
                                    return;
                                if (response != null) {
                                    if (response.m_istatus == NetCode.SUCCESS) {
                                        view.setSelected(isSelected);
                                        if (view.getId() == R.id.video_chat_iv) {
                                            videoTv.setText(view.isSelected() ? "已开启视频聊天" : "已关闭视频聊天");
                                        } else if (view.getId() == R.id.im_chat_tv) {
                                            textTv.setText(view.isSelected() ? "已开启私信聊天" : "已关闭私信聊天");
                                        } else if (view.getId() == R.id.disable_slide_iv) {
                                            floatTv.setText(view.isSelected() ? "已开启屏蔽飘窗" : "已关闭屏蔽飘窗");
                                        } else if (view.getId() == R.id.disable_rank_iv) {
                                            rankTv.setText(view.isSelected() ? "已开启屏蔽榜单" : "已关闭屏蔽榜单");
                                        }
                                    } else {
                                        ToastUtil.showToast(mContext, response.m_strMessage);
                                    }
                                }
                            }

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                if (getActivity() == null || getActivity().isFinishing())
                                    return;
                                mContext.dismissLoadingDialog();
                            }
                        });
            }
        };
        switchVideo.setOnClickListener(onClickListener);
        switchText.setOnClickListener(onClickListener);
        switchFloat.setOnClickListener(onClickListener);
        switchRank.setOnClickListener(onClickListener);
    }

    /**
     * 滚动数字
     */
    private void setTicker(TickerView tickerView, int number) {
        int currentNumber = 0;
        try {
            if (!TextUtils.isEmpty(tickerView.getText()))
                currentNumber = Integer.parseInt(tickerView.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (number == currentNumber)
            return;
        if (number > currentNumber) {
            tickerView.setPreferredScrollingDirection(TickerView.ScrollingDirection.UP);
        } else {
            tickerView.setPreferredScrollingDirection(TickerView.ScrollingDirection.DOWN);
        }
        tickerView.setText("" + number);
    }

    /**
     * 收取未拆的红包
     */
    private void receiveRedPacket() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.GET_RED_PACKET_COUNT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<RedCountBean>>() {
            @Override
            public void onResponse(BaseResponse<RedCountBean> response, int id) {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                    if (response.m_object.total > 0) {
                        Map<String, String> paramMap = new HashMap<>();
                        paramMap.put("userId", mContext.getUserId());
                        OkHttpUtils.post().url(ChatApi.RECEIVE_RED_PACKET())
                                .addParams("param", ParamUtil.getParam(paramMap))
                                .build().execute(new AjaxCallback<BaseResponse<ReceiveRedBean>>() {
                            @Override
                            public void onResponse(BaseResponse<ReceiveRedBean> response, int id) {
                                if (getActivity() == null || getActivity().isFinishing())
                                    return;
                                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                                    getInfo();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 检查bean是否有效
     */
    private boolean checkInvalidBean() {
        boolean b = userCenterBean == null;
        if (b) {
            ToastUtil.showToast("获取数据中");
            getInfo();
        }
        return b;
    }

    /**
     * 获取个人中心信息
     */
    private void getInfo() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.INDEX())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UserCenterBean>>() {
            @Override
            public void onResponse(BaseResponse<UserCenterBean> response, int id) {
                if (getActivity() == null || getActivity().isFinishing() || getView() == null)
                    return;
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    UserCenterBean bean = response.m_object;
                    userCenterBean = bean;
                    if (bean != null) {

                        //保存vip状态
                        SharedPreferenceHelper.saveUserVip(mContext, bean.t_is_vip);

                        //保存头像
                        String imgUrl = SharedPreferenceHelper.getAccountInfo(mContext).headUrl;
                        if (TextUtils.isEmpty(imgUrl) || !imgUrl.equals(bean.handImg)) {
                            SharedPreferenceHelper.saveHeadImgUrl(mContext, bean.handImg);
                            if (!TextUtils.isEmpty(bean.handImg)) {
                                AppManager.getInstance().getUserInfo().headUrl = bean.handImg;
                            }
                        }

                        //保存昵称
                        String nickName = bean.nickName;
                        String saveNick = SharedPreferenceHelper.getAccountInfo(mContext).t_nickName;
                        if (TextUtils.isEmpty(saveNick) || !saveNick.equals(nickName)) {
                            SharedPreferenceHelper.saveUserNickName(mContext, nickName);
                            AppManager.getInstance().getUserInfo().t_nickName = nickName;
                        }

                        //拉取是否邀请主播加入公会 	是否加入公会 0.未加入 1.已加入
                        getView().findViewById(R.id.company_iv).setVisibility(bean.isApplyGuild == 0 ? View.GONE : View.VISIBLE);
                        if (bean.isApplyGuild == 0) {
                            CompanyInviteDialog.getCompanyInvite(getActivity());
                        }

                        //更新UI
                        refreshUser();
                    }
                }
            }
        });
    }

    private static class MineMenu {

        int drawId;
        String menu;
        Class clazz;

        public MineMenu(int drawId, String menu, Class clazz) {
            this.drawId = drawId;
            this.menu = menu;
            this.clazz = clazz;
        }
    }
}
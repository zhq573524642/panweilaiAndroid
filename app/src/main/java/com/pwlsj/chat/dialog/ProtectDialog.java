package com.pwlsj.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.pwlsj.chat.R;
import com.pwlsj.chat.base.AppManager;
import com.pwlsj.chat.base.BaseResponse;
import com.pwlsj.chat.bean.GiftBean;
import com.pwlsj.chat.constant.ChatApi;
import com.pwlsj.chat.net.AjaxCallback;
import com.pwlsj.chat.net.NetCode;
import com.pwlsj.chat.util.DensityUtil;
import com.pwlsj.chat.util.ParamUtil;
import com.pwlsj.chat.util.ToastUtil;
import com.pwlsj.chat.view.recycle.AbsRecycleAdapter;
import com.pwlsj.chat.view.recycle.OnItemClickListener;
import com.pwlsj.chat.view.recycle.RecycleGridDivider;
import com.pwlsj.chat.view.recycle.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 赠送守护
 */
public class ProtectDialog extends Dialog implements View.OnClickListener {

    private Protect bean;
    private int actorId;
    private boolean isOk;
    private Activity activity;
    private ProgressDialog progressDialog;
    private int selected = 0;

    public ProtectDialog(@NonNull Activity context, int actorId) {
        super(context);
        this.actorId = actorId;
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_protect);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("请稍候...");

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setAttributes(lp);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        findViewById(R.id.dialog_left_button).setOnClickListener(this);
        findViewById(R.id.dismiss_btn).setOnClickListener(this);

        setOffTv(bean.giftCount);

        TextView contentTv = findViewById(R.id.dialog_content_message);

        AbsRecycleAdapter adapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_handsel_protect, GiftBean.GiftAmountBean.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {
                GiftBean.GiftAmountBean amountBean = (GiftBean.GiftAmountBean) t;
                boolean isSelected = holder.getRealPosition() == selected;
                if (isSelected) {
                    contentTv.setText(String.format("花费%s个金币守护TA", amountBean.t_two_gift_number * bean.t_gift_gold));
                }
                TextView textView = (TextView) holder.itemView;
                textView.setBackgroundResource(isSelected ?
                        R.drawable.corner5_solid_pink_fc6ef1 : R.drawable.corner5_solid_gray_f2f3f7);
                textView.setTextColor(isSelected ? 0xffffffff : 0xff868686);
                textView.setText(String.format("%s个", amountBean.t_two_gift_number));
            }
        };

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object obj, int position) {
                selected = position;
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setDatas(bean.twoGiftList);

        RecyclerView recyclerView = findViewById(R.id.content_rv);
        recyclerView.addItemDecoration(new RecycleGridDivider(DensityUtil.dip2px(getContext(), 20)));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
    }

    private void setOffTv(int off) {
        TextView offTv = findViewById(R.id.off_tv);
        if (off == 0) {
            offTv.setText(null);
            return;
        }
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        stringBuilder.append("*",
                new ForegroundColorSpan(0xffFF1515),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append(String.format("我距离上一名排名还差%s个守护", off),
                new ForegroundColorSpan(0xff868686),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        offTv.setText(stringBuilder);
    }

    /**
     * 打赏礼物
     */
    private void rewardGift(final View view, final GiftBean giftBean) {
        GiftBean.GiftAmountBean amountBean = (GiftBean.GiftAmountBean) bean.twoGiftList.get(selected);
        progressDialog.show();
        view.setClickable(false);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id + "");
        paramMap.put("coverConsumeUserId", actorId);
        paramMap.put("giftId", giftBean.t_gift_id);
        paramMap.put("giftNum", amountBean.t_two_gift_number);
        OkHttpUtils.post().url(ChatApi.USER_GIVE_GIFT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (activity != null && activity.isFinishing()) {
                    return;
                }
                progressDialog.dismiss();
                view.setClickable(true);
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        isOk = true;
                        ToastUtil.showToast(R.string.protect_success);
                        dismiss();
                        update();
                    } else if (response.m_istatus == -1) {
                        ToastUtil.showToast(R.string.gold_not_enough);
                    } else {
                        ToastUtil.showToast(R.string.pay_fail);
                    }
                } else {
                    ToastUtil.showToast(R.string.pay_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (activity != null && activity.isFinishing()) {
                    return;
                }
                progressDialog.dismiss();
                view.setClickable(true);
            }
        });
    }

    public boolean isOk() {
        return isOk;
    }

    public GiftBean getBean() {
        return bean;
    }

    protected void update() {

    }

    @Override
    public void show() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", AppManager.getInstance().getUserInfo().t_id);
        params.put("coverUserId", actorId);
        OkHttpUtils
                .post()
                .url(ChatApi.getGuard())
                .addParams("param", ParamUtil.getParam(params))
                .build()
                .execute(new AjaxCallback<BaseResponse<Protect>>() {
                    @Override
                    public void onResponse(BaseResponse<Protect> response, int id) {
                        if (response == null) {
                            return;
                        }
                        if (response.m_istatus != NetCode.SUCCESS) {
                            ToastUtil.showToast(response.m_strMessage);
                            return;
                        }
                        if (response.m_object.twoGiftList == null
                                || response.m_object.twoGiftList.size() == 0) {
                            return;
                        }
                        bean = response.m_object;
                        ProtectDialog.super.show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_left_button) {
            rewardGift(v, bean);
        } else {
            dismiss();
        }
    }

    static class Protect extends GiftBean {
        public int giftCount;
    }

}
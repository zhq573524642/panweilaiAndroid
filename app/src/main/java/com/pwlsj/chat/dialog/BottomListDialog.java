package com.pwlsj.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pwlsj.chat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 定制的通用底部弹出对话框，使应用内对话框风格统一
 * Created by jhonsjoin on 18/5/16.
 */
public class BottomListDialog extends Dialog implements View.OnClickListener {

    public static Context mContext;

    private static List<BottomListMenuItem> btnMenu;

    public static class BottomListMenuItem {
        private String content;
        private Dialog.OnClickListener clickListener;
        private int color = Color.parseColor("#4a4a4a");
        private int textsize = 0;

        /**
         * @param content       显示内容
         * @param clickListener 监听回调
         * @param color         字体颜色
         */
        public BottomListMenuItem(String content, Dialog.OnClickListener clickListener, int color) {
            this.content = content;
            this.clickListener = clickListener;
            this.color = color;
        }

        /**
         * @param content       显示内容
         * @param clickListener 监听回调
         * @param color         字体颜色
         * @param textsize      字体大小
         */
        public BottomListMenuItem(String content, Dialog.OnClickListener clickListener, int color, int textsize) {
            this.content = content;
            this.clickListener = clickListener;
            this.color = color;
            this.textsize = textsize;
        }

        /**
         * @param content  显示内容
         * @param color    字体颜色
         * @param textsize 字体大小
         */
        public BottomListMenuItem(String content, int color, int textsize) {
            this.content = content;
            this.color = color;
            this.textsize = textsize;
        }


        /**
         * @param content       显示内容
         * @param clickListener 监听回调
         */
        public BottomListMenuItem(String content, Dialog.OnClickListener clickListener) {
            this.content = content;
            this.clickListener = clickListener;
        }


        public BottomListMenuItem(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Dialog.OnClickListener getClickListener() {
            return clickListener;
        }

        public void setClickListener(Dialog.OnClickListener clickListener) {
            this.clickListener = clickListener;
        }
    }

    public BottomListDialog(@NonNull Context context) {
        super(context, R.style.common_dialog);
    }

    public BottomListDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        win.setWindowAnimations(R.style.listDialogWindowAnim);
        win.setGravity(Gravity.BOTTOM);
        win.setAttributes(lp);

        setContentView(R.layout.common_bottom_list_dialog);
        setCanceledOnTouchOutside(true);
        setCancelable(true);

        initView();
    }


    private void initView() {

        LinearLayout lyContents = findViewById(R.id.menu_content);
        if (btnMenu != null && btnMenu.size() > 0) {
            for (int i = 0; i < btnMenu.size(); i++) {
                final int index = i;
                View v = View.inflate(mContext, R.layout.common_bottom_list_dialog_item, null);
                if (i == 0) {
                    v.findViewById(R.id.menu_line).setVisibility(View.GONE);
                    v.setBackgroundResource(R.drawable.dialog_top_radius_bg);
                } else {
                    v.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                }

                TextView mTvContent = v.findViewById(R.id.menu_button);
                mTvContent.setText(btnMenu.get(i).getContent());
                mTvContent.setTextColor(btnMenu.get(i).color);
                mTvContent.setPadding(20, 0, 20, 0);
                mTvContent.setGravity(Gravity.CENTER);
                if (btnMenu.get(i).textsize != 0) {
                    mTvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, btnMenu.get(i).textsize);
                }
                final Dialog.OnClickListener mOnClickPositionListener = btnMenu.get(i).getClickListener();
                mTvContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mOnClickPositionListener) {
                            mOnClickPositionListener.onClick(BottomListDialog.this, index);
                        }
                        dismiss();
                    }
                });
                lyContents.addView(v);

            }
        }

        findViewById(R.id.menu_base_content).setOnClickListener(this);
        findViewById(R.id.menu_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.menu_cancel || i == R.id.menu_base_content) {
            dismiss();
        }

    }

    public static class Builder {

        private int theme;

        public Builder(Context context) {
            mContext = context;
            btnMenu = new ArrayList<>();
        }

        /**
         * @param theme
         */
        public Builder addThem(int theme) {
            this.theme = theme;
            return this;
        }

        /**
         * @param item 对象
         */
        public Builder addMenuItem(BottomListMenuItem item) {
            btnMenu.add(item);
            return this;
        }

        /**
         * @param mReportList   数组
         * @param clickListener item回调
         */
        public Builder addMenuListItem(String[] mReportList, Dialog.OnClickListener clickListener) {
            BottomListMenuItem item = null;
            for (int i = 0; i < mReportList.length; i++) {
                item = new BottomListMenuItem(mReportList[i], clickListener);
                btnMenu.add(item);
            }
            return this;
        }


        public BottomListDialog show() {
            if (null != mContext && (!(mContext instanceof Activity) || !((Activity) mContext).isFinishing())) {
                BottomListDialog dialog = theme != 0 ? new BottomListDialog(mContext, theme) : new BottomListDialog(mContext);
                dialog.show();
                return dialog;
            }
            return null;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        btnMenu = null;
        mContext = null;
    }
}
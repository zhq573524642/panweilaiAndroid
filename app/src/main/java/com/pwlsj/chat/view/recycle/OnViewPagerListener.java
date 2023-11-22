package com.pwlsj.chat.view.recycle;

import android.view.View;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 * 用于ViewPagerLayoutManager的监听
 */

public interface OnViewPagerListener {

    /*初始化完成*/
    void onInitComplete();

    /*释放的监听*/
   void onPageRelease(View view);

    /*选中的监听以及判断是否滑动到底部*/
    void onPageSelected(int position, boolean isBottom);


}

package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiWatermarkAdapter;
import cn.tillusory.tiui.custom.TiConfigCallBack;
import cn.tillusory.tiui.custom.TiConfigTools;
import cn.tillusory.tiui.model.TiWatermarkConfig;
import com.shizhefei.fragment.LazyFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiWatermarkFragment extends LazyFragment {

    private final List<TiWatermarkConfig.TiWatermark> items = new ArrayList<>();



    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        setContentView(R.layout.fragment_ti_sticker);

        if (getContext() == null) return;

        items.clear();
        items.add(TiWatermarkConfig.TiWatermark.NO_WATERMARK);

        TiWatermarkConfig watermarkList = TiConfigTools.getInstance().getWatermarkList();

        if (watermarkList != null && watermarkList.getWatermarks() != null && watermarkList.getWatermarks().size() != 0) {
            items.addAll(watermarkList.getWatermarks());
            initRecyclerView();
        } else {
            TiConfigTools.getInstance().getWaterMarkConfig(new TiConfigCallBack<List<TiWatermarkConfig.TiWatermark>>() {
                @Override public void success(List<TiWatermarkConfig.TiWatermark> list) {
                    items.addAll(list);
                    initRecyclerView();
                }

                @Override public void fail(Exception error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void initRecyclerView() {
        RecyclerView tiWatermarkRV = (RecyclerView) findViewById(R.id.tiRecyclerView);
        TiWatermarkAdapter watermarkAdapter = new TiWatermarkAdapter(items);
        tiWatermarkRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        tiWatermarkRV.setAdapter(watermarkAdapter);
    }

}

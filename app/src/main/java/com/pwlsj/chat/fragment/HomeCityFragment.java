package com.pwlsj.chat.fragment;

import android.view.View;
import android.widget.TextView;

import com.pwlsj.chat.dialog.CityPickerDialog;
import com.pwlsj.chat.helper.SharedPreferenceHelper;
import com.pwlsj.chat.view.tab.TabPagerViewHolder;

/**
 * 同城fragment
 */
public class HomeCityFragment extends HomeContentFragment {

    public static String city = null;

    @Override
    public void bindTab(TabPagerViewHolder viewHolder) {
        super.bindTab(viewHolder);
        viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    showCityChooser();
                }
            }
        });
    }

    @Override
    protected void beforeGetData() {
        if (city == null) {
            String localCity = SharedPreferenceHelper.getCity(getActivity());
            requester.setParam("t_city", localCity);
        } else {
            requester.setParam("t_city", city);
            city = null;
        }
    }

    private CityPickerDialog cityPickerDialog;

    private void showCityChooser() {
        if (cityPickerDialog == null) {
            cityPickerDialog = new CityPickerDialog(getActivity()) {
                @Override
                public void onSelected(String city, String city2) {
                    setCity(city2);
                }
            };
        }
        cityPickerDialog.show();
    }

    private void setCity(String city) {
        requester.setParam("t_city", city);
        requester.onRefresh();
        TextView textView = (TextView) tabPagerViewHolder.itemView;
        textView.setText(city);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (city != null) {
            setCity(city);
            city = null;
        }
    }
}
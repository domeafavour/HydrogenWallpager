package cn.zhj.hydrogenwallpager.model;

import android.databinding.BindingAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by not_n on 2016/6/26.
 */
public class LayoutParamsModel {

    public float topHeight;
    public float bottomHeight;

    public LayoutParamsModel(float topHeight, float bottomHeight) {
        this.topHeight = topHeight;
        this.bottomHeight = bottomHeight;
    }

    @BindingAdapter("dynamic_height")
    public static void setLayoutHeight(View view, float height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int) (height + 0.5);
        view.setLayoutParams(params);
    }
}

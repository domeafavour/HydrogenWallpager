package cn.zhj.hydrogenwallpager.model;

import android.databinding.BindingAdapter;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by not_n on 2016/6/26.
 */
public class LayoutParamsModel {

    public float topHeight;
    public float bottomHeight;
    public float bottomMargin;

    public LayoutParamsModel(float topHeight, float bottomHeight, float bottomMargin) {
        this.topHeight = topHeight;
        this.bottomHeight = bottomHeight;
        this.bottomMargin = bottomMargin;
    }

    @BindingAdapter("dynamic_height")
    public static void setLayoutHeight(View view, float height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int) (height + 0.5);
        view.setLayoutParams(params);
    }

    @BindingAdapter("dynamic_margin_bottom")
    public static void setMarginBottom(View view, float marginBottom){
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        layoutParams.bottomMargin += marginBottom;
        view.setLayoutParams(layoutParams);
    }
}

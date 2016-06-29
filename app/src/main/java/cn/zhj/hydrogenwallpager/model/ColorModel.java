package cn.zhj.hydrogenwallpager.model;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by not_n on 2016/6/27.
 */
public class ColorModel {

    public final ObservableField<Bitmap> color = new ObservableField<>();

    @BindingAdapter("dynamic_color")
    public static void setColor(ImageView view, Bitmap color) {
        view.setImageBitmap(color);
    }
}

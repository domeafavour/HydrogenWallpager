package cn.zhj.hydrogenwallpaper.model;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class ColorModel {

    public final ObservableField<Bitmap> color = new ObservableField<>();

    @BindingAdapter("dynamic_color")
    public static void setColor(ImageView view, Bitmap color) {
        view.setImageBitmap(color);
    }
}

package cn.zhj.hydrogenwallpaper.model;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageModel {

    public ObservableField<Bitmap> top = new ObservableField<>();
    public ObservableField<Bitmap> bottom = new ObservableField<>();

    @BindingAdapter("dynamic_src")
    public static void setImgSrc(ImageView view, Bitmap bitmap){
        view.setImageBitmap(bitmap);
    }

}

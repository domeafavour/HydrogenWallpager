package cn.zhj.hydrogenwallpager.view;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import java.util.List;

/**
 * Created by not_n on 2016/6/19.
 */
public interface MainView {

    void onGetTop(Bitmap top);

    void onGetBottom(Bitmap bottom);

    void onGetSwatches(List<Palette.Swatch> swatches);

    int getWidth();

    int getTopHeight();

    int getBottomHeight();

    void onSaveResult(boolean isSuccess);

    void onCropImage(boolean hasCrop);
}

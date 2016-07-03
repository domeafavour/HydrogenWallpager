package cn.zhj.hydrogenwallpaper.view;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import java.util.List;

public interface MainView {

    void onGetTop(Bitmap top);

    void onGetBottom(Bitmap bottom, int color);

    void onGetSwatches(List<Palette.Swatch> swatches);

    int getWidth();

    int getTopHeight();

    int getBottomHeight();

    void onSaveResult(boolean isSuccess);

    void onCropImage(boolean hasCrop);
}

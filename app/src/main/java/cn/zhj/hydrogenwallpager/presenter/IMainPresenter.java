package cn.zhj.hydrogenwallpager.presenter;

import android.content.Intent;
import android.graphics.Bitmap;

/**
 * Created by not_n on 2016/6/19.
 */
public interface IMainPresenter {

    String IMAGE_TYPE = "image/*";

    int IMAGE_REQUEST_CODE = 0;

    void selectImage();

    void selectColor(int color);

    Bitmap getFinalBitmap();

    void handleActivityResult(int requestCode, int resultCode, Intent data);

    void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void generateFromBitmap(Bitmap bitmap);

    void setWallPaper(Bitmap bitmap);

    void saveAsFile(Bitmap bitmap, String path);

    String getImagePath();


}

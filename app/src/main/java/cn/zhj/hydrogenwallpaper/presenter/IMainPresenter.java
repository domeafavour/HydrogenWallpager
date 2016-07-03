package cn.zhj.hydrogenwallpaper.presenter;

import android.content.Intent;
import android.graphics.Bitmap;

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

    void onActivityDestroy();


}

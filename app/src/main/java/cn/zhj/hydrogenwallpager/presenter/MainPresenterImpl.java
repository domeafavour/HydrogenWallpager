package cn.zhj.hydrogenwallpager.presenter;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.zhj.hydrogenwallpager.R;
import cn.zhj.hydrogenwallpager.utils.ImageUtil;
import cn.zhj.hydrogenwallpager.view.MainView;

/**
 * Created by not_n on 2016/6/19.
 */
public class MainPresenterImpl implements IMainPresenter, Palette.PaletteAsyncListener {

    Uri cropUri;
    String temp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.jpg";
    private Bitmap mTopPart;
    private Bitmap mBottomPart;
    private MainView mMainView;
    private final Handler mSaveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 3) {
                mMainView.onSaveResult(true);
            }
            switch (msg.what) {
                case 3:
                    mMainView.onSaveResult(true);
                    break;
                case 4:
                    mMainView.onSaveResult(false);
                    break;
            }
        }
    };
    private Activity mActivity;
    private String imagePath;
    private String path;
    private File imageFile;

    public MainPresenterImpl(MainView mMainView) {
        this.mMainView = mMainView;
        mActivity = (Activity) mMainView;
    }

    @Override
    public void selectImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(mActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 22);
            } else {
                select();
            }
        } else {
            select();
        }
    }

    @Override
    public void selectColor(int color) {
        mBottomPart = ImageUtil.getColorImage(mMainView.getWidth(), mMainView.getBottomHeight(), color);
        mMainView.onGetBottom(mBottomPart);
    }

    @Override
    public Bitmap getFinalBitmap() {
        return ImageUtil.combine2Images(mTopPart, mBottomPart, true);
    }

    private void select() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(IMAGE_TYPE);
        mActivity.startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                try {
                    cropImage(data.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 33:
                if (cropUri != null) {
                    try {
                        mTopPart = BitmapFactory.decodeStream(mActivity.getContentResolver().openInputStream(cropUri));
                        mMainView.onCropImage(true);
                        mMainView.onGetTop(mTopPart);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                mMainView.onCropImage(false);
                break;
        }
    }

    private void cropImage(Uri uri) {
        cropUri = Uri.fromFile(new File(temp));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1080);
        intent.putExtra("aspectY", 787);    // 1920 * 0.41
        intent.putExtra("outputX", mMainView.getWidth());
        intent.putExtra("outputY", mMainView.getTopHeight());
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        mActivity.startActivityForResult(intent, 33);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 22:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                } else {
                    Toast.makeText(mActivity, R.string.no_permission_of_local_img, Toast.LENGTH_SHORT).show();
                }
                break;
            case 23:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    save(getFinalBitmap(), path);
                } else {
                    Toast.makeText(mActivity, R.string.no_permission_of_write_img, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void generateFromBitmap(Bitmap bitmap) {
        Palette.Builder builder = Palette.from(bitmap);
        builder.generate(this);
    }

    @Override
    public void setWallPaper(Bitmap bitmap) {
        WallpaperManager manager = WallpaperManager.getInstance(mActivity);
        try {
            manager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveAsFile(final Bitmap bitmap, final String path) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 23);
            } else {
                save(bitmap, path);
            }
        } else {
            save(bitmap, path);
        }
    }

    private void save(Bitmap bitmap, String path) {
        this.path = path;
        final String imgName = System.currentTimeMillis() + ".jpg";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        if (file.exists()) {
            imagePath = path + File.separator + imgName;
            try {
                imageFile = new File(imagePath);
                FileOutputStream outStream = new FileOutputStream(imageFile);
                boolean isImgSaved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                if (isImgSaved) {
                    mSaveHandler.sendEmptyMessage(3);
                } else {
                    mSaveHandler.sendEmptyMessage(4);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                mSaveHandler.sendEmptyMessage(4);
            }
        } else {
            throw new RuntimeException("Target path: " + path + " does not exist.");
        }
    }

    @Override
    public String getImagePath() {
        return this.imagePath;
    }

    @Override
    public void onGenerated(Palette palette) {
        mMainView.onGetSwatches(palette.getSwatches());
    }
}

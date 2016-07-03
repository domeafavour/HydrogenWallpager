package cn.zhj.hydrogenwallpager.presenter;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.zhj.hydrogenwallpager.R;
import cn.zhj.hydrogenwallpager.utils.ImageUtil;
import cn.zhj.hydrogenwallpager.view.MainView;

/**
 * Created by not_n on 2016/6/19.
 */
public class MainPresenterImpl implements IMainPresenter, Palette.PaletteAsyncListener, Runnable {

    private final MainHandler mSaveHandler = new MainHandler(this);
    private final String temp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.jpg";
    private Uri cropUri;
    private Bitmap mTopPart;
    private Bitmap mBottomPart;
    private Bitmap mFinal;
    private MainView mMainView;
    private Activity mActivity;
    private String imagePath;
    private String path;

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
        mMainView.onGetBottom(mBottomPart, color);
    }

    @Override
    public Bitmap getFinalBitmap() {
        if (mFinal == null) {
            mFinal = ImageUtil.combine2Images(mTopPart, mBottomPart, true);
        }
        return mFinal;
    }

    private void select() {
        Intent intent = new Intent(Intent.ACTION_PICK);
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
                    log(data.getData());
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

    private void log(Object msg) {
        Log.i("MainPresenterImpl", String.valueOf(msg));
    }

    private void cropImage(Uri uri) {
        int width = mMainView.getWidth();
        int topHeight = mMainView.getTopHeight();
        cropUri = Uri.fromFile(new File(temp));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", topHeight);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", topHeight);
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
                    save(path);
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
                save(path);
            }
        } else {
            save(path);
        }
    }

    private void save(final String path) {
        this.path = path;
        final String imgName = System.currentTimeMillis() + ".jpg";
        imagePath = path + File.separator + imgName;
        mSaveHandler.post(this);
    }

    @Override
    public String getImagePath() {
        return this.imagePath;
    }

    @Override
    public void onActivityDestroy() {
        if (mTopPart != null) {
            mTopPart.recycle();
            mTopPart = null;
        }
        if (mBottomPart != null) {
            mBottomPart.recycle();
            mBottomPart = null;
        }
        if (mFinal != null) {
            mFinal.recycle();
            mFinal = null;
        }
    }

    @Override
    public void onGenerated(Palette palette) {
        mMainView.onGetSwatches(palette.getSwatches());
    }

    @Override
    public void run() {
        mSaveHandler.sendEmptyMessage(ImageUtil.save(getFinalBitmap(), path) ? 3 : 4);
    }

    private static final class MainHandler extends Handler {
        private WeakReference<MainPresenterImpl> ref;

        public MainHandler(MainPresenterImpl impl) {
            ref = new WeakReference<>(impl);
        }

        @Override
        public void handleMessage(Message msg) {
            MainPresenterImpl impl = ref.get();
            if (impl == null) {
                return;
            }
            switch (msg.what) {
                case 3:
                    impl.mMainView.onSaveResult(true);
                    break;
                case 4:
                    impl.mMainView.onSaveResult(false);
                    break;
            }

        }
    }

}

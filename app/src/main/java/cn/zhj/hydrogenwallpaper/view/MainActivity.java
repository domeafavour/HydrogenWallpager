package cn.zhj.hydrogenwallpaper.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.zhj.hydrogenwallpaper.R;
import cn.zhj.hydrogenwallpaper.adapter.ColorAdapter;
import cn.zhj.hydrogenwallpaper.databinding.ActivityMainBinding;
import cn.zhj.hydrogenwallpaper.model.ColorModel;
import cn.zhj.hydrogenwallpaper.model.Constants;
import cn.zhj.hydrogenwallpaper.model.ImageModel;
import cn.zhj.hydrogenwallpaper.model.LayoutParamsModel;
import cn.zhj.hydrogenwallpaper.model.LoadModel;
import cn.zhj.hydrogenwallpaper.presenter.IMainPresenter;
import cn.zhj.hydrogenwallpaper.presenter.MainPresenterImpl;
import cn.zhj.hydrogenwallpaper.utils.ImageUtil;
import cn.zhj.hydrogenwallpaper.utils.SystemUtil;

public class MainActivity extends AppCompatActivity implements MainView, ColorAdapter.OnItemClickListener {

    private int mTopHeight;
    private int mBottomHeight;
    private int mWidth;
    private IMainPresenter mMainPresenter;
    private ActivityMainBinding mBinding;
    private ImageModel mImageModel;
    private List<Palette.Swatch> mSwatches = new ArrayList<>();
    private LoadModel mLoadModel;
    private boolean isShare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWidth = SystemUtil.getScreenWidth(this);
        int height = SystemUtil.getScreenHeight(this);
        mTopHeight = (int) (height * (Constants.TOP_PERCENT / 100f));
        mBottomHeight = height - mTopHeight;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setLayoutParams(new LayoutParamsModel(mTopHeight, mBottomHeight,
                SystemUtil.getBottomStatusHeight(this)));
        mBinding.setEventHandler(new ImageHandler());
        mImageModel = new ImageModel();
        mBinding.setImgModel(mImageModel);
        mLoadModel = new LoadModel(true);
        mBinding.setLoadModel(mLoadModel);
        SystemUtil.setStatusbarTransparent(this);
        mMainPresenter = new MainPresenterImpl(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.colorRecycler.setLayoutManager(layoutManager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mMainPresenter.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mMainPresenter.handleRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onGetTop(Bitmap top) {
        mImageModel.top.set(top);
        mMainPresenter.generateFromBitmap(top);
    }

    @Override
    public void onGetBottom(Bitmap bottom, int color) {
        mImageModel.bottom.set(bottom);
        getWindow().setNavigationBarColor(color);
    }

    @Override
    public void onGetSwatches(List<Palette.Swatch> swatches) {
        this.mSwatches = swatches;
        int size = SystemUtil.dip2px(this, Constants.COLOR_SIZE_DP);
        List<ColorModel> colorModels = new ArrayList<>();
        for (Palette.Swatch swatch : swatches) {
            ColorModel colorModel = new ColorModel();
            colorModel.color.set(ImageUtil.getColorImage(size, size, swatch.getRgb()));
            colorModels.add(colorModel);
        }
        ColorAdapter mColorAdapter = new ColorAdapter(colorModels, this);
        mColorAdapter.setOnItemClickListener(this);
        mBinding.colorRecycler.setAdapter(mColorAdapter);
        // 默认是第一个颜色
        mMainPresenter.selectColor(swatches.get(0).getRgb());
    }

    @Override
    public int getWidth() {
        return this.mWidth;
    }

    @Override
    public int getTopHeight() {
        return this.mTopHeight;
    }

    @Override
    public int getBottomHeight() {
        return this.mBottomHeight;
    }

    @Override
    public void onSaveResult(boolean isSuccess) {
        if (isShare) {
            doShare(new File(mMainPresenter.getImagePath()));
            isShare = false;
        } else {
            Toast.makeText(MainActivity.this, isSuccess ? R.string.save_successfully : R.string.save_failure, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCropImage(boolean hasCrop) {
        mLoadModel.setIsFirstLoad(!hasCrop);
        if (hasCrop) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    public void onItemClick(int position) {
        mMainPresenter.selectColor(mSwatches.get(position).getRgb());
    }

    private void doShare(File file) {
        Uri imageUri = Uri.fromFile(file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
        file.deleteOnExit();
    }

    @Override
    protected void onDestroy() {
        mMainPresenter.onActivityDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mBinding.expandedMenu.isExpanded()) {
            mBinding.expandedMenu.collapse();
            return;
        }
        super.onBackPressed();
    }

    public final class ImageHandler {

        public void onSelectImage(View view) {
            mMainPresenter.selectImage();
            mBinding.expandedMenu.collapse();
        }

        public void setAsWallpaper(View view) {
            mMainPresenter.setWallPaper(mMainPresenter.getFinalBitmap());
            mBinding.expandedMenu.collapse();
            Toast.makeText(MainActivity.this, R.string.set_successfully, Toast.LENGTH_SHORT).show();
        }

        public void share(View view) {
            isShare = true;
            save(null);
            mBinding.expandedMenu.collapse();
        }

        public void save(View view) {
            String folder = Environment.getExternalStorageDirectory()
                    + File.separator + Constants.FOLDER_NAME;
            mMainPresenter.saveAsFile(mMainPresenter.getFinalBitmap(), folder);
            mBinding.expandedMenu.collapse();
        }
    }
}

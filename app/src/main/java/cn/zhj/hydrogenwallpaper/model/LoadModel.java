package cn.zhj.hydrogenwallpaper.model;

import android.databinding.ObservableField;

public class LoadModel {
    public final ObservableField<Boolean> isFirstLoad = new ObservableField<>();

    public LoadModel(boolean isFirstLoad){
        this.isFirstLoad.set(isFirstLoad);
    }

    public void setIsFirstLoad(boolean isFirstLoad) {
        this.isFirstLoad.set(isFirstLoad);
    }

}

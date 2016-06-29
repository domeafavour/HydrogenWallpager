package cn.zhj.hydrogenwallpager.model;

import android.databinding.ObservableField;

/**
 * Created by not_n on 2016/6/28.
 */
public class LoadModel {
    public final ObservableField<Boolean> isFirstLoad = new ObservableField<>();

    public LoadModel(boolean isFirstLoad){
        this.isFirstLoad.set(isFirstLoad);
    }

    public void setIsFirstLoad(boolean isFirstLoad) {
        this.isFirstLoad.set(isFirstLoad);
    }

}

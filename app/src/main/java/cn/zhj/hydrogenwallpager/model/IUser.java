package cn.zhj.hydrogenwallpager.model;

/**
 * Created by not_n on 2016/6/19.
 */
public interface IUser {

    int EMPTY_USERNAME = -1;
    int EMPTY_PASSWORD = -2;

    int checkLogin();

    int checkRegister();

}

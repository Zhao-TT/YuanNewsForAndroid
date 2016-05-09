package cn.edu.hpu.yuan.yuannews.main;

import android.app.Application;

import javax.inject.Inject;

import cn.edu.hpu.yuan.yuancore.util.CrashHandler;

/**
 * Created by yuan on 16-5-9.
 * BaseApplication
 * 1.初始化CrashHandler
 * 2.
 */
public class BaseApplication extends Application{


    @Inject
    protected CrashHandler crashHandler;

    private ApplicationComponent applicationComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        //初始化crashHandler
        crashHandler.init(getApplicationContext());
        setComponent();
    }

    //初始化ApplicationComponent
    private void setComponent() {
        applicationComponent=DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
    }

    public ApplicationComponent getApplicationComponent(){
        return applicationComponent;
    }

}

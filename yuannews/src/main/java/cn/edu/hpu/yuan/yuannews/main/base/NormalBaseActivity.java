package cn.edu.hpu.yuan.yuannews.main.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import cn.edu.hpu.yuan.yuannews.R;
import cn.edu.hpu.yuan.yuannews.main.app.ApplicationComponent;
import cn.edu.hpu.yuan.yuannews.main.app.BaseApplication;

/**
 * Created by yuan on 16-5-12.
 *
 * 正常的，独立的Activity
 * 独立于BaseActivity ：
 * 登陆
 * 注册
 * 个人中心
 * 标签管理
 * 关于
 * 这几个界面
 */
public abstract class NormalBaseActivity extends AppCompatActivity {


    public ApplicationComponent getApplicationComponent() {
        return ((BaseApplication) getApplication()).getApplicationComponent();
    }

    Toolbar toolbar;
    
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        //初始化toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化fragment
        BaseFragment fragment = initFragment();
        fragment.setApplicationComponent(getApplicationComponent());
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.contentFrame, fragment).commit();

        //初始化toolbar
        initToolbar(toolbar);
    }

    protected abstract void initToolbar(Toolbar toolbar);

    protected abstract BaseFragment initFragment();
}

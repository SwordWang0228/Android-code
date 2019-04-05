package com.lee.library.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lee.library.ioc.InjectManager;
import com.lee.library.utils.StatusUtils;

/**
 * @author jv.lee
 */
public abstract class BaseActivity extends AppCompatActivity {

    private boolean fullscreen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //帮助所有子类进行：布局/控件/事件的注入
        InjectManager.inject(this);

        if (fullscreen) {
            //全屏模式
            StatusUtils.fullWindow(this);
        }else{
            //设置沉浸式
            if (toolbar() != null) {
                StatusUtils.statusBar(this,toolbar(),false);
            }
        }


        bindData(savedInstanceState);
    }

    /**
     * 加载数据
     * @param savedInstanceState 屏幕状态
     */
    public abstract void bindData(Bundle savedInstanceState);

    /**
     * 设置当前视图toolbar
     * @return toolbar
     */
    public abstract View toolbar();

    /**
     * 是否为全屏模式
     * @return boolean
     */
    public abstract boolean isFullscreen();

}

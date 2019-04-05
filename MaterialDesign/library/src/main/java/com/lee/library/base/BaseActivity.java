package com.lee.library.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lee.library.ioc.InjectManager;

/**
 * @author jv.lee
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //帮助所有子类进行：布局/控件/事件的注入
        InjectManager.inject(this);
        bindData(savedInstanceState);
    }

    /**
     * 加载数据
     * @param savedInstanceState 屏幕状态
     */
    public abstract void bindData(Bundle savedInstanceState);
}

package jv.lee.scroll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import jv.lee.scroll.R;
import jv.lee.scroll.activity.ScrollConflict1Activity;
import jv.lee.scroll.activity.ScrollConflict2Activity;

/**
 * @author jv.lee
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 解决 下拉刷新控件+横向滑动子view(Banner) 冲突事件
     *
     * @param view
     */
    public void startScroll1(View view) {
        startActivity(new Intent(this, ScrollConflict1Activity.class));
    }

    public void startScroll2(View view) {
        startActivity(new Intent(this, ScrollConflict2Activity.class));
    }
}

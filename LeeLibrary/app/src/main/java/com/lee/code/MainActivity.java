package com.lee.code;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.code.adapter.MultiAdapter;
import com.lee.code.bean.UserInfo;
import com.lee.library.adapter.LeeViewHolder;
import com.lee.library.adapter.ProxyAdapter;
import com.lee.library.base.BaseActivity;
import com.lee.library.ioc.annotation.AutoLoadMore;
import com.lee.library.ioc.annotation.ContentView;
import com.lee.library.ioc.annotation.InjectView;
import com.lee.library.ioc.annotation.OnItemChildClick;
import com.lee.library.ioc.annotation.OnItemClick;
import com.lee.library.ioc.annotation.OnItemLongClick;
import com.lee.library.ioc.annotation.OnRefresh;
import com.lee.library.livedatabus.InjectBus;
import com.lee.library.livedatabus.LiveDataBus;
import com.lee.library.tool.ThreadTool;
import com.lee.library.widget.refresh.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jv.lee
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @InjectView(R.id.container)
    FrameLayout container;
    @InjectView(R.id.rv_container)
    RecyclerView rvContainer;
    @InjectView(R.id.refresh)
    RefreshLayout refresh;

    MultiAdapter mAdapter;
    private List<UserInfo> data = new ArrayList<>();

    @Override
    public void bindData(Bundle savedInstanceState) {
        initData();
//        LiveDataBus.getInstance().injectBus(this);
        LiveDataBus.getInstance().getChannel("event").observe(this, new Observer<Object>() {
            @Override
            public void onChanged(@Nullable Object o) {
                Toast.makeText(MainActivity.this, o.toString(), Toast.LENGTH_SHORT).show();
            }
        });
//        IntentManager.getInstance().startAct(this,InjectActivity.class);
    }

    @Override
    protected void bindView() {
        mAdapter = new MultiAdapter(this, data);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        frameLayout.setBackgroundColor(Color.parseColor("#ff0000"));

        View view = new View(mActivity);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        view.setBackgroundColor(Color.parseColor("#000000"));

        FrameLayout root = new FrameLayout(mActivity);
        View inflate = getLayoutInflater().inflate(R.layout.item_footer, root,false);
        mAdapter.addHeader(inflate);
        mAdapter.addFooter(view);

        rvContainer.setLayoutManager(new LinearLayoutManager(this));
        rvContainer.setAdapter(mAdapter.getProxy());
        refresh.setDefaultView(container, rvContainer);
    }

    int i = 0;

    @InjectBus(value = "event")
    public void event(String data) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }

    private void initData() {
        for (int i = 0; i < 2; i++) {
            for (int j = 1; j <= 15; j++) {
                UserInfo user = new UserInfo();
                if (j % 15 == 1) {
                    user.setType(1);
                    user.setAccount("lee >>>>>> 11111 >>>>>");
                } else if (j % 15 == 2 || j % 15 == 3) {
                    user.setType(2);
                    user.setAccount(" lee >>>>> 22222 >>>>>");
                } else if (j % 15 == 4 || j % 15 == 5 || j % 15 == 6) {
                    user.setType(3);
                    user.setAccount("lee >>>>> 33333 >>>>>>");
                } else if (j % 15 == 7 || j % 15 == 8 || j % 13 == 9 || j % 15 == 0) {
                    user.setType(4);
                    user.setAccount("lee >>>>> 44444 >>>>>");
                } else {
                    user.setType(5);
                    user.setAccount("lee >>>>> 55555 >>>>>");
                }
                data.add(user);
            }
        }
    }

    @OnItemClick(value = "mAdapter")
    public void onItemClick(View view, UserInfo userInfo, int position) {
        Toast.makeText(this, "position:" + position + " data:" + userInfo.getAccount(), Toast.LENGTH_SHORT).show();
    }

    @OnItemLongClick(value = "mAdapter")
    public boolean onItemLongClick(View view, UserInfo userInfo, int position) {
        Toast.makeText(this, "long position" + position, Toast.LENGTH_SHORT).show();
        return true;
    }

    @OnItemChildClick(value = "mAdapter")
    public void onItemChildClick(LeeViewHolder viewHolder, UserInfo userInfo, int position) {
        TextView tvTitle = viewHolder.getConvertView().findViewById(R.id.tv_title);
        tvTitle.setOnClickListener(v -> {
            Toast.makeText(this, "child Title -> position:" + viewHolder.getLayoutPosition(), Toast.LENGTH_SHORT).show();
        });
    }

//    @OnRefresh(values = {R.id.refresh})
//    public void onRefresh() {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                data.clear();
//                initData();
//                runOnUiThread(() -> {
//                    mAdapter.notifyDataSetChanged();
//                    refresh.setRefreshCompleted();
//                });
//            }
//        }.start();
//    }

    @AutoLoadMore(value = "mAdapter")
    public void autoLoadMore() {
        Log.i(">>>", "auto load more");
        ThreadTool.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                i++;
                if (i == 3) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.loadMoreEnd();
                        }
                    });

                    return;
                }
                initData();
                runOnUiThread(() -> {
                    mAdapter.loadMoreCompleted();
                });
            }
        });
    }

//    @OnLoadingMore(values = {R.id.refresh})
//    public void onLoadingMore() {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                initData();
//                runOnUiThread(() -> {
//                    mAdapter.notifyDataSetChanged();
//                    refresh.setLoadingMoreCompleted();
//                });
//            }
//        }.start();
//    }

}

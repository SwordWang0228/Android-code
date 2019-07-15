package com.lee.component.order;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lee.component.annotation.ARouter;
import com.lee.component.annotation.Parameter;
import com.lee.component.api.core.ParameterLoad;
import com.lee.library.base.BaseActivity;
import com.lee.library.utils.Constants;

/**
 * @author jv.lee
 */
@ARouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends BaseActivity {

    @Parameter
    String username;
    @Parameter
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);
        Log.e(Constants.TAG, "common/Order_MainActivity");

        ParameterLoad parameterLoad = new Order_MainActivity$$Parameter();
        parameterLoad.loadParameter(this);

        Toast.makeText(this, "username:" + username + "  -  count:" + count, Toast.LENGTH_SHORT).show();
    }


    public void jumpApp(View view) {

    }

    public void jumpPersonal(View view) {

    }

}

package com.examples.designpatterns.imple;

import android.util.Log;

import com.examples.designpatterns.UserInfo;
import com.examples.designpatterns.api.Api;

/**
 * @author jv.lee
 * @date 2019/6/13.
 * description：
 */
public class ApiImpl_B implements Api {
    @Override
    public UserInfo create() {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("jv.lee");
        userInfo.setAge(25);
        Log.i("lee >>>", userInfo.toString());
        return userInfo;
    }
}

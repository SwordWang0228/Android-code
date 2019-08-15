package com.lee.library.model.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.lee.library.base.IModel
import com.lee.library.model.entity.User

/**
 * @author jv.lee
 * @date 2019-08-15
 * @description
 */
class MainRepository : IModel {
    fun getUsers(size: Int): LiveData<List<User>> {
        val data = MutableLiveData<List<User>>()
        val users = ArrayList<User>()

        for (index in 0..size) {
            users.add(User(index, "name:$index", "description current index is $index"))
        }
        data.value = users

        return data
    }

    fun getUserInfo(id: Int): LiveData<User> {
        val data = MutableLiveData<User>()
        data.value = User(id, "jv.lee - x", "china.jv.lee@gmail.com - x")
        return data
    }


}


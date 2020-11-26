package com.lee.adapter.viewmodel

import com.lee.adapter.entity.Content
import com.lee.adapter.entity.Page
import com.lee.adapter.repository.FlowRepository
import com.lee.library.extensions.bindLive
import com.lee.library.mvvm.base.BaseLiveData
import com.lee.library.mvvm.base.BaseViewModel
import com.lee.library.mvvm.load.LoadStatus
import com.lee.library.mvvm.load.PageNumber
import com.lee.library.utils.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * @author jv.lee
 * @date 2020/11/26
 * @description
 */
class FlowViewModel : BaseViewModel() {

    private val repository by lazy { FlowRepository() }
    private val page by lazy { PageNumber(1) }

    val dataLiveData by lazy { BaseLiveData<Page<Content>>() }

    fun getData(@LoadStatus status: Int) {

        launchMain {
            repository.getData(page.getPage(status))
                .map {
                    val value = withContext(Dispatchers.IO) { 1 }
                    it
                }
                .bindLive(dataLiveData)
        }

    }

    fun getCacheOrNetworkData() {
        launchMain {
            val hasCache = true
            flowOf(hasCache)
                .flatMapMerge {
                    if (it) {
                        return@flatMapMerge flowOf("cache")
                    }
                    return@flatMapMerge flowOf("network")
                }.map {
                    if (hasCache) println("put cache data.")
                    it
                }.catch {

                }.collect {

                }
        }
    }

    //该方法在当前生命周期 销毁或重建前时调用
    override fun onCleared() {
        super.onCleared()
        LogUtil.i("FlowViewModel.onCleared()")
        dataLiveData.cancel()
    }

}
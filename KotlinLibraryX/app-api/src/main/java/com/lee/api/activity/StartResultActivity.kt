package com.lee.api.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.lee.api.R
import com.lee.api.databinding.ActivityStartResultBinding
import com.lee.library.base.BaseActivity
import com.lee.library.mvvm.base.BaseViewModel
import java.io.File

class StartResultActivity :
    BaseActivity<ActivityStartResultBinding, BaseViewModel>(R.layout.activity_start_result) {

    private val activityForResult = prepareCall(ActivityResultContracts.StartActivityForResult()) {
        toast(it?.data?.getStringExtra("value") ?: "")
    }

    private val requestPermission = prepareCall(ActivityResultContracts.RequestPermission()) {
        toast("request permission success")
    }

    private val requestPicture = prepareCall(ActivityResultContracts.TakePicture()) {
        binding.ivPicture.setImageBitmap(it)
    }

    override fun bindView() {
        binding.btnActivityResult.setOnClickListener {
            activityForResult.launch(Intent(this, ResultActivity::class.java))
        }

        binding.btnPictureResult.setOnClickListener {
            //权限申请
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                requestPermission.launch(Manifest.permission.CAMERA)
            }

            val file =
                File("${filesDir.absolutePath}${File.separator}${System.currentTimeMillis()}.jpg")

            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, packageName.plus(".fileprovider"), file)
            } else {
                Uri.fromFile(file)
            }

            requestPicture.launch(uri)

        }
    }

    override fun bindData() {

    }
}
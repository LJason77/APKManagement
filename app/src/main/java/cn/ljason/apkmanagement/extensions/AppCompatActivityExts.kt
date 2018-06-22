package cn.ljason.apkmanagement.extensions

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity

/**
 * 描述： AppCompatActivity 拓展类
 * @author LJason
 * @e-mail ljason@ljason.cn
 * @time 18-6-9
 */

fun AppCompatActivity.isPermissionGranted(permission: String) =
		ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun AppCompatActivity.requestPermission(permission: String, requestId: Int) =
		ActivityCompat.requestPermissions(this, arrayOf(permission), requestId)

package cn.ljason.apkmanagement.utils

import android.content.Context
import android.os.storage.StorageManager
import java.lang.reflect.Method

/**
 * 描述： 存储工具类
 * @author LJason
 * @e-mail ljason@ljason.cn
 * @time 18-6-9
 */

class StorageUtils
{
	// 获得所有存储路径
	fun getAllSdPaths(context: Context): Array<*>
	{
		val mMethodGetPaths: Method?
		var paths: Array<*>? = null
		// 通过调用类的实例 mStorageManager 的 getClass() 获取 StorageManager 类对应的Class对象
		// getMethod("getVolumePaths") 返回 StorageManager 类对应的 Class 对象的 getVolumePaths 方法，这里不带参数
		val mStorageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
		try
		{
			mMethodGetPaths = mStorageManager.javaClass.getMethod("getVolumePaths")
			paths = mMethodGetPaths!!.invoke(mStorageManager) as Array<*>?
		} catch (e: Exception)
		{
			e.printStackTrace()
		}
		return paths!!
	}
	
	// 返回存储路径
	fun storagePath(context: Context, device: Int = 1): String
	{
		return getAllSdPaths(context)[device].toString()
	}
}
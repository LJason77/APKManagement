package cn.ljason.apkmanagement.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import cn.ljason.apkmanagement.R
import cn.ljason.apkmanagement.extensions.isPermissionGranted
import cn.ljason.apkmanagement.extensions.requestPermission
import cn.ljason.apkmanagement.utils.MyFileFilter
import cn.ljason.apkmanagement.utils.StorageUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : AppCompatActivity()
{
	private lateinit var removePath: String
	private var mSearchApk: SearchApk? = null
	private lateinit var files: Array<File>
	private val apks: MutableList<File> = mutableListOf()
	
	private lateinit var mMediaPlayer: MediaPlayer
	
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)
		
		// 检查存储读写权限
		checkingAuthority()
		mMediaPlayer = MediaPlayer.create(this, R.raw.whisper)
	}
	
	// 检查存储读写权限
	private fun checkingAuthority()
	{
		if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
		{
			// 无权限
			requestStoragePermission()
		} else
		{
			// 有权限
			files = File(StorageUtils().storagePath(this)).listFiles(MyFileFilter())
			removePath = files[0].parent + "/APK"
			path.text = removePath
			mSearchApk = SearchApk(files)
			mSearchApk!!.execute(null)
		}
	}
	
	/**
	 * 请求存储权限
	 * 如果之前拒绝过权限，将弹出说明，否则直接请求权限
	 */
	private fun requestStoragePermission()
	{
		if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
		{
			Snackbar.make(mainLayout, R.string.permission_storage_rationale, Snackbar.LENGTH_INDEFINITE).setAction("允许") {
				requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_PERMISSION)
			}.show()
		} else
		{
			requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_PERMISSION)
		}
	}
	
	/**
	 * 从请求权限回调结果。
	 * 对于 requestPermissions() 上的每个调用都会调用此方法。
	 * 注意：与用户的权限请求交互可能被中断。 在这种情况下，将收到空权限和数组，这些数据应视为取消。
	 */
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
	{
		when (requestCode)
		{
			WRITE_PERMISSION ->
			{
				if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 已授权
					if (isExternalStorageWritable())
					{
						files = File(StorageUtils().storagePath(this)).listFiles(MyFileFilter())
						removePath = files[0].parent + "/APK"
						path.text = removePath
						mSearchApk = SearchApk(files)
						mSearchApk!!.execute(null)
					}
				} else
				{
					path.text = "未授权"
					requestStoragePermission()
				}
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
	}
	
	/**
	 * 检查外部存储是否可用于读取和写入
	 * Environment.getExternalStorageState() 返回外部存储媒体的当前状态
	 * Environment.MEDIA_MOUNTED 存储状态，如果介质存在并且以读/写访问的方式安装在其安装点
	 */
	private fun isExternalStorageWritable(): Boolean
	{
		val state = Environment.getExternalStorageState()
		return Environment.MEDIA_MOUNTED == state
	}
	
	companion object
	{
		// ID 以识别存储许可请求
		const val WRITE_PERMISSION = 0
	}
	
	@SuppressLint("StaticFieldLeak")
	inner class SearchApk constructor(private val files: Array<File>)
		: AsyncTask<Void, Void, Int>()
	{
		override fun onPreExecute()
		{
			progress.visibility = View.VISIBLE
			complete.text = "正在扫描"
			complete.visibility = View.VISIBLE
		}
		
		override fun doInBackground(vararg params: Void): Int
		{
			searchApk(files)
			return apks.size
		}
		
		@SuppressLint("SetTextI18n")
		override fun onPostExecute(result: Int)
		{
			progress.visibility = View.GONE
			complete.text = "共找到 $result 个 APK"
			// 播放提示音
			mMediaPlayer.start()
		}
		
		// 扫描 APK
		private fun searchApk(files: Array<File>): MutableList<File>
		{
			for (file in files)
			{
				// 若为目录则递归查找
				if (file.isDirectory)
				{
					if (file.list().isNotEmpty())
					{
						searchApk(file.listFiles(MyFileFilter()))
					}
				} else if (file.isFile)
				{
					val path = file.path
					if (path.endsWith(".apk"))
					{
						apks.add(file)
					}
				}
			}
			return apks
		}
	}
}

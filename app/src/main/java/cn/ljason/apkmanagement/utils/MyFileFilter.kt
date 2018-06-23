package cn.ljason.apkmanagement.utils

import java.io.File
import java.io.FileFilter

/**
 * 描述： 文件过滤器
 * @author LJason
 * @e-mail ljason@ljason.cn
 * @time 18-6-9
 */

class MyFileFilter : FileFilter
{
	override fun accept(pathname: File): Boolean
	{
		return !pathname.name.startsWith(".")
	}
}

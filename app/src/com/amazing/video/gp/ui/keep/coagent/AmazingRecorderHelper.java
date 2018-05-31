package com.amazing.video.gp.ui.keep.coagent;

import com.google.gson.Gson;
import com.amazing.video.gp.loger.AmazingLoger;
import com.amazing.video.gp.sharepreferencedata.AmazingPreferenceKeys;
import com.amazing.video.gp.sharepreferencedata.AmazingPreferenceUtils;
import com.yixia.camera.util.FileUtils;
import com.yixia.camera.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 拍摄帮助类
 * 
 * @author tangjun
 *
 */
public class AmazingRecorderHelper {

	/** 获取视频码率 */
	public static int getAmazingVideoBitrate() {
		return 1500;
	}

	/** 获取最大拍摄时长，默认10秒 */
	public static int getAmazingMaxDuration() {
		return AmazingPreferenceUtils.getIntProcess(AmazingPreferenceKeys.VIDEO_TIME_LIMIT, AmazingPreferenceKeys.VIDEO_TIME_LIMIT_DEFAULT);
	}

	/**
	 * 清除账号权限拍摄时长
	 */
	public static void removeAmazingDuration() {
		AmazingPreferenceUtils.remove(AmazingPreferenceKeys.VIDEO_TIME_LIMIT);
	}

	/** 讲对象实例化到磁盘 */
	public static boolean saveAmazingObject(Object obj, String target) {
		try {
			if (StringUtils.isNotEmpty(target)) {
				FileOutputStream out = new FileOutputStream(target);
				Gson gson = new Gson();
				out.write(gson.toJson(obj).getBytes());
				out.flush();
				out.close();
				return true;
			}
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
		return false;
	}

	/** 从文件中反序列化对象 */
	public static <T> T restoreAmazingObject(Class<T> cls, String target) {
		try {
			String sb = FileUtils.readFile(new File(target));
			if (sb != null) {
				String str = sb.toString();
				Gson gson = new Gson();
				T result = gson.fromJson(str.toString(), cls);
				return result;
			}
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
		return null;
	}


}

package com.amazing.video.gp.ui.keep.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.amazing.video.gp.loger.AmazingLoger;
import com.yixia.camera.util.StringUtils;

/**
 * 专门用来处理Bitmap类型的ImageView，onDetachedFromWindow时释放，防止内存泄露
 * 
 * @author tangjun
 *
 */
public class AmazingBitmapImageView extends ImageView {

	private Bitmap mBitmap;
	private String mPath;

	public AmazingBitmapImageView(Context context) {
		super(context);
	}

	public AmazingBitmapImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setImagePath(String path) {
		release();
		if (StringUtils.isNotEmpty(path)) {
			try {
				mBitmap = BitmapFactory.decodeFile(path);
				if (mBitmap != null && !mBitmap.isRecycled()) {
					mPath = path;
					setImageBitmap(mBitmap);
				}
			} catch (OutOfMemoryError e) {
				AmazingLoger.e(e);
			} catch (Exception e) {
				AmazingLoger.e(e);
			}
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (StringUtils.isNotEmpty(mPath)) {
			setImagePath(mPath);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		release();
	}

	public void release() {
		if (mBitmap != null) {
			if (!mBitmap.isRecycled())
				mBitmap.recycle();
			mBitmap = null;
		}
	}
}

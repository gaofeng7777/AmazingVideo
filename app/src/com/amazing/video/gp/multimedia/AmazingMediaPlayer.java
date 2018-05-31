package com.amazing.video.gp.multimedia;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.amazing.video.gp.loger.AmazingLoger;

public class AmazingMediaPlayer extends android.media.MediaPlayer {

	private static final String TAG = "[MediaPlayer]";

	@Override
	public void start() {
		try {
			super.start();
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
	}

	@Override
	public void setVolume(float leftVolume, float rightVolume) {
		try {
			super.setVolume(leftVolume, rightVolume);
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
	}

	private static final int HANDLER_MESSAGE_PARSE = 0;
	private static final int HANDLER_MESSAGE_LOOP = 1;

	private Handler mVideoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_MESSAGE_PARSE:
				pause();
				break;
			case HANDLER_MESSAGE_LOOP:
				if (isPlaying()) {
					seekTo(msg.arg1);
					sendMessageDelayed(mVideoHandler.obtainMessage(HANDLER_MESSAGE_LOOP, msg.arg1, msg.arg2), msg.arg2);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/** 区域内循环播放 */
	public void loopAmazingDelayed(int startTime, int endTime) {
		int delayMillis = endTime - startTime;
		seekTo(startTime);
		if (!isPlaying())
			start();
		mVideoHandler.removeMessages(HANDLER_MESSAGE_LOOP);
		mVideoHandler.sendMessageDelayed(mVideoHandler.obtainMessage(HANDLER_MESSAGE_LOOP, getCurrentPosition(), delayMillis), delayMillis);
	}

	@Override
	public void seekTo(int msec) {
		try {
			super.seekTo(msec);
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
	}

	public void loopAmazingDelayed(int endTime) {
		int delayMillis = endTime;
		//if (!isPlaying())
		start();
		mVideoHandler.removeMessages(HANDLER_MESSAGE_LOOP);
		mVideoHandler.sendMessageDelayed(mVideoHandler.obtainMessage(HANDLER_MESSAGE_LOOP, 0, delayMillis), delayMillis);
	}

	public void pauseAmazingClearDelayed() {
		pause();
		mVideoHandler.removeMessages(HANDLER_MESSAGE_PARSE);
		mVideoHandler.removeMessages(HANDLER_MESSAGE_LOOP);
	}

	@Override
	public int getCurrentPosition() {
		try {
			return super.getCurrentPosition();
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
		return 0;
	}

	@Override
	public int getDuration() {
		try {
			return super.getDuration();
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
		return 0;
	}

	@Override
	public int getVideoWidth() {
		try {
			return super.getVideoWidth();
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
		return 0;
	}

	@Override
	public int getVideoHeight() {
		try {
			return super.getVideoHeight();
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
		return 0;
	}

	@Override
	public void stop() {
		try {
			super.stop();
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
	}

	@Override
	public void pause() throws IllegalStateException {
		try {
			super.pause();
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
	}

	@Override
	public boolean isPlaying() {
		try {
			return super.isPlaying();
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
		return false;
	}

	@Override
	public void reset() {
		try {
			super.reset();
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
	}

	@Override
	public void release() {
		try {
			super.release();
		} catch (IllegalStateException e) {
			AmazingLoger.e(e);
		} catch (Exception e) {
			AmazingLoger.e(e);
		}
	}

	public static AmazingMediaPlayer create(Context context, Uri uri) {
		return create(context, uri, null);
	}

	public static AmazingMediaPlayer create(Context context, int resid) {
		try {
			AssetFileDescriptor afd = context.getResources().openRawResourceFd(resid);
			if (afd == null)
				return null;

			AmazingMediaPlayer mp = new AmazingMediaPlayer();
			mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			mp.prepare();
			return mp;
		} catch (IOException ex) {
			Log.d(TAG, "create failed:", ex);
			// fall through
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, "create failed:", ex);
			// fall through
		} catch (SecurityException ex) {
			Log.d(TAG, "create failed:", ex);
			// fall through
		} catch (Exception ex) {
			Log.d(TAG, "create failed:", ex);
			// fall through
		}
		return null;
	}

	public static AmazingMediaPlayer create(Context context, Uri uri, SurfaceHolder holder) {

		try {
			AmazingMediaPlayer mp = new AmazingMediaPlayer();
			mp.setDataSource(context, uri);
			if (holder != null) {
				mp.setDisplay(holder);
			}
			mp.prepare();
			return mp;
		} catch (IOException ex) {
			Log.e(TAG, "create failed:", ex);
			// fall through
		} catch (IllegalArgumentException ex) {
			Log.e(TAG, "create failed:", ex);
			// fall through
		} catch (SecurityException ex) {
			Log.e(TAG, "create failed:", ex);
			// fall through
		}

		return null;
	}
}

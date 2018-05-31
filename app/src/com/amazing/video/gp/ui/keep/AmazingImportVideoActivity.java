package com.amazing.video.gp.ui.keep;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.amazing.video.gp.R;
import com.yixia.camera.FFMpegUtils;
import com.amazing.video.gp.MyApplication;
import com.amazing.video.gp.ui.AmazingBaseActivity;
import com.amazing.video.gp.ui.keep.view.AmazingProgressView;
import com.amazing.video.gp.ui.widget.AmazingVideoView;
import com.amazing.video.gp.ui.widget.AmazingVideoView.OnAmazingPlayStateListener;
import com.yixia.camera.model.MediaObject;
import com.yixia.camera.model.MediaObject.MediaPart;
import com.yixia.camera.util.DeviceUtils;

public class AmazingImportVideoActivity extends AmazingBaseActivity implements
		OnClickListener, OnPreparedListener, OnAmazingPlayStateListener {

	/** 视频预览 */
	private AmazingVideoView mAmazingVideoView;
	/** 暂停图标 */
	private View mRecordPlay;
	/** 视频总进度条 */
	private AmazingProgressView mProgressView;

	/** 视频信息 */
	private MediaObject mMediaObject;
	private MediaPart mMediaPart;
	/** 窗体宽度 */
	private int mWindowWidth;
	private String mVideoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 防止锁屏

		String obj = getIntent().getStringExtra("obj");
		mVideoPath = getIntent().getStringExtra("path");
		mMediaObject = restoneAmazingMediaObject(obj);
		if (mMediaObject == null) {
			Toast.makeText(this, R.string.record_read_object_faild,
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		mWindowWidth = DeviceUtils.getScreenWidth(this);
		setContentView(R.layout.amazing_activity_import_video);

		// ~~~ 绑定控件
		mAmazingVideoView = (AmazingVideoView) findViewById(R.id.record_preview);
		mRecordPlay = findViewById(R.id.record_play);
		mProgressView = (AmazingProgressView) findViewById(R.id.record_progress);

		// ~~~ 绑定事件
		mAmazingVideoView.setOnClickListener(this);
		mAmazingVideoView.setOnAmazingPreparedListener(this);
		mAmazingVideoView.setOnAmazingPlayStateListener(this);
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);

		findViewById(R.id.record_layout).getLayoutParams().height = mWindowWidth;
		mAmazingVideoView.setAmazingVideoPath(mVideoPath);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			finish();
			break;
		case R.id.title_right:
			startAmazingEncoding();
			break;
		}
	}

	/** 开始转码 */
	private void startAmazingEncoding() {
		// 检测磁盘空间
		// if (FileUtils.showFileAvailable() < 200) {
		// Toast.makeText(this, R.string.record_camera_check_available_faild,
		// Toast.LENGTH_SHORT).show();
		// return;
		// }

		if (!isFinishing() && mMediaObject != null && mMediaPart != null) {
			new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					showAmazingProgress("",
							getString(R.string.record_camera_progress_message));
				}

				@Override
				protected Boolean doInBackground(Void... params) {
					return FFMpegUtils.importVideo(mMediaPart, mWindowWidth,
							mAmazingVideoView.getAmazingVideoWidth(),
							mAmazingVideoView.getAmazingVideoHeight(), 0, 0, true);
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					hideAmazingProgress();
					if (result) {
						saveAmazingMediaObject(mMediaObject);
						setResult(Activity.RESULT_OK);
						finish();
					} else {
						Toast.makeText(AmazingImportVideoActivity.this,
								R.string.record_video_transcoding_faild,
								Toast.LENGTH_SHORT).show();
					}
				}
			}.execute();
		}
	}

	@Override
	public void onStateChanged(boolean isPlaying) {
		if (isPlaying)
			mRecordPlay.setVisibility(View.GONE);
		else
			mRecordPlay.setVisibility(View.VISIBLE);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		if (!isFinishing()) {
			if (mAmazingVideoView.getAmazingVideoWidth() == 0
					|| mAmazingVideoView.getAmazingVideoHeight() == 0) {
				Toast.makeText(AmazingImportVideoActivity.this,
						R.string.record_camera_import_video_faild,
						Toast.LENGTH_SHORT).show();
				finish();
				return;
			}

			mAmazingVideoView.start();
			mAmazingVideoView.setAmaizngLooping(true);

			int duration = mMediaObject.getMaxDuration()
					- mMediaObject.getDuration();
			if (duration > mAmazingVideoView.getAmazingDuration())
				duration = mAmazingVideoView.getAmazingDuration();

			mMediaPart = mMediaObject.buildMediaPart(mVideoPath, duration,
					MediaObject.MEDIA_PART_TYPE_IMPORT_VIDEO);
			mProgressView.setData(mMediaObject);
		}
	}
}

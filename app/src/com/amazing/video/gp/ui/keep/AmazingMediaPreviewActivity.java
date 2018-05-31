package com.amazing.video.gp.ui.keep;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazing.video.gp.MainActivity;
import com.amazing.video.gp.R;
import com.amazing.video.gp.MyApplication;
import com.amazing.video.gp.plain.AmazingCommonIntentExtra;
import com.amazing.video.gp.important.AmazingImportantThemeSingle;
import com.amazing.video.gp.service.AmazingAssertService;
import com.amazing.video.gp.ui.AmazingBaseActivity;
import com.amazing.video.gp.ui.keep.coagent.AmazingThemeHelper;
import com.amazing.video.gp.ui.keep.view.AmazingThemeGroupLayout;
import com.amazing.video.gp.ui.keep.view.AmazingThemeSufaceView;
import com.amazing.video.gp.ui.keep.view.AmazingThemeView;
import com.amazing.video.gp.utils.AmazingConvertToUtils;
import com.amazing.video.gp.utils.AmazingIsUtils;
import com.amazing.video.gp.utils.AmazingToastUtils;
import com.yixia.camera.model.MediaObject;
import com.yixia.camera.model.MediaThemeObject;
import com.yixia.camera.util.DeviceUtils;
import com.yixia.camera.util.FileUtils;
import com.yixia.camera.util.StringUtils;
import com.yixia.videoeditor.adapter.UtilityAdapter;

/**
 * 视频预览
 * 
 * @author tangjun@yixia.com
 *
 */
public class AmazingMediaPreviewActivity extends AmazingBaseActivity implements
		OnClickListener, UtilityAdapter.OnNativeListener {

	/** 开始转码 */
	private static final int HANDLER_ENCODING_START = 100;
	/** 转码进度 */
	private static final int HANDLER_ENCODING_PROGRESS = 101;
	/** 转码结束 */
	private static final int HANDLER_ENCODING_END = 102;
	/** 无主题放的位置 */
	private final static int NO_THEME_INDEX = 0;

	/** 播放按钮、主题音量按钮 */
	private ImageView mPlayStatus;
	/** 上一步、下一步 */
	private TextView mTitleLeft, mTitleNext, mTitleText, mVideoPreviewMusic;
	/** 主题音乐，原声音 */
	private CheckBox mThemeVolumn, mVideoVolumn;
	/** 正在加载 */
	private View mLoadingView;
	/** 主题、滤镜容器 */
	private View mThemeLayout, mFilterLayout;

	/** 主题容器 */
	private AmazingThemeGroupLayout mThemes, mFilters;
	/** MV主题 */
	private AmazingThemeSufaceView mAmazingThemeSufaceView;

	/** 主题缓存的目录 */
	private File mThemeCacheDir;
	/** 当前主题 */
	private AmazingImportantThemeSingle mCurrentTheme;

	/** 主题列表 */
	private ArrayList<AmazingImportantThemeSingle> mThemeList;
	/** 滤镜列表 */
	private ArrayList<AmazingImportantThemeSingle> mFilterList;

	/** 导演签名图片 */
	private String mAuthorBitmapPath;
	/** 导出视频，导出封面 */
	private String mVideoPath, mCoverPath;
	/** 临时合并ts流 */
	private String mVideoTempPath;
	/** 当前音乐路径 */
	private String mCurrentMusicPath;
	/** 当前音乐名称 */
	private String mCurrentMusicTitle;
	/** 当前音乐名称 */
	private String mCurrentMusicName;
	/** 是否需要回复播放 */
	private boolean mNeedResume;
	/** 是否停止播放 */
	private boolean mStopPlayer;
	/** 是否正在转码 */
	private boolean mStartEncoding;
	/** 窗体宽度 */
	private int mWindowWidth;
	/** 分块边距，默认10dip */
	private int mLeftMargin;
	/** 视频时长 */
	private int mDuration;
	/** 视频信息 */
	private MediaObject mMediaObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MyApplication.getInstance().addActivity(this);
		mMediaObject = (MediaObject) getIntent().getSerializableExtra(
				AmazingCommonIntentExtra.EXTRA_MEDIA_OBJECT);
		if (mMediaObject == null) {
			Toast.makeText(this, R.string.record_read_object_faild,
					Toast.LENGTH_SHORT).show();
			finish();
			overridePendingTransition(R.anim.amazing_push_bottom_in,
					R.anim.amazing_push_bottom_out);
			return;
		}

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 防止锁屏
		preparAmazingeActivity();
		prepareAmazingViews();
	}

	/** 预处理参数 */
	private boolean preparAmazingeActivity() {
		// 加载默认参数
		mWindowWidth = DeviceUtils.getScreenWidth(this);
		// 获取传入参数
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) && !isAmaizngExternalStorageRemovable())
			mThemeCacheDir = new File(getExternalCacheDir(), "Theme");
		else
			mThemeCacheDir = new File(getCacheDir(), "Theme");
		mLeftMargin = AmazingConvertToUtils.dipToPX(this, 8);

		mVideoPath = mMediaObject.getOutputVideoPath();
		if (StringUtils.isNotEmpty(mVideoPath)) {
			mCoverPath = mVideoPath.replace(".mp4", ".jpg");
		}
		mVideoTempPath = getIntent().getStringExtra("output");

		return true;
	}

	/** 预处理UI相关 */
	private void prepareAmazingViews() {
		setContentView(R.layout.amazing_activity_media_preview);
		// 绑定控件
		mPlayStatus = (ImageView) findViewById(R.id.play_status);
		mAmazingThemeSufaceView = (AmazingThemeSufaceView) findViewById(R.id.preview_theme);
		mTitleLeft = (TextView) findViewById(R.id.titleLeft);
		mTitleNext = (TextView) findViewById(R.id.titleRight);
		mTitleText = (TextView) findViewById(R.id.titleText);
		mVideoPreviewMusic = (TextView) findViewById(R.id.video_preview_music);
		mThemes = (AmazingThemeGroupLayout) findViewById(R.id.themes);
		mFilters = (AmazingThemeGroupLayout) findViewById(R.id.filters);
		mThemeVolumn = (CheckBox) findViewById(R.id.video_preview_theme_volume);
		mVideoVolumn = (CheckBox) findViewById(R.id.video_preview_video_volume);
		mLoadingView = findViewById(R.id.loading);
		mThemeLayout = findViewById(R.id.theme_layout);
		mFilterLayout = findViewById(R.id.filter_layout);

		mTitleLeft.setOnClickListener(this);
		mTitleNext.setOnClickListener(this);
		mAmazingThemeSufaceView.setOnComplateListener(mOnComplateListener);
		mAmazingThemeSufaceView.setOnClickListener(this);
		findViewById(R.id.tab_theme).setOnClickListener(this);
		findViewById(R.id.tab_filter).setOnClickListener(this);
		mThemeVolumn.setOnClickListener(this);
		mVideoVolumn.setOnClickListener(this);

		mTitleText.setText(R.string.record_camera_preview_title);
		mTitleNext.setText(R.string.record_camera_preview_next);

		// 设置主题预览默认参数
		mAmazingThemeSufaceView.setIntent(getIntent());
		mAmazingThemeSufaceView.setAmazingOutputPath(mVideoPath);// 输出文件
		mAmazingThemeSufaceView.setAmazingMediaObject(mMediaObject);
		if (FileUtils.checkFile(mThemeCacheDir)) {
			mAmazingThemeSufaceView.setAmazingFilterCommomPath(new File(mThemeCacheDir,
					AmazingThemeHelper.THEME_VIDEO_COMMON).getAbsolutePath());
		}
		/** 设置播放区域 */
		View preview_layout = findViewById(R.id.preview_layout);
		LinearLayout.LayoutParams mPreviewParams = (LinearLayout.LayoutParams) preview_layout
				.getLayoutParams();
		mPreviewParams.height = DeviceUtils.getScreenWidth(this);
		loadAmazingThemes();
	}

	@Override
	public void onResume() {
		super.onResume();
		UtilityAdapter.registerNativeListener(this);
		if (mAmazingThemeSufaceView != null && mNeedResume && mCurrentTheme != null) {
			restartAmazingVideo();
		}
		mNeedResume = false;
	}

	@Override
	public void onPause() {
		super.onPause();
		UtilityAdapter.registerNativeListener(null);
		if (mAmazingThemeSufaceView != null && mAmazingThemeSufaceView.isPlaying()) {
			mNeedResume = true;
			releaseAmazingVideo();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titleLeft:
			startActivity(new Intent(this,MainActivity.class));
			finish();//视频的编辑界面，中的返回按钮
			break;
		case R.id.titleRight:
			startAmazingEncoding();
			break;
		case R.id.preview_theme:// 点击暂停视频播放
			if (isPlaying())
				stopAmazingVideo();
			else
				startAmazingVideo();
			break;
		case R.id.video_preview_theme_volume:// 静音主题音
			// 隐藏动画
			AmazingToastUtils
					.showAmazingToastImage(
							this,
							mThemeVolumn.isChecked() ? R.drawable.priview_theme_volumn_close
									: R.drawable.priview_theme_volumn_open);
			mAmazingThemeSufaceView.setAmazingThemeMute(mThemeVolumn.isChecked());
			restartAmazingVideo();
			break;
		case R.id.video_preview_video_volume:// 静音原声
			AmazingToastUtils
					.showAmazingToastImage(
							this,
							mVideoVolumn.isChecked() ? R.drawable.priview_orig_volumn_close
									: R.drawable.priview_orig_volumn_open);
			mAmazingThemeSufaceView.setAmazingOrgiMute(mVideoVolumn.isChecked());
			restartAmazingVideo();
			break;
		case R.id.tab_theme:
			mThemeLayout.setVisibility(View.VISIBLE);
			mFilterLayout.setVisibility(View.GONE);
			break;
		case R.id.tab_filter:
			mThemeLayout.setVisibility(View.GONE);
			mFilterLayout.setVisibility(View.VISIBLE);
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

		stopAmazingVideo();

		// 更新静音
		if (mMediaObject != null && mMediaObject.mThemeObject != null) {
			mMediaObject.mThemeObject.mThemeMute = mThemeVolumn.isChecked();
			mMediaObject.mThemeObject.mOrgiMute = mVideoVolumn.isChecked();
		}
		// 检测是否需要重新编译
		mStartEncoding = true;
		mHandler.removeMessages(HANDLER_ENCODING_START);
		mHandler.removeMessages(HANDLER_ENCODING_PROGRESS);
		mHandler.removeMessages(HANDLER_ENCODING_END);
		mHandler.sendEmptyMessage(HANDLER_ENCODING_START);
	}

	/** 加载主题 */
	private void loadAmazingThemes() {
		if (isFinishing() || mStartEncoding)
			return;

		new android.os.AsyncTask<Void, Void, File>() {

			@Override
			protected File doInBackground(Void... params) {
				// 预处理主题
				while (AmazingAssertService.isRunning()) {
					SystemClock.sleep(500);
				}

				// 预处理主题（解压更新主题包）
				File result = AmazingThemeHelper.prepareAmaingTheme(
						AmazingMediaPreviewActivity.this, mThemeCacheDir);
				if (result != null) {
					// 主题列表
					mThemeList = AmazingThemeHelper.parseAmazingTheme(
							AmazingMediaPreviewActivity.this, mThemeCacheDir,
							AmazingThemeHelper.THEME_MUSIC_VIDEO_ASSETS,
							R.array.theme_order);

					// 空主题
					AmazingImportantThemeSingle orgiTheme = AmazingThemeHelper.loadThemeJson(
							mThemeCacheDir, new File(mThemeCacheDir,
									AmazingThemeHelper.THEME_EMPTY));
					if (orgiTheme != null)
						mThemeList.add(NO_THEME_INDEX, orgiTheme);
				}

				// 加载滤镜
				mFilterList = AmazingThemeHelper.parseAmazingTheme(AmazingMediaPreviewActivity.this,
						mThemeCacheDir, AmazingThemeHelper.THEME_FILTER_ASSETS,
						R.array.theme_filter_order);

				// 生成签名
				mAuthorBitmapPath = AmazingThemeHelper
						.updateAmazingVideoAuthorLogo(
								mThemeCacheDir,
								getString(R.string.record_camera_author,
										getString(R.string.name_null)
												+ ""), false);
				return result;
			}

			@Override
			protected void onPostExecute(File result) {
				super.onPostExecute(result);
				File themeDir = result;
				if (themeDir != null && !isFinishing() && mThemeList != null
						&& mThemeList.size() > 1) {
					/** 循环添加单个主题到主题容器中 */
					mThemes.removeAllViews();

					String themeName = getIntent().getStringExtra("theme");
					int defaultIndex = NO_THEME_INDEX, index = 0;
					if (mCurrentTheme != null) {
						themeName = mCurrentTheme.themeName;
					}
					for (AmazingImportantThemeSingle theme : mThemeList) {
						addAmazingThemeItem(theme, -1);// 顺序添加
						if (StringUtils.isNotEmpty(themeName)
								&& AmazingIsUtils.equals(theme.themeName, themeName)) {
							defaultIndex = index;
						}
						index++;
					}

					// 滤镜
					mFilters.removeAllViews();
					for (AmazingImportantThemeSingle theme : mFilterList) {
						addAmazingThemeItem(mFilters, theme, -1);// 顺序添加
					}

					mCurrentTheme = null;
					mThemes.getChildAt(defaultIndex).performClick();// 默认选中无主题
				}
			}

		}.execute();
	}

	private AmazingThemeView addAmazingThemeItem(AmazingThemeGroupLayout layout,
												 AmazingImportantThemeSingle theme, int index) {
		AmazingThemeView amazingThemeView = new AmazingThemeView(AmazingMediaPreviewActivity.this, theme);
		if (theme.themeIconResource > 0) {
			amazingThemeView.getIcon().setImageResource(theme.themeIconResource);
		} else {
			if (StringUtils.isNotEmpty(theme.themeIcon)) {
				amazingThemeView.getIcon().setImagePath(theme.themeIcon);
			}
		}

		amazingThemeView.setOnClickListener(mThemeClickListener);
		amazingThemeView.setTag(theme);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);// mThemeItemWH,
														// mThemeItemWH
		lp.leftMargin = mLeftMargin;
		if (index == -1)
			layout.addView(amazingThemeView, lp);
		else
			layout.addView(amazingThemeView, index, lp);
		return amazingThemeView;
	}

	/** 添加当个主题到UI上 */
	private AmazingThemeView addAmazingThemeItem(AmazingImportantThemeSingle theme, int index) {
		return addAmazingThemeItem(mThemes, theme, index);
	}

	/** 重新播放 */
	private synchronized void restartAmazingVideo() {
		mStopPlayer = false;
		mHandler.removeMessages(UtilityAdapter.NOTIFYVALUE_PLAYFINISH);
		mHandler.sendEmptyMessageDelayed(UtilityAdapter.NOTIFYVALUE_PLAYFINISH,
				100);
	}

	private void releaseAmazingVideo() {
		mAmazingThemeSufaceView.pauseAndClearDelayed();
		mAmazingThemeSufaceView.release();
		mPlayStatus.setVisibility(View.GONE);
	}

	/** 开始播放 */
	private void startAmazingVideo() {
		mStopPlayer = false;
		mAmazingThemeSufaceView.start();
		mPlayStatus.setVisibility(View.GONE);
	}

	/** 暂停播放 */
	private void stopAmazingVideo() {
		mStopPlayer = true;
		mAmazingThemeSufaceView.pause();
		mPlayStatus.setVisibility(View.VISIBLE);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_ENCODING_START:
				if (!isFinishing()) {
					showAmazingProgress("",
							getString(R.string.record_preview_encoding));
					// WindowManager.LayoutParams lp =
					// dialog.getWindow().getAttributes();
					// lp.y = -ConvertToUtils.dipToPX(MediaPreviewActivity.this,
					// 49 + 30);
					// dialog.getWindow().setAttributes(lp);
					// showProgressLayout(false, false,
					// getString(R.string.progressbar_message_preview_making));
					releaseAmazingVideo();
					mAmazingThemeSufaceView.startAmazingEncoding();
					sendEmptyMessage(HANDLER_ENCODING_PROGRESS);
				}
				break;
			case HANDLER_ENCODING_PROGRESS:// 读取进度
				int progress = UtilityAdapter
						.FilterParserInfo(UtilityAdapter.FILTERINFO_PROGRESS);
				if (mProgressDialog != null) {
					mProgressDialog.setMessage(getString(
							R.string.record_preview_encoding_format, progress));
				}
				if (progress < 100)
					sendEmptyMessageDelayed(HANDLER_ENCODING_PROGRESS, 200);
				else {
					sendEmptyMessage(HANDLER_ENCODING_END);
				}
				break;
			case HANDLER_ENCODING_END:
				mDuration = UtilityAdapter
						.FilterParserInfo(UtilityAdapter.FILTERINFO_TOTALMS);
				mAmazingThemeSufaceView.release();
				onAmzingEncodingEnd();
				break;
			case UtilityAdapter.NOTIFYVALUE_BUFFEREMPTY:
				showAmaizngLoading();
				break;
			case UtilityAdapter.NOTIFYVALUE_BUFFERFULL:
				hideAmaizngLoading();
				break;
			case UtilityAdapter.NOTIFYVALUE_PLAYFINISH:
				/** 播放完成时报告 */
				if (!isFinishing() && !mStopPlayer) {
					showAmaizngLoading();
					mAmazingThemeSufaceView.release();
					mAmazingThemeSufaceView.initFilter();
					mPlayStatus.setVisibility(View.GONE);
				}
				break;
			case UtilityAdapter.NOTIFYVALUE_HAVEERROR:
				/** 无法播放时报告 */
				if (!isFinishing()) {
					Toast.makeText(AmazingMediaPreviewActivity.this,
							R.string.record_preview_theme_load_faild,
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	/** 转码完成 */
	private void onAmzingEncodingEnd() {
		hideAmazingProgress();
		mStartEncoding = false;
		startActivity(new Intent(this, AmazingVideoPlayerActivity.class).putExtra(
				"path", mVideoPath));
	}

	/** 显示加载中 */
	private void showAmaizngLoading() {
		if (mLoadingView != null)
			mLoadingView.setVisibility(View.VISIBLE);
	}

	/** 隐藏加载中 */
	private void hideAmaizngLoading() {
		if (mLoadingView != null)
			mLoadingView.setVisibility(View.GONE);
	}

	/** 是否正在播放 */
	private boolean isPlaying() {
		return mAmazingThemeSufaceView.isPlaying();
	}

	/** 响应主题点击事件 */
	private OnClickListener mThemeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AmazingImportantThemeSingle theme = (AmazingImportantThemeSingle) v.getTag();
			if (theme == null || mMediaObject == null)
				return;

			if (StringUtils.isNotEmpty(mAuthorBitmapPath)
					&& (mCurrentTheme == null || !AmazingIsUtils.equals(
							mCurrentTheme.themeName, theme.themeName))) {
				String key = theme.themeName;
				((AmazingThemeGroupLayout) v.getParent()).mObservable
						.notifyObservers(key);

				mCurrentTheme = theme;

				if (mMediaObject.mThemeObject == null)
					mMediaObject.mThemeObject = new MediaThemeObject();

				if (theme.amazingIsMV()) {
					mMediaObject.mThemeObject.mMVThemeName = theme.themeName;
					mMediaObject.mThemeObject.mMusicThemeName = theme.musicName;
					mAmazingThemeSufaceView.amazingReset();
					mAmazingThemeSufaceView.setAmazingMVPath(theme.themeFolder);
					mAmazingThemeSufaceView.setAmazingTheme(theme);
					mAmazingThemeSufaceView.setAmazingVideoEndPath(mAuthorBitmapPath);// 签名
					mAmazingThemeSufaceView.setAmazingInputPath(mVideoTempPath);// 输入文件
					// 添加音乐
					mCurrentMusicPath = mCurrentTheme.musicPath;
					mCurrentMusicTitle = mCurrentTheme.musicTitle;
					mCurrentMusicName = mCurrentTheme.musicName;
					mAmazingThemeSufaceView.setMusicPath(mCurrentMusicPath);

					updateAmaizngMusicTitle();

					// 清空静音状态
					mThemeVolumn.setChecked(false);

					// 清除滤镜的选中状态
					if (mFilters != null) {
						mFilters.mObservable
								.notifyObservers(AmazingImportantThemeSingle.THEME_EMPTY);
					}
				}

				// 滤镜
				if (theme.isFilter()) {
					mMediaObject.mThemeObject.mFilterThemeName = theme.themeName;
					mAmazingThemeSufaceView.setAmazingFilterPath(theme.getAmazingFilterPath());
				}

				restartAmazingVideo();
			}
		}
	};

	/** 更新音乐名称 */
	private void updateAmaizngMusicTitle() {
		if (StringUtils.isEmpty(mCurrentMusicTitle)) {
			mVideoPreviewMusic.setText(R.string.record_preview_music_nothing);
			mThemeVolumn.setVisibility(View.GONE);
		} else {
			mVideoPreviewMusic.setText(mCurrentMusicTitle);
			mThemeVolumn.setVisibility(View.VISIBLE);
		}
	}

	/** 播放完成 */
	private AmazingThemeSufaceView.OnComplateListener mOnComplateListener = new AmazingThemeSufaceView.OnComplateListener() {

		@Override
		public void onComplate() {
			if (!isFinishing()) {
				mAmazingThemeSufaceView.release();
			}
		}

	};

	public static boolean isAmaizngExternalStorageRemovable() {
		if (DeviceUtils.hasGingerbread())
			return Environment.isExternalStorageRemovable();
		else
			return Environment.MEDIA_REMOVED.equals(Environment
					.getExternalStorageState());
	}

	@Override
	public void ndkNotify(int key, int value) {
		if (!isFinishing())
			mHandler.sendEmptyMessage(value);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			startActivity(new Intent(this,MainActivity.class));
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}

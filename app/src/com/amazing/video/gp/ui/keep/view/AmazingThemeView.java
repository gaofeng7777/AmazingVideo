package com.amazing.video.gp.ui.keep.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazing.video.gp.R;
import com.amazing.video.gp.important.AmazingImportantThemeSingle;
import com.amazing.video.gp.utils.AmazingIsUtils;

public class AmazingThemeView extends RelativeLayout implements Observer {

	/** 图标 */
	private ImageView mSelectedIcon;
	private AmazingBitmapImageView mIcon;
	/** 标题 */
	private TextView mTitle;
	/** 当前主题 */
	private AmazingImportantThemeSingle mTheme;

	public AmazingThemeView(Context context, AmazingImportantThemeSingle theme) {
		super(context);
		this.mTheme = theme;

		LayoutInflater.from(context).inflate(R.layout.amazing_view_theme_item, this);
		mIcon = (AmazingBitmapImageView) findViewById(R.id.icon);
		mSelectedIcon = (ImageView) findViewById(R.id.selected);
		mTitle = (TextView) findViewById(R.id.title);

		mTitle.setText(mTheme.themeDisplayName);

		if (!mTheme.amazingIsMV()) {
			mSelectedIcon
					.setImageResource(R.drawable.record_theme_square_selected);
		}
		if (mTheme.isEmpty()) {
			mSelectedIcon.setVisibility(View.VISIBLE);
		}
	}

	/** 获取主题图标 */
	public AmazingBitmapImageView getIcon() {
		return mIcon;
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data != null && mTheme != null) {
			if (AmazingIsUtils.equals(mTheme.themeName, data.toString())) {
				mSelectedIcon.setVisibility(View.VISIBLE);
			} else {
				mSelectedIcon.setVisibility(View.GONE);
			}
		}
	}
}

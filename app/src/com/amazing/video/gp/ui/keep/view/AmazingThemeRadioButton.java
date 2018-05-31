package com.amazing.video.gp.ui.keep.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.amazing.video.gp.R;

public class AmazingThemeRadioButton extends RadioButton implements
		android.widget.CompoundButton.OnCheckedChangeListener {

	private int mColorNormal, mColorSelected;

	public AmazingThemeRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public AmazingThemeRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AmazingThemeRadioButton(Context context) {
		super(context);
		init();
	}

	private void init() {
		mColorNormal = getResources().getColor(R.color.transparent);
		mColorSelected = getResources().getColor(
				R.color.camera_theme_selected_bgcolor);
		setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked)
			setBackgroundColor(mColorSelected);
		else
			setBackgroundColor(mColorNormal);
	}
}

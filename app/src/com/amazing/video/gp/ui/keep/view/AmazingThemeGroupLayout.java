package com.amazing.video.gp.ui.keep.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.Observable;

public class AmazingThemeGroupLayout extends LinearLayout {

	public AmazingThemeGroupLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AmazingThemeGroupLayout(Context context) {
		super(context);
	}

	public void addView(AmazingThemeView child, android.view.ViewGroup.LayoutParams params) {
		super.addView(child, params);
		mObservable.addObserver(child);
	}
	
	public void addView(AmazingThemeView child, int index, android.view.ViewGroup.LayoutParams params) {
	  super.addView(child, index, params);
	  mObservable.addObserver(child);
	}

	@Override
	public void removeAllViews() {
		super.removeAllViews();
		mObservable.deleteObservers();
	}

	public Observable mObservable = new Observable() {
		@Override
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
			clearChanged();
		}

		@Override
		public void notifyObservers(Object data) {
			setChanged();
			super.notifyObservers(data);
			clearChanged();
		};
	};
}

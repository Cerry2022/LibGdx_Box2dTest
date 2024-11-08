package com.cerry.game.box2dtest;

import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import com.cerry.game.box2dtest.Box2dTest;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		//刘海屏适配
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			WindowManager.LayoutParams lp =  new WindowManager.LayoutParams();
			lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
			//lp.preferredRefreshRate = 120f;
			lp.preferredDisplayModeId = 1;
			//1->120
			//2->90
			//3->60
			getWindow().setAttributes(lp);
		}
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGL30 = true;
		initialize(new Box2dTest(), config);
	}
}

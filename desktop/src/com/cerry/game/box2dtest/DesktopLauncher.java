package com.cerry.game.box2dtest;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.cerry.game.box2dtest.Box2dTest;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32, 3, 2);
		config.setWindowedMode(1600,900);
		config.setForegroundFPS(120);
		config.setIdleFPS(60);
		config.setTitle("Box2dTest");
		config.useVsync(false);
		new Lwjgl3Application(new Box2dTest(), config);
		//new Lwjgl3Application(new MouseJointTest(),config);
	}
}

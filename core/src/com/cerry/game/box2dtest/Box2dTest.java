package com.cerry.game.box2dtest;
import com.badlogic.gdx.Game;
import com.cerry.game.box2dtest.Screens.MenuScreen;


// 这是一个Java文件，包含了一个名为Box2dTest的类，该类继承自Game类。在该类中，重写了create()和dispose()方法，create()方法加载MenuScreen并设置为当前屏幕，dispose()方法释放资源。

public class Box2dTest extends Game {
    @Override
    public void create() {
        //AssetLoader.load();
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        screen.dispose();
        //AssetLoader.dispose();
    }

}

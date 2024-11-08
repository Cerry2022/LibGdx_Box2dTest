package com.cerry.game.box2dtest.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.cerry.game.box2dtest.Box2dTest;
import com.cerry.game.box2dtest.UserInterface.Message;
import com.cerry.game.box2dtest.UserInterface.UiManager;

public class MenuScreen implements Screen {

    private final Box2dTest game;
    //文字
    private UiManager uiManager;
    Message message1;
    Message message2;

    public MenuScreen(Box2dTest game) {
        this.game = game;
    }

    @Override
    public void show() {
        uiManager = new UiManager();//Gui文字管理器

        uiManager.addControl("message1", new Message("鲨雕游戏", Gdx.graphics.getWidth() / 2f - 12*12*2, Gdx.graphics.getHeight() * 4f/6f, Color.WHITE,12f));
        uiManager.addControl("message2", new Message("点击屏幕开始游戏", Gdx.graphics.getWidth() / 2f - 12*4*4, Gdx.graphics.getHeight() * 2f/6f , Color.DARK_GRAY,4f));
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0.4f, 0.6f, 0.8f, 1);//#6699CC

        uiManager.render();

        if(Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.NUM_0) || Gdx.input.isTouched()) {
            uiManager.clear();
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        uiManager.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        uiManager.dispose();
    }
}

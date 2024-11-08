package com.cerry.game.box2dtest.UserInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;


import static com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.DEFAULT_CHARS;
import static com.cerry.game.box2dtest.Config.EXTENDED_CHARS;


public class UiManager {

    public static Stage stage = new Stage(new ScreenViewport());
    private Viewport viewport = stage.getViewport();

    protected Label.LabelStyle labelStyle = new Label.LabelStyle();
    protected TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

    private FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fusion-pixel.otf"));
    private FreeTypeFontParameter parameter = new FreeTypeFontParameter();

    private BitmapFont font12;
    private Skin skin;


    private final HashMap<String, ControlBase> controlBaseHashMap;


    public UiManager() {


        skin = new Skin();

        parameter.size = 12;
        parameter.characters = DEFAULT_CHARS+EXTENDED_CHARS;
        font12 = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // 不要忘记dispose以避免内存泄漏!


        // 添加字体到皮肤
        skin.add("font", font12);

        labelStyle.font = skin.getFont("font");

        // 创建 Pixmap 并填充纯色
        Pixmap pixmap = new Pixmap(200, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        // 从 Pixmap 创建 Texture
        Texture customTexture = new Texture(pixmap);

        // 释放 Pixmap 资源
        pixmap.dispose();

        skin.add("white", customTexture);

        //按钮
        buttonStyle.up = skin.newDrawable("white", Color.PINK); // 设置按钮正常状态的背景
        buttonStyle.over = skin.newDrawable("white", Color.PURPLE); // 设置鼠标悬停在按钮上时的背景
        buttonStyle.down = skin.newDrawable("white", Color.ORANGE); // 设置按钮按下状态的背景
        buttonStyle.font = labelStyle.font; // 设置按钮文本的字体样式


        controlBaseHashMap = new HashMap<>();

    }


    public <T extends ControlBase> T addControl(String name, T Control) {
        controlBaseHashMap.put(name, Control);
        return Control;
    }

    public <T extends ControlBase> T getControl(String name, Class<T> type) {
        ControlBase control = controlBaseHashMap.get(name);
        if (type.isInstance(control)) {
            return type.cast(control);
        } else {
            return null;
        }
    }


    public void render() {
        stage.act();
        stage.draw();
        //stage.clear();
    }

    public void dispose() {
        stage.dispose();
    }


    public void clear() {
        stage.clear();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

}

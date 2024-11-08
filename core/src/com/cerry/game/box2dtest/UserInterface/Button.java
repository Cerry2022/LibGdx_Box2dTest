package com.cerry.game.box2dtest.UserInterface;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Button extends UiManager implements ControlBase{
    public TextButton button;
    private int state;
    private boolean toggleEnable = false;

    private final Drawable button_down = buttonStyle.down;
    private final Drawable button_up = buttonStyle.up;

    public Button(String str, float x, float y, float width, float hight,  int initialState, boolean ToggleEnable) {
        super();
        button = new TextButton(str, buttonStyle);
        button.setPosition(x, y);
        button.setSize(width, hight);
        stage.addActor(button);

        state = initialState;// 设置状态

        this.toggleEnable = ToggleEnable;
    }

    public int getState() {
        return state;
    }

    public void setState(int newState) {
        state = newState;
        if(toggleEnable){
            if(state == 0){
                buttonStyle.down = button_down;
                buttonStyle.up = button_up;
                button.setStyle(buttonStyle);
            }
            else{
                buttonStyle.down = button_up;
                buttonStyle.up = button_down;
                button.setStyle(buttonStyle);
            }
        }
    }



}
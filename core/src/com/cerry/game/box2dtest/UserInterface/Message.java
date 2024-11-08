package com.cerry.game.box2dtest.UserInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Message extends UiManager implements ControlBase {
    Label label;

    private int state;

    public Message(String str, float x, float y, Color color, float size){
        label = new Label(str,labelStyle);
        label.setColor(color);
        label.setSize(64,64);
        label.setFontScale(size);
        label.setPosition(x,y);
        stage.addActor(label);
    }

    public int getState() {
        return state;
    }

    public void setState(int newState) {
        state = newState;
    }


    public void setText(String str){
        label.setText(str);
    }
}

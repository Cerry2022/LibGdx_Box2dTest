package com.cerry.game.box2dtest;
public interface StateManager {
    void addState(String name, Object state);
    void setState(String name, Object state);
    Object getState(String name);
}
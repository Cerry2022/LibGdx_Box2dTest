package com.cerry.game.box2dtest;
import java.util.HashMap;
import java.util.Map;

public class SimpleStateManager implements StateManager {
    private final Map<String, Object> states = new HashMap<>();

    @Override
    public void addState(String name, Object state) {
        states.put(name, state);
    }
    @Override
    public void setState(String name, Object state) {
        states.put(name, state);
    }

    @Override
    public Object getState(String name) {
        return states.get(name);
    }
}
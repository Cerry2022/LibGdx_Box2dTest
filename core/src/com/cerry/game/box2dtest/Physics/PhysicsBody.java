package com.cerry.game.box2dtest.Physics;

import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsBody {
    public Body[] fruitBodies;
    public String[] names;

    public PhysicsBody(Body[] fruitBodies, String[] names) {
        this.fruitBodies = fruitBodies;
        this.names = names;
    }
}

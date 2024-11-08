package com.cerry.game.box2dtest.tool;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class SmoothDamp {
    private float vX;
    private float vY;
    private final float smothFactor1;
    private final float  smothFactor2;
    private final float dampingFactor;
    public SmoothDamp(float smothFactor1,float smothFactor2,float dampingFactor){
        vX = 0;
        vY = 0;
        this.smothFactor1 = smothFactor1;
        this.smothFactor2 = smothFactor2;
        this.dampingFactor = dampingFactor;
    }
    public float Update(float position, float Offset, float deltaTime){
        vX += smothFactor1 * Offset - dampingFactor*vX;
        position += smothFactor2 * vX * deltaTime;
        return position;
    }
    public Vector2 Update(Vector2 position, Vector2 Offset, float deltaTime){
        vX += smothFactor1 * Offset.x - dampingFactor*vX;
        vY += smothFactor1 * Offset.y - dampingFactor*vY;
        position.x += smothFactor2 * vX * deltaTime;
        position.y += smothFactor2 * vY * deltaTime;
        return position;
    }

    public Vector3 Update(Vector3 position, Vector2 Offset, float deltaTime){
        vX += smothFactor1 * Offset.x - dampingFactor*vX;
        //log.logInfo("vX"+vX);
        vY += smothFactor1 * Offset.y - dampingFactor*vY;
        position.x += smothFactor2 * vX * deltaTime;
        position.y += smothFactor2 * vY * deltaTime;
        return position;
    }
}

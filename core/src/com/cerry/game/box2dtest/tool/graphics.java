package com.cerry.game.box2dtest.tool;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import static com.cerry.game.box2dtest.Screens.GameScreen.camera;


public class graphics {
    private static final SmoothDamp smoothDamp1 =
            new SmoothDamp(50000f,0.00052f,0.92f);
            //new SmoothDamp(2000f,0.005f,0.5f);
    //smothFactor1：跟随速度
    //smothFactor2：震荡因子，调大手感滑腻，尽量小一点，同时与跟随速度成正比
    //dampingFactor：阻尼，不宜太小，会造成滑脱
    private static final SmoothDamp smoothDamp2 =
            new SmoothDamp(30000f,0.005f,0.05f);
    private static final SmoothDamp smoothDamp3 =
            new SmoothDamp(350f,0.06f,0.8f);

    // 缩放平滑因子
    public static float zoomSmothFactor = 15f;
    // 滑动平滑因子
    private static final float zoomMoveSmothFactor = 20f;


    public static Vector2 touchMoveOffset = new Vector2(0,0);
    public static Vector2 touchZoomOffset = new Vector2(0,0);
    public static float touchScaleOffset = 0;

    static float vx;
    static float vy;


    public static int MoveType;
    public static Vector2 oldZoomVector = new Vector2(0, 1);
    public static Vector2 newZoomVector = new Vector2(0, 1);
    public static Vector2 oldPositionVector = new Vector2(camera.position.x,camera.position.y);
    public static Vector2 newPositionVector = new Vector2();

    public static void check(){
        // 使用线性内插，起到平顺过度的效果
        if (MoveType == 0) {
            //类型0：鼠标滚轮缩放
            oldZoomVector.lerp(newZoomVector, zoomSmothFactor * Math.min(0.05f, Gdx.graphics.getDeltaTime() ));
            if (newPositionVector.x != 0 || newPositionVector.y != 0) {
                oldPositionVector.lerp(newPositionVector, zoomMoveSmothFactor * Math.min(0.06f, Gdx.graphics.getDeltaTime()));
            }
            camera.position.x = oldPositionVector.x;
            camera.position.y = oldPositionVector.y;
            camera.zoom = oldZoomVector.y;
        } else if (MoveType == 1) {
            //类型1：触摸移动
            camera.position.set(smoothDamp1.Update(camera.position,touchMoveOffset,Gdx.graphics.getDeltaTime()));

        } else if (MoveType == 2) {
            //类型2：触摸缩放
            camera.zoom = smoothDamp2.Update(camera.zoom,touchScaleOffset,Gdx.graphics.getDeltaTime());
            //camera.position.set(smoothDamp2.Update(camera.position,touchZoomOffset,Gdx.graphics.getDeltaTime()));
        }
    }
}

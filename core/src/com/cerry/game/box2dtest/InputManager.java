package com.cerry.game.box2dtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.cerry.game.box2dtest.Physics.PhysicsWorld;

import com.cerry.game.box2dtest.tool.*;
import java.lang.Math;

import static com.cerry.game.box2dtest.Config.*;
import static com.cerry.game.box2dtest.Screens.GameScreen.*;
import static com.cerry.game.box2dtest.Physics.PhysicsThreadManager.sharedObject;
import static com.cerry.game.box2dtest.tool.graphics.*;

public class InputManager implements InputProcessor {


    PhysicsWorld physicsWorld;
    final World world;
    Body ground;


    static final public MouseJoint[] mouseJoint = new MouseJoint[4];
    public Vector3[] touchPoint = new Vector3[2];
    public int[] touchType = new int[4];
    Vector3 zoomCenterPoint = new Vector3();
    float zoomScale = 1.5f;


    public InputManager(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
        this.world = physicsWorld.world;
        this.ground = PhysicsWorld.getGround();
    }

    /*-----------------------屏幕接口方法------------------------*/
    //methods omitted for clarity
    /*-----------------------屏幕接口方法------------------------*/

    /*---------------------InputProcessor接口方法------------------*/
    @Override
    public boolean keyDown(int keycode) {
        MoveType = 1;
        switch (keycode)
        {
            case Keys.UP:
                camera.translate(0,2);
                break;
            case Keys.DOWN:
                camera.translate(0,-2);
                break;
            case Keys.LEFT:
                camera.translate(-2,0);
                break;
            case Keys.RIGHT:
                camera.translate(2,0);
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    /*-----------------------触摸屏幕方法------------------------*/
    int touchCount0 = 0;    //触摸类型0计数
    float cpOldX;           //触摸屏幕时，相机的世界x坐标
    float cpOldY;           //触摸屏幕时，相机的世界y坐标
    float touchScaleOld;    //触摸屏幕时，根据spacing()计算而出的两指间的距离
    float zoomOld;          //触摸屏幕时，相机的缩放





    /**
     * @描述: 屏幕触摸或鼠标按下事件
     * @作者: Cerry2022
     * @日期: 2024-07-31 11:57
     * @参数: [screenX, screenY, pointer, button]
     * @返回: boolean
     **/
    @Override
    public boolean touchDown(int screenX, int screenY, final int pointer, int button) {

        log.logInfo("InputManager", "Bug:touchDown");
        log.logInfo("InputManager", "Fps:"+Gdx.graphics.getFramesPerSecond());

        int tType = physicsWorld.isTouchAnObject(screenX,screenY,pointer);// 获取触摸情况

        if(tType != 0){
            // 触碰到了物体的情况
            if (tType == 1) {
                touchType[pointer] = 1;
            }
        }
        else if (pointer < 4){
            // 没有触碰物体的情况
            touchType[pointer] =0;
            if (touchCount0 < 1) {
                //拖动
                touchCount0 ++;
                touchPoint[0] = new Vector3(screenX,screenY,0);
                cpOldX = camera.position.x;
                cpOldY = camera.position.y;
            } else if (touchCount0 < 2) {
                //缩放
                touchCount0 ++;
                touchPoint[1] = new Vector3(screenX,screenY,0);
                touchScaleOld = spacing(touchPoint[0].x,touchPoint[1].x,touchPoint[0].y,touchPoint[1].y);
                zoomOld = camera.zoom;
            }
        }

        return true;
    }
    /*-----------------------触摸屏幕方法------------------------*/



    /*-----------------------触摸释放方法------------------------*/
    @Override
    public boolean touchUp(int screenX, int screenY, final int pointer, int button) {

        log.logInfo("InputManager", "Bug:touchUp");
        ///log.logInfo("touchUp");
        // 鼠标关节，不再使用时要销毁
        if (touchType[pointer] == 1) {
            if (mouseJoint[pointer] != null) {

                final MouseJoint tempJoint = mouseJoint[pointer];
                sharedObject.addMethod(new Runnable() {
                    @Override
                    public void run() {
                        log.logInfo("InputManager", "销毁");
                        world.destroyJoint(tempJoint);


                    }
                });

                mouseJoint[pointer] = null;
            }
        }
        else if (touchType[pointer] == 0) {
            oldPositionVector.set(camera.position.x,camera.position.y);
            //触摸类型为0，直接销毁全部0类型
            touchCount0 = 0;
            //清空偏移，结束平滑函数
            touchMoveOffset.set(0,0);
            touchScaleOffset = 0;
        }
        return true;
    }
    /*-----------------------触摸释放方法------------------------*/


    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }




    /*-----------------------触摸更新方法------------------------*/
    /**touchDragged() 方法期间增量目标的临时向量**/
    Vector2 target = new Vector2();

    float minWidth = Math.min(Gdx.graphics.getHeight(),Gdx.graphics.getWidth());
    float my;
    float touchScaleNew;
    Vector3 temp_Point = new Vector3();
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        my = MIN_WORLD_WIDTH/minWidth*camera.zoom;

        //log.logInfo("touchDragged:"+pointer);
        camera.unproject(temp_Point.set(screenX, screenY, 0));
        if (touchType[pointer] == 1) {
            if (mouseJoint[pointer] != null){
                mouseJoint[pointer].setTarget(target.set(temp_Point.x, temp_Point.y));
            }
        } else if (touchType[pointer] == 0) {
            if (touchCount0 == 1) {
                MoveType = 1;
                //偏移，目标点坐标与相机坐标的差值
                //log.logInfo("s-t:"+(cpOldX-my*(screenX - touchPoint[0].x)-camera.position.x));
                touchMoveOffset.set((cpOldX-my*(screenX - touchPoint[0].x)-camera.position.x)
                        ,(cpOldY+my*(screenY - touchPoint[0].y)-camera.position.y));
            } else if (touchCount0 == 2) {
                MoveType = 2;
                if (pointer<2) {
                    touchPoint[pointer].set(screenX, screenY, 0);
                    touchScaleNew = spacing(touchPoint[0].x, touchPoint[1].x, touchPoint[0].y, touchPoint[1].y);
                    //偏移Scale，当前Scale与目标Scale的差值
                    touchScaleOffset = zoomOld / (touchScaleNew / touchScaleOld) - camera.zoom;
                }
            }
        }

        return true;
    }
    /*-----------------------触摸更新方法------------------------*/


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        camera.unproject(zoomCenterPoint.set(screenX, screenY, 0));
        //log.logInfo("zcp.xy:"+zoomCenterPoint.x+","+zoomCenterPoint.y);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        MoveType = 0;
        oldPositionVector.set(camera.position.x,camera.position.y);
        float dx = (camera.position.x - zoomCenterPoint.x)*(zoomScale-1);
        float dy = (camera.position.y - zoomCenterPoint.y)*(zoomScale-1);
        if (amountY > 0) {
            //缩小
            newZoomVector.y = camera.zoom*zoomScale;
            newPositionVector.set(camera.position.x + dx,camera.position.y + dy);
        } else if (amountY < 0) {
            //放大
            newZoomVector.y = camera.zoom/zoomScale;
            newPositionVector.set(camera.position.x - dx,camera.position.y - dy);
        }
        return true;
    }

    /*---------------------end InputProcessor 接口-----------------------*/

    float spacing(float x1,float x2,float y1,float y2){
        float x = x1-x2;
        float y = y1-y2;
        return (float) Math.sqrt(x*x+y*y);
    }
}
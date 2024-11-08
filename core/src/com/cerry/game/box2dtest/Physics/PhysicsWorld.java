package com.cerry.game.box2dtest.Physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.cerry.game.box2dtest.SpriteManager;
import com.cerry.game.box2dtest.UserInterface.Button;
import com.cerry.game.box2dtest.UserInterface.Message;
import com.cerry.game.box2dtest.tool.log;

import java.util.ArrayList;

import static com.cerry.game.box2dtest.Config.*;
import static com.cerry.game.box2dtest.InputManager.mouseJoint;
import static com.cerry.game.box2dtest.Physics.PhysicsThreadManager.sharedObject;
import static com.cerry.game.box2dtest.Screens.GameScreen.*;


public class PhysicsWorld {
    private Box2DDebugRenderer debugRenderer;
    private final SpriteBatch batch;
    public World world;
    private static Body ground;
    private PhysicsBody bodies;
    private final BodyBuilder bodyBuilder;

    float accumulator = 0;

    Pixmap pixmap;
    Sprite groundSprite;


    Body tempBody;
    ArrayList<Fixture> foundFixtures = new ArrayList<>();

    public PhysicsWorld(SpriteBatch batch) {
        this.batch = batch;
        this.world = new World(new Vector2(0, -9.87f), true);
        //物体形状
        Body[] fruitBodies = new Body[COUNT];
        String[] names = new String[COUNT];
        this.bodyBuilder = new BodyBuilder(world, fruitBodies, names);
    }

    public static Body getGround() {
        return ground;
    }

    public void create() {
        // 初始化Box2D物理引擎
        Box2D.init();

        // 创建一个像素图对象
        pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        // 设置像素图颜色为浅灰色
        pixmap.setColor(0.8f, 0.8f, 0.8f, 1f); //#666666
        // 在像素图上填充一个矩形
        pixmap.fillRectangle(0, 0, 1, 1);
        // 使用像素图创建纹理对象
        Texture pixmaptex = new Texture(pixmap);

        // 创建地面精灵对象
        groundSprite = new Sprite(pixmaptex);
        // 设置地面精灵对象大小
        groundSprite.setSize(MIN_WORLD_WIDTH * 15.45f * 2 * 1.789f, 4f);
        // 设置地面精灵对象位置
        groundSprite.setPosition(MIN_WORLD_WIDTH * (0.5f - 15.45f) * 1.789f, -4f + 0.1f);

        // 生成水果物体
        bodies = bodyBuilder.generateFruit();
        // 创建地面物体
        ground = bodyBuilder.createGround();

        // 关闭自动清除力
        world.setAutoClearForces(false);
        // 关闭连续物理模拟
        world.setContinuousPhysics(false);
        // 关闭热启动
        world.setWarmStarting(false);

        // 输出日志信息
        log.logInfo("PhysicsWorld", "ground_create:" + ground);
    }

    public void dispose() {
        //debugRenderer.dispose();
        pixmap.dispose();
    }


    public void render (){
        for (int i = 0; i < bodies.fruitBodies.length; i++) {
            Body body = bodies.fruitBodies[i];
            if(body != null) {
                String name = bodies.names[i];
                Vector2 position = body.getPosition();
                float degrees = (float) Math.toDegrees(body.getAngle());
                drawSprite(name, position.x, position.y, degrees);
            }
        }

        drawGround();
    }

    public void debugRenderer (){
        debugRenderer.render(world, camera.combined);
    }

    public void stepWorld() {

        accumulator += Math.min(Gdx.graphics.getDeltaTime(), 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;

            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }

    /**
     * @描述: 定义一个方法用于绘制精灵
     * @作者: Cerry2022
     * @日期: 2024-07-31 12:01
     * @参数: [name, x, y, degrees]
     * @返回: void
     **/
    private void drawSprite(String name, float x, float y, float degrees) {
        // 从SpriteManager中获取指定名称的精灵对象
        Sprite sprite = SpriteManager.sprites.get(name);
        // 设置精灵的位置为(x, y)
        sprite.setPosition(x, y);
        // 设置精灵的旋转角度为degrees
        sprite.setRotation(degrees);
        // 设置精灵的旋转中心为左上角
        sprite.setOrigin(0f, 0f);
        // 在批处理器上绘制该精灵
        sprite.draw(batch);
    }


    private void drawGround() {
        //log.logInfo("width:"+groundSprite.getWidth());
        groundSprite.draw(batch);
    }


    Message message5;
    Button button1;
    Button button2;
    Button button3;
    public int isTouchAnObject(int screenX, int screenY, int pointer) {

        // 使用log对象记录信息，标签为"InputManager"，内容为"pointer:"+pointer
        log.logInfo("InputManager", "pointer:" + pointer);
        // 设置testPoint对象的坐标为(screenX, screenY, 0)
        testPoint.set(screenX, screenY, 0);
        // 将testPoint对象的屏幕坐标转换为世界坐标，使用camera对象的unproject方法
        camera.unproject(testPoint);


        foundFixtures.clear();
        tempBody = null;
        //询问世界上哪些物体与鼠标坐标周围的给定边界框重合
        world.QueryAABB(callback, testPoint.x, testPoint.y, testPoint.x, testPoint.y);
        //log.logInfo("touchDownBody:"+tempBody);
        // 如果我们击中某物，我们会创建一个新的鼠标关节并将其连接到被击中的身体上

        if (!foundFixtures.isEmpty()) {
            Fixture firstFixture = foundFixtures.get(0);
            tempBody = firstFixture.getBody();
            // 对第一个物体执行操作
            // ...
        } else {
            // 处理列表为空的情况
        }

        if (tempBody != null && pointer < 4) {
            //触碰到了物体的情况
            button1 = uiManager.getControl("button1", Button.class);
            button2 = uiManager.getControl("button2", Button.class);
            button3 = uiManager.getControl("button3", Button.class);
            message5 = uiManager.getControl("message5", Message.class);

            if (button3.getState() == 1){
                //焊接关节
                weldJointEvent(testPoint);
                return 4;
            }
            else if (button2.getState() == 1 || button2.getState() == 2){
                //绳索关节
                RopeJointEvent(testPoint);
                return 3;
            }
            else if (button1.getState() == 1){
                //旋转关节
                revoluteJointEvent(testPoint);
                return 2;
            }
            else {
                //拖动对象事件
                DragObjectEvent(pointer);
                return 1;
            }
        }
        return 0;
    }


    /*----------
    /*DragObjectEvent(final int pointer)
    /*拖动物体事件
    /*
    ----------*/
    void DragObjectEvent(final int pointer) {

        // 创建一个鼠标关节定义对象，并设置关节连接的主体、是否允许碰撞、目标位置和最大作用力
        final MouseJointDef def = new MouseJointDef();
        def.bodyA = ground;
        def.bodyB = tempBody;
        def.collideConnected = true;
        def.target.set(testPoint.x, testPoint.y);
        def.maxForce = 3000.0f * tempBody.getMass();
        /*----------
        /*通过共享类，传递方法
        /*实现线程数据共享
        /*需要注意，传过去的数据必须为final类型
        ----------*/
        sharedObject.addMethod(new Runnable() {
            @Override
            public void run() {
                log.logInfo("InputManager", "添加");

                mouseJoint[pointer] = (MouseJoint) world.createJoint(def);
            }
        });

    }




    /**
     * @描述: 添加焊接关节事件
     * @作者: Cerry2022
     * @日期: 2024-07-31 12:02
     * @参数: [point]
     * @返回: void
     **/
    void weldJointEvent(Vector3 point){

        if(foundFixtures.size()<2) {
            message5.setText("提示：这里不可以");
            //simpleStateManager.setState("button3",0);
        }
        else{
            final WeldJointDef weldJointDef = new WeldJointDef();
            Body bodyA = foundFixtures.get(0).getBody();
            Body bodyB = foundFixtures.get(1).getBody();

            weldJointDef.initialize(bodyA, bodyB, new Vector2(point.x, point.y)); // 设置焊接关节的锚点

            message5.setText("焊接：添加成功");
            button3.setState(0);

            sharedObject.addMethod(new Runnable() {
                @Override
                public void run() {
                    log.logInfo("InputManager", "添加");
                    // 创建焊接关节
                    world.createJoint(weldJointDef);
                }
            });
        }
    }



    /**
     * @描述: 添加旋转关节事件
     * @作者: Cerry2022
     * @日期: 2024-07-31 12:02
     * @参数: [point]
     * @返回: void
     **/
    void revoluteJointEvent(Vector3 point){

        if(foundFixtures.size()<2) {
            message5.setText("提示：这里不可以");
        }
        else{
            final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
            Body bodyA = foundFixtures.get(0).getBody();
            Body bodyB = foundFixtures.get(1).getBody();
            revoluteJointDef.initialize(bodyA, bodyB, new Vector2(point.x, point.y));

            message5.setText("轮子：添加成功");
            button1.setState(0);

            sharedObject.addMethod(new Runnable() {
                @Override
                public void run() {
                    log.logInfo("InputManager", "添加");
                    // 创建焊接关节
                    world.createJoint(revoluteJointDef);
                }
            });
        }
    }


    /**
     * @描述: 添加绳索关节事件
     * @作者: Cerry2022
     * @日期: 2024-07-31 12:02
     * @参数: [point]
     * @返回: void
     **/
    void RopeJointEvent(Vector3 point) {

    }



    /*-----------------------------辅助方法-------------------------------*/
    /*Android屏幕触摸矢量鼠标关节android screen touch vector for a mouse joint*/
    Vector3 testPoint = new Vector3(); //we instantiate this vector and the callback here, so we don't irritate the GC
    //定义了一个匿名内部类，实现了QueryCallback接口。
    //这是Java中定义回调函数的常见方法。
    // 这个callback的目的是在物理模拟中查询fixture时执行操作。

    QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture (Fixture fixture) {
            // if the hit fixture's body is the ground body
            // we ignore it

            log.logInfo("InputManager", "Bug:callback");
            if (fixture.getBody() == ground) return true;
            if (fixture.testPoint(testPoint.x, testPoint.y)) {
                //tempBody = fixture.getBody();
                foundFixtures.add(fixture);
            }
            return true;
        }
    };



    /*------------------------end 辅助方法-------------------------------*/

}

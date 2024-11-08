package com.cerry.game.box2dtest.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.cerry.game.box2dtest.Box2dTest;
import com.cerry.game.box2dtest.InputManager;
import com.cerry.game.box2dtest.Physics.PhysicsThreadManager;
import com.cerry.game.box2dtest.Physics.PhysicsWorld;
import com.cerry.game.box2dtest.SpriteManager;
import com.cerry.game.box2dtest.UserInterface.Button;
import com.cerry.game.box2dtest.UserInterface.Message;
import com.cerry.game.box2dtest.UserInterface.UiManager;
import com.cerry.game.box2dtest.tool.graphics;
import com.cerry.game.box2dtest.tool.log;

import static com.cerry.game.box2dtest.Config.*;
import static com.cerry.game.box2dtest.UserInterface.UiManager.stage;

public class GameScreen implements Screen {

    private final Box2dTest game;

    //相机和视口
    public static OrthographicCamera camera;
    public static ExtendViewport viewport;

    //精灵图集及管线
    private SpriteBatch batch;
    private SpriteManager spriteManager;

    //物理世界
    private PhysicsWorld physicsWorld;

    //物理进程
    private PhysicsThreadManager physicsThreadManager; // the physics thread object
    private Thread physicsThread; // the thread object

    // 文字
    public static UiManager uiManager;

    private static Message message1,message2,message3,message4,message5;
    private static Button button1,button2,button3, menuButton, pauseButton;


    public GameScreen(Box2dTest game) {
        this.game = game;
    }


    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);

        //Gdx.graphics.setForegroundFPS(120);
        log.logInfo("Screen", "GL_version:" + Gdx.graphics.getGLVersion().getMajorVersion());
        //Gdx.graphics.setVSync(true);

        //log.logInfo ("viewport:"+(OrthographicCamera) viewport.getCamera());
        //log.logInfo ("camara:"+camera);
        batch = new SpriteBatch();//精灵batch批处理在这里声明

        uiManager = new UiManager();//Gui文字管理器


        physicsWorld = new PhysicsWorld(batch);//物理世界管理器

        physicsThreadManager = new PhysicsThreadManager(physicsWorld); // 创建物理进程对象

        physicsThread = new Thread(physicsThreadManager); //初始化进程

        spriteManager = new SpriteManager();//精灵管理器

        spriteManager.addSprites();
        physicsWorld.create();
        physicsThread.start(); //启动进程


//        simpleStateManager = new SimpleStateManager();//状态管理器

        //输入选择器
        InputMultiplexer multiplexer = new InputMultiplexer();
        final InputProcessor gameInputProcessor = new InputManager(physicsWorld);//初始化游戏输入管理器

        //先添加ui输入管理器，才能先触发ui输入事件
        //在 InputMultiplexer 中，事件会按照添加处理器的先后顺序传递给处理器，
        //直到某个处理器处理了该事件（返回 true），然后停止传递给后续的处理器。

        multiplexer.addProcessor(stage);//添加ui输入管理器
        multiplexer.addProcessor(gameInputProcessor);//添加游戏输入管理器

        Gdx.input.setInputProcessor(multiplexer);


        //界面文本以及按钮
        message1 = uiManager.addControl("message1", new Message("LibGDX-Box2D-Test-0.12", 40, Gdx.graphics.getHeight() - 50, Color.BLACK, 2));
        message2 = uiManager.addControl("message2", new Message("鲨雕游戏", 40, Gdx.graphics.getHeight() - 80, Color.BLACK, 2));
        message3 = uiManager.addControl("message3", new Message("FPS:" + Gdx.graphics.getFramesPerSecond(), Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 50, Color.BLACK, 2));
        message4 = uiManager.addControl("message4", new Message("PhysicsFPS:" + physicsThreadManager.GetPhysicsFps(), Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 80, Color.BLACK, 2));
        message5 = uiManager.addControl("message5", new Message("提示：等待按钮", Gdx.graphics.getWidth() - 300, 120, Color.BLACK, 2));

        menuButton = uiManager.addControl("menuButton", new Button("菜单", 20, 20, 80, 80, 0, false));
        menuButton.button.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 处理按钮1的点击事件
                log.logInfo("Screen", "按钮菜单被点击了");
                switch (menuButton.getState()) {
                    case 0:
                        message5.setText("提示：按钮菜单被点击了");
                        break;
                    case 1:
                        message5.setText("提示：等待按钮");
                        break;
                }
            }
        });

        pauseButton = uiManager.addControl("pauseButton", new Button("暂停", 120, 20, 80, 80, 0, true));
        pauseButton.button.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 处理按钮1的点击事件
                log.logInfo("Screen", "按钮暂停被点击了");
                Array<Body> bodies = new Array<>();
                physicsWorld.world.getBodies(bodies);
                switch (pauseButton.getState()) {
                    case 0:
                        message5.setText("提示：游戏暂停");
                        pauseButton.setState(1);
                        spriteManager.pause(bodies);
                        break;
                    case 1:
                        message5.setText("提示：等待按钮");
                        pauseButton.setState(0);
                        spriteManager.unPause(bodies);
                        break;
                }
            }
        });

        button1 = uiManager.addControl("button1", new Button("轮子", Gdx.graphics.getWidth() - 100, 20, 80, 80, 0, true));
        button2 = uiManager.addControl("button2", new Button("绳子", Gdx.graphics.getWidth() - 200, 20, 80, 80, 0, true));
        button3 = uiManager.addControl("button3", new Button("焊接", Gdx.graphics.getWidth() - 300, 20, 80, 80, 0, true));
        button1.button.addListener(new ClickListener() {
	            @Override
	            public void clicked(InputEvent event, float x, float y) {
	                // 处理按钮1的点击事件
	                log.logInfo("Screen","按钮轮子被点击了");
	                switch (button1.getState()) {
	                    case 0:
	                        message5.setText("轮子：请点击物体1/1");
	                        button1.setState(1);
	                        button2.setState(0);
	                        button3.setState(0);
	                        break;
	                    case 1:
	                        message5.setText("提示：等待按钮");
	                        button1.setState(0);
	                        break;
	                }
	            }
	        });


        button2.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 处理按钮2的点击事件
                log.logInfo("Screen", "按钮绳子被点击了");
                switch (button2.getState()) {
                    case 0:
                        message5.setText("绳子：请点击物体1/2");
                        button1.setState(0);
                        button2.setState(1);
                        button3.setState(0);
                        break;
                    case 1:
                        message5.setText("绳子：请点击物体2/2");
                        button2.setState(2);
                        break;
                    case 2:
                        message5.setText("提示：等待按钮");
                        button2.setState(0);
                        break;
                }
            }
        });
        button3.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 处理按钮3的点击事件
                log.logInfo("Screen", "按钮焊接被点击了");
                switch (button3.getState()) {
                    case 0:
                        message5.setText("焊接：请点击物体1/1");
                        button1.setState(0);
                        button2.setState(0);
                        button3.setState(1);
                        break;
                    case 1:
                        message5.setText("提示：等待按钮");
                        button3.setState(0);
                        break;
                }
            }
        });


    }

    @Override
    public void render(float delta) {

        //batch.enableBlending();
        ScreenUtils.clear(0.4f, 0.6f, 0.8f, 1);//#6699CC
        //ScreenUtils.clear(0f, 0f, 0f, 1);

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        physicsWorld.render();
        batch.end();



        message3.setText("FPS:" + Gdx.graphics.getFramesPerSecond());
        message4.setText("PhysicsFPS:" + physicsThreadManager.GetPhysicsFps());
        uiManager.render();

        //log.logInfo("T1_DeltaTime:" + Gdx.graphics.getDeltaTime());

        graphics.check();//线性内插，使过度平滑
        //physicsWorld.stepWorld();
        camera.update();
        //physicsWorld.debugRenderer();

    }

    @Override
    public void dispose() {

        batch.dispose();
        uiManager.dispose();
        spriteManager.dispose();
        physicsThreadManager.dispose(); //停止物理线程
        try {
            physicsThread.join(); //等待线程完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resize(int width, int height) {
        //super.resize(width, height);
        viewport.update(width, height, false);

        uiManager.resize(width, height);

        //physicsWorld.resize();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
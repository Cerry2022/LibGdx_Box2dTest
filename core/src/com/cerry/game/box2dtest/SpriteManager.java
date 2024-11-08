package com.cerry.game.box2dtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.cerry.game.box2dtest.tool.log;

import java.util.HashMap;

import static com.cerry.game.box2dtest.Config.*;
import static com.cerry.game.box2dtest.Physics.PhysicsThreadManager.sharedObject;


public class SpriteManager {
    // 精灵哈希表
    public static final HashMap<String, Sprite> sprites = new HashMap<>();
    TextureAtlas textureAtlas;

    public SpriteManager() {
        textureAtlas = new TextureAtlas("sprites.txt");
    }
    public void addSprites() {
        // 设置过滤模式为临近过滤

        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);

        Array<TextureAtlas.AtlasRegion> regions = textureAtlas.getRegions();
        for (TextureAtlas.AtlasRegion region : regions) {
            Sprite sprite = new Sprite(region);
            float width = sprite.getWidth() * SCALE;
            float height = sprite.getHeight() * SCALE;
            sprite.setSize(width, height);
            sprite.setOrigin(0, 0);
            sprites.put(region.name, sprite);
        }
    }


    public void pause(final Array<Body> bodies){

        sharedObject.addMethod(new Runnable() {
            @Override
            public void run() {
                log.logInfo("SpriteManager", "设置所有对象为非活动状态");
                for(int i = 0; i<bodies.size;i++){

                    //创建一个不会与任何物体发生碰撞的filter
                    //如果我直接修改get到的filter的话，会交给下一个物理帧处理
                    //然而，下一个物理帧会先处理物体的碰撞情况，再处理我提交的filter，然后他们不可避免地发生碰撞了
                    //所以先创建一个filter，再将所有物体的filter设置为这个filter，这个修改会发生在下一个物理帧碰撞检测前
                    bodies.get(i).setAngularDamping(20);
                    bodies.get(i).setLinearDamping(20);
                    Filter filter = new Filter();
                    filter.groupIndex = -1;//设置类别为-1，表示统一组别的物体不发生碰撞
                    Array<Fixture> fixtureList = bodies.get(i).getFixtureList();
                    int count = fixtureList.size;
                    for(int t = 0; t < count ;t++) {
                        //bodies.get(i).getFixtureList().get(t).getFilterData().maskBits = 0;//不与任何物体碰撞
                        //bodies.get(i).getFixtureList().get(t).getFilterData().categoryBits = 0;//不与任何物体碰撞
                        fixtureList.get(t).setFilterData(filter);
                        //bodies.get(i).getFixtureList().get(t).getFilterData().groupIndex = -1;
                    }
                    bodies.get(i).setAwake(false);
                    bodies.get(i).setGravityScale(0.0f);
                }
            }
        });
    }

    public void unPause(final Array<Body> bodies){
        sharedObject.addMethod(new Runnable() {
            @Override
            public void run() {
                log.logInfo("SpriteManager", "设置所有对象为活动状态");
                for(int i = 0; i<bodies.size;i++){
                    bodies.get(i).setAngularDamping(0);
                    bodies.get(i).setLinearDamping(0);
                    Filter filter = new Filter();
                    filter.groupIndex = 1;//设置类别为1，表示统一组别的物体会发生碰撞
                    Array<Fixture> fixtureList = bodies.get(i).getFixtureList();
                    int count = fixtureList.size;
                    for(int t = 0; t < count ;t++) {
                        //bodies.get(i).getFixtureList().get(t).getFilterData().maskBits = -1;//与任何物体碰撞
                        //bodies.get(i).getFixtureList().get(t).getFilterData().categoryBits = 1;//与任何物体碰撞
                        fixtureList.get(t).setFilterData(filter);
                    }
                    bodies.get(i).setAwake(true);
                    bodies.get(i).setGravityScale(1.0f);
                }
            }
        });
    }
    public void dispose() {
        textureAtlas.dispose();
    }
}

package com.cerry.game.box2dtest.Physics;

import com.badlogic.gdx.physics.box2d.*;
import com.cerry.game.box2dtest.tool.log;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.util.Random;

import static com.cerry.game.box2dtest.Config.*;
import static com.cerry.game.box2dtest.Physics.PhysicsThreadManager.setStepWorldRunning;

public class BodyBuilder {

    World world;
    Body ground;

    PhysicsShapeCache physicsBodies;
    PhysicsBody bodies;

    public BodyBuilder(World world,Body[] fruitBodies, String[] names) {
        this.world = world;

        bodies = new PhysicsBody(fruitBodies,names);
        this.physicsBodies = new PhysicsShapeCache("physics.xml");

    }

    public PhysicsBody generateFruit() {
        String[] fruitNames = new String[]{"cherries", "orange", "crate", "banana"};//"cherries", "orange","crate"
        Random random = new Random();
        for (int i = 0; i < bodies.fruitBodies.length; i++) {
            String name = fruitNames[random.nextInt(fruitNames.length)];
            float x = (random.nextFloat()-0.5f) * MIN_WORLD_WIDTH*COUNT/400*1.8f;
            float y = random.nextFloat() * MIN_WORLD_HEIGHT*COUNT/200 + MIN_WORLD_HEIGHT*2*0.5f;
            bodies.names[i] = name;
            bodies.fruitBodies[i] = createBody(name, x, y, 0);
        }
        return bodies;
    }


    public Body createBody(String name, float x, float y, float rotation) {
        Body body = physicsBodies.createBody(name, world, SCALE, SCALE);
        body.setTransform(x, y, rotation);
        return body;
    }

    public Body createGround() {
        while (world.isLocked()) setStepWorldRunning(false);

        if (ground != null) world.destroyBody(ground);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 1;
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(MIN_WORLD_WIDTH*15.45f*1.789f, 0.1f);
        fixtureDef.shape = shape;
        ground = world.createBody(bodyDef);
        log.logInfo("BodyBuilder","ground_old:"+ground);

        ground.createFixture(fixtureDef);
        ground.setTransform(MIN_WORLD_WIDTH*0.5f*1.789f, 0, 0);

        shape.dispose();

        return ground;
    }
}
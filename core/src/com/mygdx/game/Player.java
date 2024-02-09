package com.mygdx.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Player extends Actor {
    Rectangle bounds; //
    AssetManager manager;
    float speedy, gravity;
    boolean invincible;
    Player()
    {
        setX(200);
        setY(280 / 2 - 64 / 2);
        setSize(64,45);
        bounds = new Rectangle();

        speedy = 0;
        gravity = 850f;
        invincible = false;
    }
    public void activateInvincibility() {
        invincible = true;
    }
    public void desactivateInvincibility() {
        invincible = false;
    }
    @Override
    public void act(float delta) {
        //Actualitza la posició del jugador amb la velocitat vertical
        moveBy(0, speedy * delta);
        //Actualitza la velocitat vertical amb la gravetat
        speedy -= gravity * delta;
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }
    void impulso()
    {
        speedy = 400f;
    }
    @Override //draw metodo para pintar al actor y llama al stage
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(manager.get("bird.png", Texture.class), getX(), getY());
    }
    public Rectangle getBounds() {
        return bounds;
    }
    public void setManager(AssetManager manager) {
        this.manager = manager;
    }

    public boolean isInvincible() {
        return invincible;
    }
    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

}

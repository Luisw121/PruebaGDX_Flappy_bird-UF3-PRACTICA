package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final Bird game;
    OrthographicCamera camera;
    Stage stage;
    Player player;//actor
    boolean dead;
    Array<Pipe> obstacles;
    long lastObstacleTime;
    float score;
    private float difficulty = 1.0f;
    private float difficultyIncreaseRate = 0.1f;
    // Variables de configuración de la velocidad inicial y el aumento de velocidad
    private float initialObstacleSpeed = 200; // Velocidad inicial de los obstáculos
    private float obstacleSpeedIncreaseRate = 400; // Tasa de aumento de velocidad de los obstáculos por unidad de dificultad

    private float initialPlayerSpeed = 400; // Velocidad inicial del jugador
    private float playerSpeedIncreaseRate = 1000; // Tasa de aumento de velocidad del jugador por unidad de dificultad

    /*
    boolean invincibilityActive;
    private static final float POWER_UP_SPAWN_INTERVAL = 10;
    private long lastPowerUpSpawnTime;
    //private Array<PowerUp> powerUps;
    private Texture powerUpTexture;
    private float powerUpWidth;
    private float powerUpHeight;
     */
    public GameScreen(final Bird gam) {
        this.game = gam;
        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        player = new Player();
        player.setManager(game.manager);
        stage = new Stage();
        stage.getViewport().setCamera(camera);
        stage.addActor(player);

        // create the obstacles array and spawn the first obstacle
        obstacles = new Array<Pipe>();
        spawnObstacle();

        //score = 0;
        //invincibilityActive = false;

        //powerUps = new Array<>();
        //lastPowerUpSpawnTime = TimeUtils.nanoTime();
    }
    /*
    public class PowerUp extends Actor {
        private Rectangle bounds;
        private Texture texture;

        public PowerUp(Texture texture, float x, float y, float widht, float height){
            this.texture = texture;
            this.setBounds(x, y, widht, height);
            this.bounds = new Rectangle(x, y, widht, height);
        }
        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        }
        public Rectangle getBounds() {
            return bounds;
        }
    }
     */

    private void spawnObstacle() {
        // Calcula la alçada de l'obstacle aleatòriament
        float holey = MathUtils.random(50, 230);
        // Crea dos obstacles: Una tubería superior i una inferior
        Pipe pipe1 = new Pipe();
        pipe1.setX(800);
        pipe1.setY(holey - 230);
        pipe1.setUpsideDown(true);
        pipe1.setManager(game.manager);
        obstacles.add(pipe1);
        stage.addActor(pipe1);
        Pipe pipe2 = new Pipe();
        pipe2.setX(800);
        pipe2.setY(holey + 200);
        pipe2.setUpsideDown(false);
        pipe2.setManager(game.manager);
        obstacles.add(pipe2);
        stage.addActor(pipe2);
        lastObstacleTime = TimeUtils.nanoTime();
    }
    /*
    private void checkPowerUpCollision() {
        Iterator<PowerUp> iter = powerUps.iterator();
        while (iter.hasNext()) {
            PowerUp powerUp = iter.next();
            if (player.getBounds().overlaps(powerUp.getBounds())) {
                player.activateInvincibility();
                iter.remove();
            }
        }
    }

    private void managePowerUps() {
        if (TimeUtils.nanoTime() - lastPowerUpSpawnTime > POWER_UP_SPAWN_INTERVAL){
            PowerUp powerUp = new PowerUp(powerUpTexture, MathUtils.random(0, 800), MathUtils.random(0, 480), powerUpWidth, powerUpHeight);
            powerUps.add(powerUp); // Agrega el power-up a la lista de power-ups en el juego
            lastPowerUpSpawnTime = TimeUtils.nanoTime();
        }
    }
    private void gameOver() {
        game.setScreen(new GameOverScreen(game));
    }
    private void checkObstacleCollision() {
        for (Iterator<PowerUp> iter = powerUps.iterator(); iter.hasNext();) {
            PowerUp powerUp = iter.next();
            if (player.getBounds().overlaps(powerUp.getBounds())) {
                player.activateInvincibility();
                iter.remove();
            }
        }
    }
     */

    @Override
    public void render(float delta) {

        //Rednder============================================================
        // clear the screen with a color
        ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch
        game.batch.begin();
        game.batch.draw(game.manager.get("background.png", Texture.class), 0, 0);
        game.batch.end();

        // Stage batch: Actors
        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.draw();

        game.batch.begin();
        game.smallFont.draw(game.batch, "Score: " + (int)score, 10, 470);
        game.batch.end();

        //aumentamos la puntuación con el tiempo
        score += Gdx.graphics.getDeltaTime();

        //actualizamos la dificultat con el tiempo
        difficulty += difficultyIncreaseRate * delta;


        float obstacleSpeed = initialObstacleSpeed + difficulty * obstacleSpeedIncreaseRate;
        float playerSpeed = initialPlayerSpeed + difficulty * playerSpeedIncreaseRate;

        //Actualización ======================================================
        stage.act();

        // process user input
        if (Gdx.input.justTouched()) {
            player.impulso();
            game.manager.get("flap.wav", Sound.class).play();
        }

        // Comprova que el jugador no es surt de la pantalla.
        // Si surt per la part inferior, game over
        if (player.getBounds().y > 480 - 45)
            player.setY( 480 - 45 );
        if (player.getBounds().y < 0 - 45) {
            dead = true;
        }

        // Comprova si cal generar un obstacle nou
        if (TimeUtils.nanoTime() - lastObstacleTime > 1500000000)
            spawnObstacle();
        // Comprova si les tuberies colisionen amb el jugador
        Iterator<Pipe> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getBounds().overlaps(player.getBounds())) {
                dead = true;
            }
        }
        // Treure de l'array les tuberies que estan fora de pantalla
        iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getX() < -64) {
                obstacles.removeValue(pipe, true);
            }
        }
        //managePowerUps();
        //checkPowerUpCollision();
        //checkObstacleCollision();

        if (dead) {
            if (player.isInvincible()) {
                player.setColor(Color.RED);
            } else {
                // Restaura la apariencia visual normal del jugador
                player.setColor(Color.BLACK); // Restaura el color del jugador a negro (o su color original)
            }
            game.manager.get("fail.wav", Sound.class).play();
            game.lastScore = (int)score;
            if(game.lastScore > game.topScore)
                game.topScore = game.lastScore;

            game.setScreen(new GameOverScreen(game));
            dispose();
        }

    }
    @Override
    public void resize(int width, int height) {
    }
    @Override
    public void show() {
    }
    @Override
    public void hide() {
    }
    @Override
    public void pause() {
    }
    @Override
    public void resume() {
    }
    @Override
    public void dispose() {
    }
}
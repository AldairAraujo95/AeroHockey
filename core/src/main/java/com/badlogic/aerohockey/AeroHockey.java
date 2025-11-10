package com.badlogic.aerohockey;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Color; // <-- IMPORT ADICIONADO

public class AeroHockey extends ApplicationAdapter {

    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 450f;
    private static final float PLAYER_SPEED = 320f; // Velocidade dos jogadores
    private static final float DISCO_SPEED = 450f; // Velocidade do disco

    private SpriteBatch batch;
    private Texture arena;
    private Texture disco;
    private Texture rebatedor;

    private OrthographicCamera camera;
    private Viewport viewport;

    // Posições
    private float discoX, discoY;
    private float p1x, p1y;
    private float p2x, p2y;

    // Física do Disco
    private float discoVelX, discoVelY;

    // Formas de Colisão
    private Circle discoCircle;
    private Circle p1Circle;
    private Circle p2Circle;

    // Placar
    private int scoreP1, scoreP2;
    private BitmapFont font;

    // Lógica do Gol
    private float goalYMin, goalYMax;
    private float goalHeight;

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);


        arena = new Texture(Gdx.files.internal("arena.png"));
        disco = new Texture(Gdx.files.internal("disco.png"));
        rebatedor = new Texture(Gdx.files.internal("rebatedor.png"));

        p1x = WORLD_WIDTH * 0.25f - rebatedor.getWidth() / 2f;
        p1y = WORLD_HEIGHT * 0.5f - rebatedor.getHeight() / 2f;

        p2x = WORLD_WIDTH * 0.75f - rebatedor.getWidth() / 2f;
        p2y = WORLD_HEIGHT * 0.5f - rebatedor.getHeight() / 2f;


        scoreP1 = 0;
        scoreP2 = 0;
        font = new BitmapFont();
        font.setColor(Color.BLACK);


        discoCircle = new Circle();
        p1Circle = new Circle();
        p2Circle = new Circle();


        discoCircle.setRadius(disco.getWidth() / 2f);
        p1Circle.setRadius(rebatedor.getWidth() / 2f);
        p2Circle.setRadius(rebatedor.getWidth() / 2f);


        goalHeight = WORLD_HEIGHT * 0.4f;
        goalYMin = (WORLD_HEIGHT / 2f) - (goalHeight / 2f);
        goalYMax = (WORLD_HEIGHT / 2f) + (goalHeight / 2f);


        resetPuck(false);
    }


    private void resetPuck(boolean servicoP1) {
        discoX = WORLD_WIDTH / 2f - disco.getWidth() / 2f;
        discoY = WORLD_HEIGHT / 2f - disco.getHeight() / 2f;


        if (servicoP1) {
            discoVelX = DISCO_SPEED;
            discoVelX = -DISCO_SPEED;
        }
        discoVelY = MathUtils.random(-50f, 50f);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();


        float nx1 = p1x;
        float ny1 = p1y;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) ny1 += PLAYER_SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) ny1 -= PLAYER_SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) nx1 -= PLAYER_SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) nx1 += PLAYER_SPEED * dt;


        float nx2 = p2x;
        float ny2 = p2y;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) ny2 += PLAYER_SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) ny2 -= PLAYER_SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) nx2 -= PLAYER_SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) nx2 += PLAYER_SPEED * dt;


        ny1 = MathUtils.clamp(ny1, 0, WORLD_HEIGHT - rebatedor.getHeight());
        ny2 = MathUtils.clamp(ny2, 0, WORLD_HEIGHT - rebatedor.getHeight());


        float mid = WORLD_WIDTH / 2f;
        nx1 = MathUtils.clamp(nx1, 0, mid - rebatedor.getWidth());
        nx2 = MathUtils.clamp(nx2, mid, WORLD_WIDTH - rebatedor.getWidth());

        p1x = nx1; p1y = ny1;
        p2x = nx2; p2y = ny2;


        p1Circle.setPosition(p1x + rebatedor.getWidth() / 2f, p1y + rebatedor.getHeight() / 2f);
        p2Circle.setPosition(p2x + rebatedor.getWidth() / 2f, p2y + rebatedor.getHeight() / 2f);



        discoX += discoVelX * dt;
        discoY += discoVelY * dt;
        discoCircle.setPosition(discoX + disco.getWidth() / 2f, discoY + disco.getHeight() / 2f);




        if (Intersector.overlaps(discoCircle, p1Circle)) {

            float angle = MathUtils.atan2(discoCircle.y - p1Circle.y, discoCircle.x - p1Circle.x);

            discoVelX = MathUtils.cos(angle) * DISCO_SPEED;
            discoVelY = MathUtils.sin(angle) * DISCO_SPEED;

            discoX = p1Circle.x + MathUtils.cos(angle) * (p1Circle.radius + discoCircle.radius);
            discoY = p1Circle.y + MathUtils.sin(angle) * (p1Circle.radius + discoCircle.radius);
        }


        if (Intersector.overlaps(discoCircle, p2Circle)) {
            float angle = MathUtils.atan2(discoCircle.y - p2Circle.y, discoCircle.x - p2Circle.x);
            discoVelX = MathUtils.cos(angle) * DISCO_SPEED;
            discoVelY = MathUtils.sin(angle) * DISCO_SPEED;
            discoX = p2Circle.x + MathUtils.cos(angle) * (p2Circle.radius + discoCircle.radius);
            discoY = p2Circle.y + MathUtils.sin(angle) * (p2Circle.radius + discoCircle.radius);
        }




        boolean isGoal = (discoY > goalYMin && discoY < (goalYMax - disco.getHeight()));


        if (discoX < 0) {
            if (isGoal) {
                scoreP2++;
                resetPuck(true);
            } else {
                discoX = 0;
                discoVelX = -discoVelX;
            }
        }
        if (discoX + disco.getWidth() > WORLD_WIDTH) { // Lado direito
            if (isGoal) {
                scoreP1++; // Ponto para o Jogador 1
                resetPuck(false); // Servir para P2
            } else {
                discoX = WORLD_WIDTH - disco.getWidth(); // Bate na parede
                discoVelX = -discoVelX;
            }
        }

        // Paredes Cima e Baixo
        if (discoY < 0) {
            discoY = 0;
            discoVelY = -discoVelY;
        }
        if (discoY + disco.getHeight() > WORLD_HEIGHT) {
            discoY = WORLD_HEIGHT - disco.getHeight();
            discoVelY = -discoVelY;
        }

        // --- DESENHO ---
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Desenha a arena
        batch.draw(arena, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Desenha os jogadores e o disco
        batch.draw(disco, discoX, discoY);
        batch.draw(rebatedor, p1x, p1y); // jogador 1
        batch.draw(rebatedor, p2x, p2y); // jogador 2

         //placar
        font.draw(batch, "P1: " + scoreP1, 20, WORLD_HEIGHT - 20);
        font.draw(batch, "P2: " + scoreP2, WORLD_WIDTH - 100, WORLD_HEIGHT - 20);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {

        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        arena.dispose();
        disco.dispose();
        rebatedor.dispose();
        font.dispose();
    }
}

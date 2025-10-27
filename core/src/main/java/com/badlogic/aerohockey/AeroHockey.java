package com.badlogic.aerohockey;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class AeroHockey extends ApplicationAdapter {

    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 450f;
    private static final float SPEED = 320f;

    private SpriteBatch batch;
    private Texture arena;
    private Texture disco;
    private Texture rebatedor;

    private OrthographicCamera camera;
    private Viewport viewport;


    private float discoX, discoY;
    private float p1x, p1y;
    private float p2x, p2y;
    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);

        arena = new Texture(Gdx.files.internal("arena.png"));
        disco = new Texture(Gdx.files.internal("disco.png"));
        rebatedor = new Texture(Gdx.files.internal("rebatedor.png"));

        discoX = WORLD_WIDTH / 2f - disco.getWidth() / 2f;
        discoY = WORLD_HEIGHT / 2f - disco.getHeight() / 2f;


        p1x = WORLD_WIDTH * 0.25f - rebatedor.getWidth() / 2f;
        p1y = WORLD_HEIGHT * 0.5f - rebatedor.getHeight() / 2f;

        p2x = WORLD_WIDTH * 0.75f - rebatedor.getWidth() / 2f;
        p2y = WORLD_HEIGHT * 0.5f - rebatedor.getHeight() / 2f;
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();


        float nx1 = p1x;
        float ny1 = p1y;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) ny1 += SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) ny1 -= SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) nx1 -= SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) nx1 += SPEED * dt;


        float nx2 = p2x;
        float ny2 = p2y;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    ny2 += SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  ny2 -= SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  nx2 -= SPEED * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) nx2 += SPEED * dt;

        // limites verticais iguais para os dois

        ny1 = MathUtils.clamp(ny1, 0, WORLD_HEIGHT - rebatedor.getHeight());
        ny2 = MathUtils.clamp(ny2, 0, WORLD_HEIGHT - rebatedor.getHeight());

        // limites horizontais por metade da arena

        float mid = WORLD_WIDTH / 2f;
        nx1 = MathUtils.clamp(nx1, 0, mid - rebatedor.getWidth());
        nx2 = MathUtils.clamp(nx2, mid, WORLD_WIDTH - rebatedor.getWidth());

        p1x = nx1; p1y = ny1;
        p2x = nx2; p2y = ny2;

        // desenho
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(arena, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(disco, discoX, discoY);
        batch.draw(rebatedor, p1x, p1y); // jogador 1
        batch.draw(rebatedor, p2x, p2y); // jogador 2
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
    }
}

package com.base.engine.core;

import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.shaders.Shader;
import com.base.engine.rendering.Window;

public class CoreEngine {

    private float m_fpsLimit;
    private boolean m_fpsUnlimited;

    private RenderingEngine m_renderingEngine;

    private Game m_game;
    private boolean m_isRunning;

    public CoreEngine(Game game, float fpsLimit, boolean fpsUnlimited) {
        m_fpsLimit = fpsLimit;
        m_fpsUnlimited = fpsUnlimited;

        m_game = game;
        m_isRunning = false;

        System.out.println("INFO: CoreEngine(fpsLimit = " + fpsLimit + ", fpsUnlimited = " + fpsUnlimited + ") was successfully created");
    }

    public void createWindow(int width, int height, String title) {
        Window.createWindow(width, height, title);

        m_renderingEngine = new RenderingEngine();

        Shader.setRenderingEngine(m_renderingEngine);
        m_game.setRenderingEngine(m_renderingEngine);
    }

    public void start() {
        if (!Window.isCreated()) {
            System.err.println("ERROR: trying to start CoreEngine when Window wasn't created!");
            new Exception().printStackTrace();
            return;
        }

        if (m_isRunning)
            return;

        m_isRunning = true;
        m_game.init();
        run();
    }

    private void run() {
        int frames = 0;
        double fpsTime = 0;
        double fpsRefreshTime = 1.0;

        double startTime = Time.getTime();
        double frameTime = 1.0 / m_fpsLimit;
        double unprocessedTime = 0;

        while (m_isRunning) {
            if (Window.isCloseRequested())
                stop();

            double endTime = Time.getTime();
            double passedTime = endTime - startTime;
            startTime = endTime;

            unprocessedTime += passedTime;
            fpsTime += passedTime;

            boolean render = false;

            if (unprocessedTime >= frameTime) {
                double sceneTime = frameTime * (int)Math.floor(unprocessedTime / frameTime);
                unprocessedTime -= sceneTime;

                render = true;

                m_game.input((float)sceneTime);
                m_game.update((float)sceneTime);
                Input.update();
            }

            if (fpsTime >= fpsRefreshTime) {
                System.out.printf("INFO: %.1f fps\n", frames / fpsTime);
                fpsTime -= fpsRefreshTime;
                frames = 0;
            }

            if (render || m_fpsUnlimited) {
                m_renderingEngine.render(m_game.getRoot());
                frames++;
            } else {
                try {
                    Thread.sleep(0, 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        cleanUp();
    }

    public void stop() {
        if (!m_isRunning)
            return;

        m_isRunning = false;
    }

    private void cleanUp() {
        Window.dispose();
    }

    public float getFPSLimit() {
        return m_fpsLimit;
    }

    public void setFPSLimit(float limit) {
        this.m_fpsLimit = limit;
    }

    public boolean isFPSUnlimited() {
        return m_fpsUnlimited;
    }

    public void setFPSUnlimited(boolean fpsUnlimited) {
        m_fpsUnlimited = fpsUnlimited;
    }
}
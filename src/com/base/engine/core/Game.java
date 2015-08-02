package com.base.engine.core;

import com.base.engine.rendering.RenderingEngine;

import java.util.ArrayList;

public abstract class Game {

    private Node m_root;

    public Game() {
        m_root = new Node("root");
    }

    public void init() {}

    public void input(float time) {
        m_root.input(time);
    }

    public void update(float time) {
        m_root.update(time);
    }

    public void render(RenderingEngine renderingEngine) {
        renderingEngine.render(m_root);
    }

    public Node getRoot() {
        return m_root;
    }
}

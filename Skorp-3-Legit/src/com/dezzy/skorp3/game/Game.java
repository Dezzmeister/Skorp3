package com.dezzy.skorp3.game;

import com.dezzy.skorp3.game.state.GameState;
import com.dezzy.skorp3.game.state.StateUpdateObject;
import com.dezzy.skorp3.messaging.Message;
import com.dezzy.skorp3.messaging.MessageHandlerRegistry;

public class Game implements Runnable {
	private volatile boolean isRunning = false;
	private GameState currentState = GameState.INTRO;
	
	private void init() {
		postNewState();
	}
	
	private void postNewState() {
		StateUpdateObject suo = new StateUpdateObject(currentState, currentState);
		MessageHandlerRegistry.GAME_STATE_HANDLER.dispatch(new Message("STATE_UPDATE", suo));
	}
	
	private void render(float delta) {
		
	}
	
	private void update(float delta) {
		
	}
	
	private static final float FRAMES = 60.0f;
	private static final float NANOS_PER_FRAME = 1E9f / FRAMES;
	
	@Override
	public void run() {
		init();
		long lastTime = System.nanoTime();
		float delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / NANOS_PER_FRAME;
			lastTime = now;
			
			if (delta >= 1.0) {
				update(delta);
				updates++;
				delta--;
			}
			render(delta);
			frames++;
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				updates = 0;
				frames = 0;
			}
		}
	}
}

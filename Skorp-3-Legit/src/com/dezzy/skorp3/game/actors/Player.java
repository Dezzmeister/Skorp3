package com.dezzy.skorp3.game.actors;

import com.dezzy.skorp3.game.math.Vec4;

public class Player {
	private static final float HALF_PI = (float)Math.PI / 2;
	
	public final Vec4 position;
	public float yaw = 0.0f;
	public float pitch = 0.0f;
	
	public Player(final Vec4 initialPosition) {
		position = initialPosition;
	}
	
	public void lookAround(float amount) {
		yaw = (yaw + amount) % ((float)Math.PI * 2);
	}
	
	public void moveTo(final Vec4 newPosition) {
		position.x = newPosition.x;
		position.y = newPosition.y;
		position.z = newPosition.z;
	}
	
	public void moveForward(float amount) {
		position.x += amount * (float)Math.sin(yaw);
		position.z += amount * (float)Math.cos(yaw);
	}
	
	public void moveBackward(float amount) {
		moveForward(-amount);
	}
	
	public void moveRight(float amount) {
		position.x += amount * (float)Math.sin(yaw - HALF_PI);
		position.z += amount * (float)Math.cos(yaw - HALF_PI);
	}
	
	public void moveLeft(float amount) {
		moveRight(-amount);
	}
}

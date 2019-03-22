package com.dezzy.skorp3.game.input;

import java.awt.Point;
import java.awt.Robot;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import com.dezzy.skorp3.game.state.StateUpdateObject;
import com.dezzy.skorp3.logging.Logger;
import com.dezzy.skorp3.messaging.Message;
import com.dezzy.skorp3.messaging.MessageHandlerRegistry;
import com.dezzy.skorp3.messaging.meta.Handles;
import com.dezzy.skorp3.messaging.meta.HandlesFor;

public class MouseHandler implements GLFWCursorPosCallbackI {
	private final Robot robot;
	private boolean robotEnabled;
	private final Point oldCoords;
	private final Point coords;
	private final Point centerCoords;
	
	{
		MessageHandlerRegistry.GAME_STATE_HANDLER.registerCallback(this::handleStateUpdates);
	}
	
	public MouseHandler(final Point startingCoords, final Point _centerCoords) {		
		Robot tempRobot = null;
		
		try {
			tempRobot = new Robot();
		} catch (Exception e) {
			Logger.error("MouseHandler's Robot could not be created.");
			e.printStackTrace(Logger.getLogger());
			e.printStackTrace();
		}
		
		robot = tempRobot;
		centerCoords = _centerCoords;
		
		oldCoords = new Point();
		oldCoords.x = startingCoords.x;
		oldCoords.y = startingCoords.y;
		
		coords = new Point();
		coords.x = startingCoords.x;
		coords.y = startingCoords.y;
	}
	
	@Handles({"STATE_UPDATE"})
	@HandlesFor("Game State")
	private void handleStateUpdates(final Message message) {
		String messageText = message.messageText;
		Object data = message.data;
		
		if (messageText.equals("STATE_UPDATE")) {
			StateUpdateObject suo = (StateUpdateObject) data;
			
			String lockMouse = suo.newState.properties.getProperty("mouse_lock");
			robotEnabled = Boolean.parseBoolean(lockMouse);
		}
	}
	
	@Override
	public void invoke(long window, double xpos, double ypos) {
		oldCoords.x = coords.x;
		oldCoords.y = coords.y;
		
		coords.x = (int) xpos;
		coords.y = (int) ypos;
		
		if (robotEnabled) {
			robot.mouseMove(centerCoords.x, centerCoords.y);
		}
	}
	
}

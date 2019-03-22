package com.dezzy.skorp3.game.state;

import java.util.Properties;

/**
 * Defines several different overall game states and properties of each game state. <br/>
 * List of game state properties:
 * <ul>
 * <li> render_enabled: true if the renderer should be drawing to the screen
 * <li> mouse_lock: true if {@link java.awt.Robot Robot} should lock the mouse
 * <li> mouse_hide: true if the cursor should be hidden
 * <li> timer: optional property, if it exists the game will only hold this state for the given time in milliseconds
 * </ul>
 *
 * @author Joe Desmond
 */
public enum GameState {
	NONE(null),
	INTRO(PropertyCreator.getIntroProperties()),
	MAIN_MENU(PropertyCreator.getMainMenuProperties()), 
	IN_GAME(PropertyCreator.getInGameProperties());
	
	public final Properties properties;
	
	private GameState(final Properties _properties) {
		properties = _properties;
	}
	
	private static class PropertyCreator {
		
		private static Properties getIntroProperties() {
			Properties properties = new Properties();
			
			properties.setProperty("render_enabled", "false");
			properties.setProperty("mouse_lock", "false");
			properties.setProperty("mouse_hide", "true");
			properties.setProperty("timer", "2000");
			
			return properties;
		}
		
		private static Properties getMainMenuProperties() {
			Properties properties = new Properties();
			
			properties.setProperty("render_enabled", "false");
			properties.setProperty("mouse_lock", "false");
			properties.setProperty("mouse_hide", "false");
			
			return properties;
		}
		
		private static Properties getInGameProperties() {
			Properties properties = new Properties();
			
			properties.setProperty("render_enabled", "true");
			properties.setProperty("mouse_lock", "true");
			properties.setProperty("mouse_hide", "true");
			
			return properties;
		}
	}
}

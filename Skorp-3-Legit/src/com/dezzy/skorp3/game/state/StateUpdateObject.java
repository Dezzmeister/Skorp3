package com.dezzy.skorp3.game.state;

/**
 * Used in the Game State message handler as the data portion of STATE_UPDATE messages.
 *
 * @author Joe Desmond
 * @see com.dezzy.skorp3.messaging.MessageHandler MessageHandler
 */
public class StateUpdateObject {
	public final GameState oldState;
	public final GameState newState;
	
	public StateUpdateObject(final GameState _oldState, final GameState _newState) {
		oldState = _oldState;
		newState = _newState;
	}
}

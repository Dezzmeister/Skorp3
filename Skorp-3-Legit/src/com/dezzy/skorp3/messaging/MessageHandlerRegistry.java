package com.dezzy.skorp3.messaging;

import com.dezzy.skorp3.meta.RequiresForcedLoading;

import static com.dezzy.skorp3.messaging.MessageHandler.*;

/**
 * Registers core message handlers.
 *
 * @author Joe Desmond
 */
public class MessageHandlerRegistry extends RequiresForcedLoading {
	public static final MessageHandler GAME_STATE_HANDLER = createHandler("Game State");
	
	public static final MessageHandler CONTROL_HANDLER = createHandler("Control");
}

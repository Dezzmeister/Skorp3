package com.dezzy.skorp3.game.world;

import com.dezzy.skorp3.game.actors.Player;
import com.dezzy.skorp3.game.graphics.geometry.composite.Mesh;
import com.dezzy.skorp3.game.math.Vec4;

public class PlayerPhysics {
	private final Player player;
	private final Mesh mesh;
	
	public PlayerPhysics(final Player _player, final Mesh _mesh) {
		player = _player;
		mesh = _mesh;
	}
	
	public Vec4 getDirectionVector(final Vec4 dir) {
		Vec4 dest = player.position.plus(dir);
		
		return null;
	}
}

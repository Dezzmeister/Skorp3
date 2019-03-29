package com.dezzy.skorp3.game.world;

import java.util.ArrayList;
import java.util.List;

import com.dezzy.skorp3.game.graphics.geometry.composite.Block;
import com.dezzy.skorp3.game.graphics.geometry.composite.Mesh;
import com.dezzy.skorp3.game.graphics.utils.TransformUtils;
import com.dezzy.skorp3.game.math.Vec4;

public class World {
	public final List<Block> blocks = new ArrayList<Block>();
	public Mesh worldMesh = new Mesh();
	
	public World() {
		
	}
	
	public World addBlock(final Block block) {
		blocks.add(block);
		
		return this;
	}
	
	public void computeWorldMesh() {
		worldMesh = new Mesh();
		
		for (Block b : blocks) {
			worldMesh.add(b);
		}
	}
	
	public World addBlockAt(final Block block, final Vec4 at) {
		TransformUtils.translate(at.x, at.y, at.z).transform(block);
		blocks.add(block);
		
		return this;
	}
}

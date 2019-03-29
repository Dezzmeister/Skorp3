package com.dezzy.skorp3.game.world;

import java.util.ArrayList;
import java.util.List;

import com.dezzy.skorp3.game.graphics.geometry.composite.Block;
import com.dezzy.skorp3.game.graphics.geometry.composite.Mesh;

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
}

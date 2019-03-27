package com.dezzy.skorp3.game.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.dezzy.skorp3.logging.Logger;

public class Texture {
	public final int[] pixels;
	public final int WIDTH;
	public final int HEIGHT;
	
	public int glTextureID;
	public int glImageUnit;
	public int index;
	
	public Texture(final String path) {
		int[] tempPixels = new int[0];
		int tempWidth = 0;
		int tempHeight = 0;
		
		try {
			BufferedImage image = ImageIO.read(new File(path));
			
			tempWidth = image.getWidth();
			tempHeight = image.getHeight();
			
			tempPixels = new int[tempWidth * tempHeight];
			
			image.getRGB(0, 0, tempWidth, tempHeight, tempPixels, 0, tempWidth);			
		} catch (IOException e) {
			Logger.error("Problem loading texture at " + path);;
			e.printStackTrace(Logger.getLogger());
		}
		
		pixels = tempPixels;
		WIDTH = tempWidth;
		HEIGHT = tempHeight;
	}
	
	public void setImageUnit(int imageUnit) {
		glImageUnit = imageUnit;
	}
	
	public void setTextureID(int textureID) {
		glTextureID = textureID;
	}
}

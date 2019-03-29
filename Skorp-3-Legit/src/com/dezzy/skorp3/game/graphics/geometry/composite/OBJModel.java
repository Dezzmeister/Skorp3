package com.dezzy.skorp3.game.graphics.geometry.composite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dezzy.skorp3.game.graphics.Texture;
import com.dezzy.skorp3.game.graphics.geometry.Triangle;
import com.dezzy.skorp3.game.math.Vec2;
import com.dezzy.skorp3.game.math.Vec4;
import com.dezzy.skorp3.logging.Logger;

public class OBJModel extends Mesh {
	private Path mtlName;
	
	public OBJModel(final String _path) {
		Path path = Paths.get(_path);
		
		if (Files.exists(path)) {
			Path directory = path.getParent();
			
			try {
				triangles = loadModel(path, directory);
			} catch (Exception e) {
				Logger.error("Problem loading OBJ file \"" + _path + "\"");
				e.printStackTrace(Logger.getLogger());
				e.printStackTrace();
			}
		} else {
			Logger.error("OBJ file \"" + _path + "\" does not exist!");
		}
	}
	
	private Triangle[] loadModel(final Path path, final Path root) throws IOException {
		List<String> objLines = Files.readAllLines(path);
		List<String> mtlLines;
		List<Vec4> vertices = new ArrayList<Vec4>();
		List<Vec2> textureVertices = new ArrayList<Vec2>();
		List<Triangle> faces = new ArrayList<Triangle>();
		Map<String, Material> mtlLib = null;
		Material currentMaterial = null;

		for (String s : objLines) {
			if (s.length() >= 1 && s.charAt(0) == '#') {
				continue;
			}

			if (s.indexOf("mtllib ") == 0) {
				String name = s.substring(s.indexOf(" ") + 1);
				mtlName = Paths.get(name);
				mtlLines = Files.readAllLines(Paths.get(root.toString(), mtlName.toString()));
				mtlLib = loadMaterialLibrary(mtlLines, root);
				continue;
			}

			if (s.indexOf("usemtl ") == 0) {
				String name = s.substring(s.indexOf(" ") + 1);

				if (mtlLib != null) {
					currentMaterial = mtlLib.get(name);
				}

				continue;
			}

			if (s.indexOf("v ") == 0) {
				Vec4 vertex = getFloatVector(s);
				vertices.add(vertex);

				continue;
			}

			if (s.indexOf("vt ") == 0) {
				String[] elements = s.split(" ");

				if (elements.length >= 3) {
					float u = Float.parseFloat(elements[1]);
					float v = Float.parseFloat(elements[2]);

					Vec2 uv = new Vec2(u, v);
					textureVertices.add(uv);
				}

				continue;
			}

			if (s.indexOf("f ") == 0) {
				if (s.indexOf("/") == -1) {
					Vec4 fVertices = getFloatVector(s);
					
					Vec4 v0 = vertices.get((int) (fVertices.x - 1));
					Vec4 v1 = vertices.get((int) (fVertices.y - 1));
					Vec4 v2 = vertices.get((int) (fVertices.z - 1));

					if (currentMaterial != null) {
						Vec4 color = currentMaterial.Kd;
						
						Triangle triangle = new Triangle(v0, v1, v2).setColors(color, color, color);
						faces.add(triangle);
					}
				} else {
					String[] elements = s.split(" ");

					if (elements.length >= 4) {
						String e0 = elements[1];
						String e1 = elements[2];
						String e2 = elements[3];

						int v0index = Integer.parseInt(e0.substring(0, e0.indexOf("/"))) - 1;
						int v1index = Integer.parseInt(e1.substring(0, e1.indexOf("/"))) - 1;
						int v2index = Integer.parseInt(e2.substring(0, e2.indexOf("/"))) - 1;

						int uv0index = Integer.parseInt(e0.substring(e0.indexOf("/") + 1)) - 1;
						int uv1index = Integer.parseInt(e1.substring(e1.indexOf("/") + 1)) - 1;
						int uv2index = Integer.parseInt(e2.substring(e2.indexOf("/") + 1)) - 1;

						Vec4 v0 = vertices.get(v0index);
						Vec4 v1 = vertices.get(v1index);
						Vec4 v2 = vertices.get(v2index);

						Vec2 uv0 = textureVertices.get(uv0index);
						Vec2 uv1 = textureVertices.get(uv1index);
						Vec2 uv2 = textureVertices.get(uv2index);

						Texture texture = currentMaterial.map_Kd;
						Vec4 color = currentMaterial.Kd;
						
						Triangle triangle = new Triangle(v0, v1, v2)
								.setColors(color, color, color)
								.setUV(uv0, uv1, uv2)
								.setTexture(texture);
						faces.add(triangle);
					}
				}

				continue;
			}
		}

		return faces.toArray(new Triangle[0]);
	}
	
	private Map<String, Material> loadMaterialLibrary(final List<String> mtlLines, final Path root) {
		Map<String, Material> map = new HashMap<String, Material>();
		Material activeMaterial = null;

		for (String s : mtlLines) {

			if (s.length() >= 1 && s.charAt(0) == '#') {
				continue;
			}

			if (s.indexOf("newmtl ") == 0) {
				if (activeMaterial != null) {
					map.put(activeMaterial.name, activeMaterial);
				}
				String name = s.substring(s.indexOf(" ") + 1);
				activeMaterial = new Material();
				activeMaterial.name = name;
				continue;
			}

			if (s.indexOf("Kd ") == 0) {
				activeMaterial.Kd = getFloatVector(s);
				activeMaterial.Kd = activeMaterial.Kd;

				continue;
			}

			if (s.indexOf("map_Kd ") == 0) {
				String filename = s.substring(s.indexOf(" ") + 1);

				activeMaterial.map_Kd = new Texture(root.toString() + "/" + filename);
				continue;
			}
		}

		map.put(activeMaterial.name, activeMaterial);

		return map;
	}
	
	private Vec4 getFloatVector(String line) {
		String[] elements = line.split(" ");

		if (elements.length >= 4) {
			float x = Float.parseFloat(elements[1]);
			float y = Float.parseFloat(elements[2]);
			float z = Float.parseFloat(elements[3]);
			
			return new Vec4(x, y, z);
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	private class Material {
		private String name;
		private Vec4 Ka = new Vec4(0.15f, 0.15f, 0.15f);
		private Vec4 Kd = new Vec4(0.85f, 0.85f, 0.85f);
		private Vec4 Ks = new Vec4(1.0f, 1.0f, 1.0f);
		private float d = 255;
		private float Tr = 0;
		private float Ns = 0;
		private float illum = 1;
		private Texture map_Kd = null;

		private int Kd() {
			return ((int) Kd.z) | ((int) Kd.y << 8) | ((int) Kd.x << 16);
		}
	}
}

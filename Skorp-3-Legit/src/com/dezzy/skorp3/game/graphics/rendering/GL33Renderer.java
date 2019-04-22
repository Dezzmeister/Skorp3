package com.dezzy.skorp3.game.graphics.rendering;

import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderCapability.MATRICES;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderCapability.TEXTURES;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderCapability.VERTICES;
import static com.dezzy.skorp3.game.graphics.rendering.shader.ShaderSubCapability.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import com.dezzy.skorp3.game.graphics.Texture;
import com.dezzy.skorp3.game.graphics.geometry.Triangle;
import com.dezzy.skorp3.game.graphics.geometry.composite.Mesh;
import com.dezzy.skorp3.game.graphics.rendering.shader.ShaderPair;
import com.dezzy.skorp3.game.graphics.utils.ShaderUtils;
import com.dezzy.skorp3.game.math.Mat4;
import com.dezzy.skorp3.game.math.Vec4;
import com.dezzy.skorp3.logging.Logger;
import com.dezzy.skorp3.messaging.DesignatedCaller;
import com.dezzy.skorp3.messaging.Message;
import com.dezzy.skorp3.messaging.MessageHandler;
import com.dezzy.skorp3.messaging.meta.Handles;
import com.dezzy.skorp3.messaging.meta.HandlesFor;
import com.dezzy.skorp3.messaging.meta.Sends;
import com.dezzy.skorp3.messaging.meta.SendsTo;

public final class GL33Renderer extends DesignatedCaller {
	/**
	 * Internal event stream to facilitate communication between caller/control threads and the renderer thread.
	 * OpenGL only allows one thread to make GL calls for a context, so any external calls must be piped through
	 * one thread.
	 */
	private final MessageHandler internalPipe;
	private static final String INTERNAL_PIPE_NAME = GL33Renderer.class.getName() + " Internal Message Pipe";
	
	private final ShaderPair shaders;
	
	private int vaoID;
	private final Map<Mesh, Renderable> renderables = new HashMap<Mesh, Renderable>();
	
	private int programID;
	
	private Texture[] textures;
	private int[] textureIDs;
	
	public GL33Renderer(final ShaderPair _shaders) {
		super(new ArrayBlockingQueue<CallTask>(20));
		shaders = _shaders;
		
		internalPipe = MessageHandler.createHandler(INTERNAL_PIPE_NAME);
		internalPipe.registerCallback(this::handleInternalMessages, this);
	}
	
	@Override
	protected void runOtherTasks() {
		
	}
	
	private void initialize() {
		createGLProgram();
		createAndBindVAO();
	}
	
	private void createAndBindVAO() {		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer id = stack.mallocInt(1);
			GL33.glGenVertexArrays(id);
			vaoID = id.get(0);
		}
		GL33.glBindVertexArray(vaoID);
	}
	
	private void createGLProgram() {
		programID = ShaderUtils.createGLProgram(shaders);
		shaders.getFieldPointers(programID);
	}
	
	@Sends("setMatricesFor")
	@SendsTo("com.dezzy.skorp3.game.graphics.rendering.GL33Renderer Internal Message Pipe")
	public void setMatricesFor(final Mat4 model, final Mat4 view, final Mat4 MVP, final Mesh mesh) {
		MatrixUpdateObject muo = new MatrixUpdateObject(model, view, MVP, mesh);
		Message matrixMessage = new Message("setMatricesFor", muo);
		internalPipe.dispatch(matrixMessage);
	}
	
	@Sends("addMesh")
	@SendsTo("com.dezzy.skorp3.game.graphics.rendering.GL33Renderer Internal Message Pipe")
	public void addMesh(final Mesh mesh) {
		Message meshMessage = new Message("setWorldMesh", mesh);
		internalPipe.dispatch(meshMessage);
	}
	
	@Handles({"addMesh", "setMatricesFor"})
	@HandlesFor("com.dezzy.skorp3.game.graphics.rendering.GL33Renderer Internal Message Pipe")
	private void handleInternalMessages(final Message msg) {
		String text = msg.messageText;
		
		if (text.equals("addMesh")) {
			Renderable renderable = new Renderable();
			renderable.sendTriangleInfo((Mesh) msg.data);
			renderables.put((Mesh) msg.data, renderable);
		} else if (text.equals("setMatricesFor")) {
			MatrixUpdateObject muo = (MatrixUpdateObject) msg.data;
			Renderable renderable = renderables.get(muo.mesh);
			renderable.setMatrices(muo.model, muo.view, muo.MVP);
		}
	}
	
	private class Renderable {
		private int vboID;
		private int uvBuffer;
		private int normalBuffer;
		
		private float[] vertices;
		private float[] colors;
		private float[] uvVertices;
		private float[] normals;
		private Triangle[] triangles;
		
		private final FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
		private final FloatBuffer viewMatrixBuffer = BufferUtils.createFloatBuffer(16);
		private final FloatBuffer mvpMatrixBuffer = BufferUtils.createFloatBuffer(16);
		
		public Renderable() {
			
			if (shaders.isCapableOf(VERTICES)) {
				vboID = createVBO();
				normalBuffer = createNormalBuffer();
			}
			
			if (shaders.isCapableOf(TEXTURES)) {
				uvBuffer = createUVBuffer();
			}
		}
		
		private void setMatrices(final Mat4 model, final Mat4 view, final Mat4 MVP) {
			if (shaders.isCapableOf(MATRICES)) {
				if (shaders.isCapableOf(MODEL_MATRIX)) {
					model.store(modelMatrixBuffer);
					modelMatrixBuffer.flip();
					GL33.glUniformMatrix4fv(shaders.getGLLocationOf(MODEL_MATRIX), false, modelMatrixBuffer);
				} else {
					Logger.error(unsupported(MODEL_MATRIX));
				}
				
				if (shaders.isCapableOf(VIEW_MATRIX)) {
					view.store(viewMatrixBuffer);
					viewMatrixBuffer.flip();
					GL33.glUniformMatrix4fv(shaders.getGLLocationOf(VIEW_MATRIX), false, viewMatrixBuffer);
				} else {
					Logger.error(unsupported(VIEW_MATRIX));
				}
				
				if (shaders.isCapableOf(MVP_MATRIX)) {
					MVP.store(mvpMatrixBuffer);
					mvpMatrixBuffer.flip();
					GL33.glUniformMatrix4fv(shaders.getGLLocationOf(MVP_MATRIX), true, mvpMatrixBuffer);
				} else {
					Logger.error(unsupported(MVP_MATRIX));
				}
			} else {
				Logger.error(unsupported(MATRICES));
			}
		}
		
		private String unsupported(final Enum<?> cap) {
			return shaders.toString() + " does not support " + cap.name();
		}
		
		private void sendTriangleInfo(final Mesh mesh) {
			triangles = mesh.triangles;
			normals = new float[mesh.normals.length * 3];
			
			for (int i = 0; i < mesh.normals.length; i++) {
				Vec4 normal = mesh.normals[i];
				
				int offset = i * 3;
				normals[offset] = normal.x;
				normals[offset + 1] = normal.y;
				normals[offset + 2] = normal.z;
			}
			
			List<Texture> knownTextures = new ArrayList<Texture>();
			
			textureIDs = new int[triangles.length];
			vertices = new float[triangles.length * 9];
			colors = new float[triangles.length * 9];
			uvVertices = new float[triangles.length * 6];
			
			int freeImageUnit = findNewImageUnit();
			
			if (freeImageUnit == -1) {
				Logger.error("Unable to send triangle info, no free texture units available.");
				return;
			}
			
			for (int i = 0; i < triangles.length; i++) {
				Triangle t = triangles[i];
				Texture tex = t.tex;
				
				int offset = i * 9;
				vertices[offset] = t.v0.x;
				vertices[offset + 1] = t.v0.y;
				vertices[offset + 2] = t.v0.z;
				
				vertices[offset + 3] = t.v1.x;
				vertices[offset + 4] = t.v1.y;
				vertices[offset + 5] = t.v1.z;
				
				vertices[offset + 6] = t.v2.x;
				vertices[offset + 7] = t.v2.y;
				vertices[offset + 8] = t.v2.z;
				
				colors[offset] = t.v0c.x;
				colors[offset + 1] = t.v0c.y;
				colors[offset + 2] = t.v0c.z;
				
				colors[offset + 3] = t.v1c.x;
				colors[offset + 4] = t.v1c.y;
				colors[offset + 5] = t.v1c.z;
				
				colors[offset + 6] = t.v2c.x;
				colors[offset + 7] = t.v2c.y;
				colors[offset + 8] = t.v2c.z;
				
				offset = i * 6;
				uvVertices[offset] = t.uv0.x;
				uvVertices[offset + 1] = t.uv0.y;
				
				uvVertices[offset + 2] = t.uv1.x;
				uvVertices[offset + 3] = t.uv1.y;
				
				uvVertices[offset + 4] = t.uv2.x;
				uvVertices[offset + 5] = t.uv2.y;
				
				int id;
				
				if (!knownTextures.contains(tex)) {
					knownTextures.add(tex);
					
					id = addTexture(tex);
					tex.glTextureID = id;
					
					tex.setImageUnit(freeImageUnit);
					textures[freeImageUnit] = tex;
					
					freeImageUnit = findNewImageUnit();
					
					if (freeImageUnit == -1) {
						textureIDs[i] = id;
						Logger.error("Unable to send triangle info, no free texture units available.");
						return;
					}
				} else {
					id = tex.glTextureID;
				}
				textureIDs[i] = id;
			}
			
			if (shaders.isCapableOf((VERTEX_NORMAL))) {
				GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, normalBuffer);
				GL33.glBufferData(GL33.GL_ARRAY_BUFFER, normals, GL33.GL_STATIC_DRAW);
			}
			
			if (shaders.isCapableOf(VERTICES)) {
				GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vboID);
				GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertices, GL33.GL_STATIC_DRAW);
			}
			
			if (shaders.isCapableOf(VERTEX_UV)) {
				GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, uvBuffer);
				GL33.glBufferData(GL33.GL_ARRAY_BUFFER, uvVertices, GL33.GL_STATIC_DRAW);
			}
		}
		
		private int createVBO() {
			int _vboID = -1;
			
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer id = stack.mallocInt(1);
				
				GL33.glGenBuffers(id);
				_vboID = id.get(0);
			}
			
			return _vboID;
		}
		
		private int createUVBuffer() {
			int id;
			
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer buf = stack.mallocInt(1);
				
				GL33.glGenBuffers(buf);
				id = buf.get(0);
			}
			
			return id;
		}
		
		private int createNormalBuffer() {
			int id;
			
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer buf = stack.mallocInt(1);
				
				GL33.glGenBuffers(buf);
				id = buf.get(0);
			}
			
			return id;
		}
		
		private int findNewImageUnit() {
			for (int i = 0; i < textures.length; i++) {
				if (textures[i] == null) {
					return i;
				}
			}
			
			return -1;
		}
		
		private int addTexture(final Texture tex) {
			int id = -1;
			
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer buf = stack.mallocInt(1);
				
				GL33.glGenTextures(buf);
				id = buf.get(0);
			}
			
			GL33.glBindTexture(GL33.GL_TEXTURE_2D, id);
			GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, tex.WIDTH, tex.HEIGHT, 0, GL33.GL_BGRA, GL33.GL_UNSIGNED_BYTE, tex.pixels);
			GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
			GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
			
			return id;
		}
	}
	
	private class MatrixUpdateObject {
		final Mat4 model;
		final Mat4 view;
		final Mat4 MVP;
		final Mesh mesh;
		
		public MatrixUpdateObject(final Mat4 _model, final Mat4 _view, final Mat4 _MVP, final Mesh _mesh) {
			model = _model;
			view = _view;
			MVP = _MVP;
			mesh = _mesh;
		}
	}
}

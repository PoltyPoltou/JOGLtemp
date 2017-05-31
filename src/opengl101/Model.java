package opengl101;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

import org.joml.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;

public class Model {
	private Path path;
	private FloatBuffer objectBuffer;
	private IntBuffer VAO, VBO;
	private GL4 gl;
	private Matrix4f model;
	private int verticesCount;

	public Model(String file) {
		model = new Matrix4f();
		readObgFileToObjectBuffer(file);
	}

	public Model(String file, Matrix4f model) {
		model.get(this.model);
		readObgFileToObjectBuffer(file);
	}

	public Model(String file, GL4 gl) {
		model = new Matrix4f();
		this.gl = gl;
		genBuffers();
		readObgFileToObjectBuffer(file);
	}

	public Model(String file, GL4 gl, Matrix4f model) {
		model.get(this.model);
		this.gl = gl;
		genBuffers();
		readObgFileToObjectBuffer(file);
	}

	// must have set a GL context !
	private void genBuffers() {
		VAO = GLBuffers.newDirectIntBuffer(1);
		VBO = GLBuffers.newDirectIntBuffer(1);
		gl.glGenVertexArrays(1, VAO);
		gl.glGenBuffers(1, VBO);
	}

	// must have set a GL context !
	public void loadModel() {
		gl.glBindVertexArray(VAO.get(0));
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO.get(0));
		gl.glBufferData(GL.GL_ARRAY_BUFFER, objectBuffer.limit() * Float.BYTES, objectBuffer, GL.GL_STATIC_DRAW);
		gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 8 * Float.BYTES, 0);// vertices
		gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);// texture
		// gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, true, 8 * Float.BYTES, 5 * Float.BYTES);// normal vertices normalized
		gl.glEnableVertexAttribArray(0);
		gl.glEnableVertexAttribArray(1);
		// gl.glEnableVertexAttribArray(2);
		gl.glBindVertexArray(0);
	}

	// must have set a GL context !
	public void bindVAO() {
		gl.glBindVertexArray(VAO.get(0));
	}

	// must have set a GL context !
	public void drawModel() {
		gl.glDrawArrays(GL4.GL_TRIANGLES, 0, verticesCount);
	}

	private void readObgFileToObjectBuffer(String file) {
		//format off
		FloatBuffer vertice = Buffers.newDirectFloatBuffer(5000), texture = Buffers.newDirectFloatBuffer(5000),
					normal = Buffers.newDirectFloatBuffer(5000);
		//format on
		IntBuffer index = Buffers.newDirectIntBuffer(5000);
		boolean v, vt, vn, f;
		BufferedReader entry = null;
		StringTokenizer actualLine, faceIndex;
		String line = " ", str;
		path = Paths.get(file);
		try {
			entry = new BufferedReader(new FileReader(path.toFile()));
		} catch (FileNotFoundException e) {
			System.out.println("Failed to load model file at" + this.getClass().getName());
		}
		while (line != null) {
			try {
				line = entry.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (line != null) {
				actualLine = new StringTokenizer(line, " ");

				String token = actualLine.nextToken();
				if (token.contains("vt")) {
					v = false;
					vt = true;
					vn = false;
					f = false;
				} else if (token.contains("vn")) {
					v = false;
					vt = false;
					vn = true;
					f = false;
				} else if (token.contains("f")) {
					v = false;
					vt = false;
					vn = false;
					f = true;
					actualLine = new StringTokenizer(line.substring(2, line.length()), " ");
				} else if (token.contains("v")) {
					v = true;
					vt = false;
					vn = false;
					f = false;
				} else
					continue;
				int count = actualLine.countTokens();
				for (int i = 0; i < count; i++) {
					if (v)
						vertice.put(Float.parseFloat(actualLine.nextToken()));
					else if (vt)
						texture.put(Float.parseFloat(actualLine.nextToken()));
					else if (vn)
						normal.put(Float.parseFloat(actualLine.nextToken()));
					else if (f) {
						str = actualLine.nextToken();
						faceIndex = new StringTokenizer(str, "/");
						index.put(Integer.parseInt(faceIndex.nextToken()));
						index.put(Integer.parseInt(faceIndex.nextToken()));
						index.put(Integer.parseInt(faceIndex.nextToken()));
					}

				}

			}
		}
		vertice.flip();
		texture.flip();
		normal.flip();
		index.flip();
		float[] a = new float[index.limit() * 8 / 3];
		objectBuffer = GLBuffers.newDirectFloatBuffer(a);
		for (int i = 0; i < index.limit(); i += 3) {
			//format off
			objectBuffer.put(vertice.get((index.get(i) - 1) * 3));
			objectBuffer.put(vertice.get((index.get(i) - 1) * 3 + 1));
			objectBuffer.put(vertice.get((index.get(i) - 1) * 3 + 2));

			objectBuffer.put(texture.get((index.get(i  + 1) - 1) * 2));
			objectBuffer.put(texture.get((index.get(i  + 1) - 1) * 2) + 1);

			objectBuffer.put(normal.get ((index.get(i  + 2) - 1) * 3));
			objectBuffer.put(normal.get ((index.get(i  + 2) - 1) * 3 + 1));
			objectBuffer.put(normal.get ((index.get(i  + 2) - 1) * 3 + 2));
			//format on
		}
		objectBuffer.flip();
		verticesCount = index.limit() / 3;
		System.out.println(verticesCount);
	}

	public FloatBuffer getVBO() {
		return objectBuffer;
	}

	public IntBuffer getVAO() {
		return VAO;
	}

	public void setModelMatrix(Matrix3f m) {
		model.set(m);
	}

	public Matrix4f getModelMatrix() {
		return model;
	}

	public float[] getArray() {
		return objectBuffer.array();
	}

	public void setGl(GL4 gl) {
		this.gl = gl;
	}
}

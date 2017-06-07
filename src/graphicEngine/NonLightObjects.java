package graphicEngine;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

import org.joml.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;

import opengl101.*;

public class NonLightObjects extends Model {
	private Path path;
	private FloatBuffer objectBuffer;
	private IntBuffer VAO, VBO;
	private GL4 gl;
	private Matrix4f model;
	private Matrix3f normalMatrix;
	private int verticesCount;
	private Texture texture;
	private ShaderProgram shader;
	private boolean text;
	private static final String DEFAULT_FOLDER_PATH = "models/";

	public NonLightObjects(String file, GL4 gl) {
		model = new Matrix4f();
		normalMatrix = new Matrix3f();
		this.gl = gl;
		genBuffers();
		readObgFileToObjectBuffer(file);
		updateNormalMatrix();
	}

	public NonLightObjects(String file, GL4 gl, Texture t, ShaderProgram s) {
		this.model = new Matrix4f();
		normalMatrix = new Matrix3f();
		this.gl = gl;
		this.shader = s;
		this.texture = t;
		text = true;
		genBuffers();
		readObgFileToObjectBuffer(file);
		updateNormalMatrix();
	}

	public NonLightObjects(String file, GL4 gl, Matrix4f model) {
		this.model = new Matrix4f(model);
		normalMatrix = new Matrix3f();
		this.gl = gl;
		genBuffers();
		readObgFileToObjectBuffer(file);
		updateNormalMatrix();
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
		if (text) {
			gl.glBindVertexArray(VAO.get(0));
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO.get(0));
			gl.glBufferData(GL.GL_ARRAY_BUFFER, objectBuffer.limit() * Float.BYTES, objectBuffer, GL.GL_STATIC_DRAW);
			gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 8 * Float.BYTES, 0);// vertices
			gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);// texture
			gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, true, 8 * Float.BYTES, 5 * Float.BYTES);// normal vertices normalized
			gl.glEnableVertexAttribArray(0);
			gl.glEnableVertexAttribArray(1);
			gl.glEnableVertexAttribArray(2);
			gl.glBindVertexArray(0);
		} else {
			gl.glBindVertexArray(VAO.get(0));
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO.get(0));
			gl.glBufferData(GL.GL_ARRAY_BUFFER, objectBuffer.limit() * Float.BYTES, objectBuffer, GL.GL_STATIC_DRAW);
			gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 0);// vertices
			gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, true, 6 * Float.BYTES, 3 * Float.BYTES);// normal vertices normalized
			gl.glEnableVertexAttribArray(0);
			gl.glEnableVertexAttribArray(2);
			gl.glBindVertexArray(0);
		}
	}

	// must have set a GL context !
	public void drawModel() {
		gl.glBindVertexArray(VAO.get(0));
		shader.use(gl);
		shader.setMat4("uni_model", model);
		shader.setMat3("uni_normalMatrix", normalMatrix);
		if (this.hasTexture()) {
			texture.bindTexture(gl, shader);
		}
		gl.glDrawArrays(GL4.GL_TRIANGLES, 0, verticesCount);
		gl.glBindVertexArray(0);
	}

	private void updateNormalMatrix() {
		Matrix4f test = new Matrix4f(model);
		normalMatrix = new Matrix3f(test.invert().transpose());
	}

	private void readObgFileToObjectBuffer(String file) {

		boolean f = false;
		int size = 0, endv = 0, endvt = 0;
		BufferedReader entry = null;
		StringTokenizer actualLine, faceIndex;
		String line = " ", str;
		path = Paths.get(DEFAULT_FOLDER_PATH + file);
		LineNumberReader lnr;
		try {
			lnr = new LineNumberReader(new FileReader(path.toFile()));
			lnr.skip(Long.MAX_VALUE);
			size = (lnr.getLineNumber() + 1) * 3;
			lnr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		float[] b = new float[size];
		FloatBuffer data = FloatBuffer.wrap(b);
		int[] a = new int[size];
		IntBuffer index = IntBuffer.wrap(a);

		try {
			entry = new BufferedReader(new FileReader(path.toFile()));
		} catch (FileNotFoundException e) {
			System.out.println("Failed to load model file at " + this.getClass().getName());
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
				if (token.contains("f")) {
					f = true;
					actualLine = new StringTokenizer(line.substring(2, line.length()), " ");
				} else if (token.contains("vt"))
					endvt = data.position() + 2;
				else if (token.contains("v") && !token.contains("vn"))
					endv = data.position() + 3;
				else if (!token.contains("v"))
					continue;
				int count = actualLine.countTokens();
				for (int i = 0; i < count; i++) {
					if (f) {
						str = actualLine.nextToken();
						faceIndex = new StringTokenizer(str, "/");
						if (faceIndex.countTokens() == 3) {
							index.put(Integer.parseInt(faceIndex.nextToken()));
							index.put(Integer.parseInt(faceIndex.nextToken()));
							index.put(Integer.parseInt(faceIndex.nextToken()));
							text = true;
						} else {
							index.put(Integer.parseInt(faceIndex.nextToken()));
							index.put(Integer.parseInt(faceIndex.nextToken()));
							text = false;
						}
					} else {
						data.put(Float.parseFloat(actualLine.nextToken()));
					}

				}

			}
		}
		data.flip();
		index.flip();
		if (text) {
			float[] z = new float[index.limit() * 8 / 3];
			objectBuffer = FloatBuffer.wrap(z);
			for (int i = 0; i < index.limit(); i += 3) {
				//format off
				objectBuffer.put(data.get((index.get(i)-1)*3 ));
				objectBuffer.put(data.get((index.get(i)-1)*3 +1));
				objectBuffer.put(data.get((index.get(i)-1)*3 +2));

				objectBuffer.put(data.get((index.get(i+1)-1)*2    +endv));
				objectBuffer.put(data.get((index.get(i+1)-1)*2 +1 +endv));

				objectBuffer.put(data.get((index.get(i+2)-1)*3    +endvt));
				objectBuffer.put(data.get((index.get(i+2)-1)*3 +1 +endvt));
				objectBuffer.put(data.get((index.get(i+2)-1)*3 +2 +endvt));
				//format on
			}
			verticesCount = index.limit() / 3;
		} else {
			float[] z = new float[index.limit() * 3];
			objectBuffer = FloatBuffer.wrap(z);
			for (int i = 0; i < index.limit(); i += 2) {
				//format off
				objectBuffer.put(data.get((index.get(i)-1)*3 ));
				objectBuffer.put(data.get((index.get(i)-1)*3 +1));
				objectBuffer.put(data.get((index.get(i)-1)*3 +2));

				objectBuffer.put(data.get((index.get(i+1)-1)*6   ));
				objectBuffer.put(data.get((index.get(i+1)-1)*6 +1));
				objectBuffer.put(data.get((index.get(i+1)-1)*6 +2));
				//format on
			}
			verticesCount = index.limit() / 2;
		}
		objectBuffer.flip();

	}

	public FloatBuffer getVBO() {
		return objectBuffer;
	}

	public IntBuffer getVAO() {
		return VAO;
	}

	public Matrix4f getModelMatrix() {
		return model;
	}

	public float[] getArray() {
		return objectBuffer.array();
	}

	public Texture getTexture() {
		return texture;
	}

	public ShaderProgram getShader() {
		return shader;
	}

	private boolean hasTexture() {
		return text;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
		text = true;
	}

	public void setShader(ShaderProgram shader) {
		this.shader = shader;
	}

	public void setGl(GL4 gl) {
		this.gl = gl;
	}

	public void setModelMatrix(Matrix4f m) {
		model = new Matrix4f(m);
		updateNormalMatrix();
	}

	public void transformModelMatrix(Matrix4f m) {
		m.mul(model, model);
		updateNormalMatrix();
	}
}

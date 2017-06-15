package opengl101;

import java.io.*;
import java.nio.*;

import org.joml.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;

public class ShaderProgram {

	private int vertexId, fragmentId, geometryId, pgrmId;
	private GL4 gl;
	private FloatBuffer matrixUniformBuffer;
	private IntBuffer errorBfr;
	private static final String DEFAULT_FOLDER_PATH = "/shaders/";
	private String vertexPath;

	public ShaderProgram(GL4 gl, String vertexPath, String fragmentPath) {
		this.vertexPath = vertexPath;
		errorBfr = GLBuffers.newDirectIntBuffer(1);
		matrixUniformBuffer = GLBuffers.newDirectFloatBuffer(16);
		geometryId = -1;
		this.gl = gl;
		loadVertexAndFragment(vertexPath, fragmentPath);
		compileShader();
	}

	public ShaderProgram(GL4 gl, String vertexPath, String fragmentPath, String geometryPath) {
		this.vertexPath = vertexPath;
		errorBfr = GLBuffers.newDirectIntBuffer(1);
		matrixUniformBuffer = GLBuffers.newDirectFloatBuffer(16);
		this.gl = gl;
		loadVertexAndFragment(vertexPath, fragmentPath);
		loadGeometryShader(geometryPath);
		compileShader();
	}

	private void loadGeometryShader(String geometryPath) {
		String str = loadStringFileFromCurrentPackage(geometryPath);
		geometryId = gl.glCreateShader(GL4.GL_GEOMETRY_SHADER);
		gl.glShaderSource(geometryId, 1, new String[]
			{
					str
			}, null);
		gl.glCompileShader(geometryId);
		gl.glGetShaderiv(geometryId, GL4.GL_COMPILE_STATUS, errorBfr);
		if (errorBfr.get(0) == 0) {
			System.out.println("ERROR:SHADER:GEOMETRY:COMPILATION:FAILED " + geometryPath);
		}
	}

	private void loadVertexAndFragment(String vertexPath, String fragmentPath) {
		String str = loadStringFileFromCurrentPackage(vertexPath);
		vertexId = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
		gl.glShaderSource(vertexId, 1, new String[]
			{
					str
			}, null);
		gl.glCompileShader(vertexId);
		gl.glGetShaderiv(vertexId, GL4.GL_COMPILE_STATUS, errorBfr);
		if (errorBfr.get(0) != GL4.GL_TRUE) {
			System.out.println("ERROR:SHADER:VERTEX:COMPILATION:FAILED " + vertexPath);
		}

		str = loadStringFileFromCurrentPackage(fragmentPath);
		fragmentId = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fragmentId, 1, new String[]
			{
					str
			}, null);
		gl.glCompileShader(fragmentId);
		gl.glGetShaderiv(fragmentId, GL4.GL_COMPILE_STATUS, errorBfr);
		if (errorBfr.get(0) != GL4.GL_TRUE) {
			System.out.println("ERROR:SHADER:FRAGMENT:COMPILATION:FAILED " + fragmentPath);
		}
	}

	public void compileShader() {
		pgrmId = gl.glCreateProgram();
		gl.glAttachShader(pgrmId, vertexId);
		if (geometryId != -1) {
			gl.glAttachShader(pgrmId, geometryId);
		}
		gl.glAttachShader(pgrmId, fragmentId);
		gl.glLinkProgram(pgrmId);
		gl.glGetProgramiv(pgrmId, GL4.GL_LINK_STATUS, errorBfr);
		if (errorBfr.get(0) != GL4.GL_TRUE) {
			System.out.println("ERROR:SHADER:PROGRAM:LINKING:FAILED " + vertexPath);
			IntBuffer size = GLBuffers.newDirectIntBuffer(1);
			gl.glGetProgramiv(pgrmId, GL4.GL_INFO_LOG_LENGTH, size);
			byte[] a = new byte[size.get(0)];
			ByteBuffer b = ByteBuffer.wrap(a);
			gl.glGetProgramInfoLog(pgrmId, size.get(0), size, b);
			for (int i = 0; i < b.capacity(); i++) {
				System.out.print((char) b.get());
			}
		}
	}

	public void bind() {
		gl.glUseProgram(pgrmId);
	}

	protected String loadStringFileFromCurrentPackage(String fileName) {
		InputStream stream = ShaderProgram.class.getResourceAsStream(DEFAULT_FOLDER_PATH + fileName);

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		// allocate a string builder to add line per line
		StringBuilder strBuilder = new StringBuilder();

		try {
			String line = reader.readLine();
			// get text from file, line per line
			while (line != null) {
				strBuilder.append(line + "\n");
				line = reader.readLine();
			}
			// close resources
			reader.close();
			stream.close();
		} catch (IOException e) {
			System.out.println("ERROR:SHADER:FILE NOT READ");
		}

		return strBuilder.toString();
	}

	public int getVertexId() {
		return vertexId;
	}

	public int getFragmentId() {
		return fragmentId;
	}

	public int getPgrmId() {
		return pgrmId;
	}

	public void setVec3(String name, float a, float b, float c) {
		this.bind();
		gl.glUniform3f(gl.glGetUniformLocation(pgrmId, name), a, b, c);
	}

	public void setVec3(String name, Vector3f v) {
		this.bind();
		gl.glUniform3f(gl.glGetUniformLocation(pgrmId, name), v.x, v.y, v.z);
	}

	public void setFloat(String name, float f) {
		this.bind();
		gl.glUniform1f(gl.glGetUniformLocation(pgrmId, name), f);
	}

	public void setMat4(String name, Matrix4f m) {
		this.bind();
		m.get(matrixUniformBuffer);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(pgrmId, name), 1, false, matrixUniformBuffer);
	}

	public void setMat3(String name, Matrix3f m) {
		this.bind();
		m.get(matrixUniformBuffer);
		gl.glUniformMatrix3fv(gl.glGetUniformLocation(pgrmId, name), 1, false, matrixUniformBuffer);
	}
}

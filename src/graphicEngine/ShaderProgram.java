package graphicEngine;

import java.io.*;
import java.nio.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;

public class ShaderProgram {

	int vertexId, fragmentId, pgrmId;
	String vertexPath, fragmentPath;
	GL4 gl;

	public ShaderProgram(String vertexPath, String fragmentPath, GL4 gl) {

		this.gl = gl;
		this.vertexPath = vertexPath;
		this.fragmentPath = fragmentPath;
		loadVertexAndFragment();
	}

	private void loadVertexAndFragment() {
		String str = loadStringFileFromCurrentPackage(vertexPath);;
		IntBuffer errorBfr = GLBuffers.newDirectIntBuffer(1);
		vertexId = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
		gl.glShaderSource(vertexId, 1, new String[]
			{
					str
			}, null);
		gl.glCompileShader(vertexId);
		gl.glGetShaderiv(vertexId, GL4.GL_COMPILE_STATUS, errorBfr);
		if (errorBfr.get(0) == 0) {
			System.out.println("ERROR:SHADER:VERTEX:COMPILATION:FAILED");
		}

		str = loadStringFileFromCurrentPackage(fragmentPath);
		fragmentId = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fragmentId, 1, new String[]
			{
					str
			}, null);
		gl.glCompileShader(fragmentId);
		gl.glGetShaderiv(fragmentId, GL4.GL_COMPILE_STATUS, errorBfr);
		if (errorBfr.get(0) == 0) {
			System.out.println("ERROR:SHADER:FRAGMENT:COMPILATION:FAILED");
		}

		pgrmId = gl.glCreateProgram();
		gl.glAttachShader(pgrmId, vertexId);
		gl.glAttachShader(pgrmId, fragmentId);
		gl.glLinkProgram(pgrmId);
		gl.glGetProgramiv(pgrmId, GL4.GL_LINK_STATUS, errorBfr);
		if (errorBfr.get(0) == 0) {
			System.out.println("ERROR:SHADER:PROGRAM:LINKING:FAILED");
		}
	}

	public void use(GL4 gl) {
		gl.glUseProgram(pgrmId);
	}

	protected String loadStringFileFromCurrentPackage(String fileName) {
		InputStream stream = this.getClass().getResourceAsStream(fileName);

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

}

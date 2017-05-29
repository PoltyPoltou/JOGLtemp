package opengl101;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

import com.jogamp.common.nio.*;

public class Model {
	private Path path;
	private FloatBuffer VAO;

	public static void main(String args[]) {
		new Model("untitled.obj");
	}

	public Model(String file) {
		//format off
		FloatBuffer vertice = Buffers.newDirectFloatBuffer(5000), texture = Buffers.newDirectFloatBuffer(5000),
					normal = Buffers.newDirectFloatBuffer(5000);
		//format on
		IntBuffer index = Buffers.newDirectIntBuffer(5000);
		boolean v, vt, vn, f;
		BufferedReader entry = null;
		StringTokenizer s;
		String line = " ";
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
				s = new StringTokenizer(line, " ");

				String token = s.nextToken();
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
					s = new StringTokenizer(line.substring(2, line.length()), " ");
				} else if (token.contains("v")) {
					v = true;
					vt = false;
					vn = false;
					f = false;
				} else
					continue;
				int count = s.countTokens();
				for (int i = 0; i < count; i++) {
					if (v)
						vertice.put(Float.parseFloat(s.nextToken()));
					else if (vt)
						texture.put(Float.parseFloat(s.nextToken()));
					else if (vn)
						normal.put(Float.parseFloat(s.nextToken()));
					else if (f) {
						String str = s.nextToken();
						StringTokenizer face = new StringTokenizer(str, "/");
						//format off
						index.put(Integer.parseInt(face.nextToken()));
						index.put(Integer.parseInt(face.nextToken()));
						index.put(Integer.parseInt(face.nextToken()));
						//format on
					}

				}

			}
		}
		vertice.flip();
		texture.flip();
		normal.flip();
		index.flip();
		VAO = Buffers.newDirectFloatBuffer(vertice.limit() + texture.limit() + normal.limit());
		for (int i = 0; i < VAO.capacity(); i += 3) {
			//format off
			VAO.put(vertice.get((index.get(i) - 1) * 3));
			VAO.put(vertice.get((index.get(i) - 1) * 3 + 1));
			VAO.put(vertice.get((index.get(i) - 1) * 3 + 2));

			VAO.put(texture.get((index.get(i  + 1) - 1) * 2));
			VAO.put(texture.get((index.get(i  + 1) - 1) * 2) + 1);

			VAO.put(normal.get ((index.get(i  + 2) - 1) * 3));
			VAO.put(normal.get ((index.get(i  + 2) - 1) * 3 + 1));
			VAO.put(normal.get ((index.get(i  + 2) - 1) * 3 + 2));
			//format on
		}
	}
}

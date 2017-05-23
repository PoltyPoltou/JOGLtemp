package opengl101;

import java.lang.Math;
import java.nio.*;
import java.time.*;

import javax.swing.*;

import org.joml.*;

import com.jogamp.common.nio.*;
import com.jogamp.newt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;

@SuppressWarnings("unused")
public class Game extends JFrame implements GLEventListener, MouseListener {

	private static final long serialVersionUID = 1L;
	final private int width = 800;
	final private int height = 600;
	//format off
	private float vertices[] =

		{
			    -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
			     0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
			     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
			     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
			    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
			    -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

			    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
			     0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
			     0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
			     0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
			    -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
			    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

			    -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
			    -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
			    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
			    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
			    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
			    -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

			     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
			     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
			     0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
			     0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
			     0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
			     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

			    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
			     0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
			     0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
			     0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
			    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
			    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

			    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
			     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
			     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
			     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
			    -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
			    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f

		};

	private Vector3f cubes[] =
		{
				new Vector3f(0, 0, 0) ,
				new Vector3f(2, 5, -15) ,
				new Vector3f(-1.5f, -2.2f, -2.5f) ,
				new Vector3f(-3.8f, -2.0f, -12.3f) ,
				new Vector3f(2.4f, -0.4f, -3.5f) ,
				new Vector3f(-1.7f, 3.0f, -7.5f) ,
				new Vector3f(1.3f, -2.0f, -2.5f) ,
				new Vector3f(1.5f, 2.0f, -2.5f) ,
				new Vector3f(1.5f, 0.2f, -1.5f) ,
				new Vector3f(-1.3f, 1.0f, -1.5f)
		};
	//format on
	private IntBuffer VAO, VBO, EBO;
	private GL4 gl;
	private String vertexPath = "vertex.shader", fragmentPath = "fragment.shader", imagePath = "container.jpg", imgPath = "awesomeface.png";
	private GLCanvas canvas;
	private final FPSAnimator anim;
	private ShaderPgrm shader;
	private TexturePerso texture, second;
	private FloatBuffer matrixData;
	private Instant launch;
	private Matrix4f modelMatrix, projectionMatrix;
	private Camera view;

	public Game() {

		// stuff for window
		super("Minimal OpenGl");
		GLProfile profile = GLProfile.get(GLProfile.GL4);
		GLCapabilities capabilities = new GLCapabilities(profile);
		canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		this.setName("Minimal Test");
		this.getContentPane().add(canvas);
		this.setSize(width, height);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(true);
		anim = new FPSAnimator(canvas, 60);
		anim.start();
		view = new Camera(new Vector3f(0, 0, 3), new Vector3f(0, 0, 0));
		canvas.addKeyListener(view.getKeyboard());
		// matrix math
		matrixData = Buffers.newDirectFloatBuffer(16);
		modelMatrix = new Matrix4f().rotation((float) Math.toRadians(-55.0f), new Vector3f(1, 0, 0)).scale(0.25f);
		projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(45), this.width / this.height, 0.1f, 100);

		launch = Instant.now();

	}

	public void createBuffers(GL4 gl) {

		FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(vertices);
		// IntBuffer indiceBuffer = GLBuffers.newDirectIntBuffer(indices);
		VBO = GLBuffers.newDirectIntBuffer(1);
		VAO = GLBuffers.newDirectIntBuffer(1);
		// EBO = GLBuffers.newDirectIntBuffer(1);

		gl.glGenVertexArrays(1, VAO);
		gl.glGenBuffers(1, VBO);
		// gl.glGenBuffers(1, EBO);

		gl.glBindVertexArray(VAO.get(0));

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO.get(0));
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL.GL_STATIC_DRAW);

		// gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, EBO.get(0));
		// gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer.capacity() * Float.BYTES, indiceBuffer, GL.GL_STATIC_DRAW);

		gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 5 * Float.BYTES, 0);
		// gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
		gl.glVertexAttribPointer(2, 2, GL.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);

		gl.glEnableVertexAttribArray(0);
		// gl.glEnableVertexAttribArray(1);
		gl.glEnableVertexAttribArray(2);
		gl.glBindVertexArray(0);
		// FloatBuffer colorBuffer =
		// GLBuffers.newDirectFloatBuffer(verticesColor);
		// colorBufferInt = GLBuffers.newDirectIntBuffer(1);
		// gl.glGenBuffers(1, colorBufferInt);
		// gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colorBufferInt.get(0));
		// gl.glBufferData(GL.GL_ARRAY_BUFFER, colorBuffer.capacity() *
		// Float.BYTES, colorBuffer, GL.GL_STATIC_DRAW);
		// gl.glVertexAttribPointer(1, 4, GL.GL_FLOAT, false, 0, 0);
		// gl.glEnableVertexAttribArray(1);

	}

	private void pushMatrix(Matrix4f model, Matrix4f view, Matrix4f proj) {
		int prgrmId = shader.getPgrmId();
		model.get(matrixData);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(prgrmId, "uni_model"), 1, false, matrixData);
		view.get(matrixData);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(prgrmId, "uni_view"), 1, false, matrixData);
		proj.get(matrixData);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(prgrmId, "uni_proj"), 1, false, matrixData);

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL4();

		gl.glGetString(GL.GL_VERSION);
		System.out.println(gl.glGetString(GL.GL_VERSION));
		gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
		gl.glEnable(GL4.GL_DEPTH_TEST);
		shader = new ShaderPgrm(vertexPath, fragmentPath, gl);
		createBuffers(gl);

		texture = new TexturePerso(imagePath, ".jpg", gl, false);
		second = new TexturePerso(imgPath, ".png", gl, true);

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
		shader.use(gl);
		texture.bindTexture(gl, shader);
		second.bindTexture(gl, shader);
		gl.glBindVertexArray(VAO.get(0));
		for (int i = 0; i < 10; i++) {
			Duration t = Duration.between(launch, Instant.now());
			Matrix4f mod = new Matrix4f().translate(cubes[i]);
			mod.rotate(20 * i, new Vector3f(1, 0.3f, 0.5f)).rotate((float) (Math.toRadians(t.toMillis()) / 75), 0, 1, 0);
			pushMatrix(mod, view.getLookAt(), projectionMatrix);
			gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 36);
		}
		gl.glBindVertexArray(0);
		gl.glFlush();

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		anim.resume();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		anim.pause();

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseEvent e) {

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

}

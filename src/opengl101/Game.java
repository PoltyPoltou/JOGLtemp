package opengl101;

import java.awt.*;
import java.awt.image.*;
import java.lang.Math;
import java.nio.*;
import java.time.*;

import javax.swing.*;

import org.joml.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;

import graphicEngine.*;
import inputs.*;

@SuppressWarnings("unused")
public class Game extends JFrame implements GLEventListener {

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
	private String vertexPath = "container.vertex", fragmentPath = "container.fragment", imagePath = "container.png", modelPath = "untitled.obj";
	private GLCanvas canvas;
	private final FPSAnimator anim;
	private ShaderProgram shader, lightShader;
	private Texture texture, second;
	private FloatBuffer matrixData;
	private Instant launch;
	private Matrix4f modelMatrix, projectionMatrix;
	private Camera view;
	private Model container, light;
	int i = 0;

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
		this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "invisible cursor"));
		// change cursor to an invisible one

		this.anim = new FPSAnimator(canvas, 60);
		anim.start();
		view = new Camera(new Vector3f(0, 0, 3), new Vector3f(0, 0, 0), new MouseLocker(canvas));
		canvas.addKeyListener(view.getKeyboard());
		canvas.addMouseMotionListener(view.getMouseLocker());
		canvas.addMouseListener(view.getMouseLocker());
		canvas.requestFocus();
		// matrix math
		matrixData = Buffers.newDirectFloatBuffer(16);
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
		// gl.glVertexAttribPointer(2, 2, GL.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);

		gl.glEnableVertexAttribArray(0);
		// gl.glEnableVertexAttribArray(1);
		// gl.glEnableVertexAttribArray(2);
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

	private void pushMatrix(Matrix4f view, Matrix4f proj, ShaderProgram s) {
		int prgrmId = s.getPgrmId();
		s.use(gl);
		view.get(matrixData);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(prgrmId, "uni_view"), 1, false, matrixData);
		proj.get(matrixData);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(prgrmId, "uni_proj"), 1, false, matrixData);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL4();

		System.out.println(gl.glGetString(GL.GL_VERSION));
		gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
		gl.glEnable(GL4.GL_DEPTH_TEST);
		shader = new ShaderProgram(vertexPath, fragmentPath, gl);
		lightShader = new ShaderProgram(vertexPath, "light.fragment", gl);
		//format off
		texture = new Texture(new String[] {imagePath,imagePath}, new String[] {"material.diffuse","material.specular"}, gl);
		//format on
		container = new Model(modelPath, gl, texture, shader);
		container.loadModel();
		light = new Model("test.obj", gl, null, lightShader);
		light.loadModel();
		shader.setVec3("objectColor", 1.0f, 0.5f, 0.31f);
		shader.setVec3("lightColor", 1.0f, 1.0f, 1.0f);
		shader.setFloat("material.shininess", 32.0f);
		shader.setVec3("light.ambient", 0.1f, 0.1f, 0.1f);
		shader.setVec3("light.diffuse", 0.5f, 0.5f, 0.5f); // darken the light a bit to fit the scene
		shader.setVec3("light.specular", 1.0f, 1.0f, 1.0f);
		lightShader.setVec3("uni_color", 1, 1, 1);
		light.setModelMatrix(new Matrix4f().translate(0, 0, 4));
		container.setModelMatrix(new Matrix4f().rotate(0, 0, 1, 0).translate(0, 0, -1));
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
		// double b = Duration.between(launch, Instant.now()).toMillis() * 0.0005;
		// float a = (float) Math.cos(b);
		// float c = (float) Math.sin(b);
		shader.setVec3("viewPos", view.getPosition());
		Vector4f pos = new Vector4f().mul(light.getModelMatrix());
		shader.setVec3("light.position", pos.x, pos.y, pos.z);
		pushMatrix(view.getLookAt(), projectionMatrix, container.getShader());
		container.drawModel();
		pushMatrix(view.getLookAt(), projectionMatrix, light.getShader());
		light.drawModel();
		gl.glFlush();

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

}

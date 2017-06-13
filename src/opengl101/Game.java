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

public class Game extends JFrame implements GLEventListener {

	private static final long serialVersionUID = 1L;
	private static final int SAMPLES_MSAA = 16;
	final private int width = 800;
	final private int height = 600;
	//format off
    float quadVertices[] = { // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
            // positions   // texCoords
            -1.0f,  1.0f,  0.0f, 1.0f,
            -1.0f, -1.0f,  0.0f, 0.0f,
             1.0f, -1.0f,  1.0f, 0.0f,

            -1.0f,  1.0f,  0.0f, 1.0f,
             1.0f, -1.0f,  1.0f, 0.0f,
             1.0f,  1.0f,  1.0f, 1.0f
        };
	//format on
	public GL4 gl;
	private String vertexPath = "container.vertex", fragmentPath = "container.fragment", imagePath = "container.png", modelPath = "cubetext.obj";
	private GLCanvas canvas;
	private final FPSAnimator anim;
	private ShaderProgram standardShader, lightShader, textureDisplayerShader;
	private Texture texture;
	private FloatBuffer matrixData;
	private Instant launch;
	private Matrix4f projectionMatrix;
	private Camera view;
	private NonLightObjects floor, light, block;
	private ShadowDepthFrameBuffer<DepthTexture> shadowBuffer;
	private DepthTexture shadowTexture;
	private DirLight sun;
	private PointLight lamp;

	public Game() {

		// stuff for window
		super("Minimal OpenGl");
		GLProfile profile = GLProfile.get(GLProfile.GL4);
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setSampleBuffers(true);
		capabilities.setNumSamples(SAMPLES_MSAA);
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
		view = new Camera(new Vector3f(0, 1, 1), new Vector3f(0, 0, 0), new MouseLocker(canvas));
		canvas.addKeyListener(view.getKeyboard());
		canvas.addMouseMotionListener(view.getMouseLocker());
		canvas.addMouseListener(view.getMouseLocker());
		canvas.requestFocus();
		// matrix math
		matrixData = Buffers.newDirectFloatBuffer(16);
		projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(45), this.width / this.height, 0.1f, 100);

		launch = Instant.now();

	}

	private void pushMatrix(Matrix4f view, Matrix4f proj, ShaderProgram s) {
		int prgrmId = s.getPgrmId();
		s.bind();
		view.get(matrixData);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(prgrmId, "uni_view"), 1, false, matrixData);
		proj.get(matrixData);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(prgrmId, "uni_proj"), 1, false, matrixData);
	}
	IntBuffer quadVAO, quadVBO;

	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL4();

		System.out.println(gl.glGetString(GL.GL_VERSION));
		gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
		gl.glEnable(GL4.GL_DEPTH_TEST);
		standardShader = new ShaderProgram(gl, vertexPath, fragmentPath);
		lightShader = new ShaderProgram(gl, vertexPath, "light.fragment");
		//format off
		texture = new Texture(gl,new String[] {imagePath,"container_specular.png"}, new String[] {"material.diffuse","material.specular"});
		//format on
		floor = new NonLightObjects(gl, modelPath, texture, standardShader);
		floor.loadModel();
		light = new NonLightObjects(gl, "light.obj", null, lightShader);
		light.setModel(new Matrix4f().translate(0, 3, 0).scale(0.1f));
		light.loadModel();
		block = new NonLightObjects(gl, "cubetexturecase.obj", texture, standardShader);
		block.setModel(new Matrix4f().translate(0, 0.5f, 0).scale(0.2f));
		block.loadModel();
		textureDisplayerShader = new ShaderProgram(gl, "texture.vertex", "texture.fragment");
		standardShader.setFloat("material.shininess", 32);
		sun = new DirLight(gl, new Vector3f(-5, -4, -5), new Vector3f(1), new Vector3f(0.1f, 0.4f, 1));
		sun.loadParameters(standardShader);
		lamp = new PointLight(gl, new Vector3f(0, 3, 0), new Vector3f(1), new Vector3f(1, 0, 0), new Vector3f(0.1f, 0.4f, 1f));
		lamp.loadParameters(standardShader);
		lightShader.setVec3("uni_color", 1, 1, 1);
		shadowTexture = new DepthTexture(gl, 1024, 1024, "dirLight.shadowMap");
		shadowBuffer = new ShadowDepthFrameBuffer<>(gl, shadowTexture);

		FloatBuffer buf = GLBuffers.newDirectFloatBuffer(quadVertices);
		quadVBO = GLBuffers.newDirectIntBuffer(1);
		quadVAO = GLBuffers.newDirectIntBuffer(1);
		gl.glGenVertexArrays(1, quadVAO);
		gl.glGenBuffers(1, quadVBO);
		gl.glBindVertexArray(quadVAO.get(0));
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, quadVBO.get(0));
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, quadVertices.length * Float.BYTES, buf, GL4.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(0);
		gl.glVertexAttribPointer(0, 2, GL4.GL_FLOAT, false, 4 * Float.BYTES, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 4 * Float.BYTES, Float.BYTES * 2);

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		double a = Duration.between(launch, Instant.now()).toMillis() * 0.005;
		float b = (float) Math.cos(a);
		float c = (float) Math.sin(a);
		Matrix4f lightProj = new Matrix4f().ortho(-10.0f, 10.0f, -10.0f, 10.0f, 1.0f, 25f);
		Matrix4f lightView = new Matrix4f().lookAt(5, 4, 5, 0, 0, 0, 0, 1, 0);
		Matrix4f lightSpaceMatrix = new Matrix4f(lightProj).mul(lightView);
		shadowBuffer.getShader().setMat4("uni_lightSpaceMatrix", lightSpaceMatrix);
		standardShader.setMat4("uni_lightSpaceMatrix", lightSpaceMatrix);

		gl.glViewport(0, 0, 1024, 1024);
		shadowBuffer.bind();
		shadowBuffer.bindDepthTexture();
		shadowBuffer.clearBuffer();

		floor.strippedDraw(shadowBuffer.getShader());
		block.strippedDraw(shadowBuffer.getShader());
		light.strippedDraw(shadowBuffer.getShader());

		shadowBuffer.unBind();

		KeyboardInput k = view.getKeyboard();
		if (k.isPressed('i'))
			drawDepthMap();
		else
			drawScene();

		gl.glFlush();

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

	}

	public void drawScene() {
		gl.glClearColor(0.2f, 0.3f, 0.1f, 1);
		gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
		gl.glViewport(0, 0, canvas.getWidth(), canvas.getHeight());
		shadowBuffer.bindDepthTexture(standardShader);
		standardShader.setVec3("viewPos", view.getPosition());
		pushMatrix(view.getLookAt(), projectionMatrix, floor.getShader());
		floor.drawModel();
		pushMatrix(view.getLookAt(), projectionMatrix, block.getShader());
		block.drawModel();
		pushMatrix(view.getLookAt(), projectionMatrix, light.getShader());
		light.drawModel();
	}

	public void drawDepthMap() {
		gl.glClearColor(0.2f, 0.3f, 0.1f, 0);
		gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
		gl.glViewport(0, 0, canvas.getWidth(), canvas.getHeight());
		textureDisplayerShader.bind();
		gl.glActiveTexture(GL4.GL_TEXTURE0);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, shadowTexture.getId());
		gl.glUniform1i(gl.glGetUniformLocation(textureDisplayerShader.getPgrmId(), "depthMap"), 0);
		gl.glBindVertexArray(quadVAO.get(0));

		gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 6);
	}
}

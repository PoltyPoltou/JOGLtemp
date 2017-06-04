package graphicEngine;

import java.awt.*;

import javax.swing.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class Renderer extends JFrame {

	public static void main(String[] args) {
		new Renderer(new Dimension(500, 500), "test");
	}

	private GLJPanel canvas;
	private GL4 gl;
	private ShaderProgram defaultShader;

	protected Renderer(Dimension size, String name) {

		super(name);
		GLProfile profile;
		GLCapabilities capabilities;

		this.setSize(size);
		this.requestFocus();
		profile = GLProfile.get(GLProfile.GL4);
		capabilities = new GLCapabilities(profile);
		canvas = new GLJPanel(capabilities);
		this.getContentPane().add(canvas);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.setVisible(true);

		canvas.addGLEventListener(new GLEventListener() {
			@Override
			public void init(GLAutoDrawable drawable) {
				gl = drawable.getGL().getGL4();
				System.out.println(gl.glGetString(GL.GL_VERSION));
				gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
				gl.glEnable(GL4.GL_DEPTH_TEST);
			}

			@Override
			public void dispose(GLAutoDrawable drawable) {}

			@Override
			public void display(GLAutoDrawable drawable) {
				gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

				gl.glFlush();
			}

			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

		});

	}

}

package graphicEngine;

import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;

import javax.imageio.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;

public class Texture {
	static int PARAM_NEAREST = 0, PARAM_LINEAR = 1, PARAM_WIDTH = 2, PARAM_HEIGHT = 3, PARAM_MIPMAP = 4, PARAM_MIN_FILTER = 5, PARAM_MAG_FILTER = 6;
	String imgPath;
	IntBuffer textId;
	Path file;
	BufferedImage img;
	byte[] imageInByte;
	ByteBuffer byteBfr;
	static int nbTextureUnit = 0;
	static String[] str =
		{
				"uni_Texture1" , "uni_Texture2"
		};
	int textureUnit;

	public Texture(String imgPath, GL4 gl) {

		InputStream stream = this.getClass().getResourceAsStream(imgPath);
		DataBuffer buffer = null;
		boolean alpha;
		textId = GLBuffers.newDirectIntBuffer(1);
		textureUnit = nbTextureUnit++;
		int x = 0, y = 0, rgb;
		this.imgPath = imgPath;
		this.file = Paths.get(imgPath);
		try {
			img = ImageIO.read(stream);
			buffer = img.getRaster().getDataBuffer();
		} catch (IOException e) {
			System.out.println("ERROR:TEXTURE:IMAGE:LOADING:FAILED");
		}
		if (img.getAlphaRaster() == null)
			alpha = false;
		else
			alpha = true;
		byteBfr = GLBuffers.newDirectByteBuffer(buffer.getSize() * 4 / 3);
		byteBfr.mark();
		if (alpha) {
			for (int i = 0; i < buffer.getSize(); i += 4) { // RGB is one byte Alpha one Red one Green one blue
				rgb = img.getRGB(x, y);
				byte a = (byte) (img.getAlphaRaster().getDataBuffer().getElem(i / 4));
				int rgbalpha = (rgb & 0x00FFFFFF) | a << 24;
				byteBfr.putInt(rgbalpha);
				x++;

				if (x == img.getWidth()) {
					y++;
					x = 0;
				}
			}
		} else {
			for (int i = 0; i < buffer.getSize(); i += 4) { // RGB is one byte Alpha one Red one Green one blue
				rgb = img.getRGB(x, y);
				byteBfr.putInt(rgb | 0xFF000000);
				x++;

				if (x == img.getWidth()) {
					y++;
					x = 0;
				}
			}
		}
		byteBfr.flip();
		gl.glGenTextures(1, textId);
		gl.glActiveTexture(GL4.GL_TEXTURE0 + textureUnit);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, textId.get(0));
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		if (alpha)
			gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, img.getWidth(), img.getHeight(), 0, GL4.GL_BGRA, GL4.GL_UNSIGNED_BYTE, byteBfr);
		else
			gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, img.getWidth(), img.getHeight(), 0, GL4.GL_BGR, GL4.GL_UNSIGNED_BYTE, byteBfr);
		gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);

		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		gl.glActiveTexture(GL4.GL_TEXTURE0);

	}

	public int getTextId() {
		return textId.get(0);
	}

	public void bindTexture(GL4 gl, ShaderProgram shader) {
		gl.glActiveTexture(GL4.GL_TEXTURE0 + textureUnit);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, textId.get(0));
		gl.glUniform1i(gl.glGetUniformLocation(shader.getPgrmId(), str[textureUnit]), textureUnit);
	}

	public void unBindTexture(GL4 gl) {
		gl.glActiveTexture(GL4.GL_TEXTURE0 + textureUnit);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
}

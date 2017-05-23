package opengl101;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.GLBuffers;

public class TexturePerso {
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

	public TexturePerso(String imgPath, String fileExt, GL4 gl, boolean alpha) {

		InputStream stream = this.getClass().getResourceAsStream(imgPath);
		DataBuffer buffer = null;
		textId = GLBuffers.newDirectIntBuffer(1);
		textureUnit = nbTextureUnit++;
		int x = 0, y = 0, rgb, offset;
		this.imgPath = imgPath;
		this.file = Paths.get(imgPath);
		if (alpha)
			offset = 4;
		else
			offset = 3;

		try {
			img = ImageIO.read(stream);
			buffer = img.getRaster().getDataBuffer();
		} catch (IOException e) {
			System.out.println("ERROR:TEXTURE:IMAGE:LOADING:FAILED");
		}
		byteBfr = GLBuffers.newDirectByteBuffer(buffer.getSize() * 4 / 3);

		byteBfr.mark();
		for (int i = 0; i < buffer.getSize(); i += offset) { // RGB is one byte Alpha one Red one Green one blue
			rgb = img.getRGB(x, y);
			byteBfr.putInt(rgb | 0xFF000000);
			x++;

			if (x == img.getWidth()) {
				y++;
				x = 0;
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
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, img.getWidth(), img.getHeight(), 0, GL4.GL_BGRA, GL4.GL_UNSIGNED_BYTE, byteBfr);
		gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);

		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		gl.glActiveTexture(GL4.GL_TEXTURE0);

	}

	public int getTextId() {
		return textId.get(0);
	}

	void bindTexture(GL4 gl, ShaderPgrm shader) {
		gl.glActiveTexture(GL4.GL_TEXTURE0 + textureUnit);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, textId.get(0));
		gl.glUniform1i(gl.glGetUniformLocation(shader.getPgrmId(), str[textureUnit]), textureUnit);
	}

	void unBindTexture(GL4 gl) {
		gl.glActiveTexture(GL4.GL_TEXTURE0 + textureUnit);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
}

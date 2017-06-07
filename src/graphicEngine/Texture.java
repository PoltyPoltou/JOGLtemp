package graphicEngine;

import java.awt.image.*;
import java.io.*;
import java.nio.*;

import javax.imageio.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;

import opengl101.*;

public class Texture {
	static int PARAM_NEAREST = 0;
	static int PARAM_LINEAR = 1;
	static int PARAM_WIDTH = 2;
	static int PARAM_HEIGHT = 3;
	static int PARAM_MIPMAP = 4;
	static int PARAM_MIN_FILTER = 5;
	static int PARAM_MAG_FILTER = 6;
	private IntBuffer[] textId;
	private BufferedImage[] img;
	private ByteBuffer[] imgBuffers;
	private int nbTexture;
	private static int nbTextureUnit = 0;
	private static final String DEFAULT_FOLDER_PATH = "textures/";
	//format off
	private static final String[] str =
		{
				"uni_Texture1" ,  "uni_Texture2" , "uni_Texture3"  , "uni_Texture4",
				"uni_Texture5" ,  "uni_Texture6" , "uni_Texture7"  , "uni_Texture8",
				"uni_Texture9" ,  "uni_Texture10", "uni_Texture11" , "uni_Texture12",
				"uni_Texture13",  "uni_Texture14", "uni_Texture15" , "uni_Texture16",
		};
	//format on
	private String[] textureUnitNames;

	public Texture(String[] imgPath, String[] names, GL4 gl) {
		nbTexture = 0;
		textureUnitNames = names;
		img = new BufferedImage[imgPath.length];
		imgBuffers = new ByteBuffer[imgPath.length];
		textId = new IntBuffer[imgPath.length];
		nbTextureUnit += imgPath.length;

		DataBuffer d;
		boolean alpha;
		for (String s : imgPath) {
			d = getDataBufferFromImg(s);
			alpha = hasAlpha(img[nbTexture]);
			imgBuffers[nbTexture] = getByteBufferFromData(alpha, d);
			textId[nbTexture] = GLBuffers.newDirectIntBuffer(1);
			gl.glGenTextures(1, textId[nbTexture]);
			gl.glActiveTexture(GL4.GL_TEXTURE0 + nbTexture);
			gl.glBindTexture(GL4.GL_TEXTURE_2D, textId[nbTexture].get(0));
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR_MIPMAP_LINEAR);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
			if (alpha)
				gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, img[nbTexture].getWidth(), img[nbTexture].getHeight(), 0, GL4.GL_BGRA, GL4.GL_UNSIGNED_BYTE, imgBuffers[nbTexture]);
			else
				gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, img[nbTexture].getWidth(), img[nbTexture].getHeight(), 0, GL4.GL_BGR, GL4.GL_UNSIGNED_BYTE, imgBuffers[nbTexture]);
			gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);

			gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
			gl.glActiveTexture(GL4.GL_TEXTURE0);
			nbTexture++;
		}
	}

	public Texture(String imgPath, String name, GL4 gl) {
		nbTexture = 0;
		textureUnitNames = new String[]
			{
					name
			};
		img = new BufferedImage[1];
		imgBuffers = new ByteBuffer[1];
		textId = new IntBuffer[1];
		nbTextureUnit++;

		DataBuffer d;
		boolean alpha;
		textId[0] = GLBuffers.newDirectIntBuffer(1);
		d = getDataBufferFromImg(imgPath);
		alpha = hasAlpha(img[nbTexture]);
		imgBuffers[nbTexture] = getByteBufferFromData(alpha, d);
		gl.glGenTextures(1, textId[nbTexture]);
		gl.glActiveTexture(GL4.GL_TEXTURE0 + nbTexture);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, textId[nbTexture].get(0));
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		if (alpha)
			gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, img[nbTexture].getWidth(), img[nbTexture].getHeight(), 0, GL4.GL_BGRA, GL4.GL_UNSIGNED_BYTE, imgBuffers[nbTexture]);
		else
			gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, img[nbTexture].getWidth(), img[nbTexture].getHeight(), 0, GL4.GL_BGR, GL4.GL_UNSIGNED_BYTE, imgBuffers[nbTexture]);
		gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);

		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		gl.glActiveTexture(GL4.GL_TEXTURE0);
		nbTexture++;

	}

	private DataBuffer getDataBufferFromImg(String imgPath) {
		FileInputStream stream = null;
		DataBuffer buffer = null;
		try {
			stream = new FileInputStream(new File(DEFAULT_FOLDER_PATH + imgPath));

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			img[nbTexture] = ImageIO.read(stream);
			buffer = img[nbTexture].getRaster().getDataBuffer();
		} catch (IOException e) {
			System.out.println("ERROR:TEXTURE:IMAGE:LOADING:FAILED");
		}
		return buffer;
	}

	private ByteBuffer getByteBufferFromData(boolean alpha, DataBuffer buffer) {
		ByteBuffer data;
		int x = 0, y = 0, rgb;
		data = GLBuffers.newDirectByteBuffer(buffer.getSize() * 4 / 3);
		data.mark();
		if (alpha) {
			for (int i = 0; i < buffer.getSize(); i += 4) { // RGB is one byte Alpha one Red one Green one blue
				rgb = img[nbTexture].getRGB(x, y);
				byte a = (byte) (img[nbTexture].getAlphaRaster().getDataBuffer().getElem(i / 4));
				int rgbalpha = (rgb & 0x00FFFFFF) | a << 24;
				data.putInt(rgbalpha);
				x++;

				if (x == img[nbTexture].getWidth()) {
					y++;
					x = 0;
				}
			}
		} else {
			for (int i = 0; i < buffer.getSize(); i += 4) { // RGB is one byte Alpha one Red one Green one blue
				rgb = img[nbTexture].getRGB(x, y);
				data.putInt(rgb | 0xFF000000);
				x++;

				if (x == img[nbTexture].getWidth()) {
					y++;
					x = 0;
				}
			}
		}
		data.flip();
		return data;
	}

	private boolean hasAlpha(BufferedImage image) {
		if (image.getAlphaRaster() != null)
			return true;
		else
			return false;
	}

	public void bindTexture(GL4 gl, ShaderProgram shader) {
		for (int i = 0; i < nbTexture; i++) {
			gl.glActiveTexture(GL4.GL_TEXTURE0 + i);
			gl.glBindTexture(GL4.GL_TEXTURE_2D, textId[i].get(0));
			gl.glUniform1i(gl.glGetUniformLocation(shader.getPgrmId(), textureUnitNames[i]), i);
		}
	}

	public void unBindTexture(GL4 gl) {
		for (int i = 0; i <= nbTexture; i++) {
			gl.glActiveTexture(GL4.GL_TEXTURE0 + i);
			gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		}
	}
}

//<pre>

package render;

import java.awt.*;
import java.awt.image.*;


/**
 * @author Du Nguyen
 *
 */

public class InteractiveBuffer extends InteractiveMesh {
	InteractiveRenderable rend;
	BufferedImage image;
	Graphics graphics;

	/**
	 * @param m
	 * @param n
	 * @param applet
	 */
	public InteractiveBuffer(int m, int n, int w, int h, InteractiveRenderable ap) {
		rend = ap;
		W = w;
		H = h;
		image = new BufferedImage(W,H,BufferedImage.TYPE_INT_RGB);
		graphics = image.getGraphics();
		rend.render(graphics);		
		pixels = new int[W*H];
		getPixels(pixels,image);
		Geometry g = this.add();
		texture = new Texture(pixels,W,H,"grid",false);
		Material material = new Material();
		material.setTexture(texture);		
		g.setMaterial(material);
		g.setDoubleSided(true);
		g.mesh(m,n);
		
	}
	
	public void getPixels(int[]pixels, BufferedImage image) {
		PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, W, H, pixels, 0, W);
		try {
			pixelgrabber.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		damage = false;
		
	}
	
	public void animate(double time) {
		if (damage) {
			rend.render(image.createGraphics());
			getPixels(pixels,image);
		}
	}
	
	public boolean mouseDown(Event evt, double[]xyz,Geometry g) {
		if (!super.mouseDown(evt,xyz,g))
			return false;
		int[] p = this.getXY(xyz);
		rend.mouseDown(evt,p[0],p[1]);
		return true;
	}
	
	public boolean mouseUp(Event evt, double[]xyz) {
		int[] p = this.getXY(xyz);
		rend.mouseUp(evt,p[0],p[1]);
		damage = true;
		return true;
	}
	
	public boolean mouseDrag(Event evt, double[]xyz) {
		if (!super.mouseUp(evt,xyz))
			return false;
		int[] p = this.getXY(xyz);
		rend.mouseDrag(evt,p[0],p[1]);
		return true;
	}

}

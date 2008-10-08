/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package render.jogl;

import java.awt.*;
import java.awt.image.*;
import java.text.*;
import net.java.games.jogl.*;
import net.java.games.jogl.util.*;

/**
 * @author zster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JoglGraphics extends Graphics {

	static int ARC_PRECISION = 32;
	Color c = Color.white;
	Font f;
	FontMetrics fm;
	GL gl;
	GLU glu;
	GLUT glut;
	double fh = 119.5+33.33;
	double fw = 104.76;
	/**
	 * 
	 */
	public JoglGraphics(GL gl,GLU  glu) {
		super();
		this.gl = gl;
		this.glu = glu;
		glut = new GLUT();
		f = new Font(null,Font.PLAIN,12);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#create()
	 */
	public Graphics create() {
		// TODO Auto-generated method stub
		return null;
	}




	//
	// HELPERS
	//
	private void beginShapeDraw(int x, int y, int width, int height)
	{
		enable2dDrawMode(true);
		gl.glEnable(GL.GL_CULL_FACE);
		float sx = (float)(width - 1) / 2.0f;
		float sy = (float)(height - 1) / 2.0f;
		gl.glTranslatef((float)x + sx, (float)y + sy, 0);
		gl.glScalef(sx, sy, 1);
	}

	private void endShapeDraw()
	{
		enable2dDrawMode(false);
		gl.glDisable(GL.GL_CULL_FACE);
	}

	//
	// OVALS
	//

	public void oval(int x, int y, int width, int height, boolean filled)
	{
		beginShapeDraw(x, y, width, height);
			float MAX = (float)(2*Math.PI);
			float dt = MAX / ARC_PRECISION;
			gl.glBegin(filled ? GL.GL_TRIANGLES : GL.GL_LINES);
				for (float t = 0; t < MAX; t += dt)
				{
					if (filled)
						gl.glVertex2f(0,0);
					gl.glVertex2d(Math.cos(t), Math.sin(t));
					gl.glVertex2d(Math.cos(t + dt), Math.sin(t+dt));
				}
			gl.glEnd();
		endShapeDraw();
	}

	public void drawOval(int x, int y, int width, int height)
	{
		oval(x, y, width, height, false);
	}

	public void fillOval(int x, int y, int width, int height)
	{
		oval(x, y, width, height, true);
	}

	//
	// RECTANGLES
	//

	public void rect(int x, int y, int width, int height, boolean filled)
	{
		beginShapeDraw(x, y, width, height);
			gl.glBegin(filled ? GL.GL_QUADS : GL.GL_LINE_LOOP);
				gl.glVertex2f( 1,  1);
				gl.glVertex2f(1,  -1);
				gl.glVertex2f(-1, -1);
				gl.glVertex2f( -1, 1);
			gl.glEnd();
		endShapeDraw();
	}

	public void fillRect(int x, int y, int width, int height)
	{
		rect(x, y, width, height, true);
	}

	public void drawRect(int x, int y, int width, int height)
	{
		rect(x, y, width, height, false);
	}

	public void drawLine(int x1, int y1, int x2, int y2)
	{
		enable2dDrawMode(true);
			gl.glColor3fv(getColorf());
			gl.glBegin(GL.GL_LINES);
				gl.glVertex2f(x1, y1);
				gl.glVertex2f(x2, y2);
			gl.glEnd();
		enable2dDrawMode(false);
	}

	public void polygon(int[] xPoints, int[] yPoints, int nPoints, boolean filled)
	{
		//assert(xPoints != NULL);
		//assert(yPoints != NULL);
		enable2dDrawMode(true);
			gl.glColor3fv(getColorf());
			gl.glBegin(filled ? GL.GL_POLYGON : GL.GL_LINE_LOOP);
				for (int i = 0; i < nPoints; ++i)
					gl.glVertex2f(xPoints[i], yPoints[i]);
			gl.glEnd();
		enable2dDrawMode(false);
	}

	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints)
	{
		polygon(xPoints, yPoints, nPoints, false);
	}

	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints)
	{
		polygon(xPoints, yPoints, nPoints, true);
	}

	public void roundRectCorner(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean filled)
	{
		gl.glPushMatrix();
			gl.glScalef(arcWidth / 2, arcHeight / 2, 1);
			gl.glBegin(filled ? GL.GL_TRIANGLES : GL.GL_LINES);
				float M_PI_2 = (float) (Math.PI/2);
				float dt = (float)(M_PI_2 / 32.0f);
				for (float t = 0; t < M_PI_2; t += dt)
				{
					if (filled) gl.glVertex2f(0, 0);	//for triangle
					gl.glVertex2d(Math.cos(t), -Math.sin(t));
					gl.glVertex2d(Math.cos(t + dt), -Math.sin(t + dt));
				}
			gl.glEnd();
		gl.glPopMatrix();
	}

	public void roundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean filled)
	{
		enable2dDrawMode(true);
		gl.glTranslatef(x, y, 0);
		if (!filled) {
			gl.glBegin(GL.GL_LINES);  
				gl.glVertex2f(width - arcWidth/2, 0);
				gl.glVertex2f(arcWidth/2, 0);
				gl.glVertex2f(arcWidth/2 , height);
				gl.glVertex2f(width - arcWidth/2, height);
				gl.glVertex2f(0, arcHeight/2);
				gl.glVertex2f(0, height - arcHeight/2);
				gl.glVertex2f(width, height - arcHeight/2);
				gl.glVertex2f(width, arcHeight/2);
			gl.glEnd();
		} else {
			gl.glBegin(GL.GL_QUADS);
				gl.glVertex2f(width - arcWidth/2, 0);
				gl.glVertex2f(arcWidth/2, 0);
				gl.glVertex2f(arcWidth/2, arcHeight/2);
				gl.glVertex2f(width - arcWidth/2, arcHeight/2);
				
				gl.glVertex2f(width - arcWidth/2, height - arcHeight/2);
				gl.glVertex2f(arcWidth/2 , height - arcHeight/2);
				gl.glVertex2f(arcWidth/2 , height);
				gl.glVertex2f(width - arcWidth/2, height);
	
				gl.glVertex2f(0, arcHeight/2);
				gl.glVertex2f(0, height - arcHeight/2);
				gl.glVertex2f(arcWidth/2, height - arcHeight/2);
				gl.glVertex2f(arcWidth/2, arcHeight/2);
				
	
				gl.glVertex2f(width, height - arcHeight/2);
				gl.glVertex2f(width, arcHeight/2);
				gl.glVertex2f(width - arcWidth/2, arcHeight/2);
				gl.glVertex2f(width - arcWidth/2, height - arcHeight/2);
				
				gl.glVertex2f(arcWidth/2, arcHeight/2);
				gl.glVertex2f(width - arcWidth/2, arcHeight/2);
				gl.glVertex2f(width - arcWidth/2, height -  arcHeight/2);
				gl.glVertex2f(arcWidth/2, height -  arcHeight/2);			
			
			gl.glEnd();
		}
			// draw rounded corners:
		gl.glPushMatrix();
			float halfW = (float)width / 2.0f;
			float halfH = (float)height / 2.0f;
			gl.glTranslatef(halfW, halfH, 0);	// center
			for (int i = 0; i < 4; ++i)
			{
				gl.glPushMatrix();
					gl.glScalef((i == 1 || i == 2) ? -1 : 1,i < 2 ? 1 : -1,1);
					gl.glTranslatef(halfW - arcWidth / 2, -halfH + arcHeight / 2, 0);
					roundRectCorner(x, y, width, height, arcWidth, arcHeight, filled);
				gl.glPopMatrix();
			}
		gl.glPopMatrix();
			
		enable2dDrawMode(false);
	}

	public void drawRoundRect(int x, int y, int width, int height,int arcWidth, int arcHeight)
	{
		roundRect(x, y, width, height, arcWidth, arcHeight, false);
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		roundRect(x, y, width, height, arcWidth, arcHeight, true);
	}

	
	public void drawString(String str, int x, int y)
	{
		
		int fsize = f.getSize();
		float scale = (float)(1.*fsize/fh);
		if (f.isBold())
			gl.glLineWidth(2);
		//f.
		//		TODO this is very important
		enable2dDrawMode(true);
		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0);
		gl.glScalef(scale,-scale,scale);
		//gl.glScalef(.25f,-.25f,.25f);
		glut.glutStrokeString(gl,GLUT.STROKE_MONO_ROMAN,str);
		gl.glPopMatrix();
		gl.glLineWidth(1);
		enable2dDrawMode(false);
	}

	private void enable2dDrawMode(boolean enable)
	{
		if (enable) {
			// switch to project mode:
			gl.glMatrixMode(GL.GL_PROJECTION);
			
			
			//enable transparency
			if (c.getAlpha()!=255) {			
				gl.glDepthMask(false);
			}
				// Enable transparency:
				 
				//This is the safer function (but we would have to sort)
				// This blend function renders the same independently of the
				//order in which
				// the transparent polygons are drawn.
			gl.glEnable (GL.GL_BLEND);
			gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			//gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE);
			gl.glEnable(GL.GL_LINE_SMOOTH);
			gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);
			
			
			
			
			// push perspective matrix:
			gl.glPushMatrix();
			// load identity and setup ortho mode:
			gl.glLoadIdentity();
			float params[] = new float[4];
			gl.glGetFloatv(GL.GL_VIEWPORT, params);
			glu.gluOrtho2D(0, params[2], 0, params[3]);
			// set to modelview matrix mode:
			gl.glMatrixMode(GL.GL_MODELVIEW);
			// push current matrix:
			gl.glPushMatrix();
			// load identity:
			gl.glLoadIdentity();
			// this is recommended on page 601 of the redbook for accurate pixel alignment:
			gl.glTranslatef(0.375f, 0.375f, 0.0f);
			// flip y axis so that y=0 starts at top and increases downward:
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glTranslatef(1, params[3] - 1, 0);
			gl.glScalef(1, -1, 1);
			gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT | GL.GL_ENABLE_BIT | GL.GL_LIGHTING_BIT | GL.GL_TEXTURE_BIT);
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glDisable(GL.GL_LIGHTING);
			//gl.glColor4iv(getColori());
			gl.glColor4fv(getColorf());
			
		}
		else {
			gl.glPopAttrib();
			gl.glPopMatrix();
			// set back to projection mode for a moment:
			gl.glMatrixMode(GL.GL_PROJECTION);
			// set back to perspective matrix:
			gl.glPopMatrix();
			// set back to modelview mode:
			gl.glMatrixMode(GL.GL_MODELVIEW);
			// pop the matrix we messed with before:
			gl.glPopMatrix();
			gl.glDisable(GL.GL_BLEND);
			if (c.getAlpha()!=255)
				gl.glDepthMask(true);			
		}
	}


	/* (non-Javadoc)
	 * @see java.awt.Graphics#translate(int, int)
	 */
	public void translate(int x, int y) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#getColor()
	 */
	public Color getColor() {
		return c;
	}
	
	private float[] getColorf() {
		float cf[] = new float[4];
		Color color = getColor();
		cf[0] = (float)(1.*color.getRed()/255);
		cf[1] = (float)(1.*color.getGreen()/255);
		cf[2] = (float)(1.*color.getBlue()/255);
		cf[3] = (float)(1.*color.getAlpha()/255);
		return cf;
	}

	private int[] getColori() {
		int cf[] = new int[4];
		Color color = getColor();
		cf[0] = color.getRed();
		cf[1] = color.getGreen();
		cf[2] = color.getBlue();
		cf[3] = color.getAlpha();
		return cf;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#setColor(java.awt.Color)
	 */
	public void setColor(Color c) {
		this.c = c;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#setPaintMode()
	 */
	public void setPaintMode() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#setXORMode(java.awt.Color)
	 */
	public void setXORMode(Color c1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#getFont()
	 */
	public Font getFont() {
		return f;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#setFont(java.awt.Font)
	 */
	public void setFont(Font font) {
		f = font;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#getFontMetrics(java.awt.Font)
	 */
	public FontMetrics getFontMetrics(Font f) {
		// TODO Auto-generated method stub
		return super.getFontMetrics();
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#getClipBounds()
	 */
	public Rectangle getClipBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#clipRect(int, int, int, int)
	 */
	public void clipRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#setClip(int, int, int, int)
	 */
	public void setClip(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#getClip()
	 */
	public Shape getClip() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#setClip(java.awt.Shape)
	 */
	public void setClip(Shape clip) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#copyArea(int, int, int, int, int, int)
	 */
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		// TODO Auto-generated method stub

	}


	/* (non-Javadoc)
	 * @see java.awt.Graphics#clearRect(int, int, int, int)
	 */
	public void clearRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}



	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawArc(int, int, int, int, int, int)
	 */
	public void drawArc(
		int x,
		int y,
		int width,
		int height,
		int startAngle,
		int arcAngle) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#fillArc(int, int, int, int, int, int)
	 */
	public void fillArc(
		int x,
		int y,
		int width,
		int height,
		int startAngle,
		int arcAngle) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawPolyline(int[], int[], int)
	 */
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		// TODO Auto-generated method stub

	}


	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawString(java.text.AttributedCharacterIterator, int, int)
	 */
	public void drawString(
		AttributedCharacterIterator iterator,
		int x,
		int y) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(
		Image img,
		int x,
		int y,
		int width,
		int height,
		ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(
		Image img,
		int x,
		int y,
		Color bgcolor,
		ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(
		Image img,
		int x,
		int y,
		int width,
		int height,
		Color bgcolor,
		ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(
		Image img,
		int dx1,
		int dy1,
		int dx2,
		int dy2,
		int sx1,
		int sy1,
		int sx2,
		int sy2,
		ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
	 */
	public boolean drawImage(
		Image img,
		int dx1,
		int dy1,
		int dx2,
		int dy2,
		int sx1,
		int sy1,
		int sx2,
		int sy2,
		Color bgcolor,
		ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

}

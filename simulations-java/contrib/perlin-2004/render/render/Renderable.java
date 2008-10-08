/*
 * Created on Apr 5, 2004
 *
 */
package render;

import java.awt.Graphics;

/**
 * Main interface that for objects to be rendered. 
 */
public interface Renderable {
   
   /**
    * initialize all your geometries, materials and lights here
    */
	public void initialize();
	
	/**
	 * this is the main animation routine, called per frame
	 * @param time system time in seconds
	 */
	public void animate(double time);
	
	/**
	 * allows for drawing an overlay layer on top of the rendered graphics
	 * @param g graphics onto which the overlay will be rendered
	 */
	public void drawOverlay(Graphics g);
}

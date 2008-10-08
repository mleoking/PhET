//<pre>

/*
 * Created on Apr 7, 2004
 * Example code demonstrating use of the renderer. 
 * using RenderPanel vs. RenderPanelGL is the choice between hardware
 * and software rendering. 
 * 
 * @author zster
 * 
 */
package srr;
import java.awt.Graphics;
import java.awt.Panel;

import javax.swing.*;
import render.*;
import render.demo.PointApp;

public class Demo2 extends JFrame implements Renderable {

   //the renderer
   RenderablePanel render;

	public Demo2() {
		super("simple example");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 600);

		render = new RenderPanel();
//		render = new RenderPanelGL(600,600);

		render.setRenderable(this);

		this.setBounds(0, 0, 600, 600);
		this.getContentPane().setBounds(0, 0, 600, 600);
		this.getContentPane().add((Panel) render);

		setVisible(true);
	}

	public void initialize() {
		render.addLight(1, 1, 1, 1, 1, 1); // LIGHTS
		render.addLight(0, 1, 0, 1, 1, 1);
		render.setBgColor(.2, .2, .8); // BACKGROUND COLOR
		render.push();
		render.transform(render.getWorld()); // INITIAL VIEW ANGLE
		render.pop();
		Material red = new Material();
		red.setDiffuse(1, 0, 0);
		Geometry cube1 = render.getWorld().add().cube().setMaterial(red);

		render.push();
		render.rotateY(-Math.PI/12.);
		render.rotateX(-Math.PI/2.4);
		render.scale(2,3,1);
		render.transform(cube1);
		render.pop();

	}

	public void animate(double time) {
	   render.pause();
		//no need to keep re-rendering if nothing is changing
	}


	public static void main(String[] args) {
		Demo2 pointApp = new Demo2();
		pointApp.render.init();
		pointApp.render.start();
	}

   /**
    * @see render.Renderable#drawOverlay(java.awt.Graphics)
    */
   public void drawOverlay(Graphics g) { }
}
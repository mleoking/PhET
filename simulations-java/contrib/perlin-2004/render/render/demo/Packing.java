//<pre>

package render.demo;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Panel;

import render.*;

//
//SHOW CLOSE-PACKING OF SPHERES

/**
 * revised version of Ken Perlin's code from 
 * http://mrl.nyu.edu/~perlin/experiments/packing/
 *
 * Created on Nov 12, 2004
 */
public class Packing extends Applet implements Renderable {

   //** BEGIN ADDED CODE  **// 

   RenderablePanel render;

   public void init() {

      //  decide on software vs. hardware rendering :
      render = new RenderPanel();
    //render = new RenderPanelGL(600,600);

      //set this as the renderable 
      render.setRenderable(this);
      //initialize the renderer
      render.init();

      //set up local layout gui 
      this.setLayout(new BorderLayout());
      this.setBounds(0, 0, 600, 600);
      this.add((Panel) render);
      this.setVisible(true);

      //start rendering
      render.start();

   }

   // ** END ADDED CODE  **//
   // additionally all render calls now need to be 
   // prefixed by "render."

   Geometry spheres, links;
   Material pearl, bronze, steel;
   double radius = .5, fov = .3;
   boolean showLinks = true;

   // INITIALIZE EVERYTHING
   public void initialize() {

      // SET BACKGROUND COLOR, FIELD OF VIEW AND FOCAL LENGTH

      render.setBgColor(.4, .4, .8);
      render.setFOV(fov);
      render.setFL(20);

      // DEFINE X,Y,Z AND RED,GREEN,BLUE FOR EACH LIGHT SOURCE

      render.addLight(1, 1, 1, .5, .4, .3);
      render.addLight(-1, 1, -1, .1, .07, .35);
      render.addLight(1, 1, -1, .012, .02, .22);
      render.addLight(.5, 1, -1, .012, .02, .22);
      render.addLight(.0, -1, -1, .22, .12, .02);

      // CREATE SURFACE MATERIALS

      pearl = (new Material()).setColor(.8, .8, .8, 1, 1, 1, 10, .4, .4, .4);
      bronze = (new Material()).setColor(.5, .35, .25, 1, 1, 1, 3, .2, .14, .1);
      steel = (new Material()).setColor(.5, .5, .5, 1.5, 1.5, 1.5, 20, .1, .1, .25);

      pearl.noiseF = 2;
      pearl.noiseA = 1;
      bronze.noiseF = 3;
      bronze.noiseA = .5;

      spheres = render.getWorld().add();
      links = render.getWorld().add();

      // MAKE THE CENTRAL (WHITE) SPHERE

      Geometry s = spheres.add().setMaterial(pearl).add().ball(16);

      // PLACE THE 12 SURROUNDING (RED) SPHERES AS FOLLOWS:

      for (int i = 0; i < 12; i++) {
         render.push();

         // TRAVEL AROUND THE EQUATOR IN 12 EQUAL STEPS

         render.rotateY(i * Math.PI / 6);

         // ODD NUMBERED SPHERE: REMAIN ON THE EQUATOR
         // ODD MULTIPLE OF TWO: TILT UP BY .955 RADIANS
         // MULTIPLE OF FOUR:    TILT DOWN  .955 RADIANS

         render.rotateX(i % 2 != 0 ? 0 : i % 4 != 0 ? -.955 : .955);

         // CREATE AND POSITION THE SPHERE OBJECT

         render.translate(0, 0, 2);
         render.transform(s = spheres.add());

         // - CREATE THE (RE-SIZABLE) SPHERICAL BALL

         s.add().ball(12).setMaterial(bronze);

         // - CREATE A CYLINDER LINKING TO THE CENTER SPHERE

         render.translate(0, 0, -1);
         render.scale(.07, .07, 1);
         render.transform(links.add().cylinder(16).setMaterial(steel));

         render.pop();
      }

      // SET THE INITIAL SIZE FOR EACH SPHERE

      scaleSpheres(radius);
   }

   // SCALE ALL SPHERES

   void scaleSpheres(double r) {
      radius = Math.max(.2, Math.min(1, r));
      render.scale(radius, radius, radius);
      for (int i = 0; i <= 12; i++)
         render.transform(spheres.child[i].child[0]);
   }

   // RESPOND TO USER KEYBOARD COMMANDS

   public boolean keyUp(Event e, int key) {
      super.keyUp(e, key);
      switch (key) {
         case ' ': // SPACE KEY:   TOGGLE LINKS
            showLinks = !showLinks;
            steel.setTransparency(showLinks ? 0 : 1);
            break;
         case 1004: // UP ARROW:    NARROW VIEW
            render.setFOV(fov = Math.max(.1, fov - .05));
            break;
         case 1005: // DOWN ARROW:  WIDEN VIEW
            render.setFOV(fov = Math.min(.5, fov + .05));
            break;
         case 1006: // LEFT ARROW:  SHRINK SPHERES
            scaleSpheres(radius - .1);
            break;
         case 1007: // RIGHT ARROW: GROW SPHERES
            scaleSpheres(radius + .1);
            break;
      }
      render.refresh();
      return true;
   }

   /** 
    * @see render.Renderable#animate(double)
    */
   public void animate(double time) {
   // TODO Auto-generated method stub

   }

   /** 
    * @see render.Renderable#drawOverlay(java.awt.Graphics)
    */
   public void drawOverlay(Graphics g) {
   // TODO Auto-generated method stub

   }

}

package render;

//<pre>
import render.*;

public class BouncingThingie extends RenderApplet
{
   public void initialize() {
      setBgColor(.7,.7,1); // SKY COLORED BACKGROUND
      setFOV(.6); // SMALLER FIELD OF VIEW

      ren.isHeadsUp = false;

      Material red = new Material(), gold  = new Material();
      red.setColor(.5,0,0, 1,1,1,20); // WHITE HILITE MAKES PLASTIC LOOK
      double r=.4,g=.3,b=.12,S=3.3,G=.13;
      gold.setColor(r,g,b,S*r,S*g,S*b,10);//.setGlow(G*r,G*g,G*b);

      world.add().cylinder(15).setMaterial(red); // RED CYLINDER
      world.add().ball(30).setMaterial(gold);    // GOLD BALL
      world.child[1].addNoise(4,.15); // NOISE HAS FREQ. AND AMPLITUDE
      push();
	 translate(0,-1,0);
         rotateX(Math.PI/2);
         transform(world.child[0]); // MAKE THE CYLINDER VERTICAL
      pop();

      addLight( 1, 1,-1,  .5 ,.4 ,.5 ); // USE MULTI-TINTED SOFT LIGHTING
      addLight( 1,-1, 1,  .6 ,.4 ,.4 );
      addLight(-1, 1, 1,  .4 ,.4 ,.6 );
      addLight(-1,-1, 1,  .35,.2 ,.2 );
      addLight(-1, 1,-1,  .2 ,.25,.3 );
      addLight( 1,-1,-1,  .3 ,.2 ,.25);
   }

   

   public void animate(double time) {
      double s = Math.sin(10*time), c = Math.cos(10*time);
      push();
         translate(0, 1.2 + .2*c, 0);
         scale(1 - .1*s, 1 + .2*s, 1 - .1*s);
         transform(world.child[1]);
      pop();
   }
}


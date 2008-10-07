// Copyright 2001 Ken Perlin <pre>

import render.*;

public class Face extends RenderApplet {

   Geometry s, eyes, rightEye, mouth, teeth, insideNose;
   Material skin;
   boolean transparentSkin = false;

   double aahTarget, oohTarget, lidsTarget, browTarget, smileTarget;

   double transition(double aTarget, double a) {
      return .5 * a + .5 * aTarget;
   }

   public void set(int i, double v) {
      switch (i) {
      case -1:
         state = 0;
         break;
      case 0:
         v = 2 * (v - .5);
         aahTarget = 1.1 * (v > 0 ? Math.pow(v,.2) : -Math.pow(-v,.2));
         state = 1;
         break;
      case 1:
         oohTarget = v - .2;
         state = 1;
         break;
      case 2:
         lidsTarget = 1.5 * v - 1.2;
         state = 1;
         break;
      case 3:
         browTarget = .1 * (v - .5);
         state = 1;
         break;
      case 4:
         smileTarget = .16 * (v - .5) + .02;
         state = 1;
         break;
      }
   }

   public void initialize() {
      setBgColor(.2,.2,.2);

      skin = new Material();
      skin.setColor(.58,.35,.15,.15,.15,.2,4).setAmbient(.2,.15,.1);

      s = world.add();
      s.add().globe( 5,40, .94,1.01, 0.0,1.0);
      s.add().globe(20,20, .00,.50, 0.0,1.0);
      s.add().globe( 5,40, .49,.56, 0.0,1.0);

      s.add().globe(20,10, .55,.95, 0.0,.2);
      s.add().globe(20,20, .55,.95, 0.2,.4);
      s.add().globe(20,15, .55,.95, 0.4,.473);

      s.add().globe(10,10, .55 ,.69 , .471,.565);
      s.add().globe(25,35, .686,.814, .471,.565);
      s.add().globe(10,10, .81 ,.95 , .471,.565);

      s.add().globe(40,30, .55,.95, .564,.67);

      s.add().globe(20,20, .55,.95, .66,1.0);

      push();
         rotateX(-Math.PI/2);
         for (int i = 0 ; i < s.child.length ; i++)
            if (s.child[i] != null)
               transform(s.child[i]);
      pop();
      s.setMaterial(skin);

      Material darkRed = new Material();
      darkRed.setColor(0,0,0).setAmbient(.2,0,0).setTransparency(0);

      mouth = world.add().globe(10,5, 0,1, .5,1);
      mouth.setMaterial(darkRed);

      Material white = new Material();
      white.setColor(.1,.1,.1, .5,.5,.5,30).setAmbient(.6,.6,.6);

      Material iris = new Material();
      iris.setColor(.4,.5,.3).setAmbient(.4,.5,.3);

      Material cornea = new Material();
      cornea.setColor(0,0,0, 10,10,10,30).setTransparency(.5);

      eyes = world.add();
      for (int i = 0 ; i < 2 ; i++) {
         eyes.add();
         eyes.child[i].add().ball(10).setMaterial(white);
         eyes.child[i].add().globe(10,3, 0,1, .9,1); // PUPIL
         eyes.child[i].add().globe(10,3, 0,1, .85,.91).setMaterial(iris);
         eyes.child[i].add().globe(10,6, 0,1, .85,1).setMaterial(cornea);
         push();
            rotateY(i==0?-.2:.2);
            translate(0,0,.02);
            transform(eyes.child[i].child[1]);
            translate(0,0,.02);
            transform(eyes.child[i].child[2]);
            scale(1,1,2);
            translate(0,0,-.44);
            transform(eyes.child[i].child[3]);
         pop();
      }

      Material dark = new Material();
      dark.setColor(0,0,0).setTransparency(.8);

      insideNose = world.add().disk(8);
      insideNose.setMaterial(dark);

      teeth = world.add().tube(10);
      teeth.setMaterial(white);

      addLight(1,1,1, 1,1,1);
      addLight(-1,-1,0, .6,.6,1);

      setFOV(.55);
   }

   private double time = 0;
   private int frameCount = 0;
   private int state = 0;

   double aah, ooh, lids, brow, smile;

   public void animate(double time) {

//      skin.setTransparency(transparentSkin ? .5 : 0);

      pullCount = 0;

      this.time = time;
      boolean T = (int)time % 2 == 0;

      if (state == 0) {
         aah = Math.min(1,Math.max(-1,1.8*Noise.noise(.5*time)));
         aah = 1.1 * (aah > 0 ? Math.pow(aah, .2) : -Math.pow(-aah,.2));
         ooh = .5 + Noise.noise(time+100);
         lids = Noise.noise(time+200);
         lids = (lids < 0 ? lids : .5*Math.pow(2*lids,.2));
         lids = 1.2 * Math.pow(.5+lids,8) - .7;
         if (lids > 0) lids = .5;
         brow = .05*Noise.noise(time+300);
         smile = .2*Noise.noise(.3*time+400) + .02;
      }
      else {
         aah   = transition(aahTarget, aah);
         ooh   = transition(oohTarget, ooh);
         lids  = transition(lidsTarget, lids);
         brow  = transition(browTarget, brow);
         smile = transition(smileTarget, smile);
      }

      double x=0, xm=0, y=0, z=0, t=0, l=(.5-lids)/1.3;

      double theta = .02 * Noise.noise(time);
      double phi   = .02 * Noise.noise(time + 100);


      push();
         if (frameCount < 2)
            scale(0,0,0);

         rotateY(theta);
         rotateX(phi);
         push();
            translate(0,.79,.92);
            double lookX = .007*((int)(1000*Noise.noise(.2*time + 1000))%10);
            double lookY = .01 + .05*Noise.noise(5*time + 1100);
            push();
               translate(-.59,0,0);
               rotateY(lookX);
               rotateX(lookY);
               scale(.3,.3,.3);
               transform(eyes.child[0]);
            pop();
            push();
               translate(.59,0,0);
               rotateY(lookX);
               rotateX(lookY);
               scale(.3,.3,.3);
               transform(eyes.child[1]);
            pop();
         pop();

         push();
            translate(0,-.19,1.45 + .04*ooh);
            scale(.34-.25*ooh,.2,.1);
            transform(mouth);
         pop();

         push();
            translate(0,-.03+.01*aah,1.25);
            rotateX(-Math.PI/2);
            scale(.36,.4,.1);
            transform(teeth);
         pop();

         push();
            translate(0,.3,1.73);
            rotateX(Math.PI/4);
            scale(.2,.2,1);
            transform(insideNose);
         pop();
      pop();

      push();
	 if (frameCount < 1)
	    scale(0,0,0);

         scale(2.05,3,2.2);
         transform(s);
         push(); // CRANIAL BULGE IN BACK
            scale(1,1,1.1);
            pull(s, 0,0,0, -.4,.4,1, 0,-1,-1);
         pop();
         push(); // LATERAL CRANIAL BULGE
            scale(1.1,1,1);
            pull(s, 0,0,0, .5,.85,1, 0,0,0);
         pop();
         push(); // ADJUST SHAPE OF TOP OF HEAD
            scale(1,.975,1);
            translate(0,0,-.05);
            pull(s, -.9,0,.9, .5,1,1, 0,0,0);
         pop();
         push(); // NECK
            scale(.4,1,.35);
            pull(s, 0,0,0, 1,-.7,-.7, 0,0,0);
            scale(5,1,7);
            pull(s, 0,0,0, -.4,-1,-1, 0,0,0);
         pop();
         push(); // TEMPLES
            scale(.8,.9,1);
            pull(s, 0,1,1,   1,1,-.2, 0,0,0);
            pull(s, 0,-1,-1, 1,1,-.2, 0,0,0);
         pop();
         push(); // JAW
            rotateX(.21+.04*aah);
            scale(1.18,1,1.3);
            translate(0,.03,.13);
            pull(s, -.9,0,.9, .3,-.2,-.4, .0,.2,.2);
         pop();

         push(); // CHIN
            translate(0,-.03-.02*aah,.05-.03*aah);
            pull(s, -.4,0,.4, -.38,-.26,-.2-.03*aah, 0,1,1);
         pop();
         push(); // EYE SOCKETS
            push();
               translate(0,0,-.2);
               pull(s, -.01,.2,.2,  .45,.3,.1+.5*smile, 0,1,1);
               pull(s, .01,-.2,-.2, .45,.3,.1+.5*smile, 0,1,1);
            pop();
            push();
               translate(0,brow>0?1.2*brow:2*brow,-.03);
               pull(s, -.45,0,.45, .2,.45,.6, 0,1,1);
            pop();
            push();
               translate(0,0,-.03);
               pull(s, 0,0,0, .3,.1,.1, 0,1,1);
            pop();
         pop();
         push(); // EYE LIDS
            y = -.015 + .1*lids;
            z = 0;
            push(); // TO HELP EYES TO SHUT
               translate(0,0,.2 * (.5+.5*lids));
               pull(s, -.10,-.36,-.53,  .29,.27,.25, .2,.8,.8);
               pull(s,  .10, .36, .53,  .29,.27,.25, .2,.8,.8);
            pop();

            translate(0,0,.2);
            push(); // UPPER LID
               translate(0,-.37,-z);
               rotateX(y);
               translate(0,.37,z+.01);
               pull(s,  .10, .36, .53,  .39,.285,.27, .2,.8,.8);
               pull(s, -.10,-.36,-.53,  .39,.285,.27, .2,.8,.8);
            pop();

            push(); // LOWER LID
               t = Math.abs(smile) * l * .5;
               x = smile > 0 ? 0 : .4*t;
               translate(0,-.37,-z);
               rotateX(-y);
               translate(0,.37+t,z+y+.7*x);
               pull(s,  .10-x/2, .36-x, .53,  .27,.255,.17, .2,.9,.9);
               pull(s, -.10+x/2,-.36+x,-.53,  .27,.255,.17, .2,.9,.9);
            pop();
         pop();
         push(); // CHEEKS
            push();
               translate(.5*smile,-.04+.03*lids+.5*smile,.25);
               pull(s, .1,.7,.7,   .32,.12,-.2, 0,1,1);
            pop();
            push();
               translate(-.5*smile,-.04+.03*lids+.5*smile,.25);
               pull(s, -.1,-.7,-.7, .32,.12,-.2, 0,1,1);
            pop();
         pop();
         push(); // BROW FURROWS
            translate(0,0,-.05);
            pull(s, -.6,.4,.4,  .4+brow,.6,.6, 0,1,1);
            pull(s, .6,-.4,-.4, .4+brow,.6,.6, 0,1,1);
         pop();
         push(); // NOSE
            push();
               translate(0,-.01,.20);
               pull(s, -.25,0,.25, .36,.15,.015, 0,1,1);
               translate(0,.037 + .003*aah,-.18);
               pull(s, -.15,0,.15, .24,.15,.033, 0,1,1);
            pop();
            push();
               translate(0,.02+.006*aah,0); // NOSTRILS
               push();
                  push();
                     translate(-.04,0,0);
                     pull(s, -.2,-.1,0, .2,.09,.06, .73,.81,.88);
                  pop();
                  translate(0,.2,-.15);
                  pull(s, -.085,-.045,-.01, .12,.06,.05, 0,1,1);
               pop();
               push();
                  push();
                     translate(.04,0,0);
                     pull(s, .2,.1,0, .2,.09,.06, .73,.81,.88);
                  pop();
                  translate(0,.2,-.15);
                  pull(s,  .085, .045, .01, .12,.06,.05, 0,1,1);
               pop();
            pop();
         pop();
         push(); // LIPS
            x = .35-.2*ooh;
            push(); // MOUTH CAVITY
               translate(0,0,-.01-.3*Math.pow(Math.max(.01,.3+.7*aah), .2));
               pull(s, -x,0,x, -.035,-.055,-.08, 0,1,1);
            pop();
            push(); // UPPER LIP
               xm = Math.max(x,.3);
               translate(0,-.003+.012*aah,.065+.01*(ooh-aah));
               translate(0,.13,.63);
               rotateX(-.65); // CURL UP
               translate(0,-.13,-.63);
               pull(s, -x,0,x, -.08,-.05,.18, 0,1,1);
            pop();
            push(); // LOWER LIP
               y = .03*aah;
               translate(0,-y,.03+.005*(ooh-aah));
               pull(s, -x,0,x, -.20-.03*ooh,-.09,-.06, 0,1,1);
               translate(0,-.16,.75);
               rotateX(.3); // CURL DOWN
               translate(0,.16,-.75);
               pull(s, -x,0,x, -.21,-.12,-.06, .7,.75,.75);
            pop();
            translate(0,0,.04*ooh); // PUSH MOUTH FWD WHEN PUCKERING
            pull(s, -x-.1,0,x+.1, -.20,-.05,.01, 0,1,1);
            push(); // PULL UP OR DOWN SIDES OF FACE AROUND MOUTH
               x = .45-.2*ooh;
               translate(0,-.02-.02*aah + smile,0);
               pull(s, 0, x, 2*x, -.8,-.02,.2, 0,1,1);
               pull(s, 0,-x,-2*x, -.8,-.02,.2, 0,1,1);
            pop();
         pop();
         push(); // TURN HEAD
            rotateY(theta);
            rotateX(phi);
            pull(s, 0,0,0, -1,-.2,-.2, 0,0,0);
         pop();
      pop();

      frameCount++;
   }

   public void setTransparent() {
      transparentSkin = !transparentSkin;
      renderer.updateTransparency = true;
   }

   public void setVisibleMesh() {
      if (transparentSkin)
         setTransparent();
      renderer.showMesh = ! renderer.showMesh;
   }

   private int pullCount = 0;
   private double pStartTime;
   private boolean progression = false;

   public void setProgression() {
      progression = true;
      pStartTime = time;
      maskTime = time;
      for (int i = 0 ; i < mask.length ; i++)
         mask[i] = 0xffffffff;
      pullCount = 0;
   }

   private final int nPulls = 37;
   private int mask[] = new int[nPulls];
   private double maskTime = 0;

   public int pull(Geometry s, double x0,double x1,double x2,
                      double y0,double y1,double y2,
                      double z0,double z1,double z2) {
      Geometry.pullWeight = 1;
      Geometry.pullMask = 0xffffffff;
      if (progression) {
         double t = time - pStartTime;
         if (t >= nPulls)
            progression = false;
         else {
            t = t % nPulls;
            if (t < pullCount) 
               return 0;
            if (t < pullCount+1)
               Geometry.pullWeight = t - pullCount;
         }
      }

      if (time - maskTime > 0 && mask[pullCount] != 0)
         Geometry.pullMask = mask[pullCount];
      int msk = super.pull(s, x0,x1,x2, y0,y1,y2, z0,z1,z2);
      if (time - maskTime > 1)
         mask[pullCount] |= msk;
      pullCount++;
      return 0;
   }
}


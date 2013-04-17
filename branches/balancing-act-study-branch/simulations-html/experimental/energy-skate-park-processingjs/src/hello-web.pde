// All Examples Written by Casey Reas and Ben Fry

// unless otherwise stated.

/* @pjs preload="resources/skater.png"; */

float bx;

float by;

int bs = 20;

boolean bover = false;

boolean locked = false;

float bdifx = 0.0;

float bdify = 0.0;

PImage im=null;



            void touchMove(TouchEvent touchEvent) {
            //scale(2);
              // empty the canvas
              noStroke();
              fill(255);
              rect(0, 0, 1024, 768);

              // draw circles at where fingers touch
              fill(180, 180, 100);
              for (int i = 0; i < touchEvent.touches.length; i++) {
                int x = touchEvent.touches[i].offsetX;
                int y = touchEvent.touches[i].offsetY;
                ellipse(x, y, 50, 50);
                  image(im, x,y, 100,100);
              }
              //scale(0.5);
            }

            void touchEnd(TouchEvent touchEvent) {
              noStroke();
              fill(255);
              rect(1, 1, 1024, 768);
            }

void setup()

{

  size(1024,768);

  bx = width/2.0;

  by = height/2.0;

  rectMode(CENTER_RADIUS);


im = loadImage("resources/skater.png");
}



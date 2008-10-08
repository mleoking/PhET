/*
 * Created on Apr 7, 2004
 *
 */
package render.jogl;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import net.java.games.jogl.*;
import render.*;

/**
 * @author Du Nguyen
 *
 */
public class RenderPanelGL extends Panel implements GLEventListener, MouseListener, MouseMotionListener,
      MouseWheelListener, KeyListener, RenderablePanel {

   private static final long serialVersionUID = -3207047012506774105L;
   protected GLCanvas glCanvas;
   //protected int H,W;
   GL gl;
   GLU glu;
   GLDrawable drawable;
   java.util.List mouseEventQueue;
   MouseEvent lastMoved = null;
   MouseEvent lastDragged = null;
   java.util.List keyEventQueue;
   Dimension queuedReshape = null;

   JoglGraphics graphics;

   Animator animator;

   //DEBUG Bits
   private static final int DEBUG_GL = 0x00000001; // OR this w/ DEBUG for GL
   private static final int DEBUG_GLU = 0x00000002; // OR this w/ DEBUG for GLU
   private static final int DEBUG_WINDOW_EVENT = 0x00000004; // etc...
   private int DEBUG = 0x00000000 | DEBUG_WINDOW_EVENT;

   Renderable renderable;

   public RenderPanelGL(int w, int h) {
      H = h;
      W = w;

      glCanvas = GLDrawableFactory.getFactory().createGLCanvas(new GLCapabilities());
      glCanvas.setBounds(0, 0, W, H);

      if ((DEBUG & DEBUG_GL) != 0)
         System.err.println("CANVAS GL IS: " + glCanvas.getGL().getClass().getName());

      if ((DEBUG & DEBUG_GLU) != 0)
         System.err.println("CANVAS GLU IS: " + glCanvas.getGLU().getClass().getName());

      glCanvas.addGLEventListener(this);
      this.setLayout(null);
      this.add(glCanvas);

      animator = new Animator(glCanvas);

      validate();
   }

   public void setRenderable(Renderable renderable) {
      this.renderable = renderable;
   }

   public Renderable getRenderable() {
      return renderable;
   }

   public Renderer getRenderer() {
      return renderer;
   }

   public void addNotify() {
      super.addNotify();
      this.setBounds(0, 0, W, H);
   }

   public void start() {
      if (animator == null)
         throw new RuntimeException("RenderPanelGL requires init() call before begin()");

      setVisible(true);
      animator.start();

   }

   /* (non-Javadoc)
    * @see net.java.games.jogl.GLEventListener#init(net.java.games.jogl.GLDrawable)
    */
   public void init(GLDrawable drawable) {
      this.drawable = drawable;
      gl = drawable.getGL();
      glu = drawable.getGLU();

      graphics = new JoglGraphics(gl, glu);

      mouseEventQueue = new ArrayList();
      keyEventQueue = new ArrayList();

      renderer = new JoglRenderer();
      JoglRenderer gr = (JoglRenderer) renderer;
      gr.gl = gl;
      gr.drawable = drawable;
      gr.glu = glu;

      drawable.addMouseListener(this);
      drawable.addMouseMotionListener(this);
      drawable.addMouseWheelListener(this);

      drawable.addKeyListener(this);
      init(W, H);
      //System.out.println("done w/ init ");
      gl.glMatrixMode(GL.GL_PROJECTION);
      gl.glLoadIdentity();
      glu.gluPerspective(Math.toDegrees(renderer.getFOV()), 1. * W / H, 1.5, 1000);
      //System.out.println("done w/ initialize ");	
   }

   /* (non-Javadoc)
    * @see net.java.games.jogl.GLEventListener#display(net.java.games.jogl.GLDrawable)
    */
   public void display(GLDrawable drawable) {
      // TODO Auto-generated method stub
      // MEASURE ELAPSED TIME AND FRAMERATE
      //disc.start();

      //((JoglRenderer) renderer).damage = this.isDamage;

      elapsed += getCurrentTime() - currentTime;
      currentTime = getCurrentTime();

      frameRate = .9 * frameRate + .1 / elapsed;
      elapsed = 0;

      if (queuedReshape != null) {
         reshape(drawable, getX(), getY(), queuedReshape.width, queuedReshape.height);
         queuedReshape = null;
      }
      // LET THE APPLICATION PROGRAMMER MOVE THINGS INTO PLACE

      identity(); // APPLIC. MATRIX STARTS UNTRANSFORMED
      isDamage = true;

      if (!renderer.manualCameraControl) {
         renderer.rotateView(theta, phi);
         theta = phi = 0;
      }

      animate(currentTime - startTime);
      // APPLICATION ANIMATES THINGS

      // SHADE AND SCAN CONVERT GEOMETRY INTO FRAME BUFFER

      renderer.render();

      //ok, change render to drawOverlay and look at pointComponent
      //      if (showFPS || menus.size() > 0) {
      Graphics G = graphics;
      G.drawImage(im, 0, 0, null);

      renderable.drawOverlay(G);

      menus.render(G);

      if (showFPS) {
         G.setColor(Color.white);
         G.fillRect(0, H - 14, 47, 14);
         G.setColor(Color.black);
         G.drawString((int) frameRate + "." + ((int) (frameRate * 10) % 10) + " fps", 2, H - 2);
      }
      //      }

      //render(graphics);

      mouseEvent();
      keyEvent();
      //disc.setTextures();
      //disc.glDraw();

      // KEEP REFINING LEVEL OF DETAIL UNTIL PERFECT (WHEN LOD=1)

      if (renderer.lod > 1) {
         isDamage = true;
         renderer.lod--;
      }

      try {
         //if (currentTime - startTime <20)
         Thread.sleep(5);
      } catch (InterruptedException ie) {
         ;
      }
   }

   /* (non-Javadoc)
    * @see net.java.games.jogl.GLEventListener#reshape(net.java.games.jogl.GLDrawable, int, int, int, int)
    */
   public void reshape(GLDrawable drawable, int x, int y, int width, int height) {
//      System.out.println("RenderPanelGL.reshape(" + x + "," + y + "," + width + "," + height + ")");
      renderer.setW(width);
      renderer.setH(height);
      W = width;
      H = height;
      gl.glMatrixMode(GL.GL_PROJECTION);
      gl.glLoadIdentity();
      glu.gluPerspective(Math.toDegrees(renderer.getFOV()), 1. * W / H, 1.5, 1000);

   }

   /* (non-Javadoc)
    * @see net.java.games.jogl.GLEventListener#displayChanged(net.java.games.jogl.GLDrawable, boolean, boolean)
    */
   public void displayChanged(GLDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

   private String notice = "Copyright 2001 Ken Perlin. All rights reserved.";

   //--- PUBLIC DATA FIELDS

   /**
    {@link Renderer} object
    */
   public Renderer renderer;

   /** 
    root of the scene {@link Geometry}
    */
   public Geometry world;

   /** 
    Flag that determines whether to display current frame rate.
    */
   public boolean showFPS = false;

   /** 
    Enables level of detail computation for meshes.
    */
   public boolean enableLod = false;

   /**
    Euler angle for camera positioning (horizontal view rotation).
    */
   public double theta = 0;
   /**
    Euler angle for camera positioning (vertical view rotation).
    */
   public double phi = 0;

   //--- PUBLIC METHODS

   /** 
    Override this to animate.
    
    @param time system time 
    */
   public void animate(double time) {
      renderable.animate(time);
   }

   /** prevents the renderer from redrawing the scene.
    */
   public void pause() {
      isDamage = false;
   }

   /**
    Forces a refresh of the renderer. Sets isDamage true.
    */
   public void damage() {
      renderer.refresh();
      isDamage = true;
   }

   /**
    Sets the field of view value.<p>       
    @param value 
    @see Renderer#setFOV(double value)
    */
   public void setFOV(double value) {
      renderer.setFOV(value);
   }

   public double getFOV() {
      return renderer.getFOV();
   }

   /**
    Sets the camera's focal length.<p>       
    @param value focal length
    @see Renderer#setFL(double value)
    */
   public void setFL(double value) {
      renderer.setFL(value);
   }

   /**
    Sets the background color ( RGB values range: 0..1).<p>       
    @param r red component 0..1
    @param g green component 0..1
    @param b blue component 0..1
    */
   public void setBgColor(double r, double g, double b) {
      renderer.setBgColor(r, g, b);
   }

   /** 
    Adds light source with direction (x, y, z) & color (r, g, b).
    <p>
    Arguments x,y,z indicate light direction. Arguments r,g,b indicate light direction.<p>
    @see Renderer#addLight(double x,double y,double z,
    double r,double g,double b) 
    */
   public void addLight(double x, double y, double z, // ADD A LIGHT SOURCE
         double r, double g, double b) {
      renderer.addLight(x, y, z, r, g, b);
   }

   // PUBLIC METHODS TO LET THE PROGRAMMER MANIPULATE A MATRIX STACK

   /**
    Sets current matrix to the identity matrix.
    */
   public void identity() {
      m().identity();
   }

   /**
    Returns the matrix at the top of the stack.<p>
    @return the top matrix on the stack
    */
   public Matrix m() {
      return matrix[top];
   }

   /**
    Pops the top matrix from the stack.
    */
   public void pop() {
      top--;
   }

   /**
    Pushes a copy of the top matrix onto the stack.
    */
   public void push() {
      matrix[top + 1].copy(matrix[top]);
      top++;
   }

   /**
    Rotates the top matrix around the X axis by angle t (radians).<p>
    @param t angle in radians
    */
   public void rotateX(double t) {
      m().rotateX(t);
   }

   /**
    Rotates the top matrix around the Y axis by angle t (radians).<p>
    @param t angle in radians
    */
   public void rotateY(double t) {
      m().rotateY(t);
   }

   /**
    Rotates the top matrix around the Z axis by angle t (radians).<p>       
    @param t angle in radians
    */
   public void rotateZ(double t) {
      m().rotateZ(t);
   }

   /**
    Scales the top matrix by x, y, z in their respective dimensions.<p>
    @param x x scale factor
    @param y y scale factor
    @param z z scale factor
    */
   public void scale(double x, double y, double z) {
      m().scale(x, y, z);
   }

   /**
    Applies the top transformation matrix to {@link Geometry} s.<p>
    @param s Geometry object
    */
   public void transform(Geometry s) {
      s.setMatrix(m());
   }

   /** 
    Translates the top matrix by vector v.<p>
    @param v an array of three doubles representing translations 
    in the x,y,z directions.
    */
   public void translate(double v[]) {
      translate(v[0], v[1], v[2]);
   }

   /**
    Translates the top matrix by x, y, z.<p>
    @param x - translation in the x direction.
    @param y - translation in the y direction.
    @param z - translation in the z direction.
    */
   public void translate(double x, double y, double z) {
      m().translate(x, y, z);
   }

   // PUBLIC METHODS TO LET THE PROGRAMMER DEFORM AN OBJECT

   /**
    Deforms a geometric shape according to the beginning, middle, and 
    end parameters in each dimension. For each dimesion the three 
    parameters indicate the amount of deformation at each position. <p>
    0 - beginning, 1 - middle, 2 - end. To indicate infinity (a constant
    transformation) set two adjacent parameters to the same value.
    Setting all three parameters to the same value transforms the 
    shape geometry consistently across the entire axis of the parameters.
    @param s shape object to be deformed
    @param x0 location of beginning of deformation along the x axis
    @param x1 location of beginning of deformation along the x axis
    @param x2 location of beginning of deformation along the x axis
    @param y0 location of beginning of deformation along the y axis
    @param y1 location of beginning of deformation along the y axis
    @param y2 location of beginning of deformation along the y axis
    @param z0 location of beginning of deformation along the z axis
    @param z1 location of beginning of deformation along the z axis
    @param z2 location of beginning of deformation along the z axis
    @return 1 if pull operation was successful, 0 otherwise 
    @see Geometry#pull
    */
   public int pull(Geometry s, double x0, double x1, double x2, double y0, double y1, double y2, double z0, double z1,
         double z2) {
      return s.pull(m(), x0, x1, x2, y0, y1, y2, z0, z1, z2);
   }

   //--- SYSTEM LEVEL PUBLIC METHODS ---

   int pix[];

   /**
    Initializes the applet and internal variables. To initialize components of
    the application program use {@link #initialize()}.<p>
    @see #initialize()
    */
   public void init(int W, int H) {
//      System.out.println("init w h is called");
      this.W = W;
      this.H = H;
      pix = renderer.init(W, H);
      startTime = getCurrentTime();
      world = renderer.getWorld(); // GET ROOT OF GEOMETRY
      for (int i = 0; i < matrix.length; i++)
         matrix[i] = new Matrix();
      identity();
      initialize();
      if (world != null && world.child != null)
         for (int n = 0; n < world.child.length; n++)
            if (world.child[n] != null && world.child[n].material != null)
               mat = world.child[n].material;
   }

   /**
    Override this to initialize the application program.
    */
   public void initialize() {
      renderable.initialize();
   }

   private boolean isRunning;

   /**
    Returns the Geometry of the frontmost object at the point (x, y) in
    the image (like a z-buffer value of geometries).
    @param x x coordinate in the image
    @param y y coordinate in the image
    @return the geometry of the foremost object at that location
    */
   public Geometry getGeometry(int x, int y) {
      return renderer.getGeometry(x, y);
   }

   //--- INTERACTIVE VIEW ROTATION EVENT CALLBACKS

   // IF MOUSE MOVES TO LOWER LEFT CORNER, THEN DISPLAY FRAME RATE

   //--- PRIVATE METHODS

   // GET THE CURRENT TIME IN SECONDS

   public double getCurrentTime() {
      return System.currentTimeMillis() / 1000.;
   }

   //--- PRIVATE DATA FIELDS

   /**
    Current mouse position
    */
   protected int mx, my;

   /** 
    Image width
    */
   protected int W;

   /** 
    Image height
    */
   protected int H;

   /** 
    Image framebuffer
    */
   protected Image im; // IMAGE OF MEMORY SOURCE OBJECT

   /**
    * 	Secondary frambuffer for displaying additional information
    */
   protected Image bufferIm;

   /** 
    Flag to force a renderer refresh when true.
    */
   protected boolean isDamage = true; // WHETHER WE NEED TO RECOMPUTE IMAGE

   /**
    Holds actual time of initialization.
    */
   protected double startTime = 0;

   /**
    Holds current system time. Used to compute time elapsed between frames.
    */
   protected double currentTime = 0;

   /**
    Measures time elapsed from initialization.
    */
   protected double elapsed = 0;

   /**
    Contains current frame rate of the renderer
    */
   protected double frameRate = 10;

   private Matrix matrix[] = new Matrix[10]; // THE MATRIX STACK
   private int top = 0; // MATRIX STACK POINTER
   private boolean seeMaterial = false;
   private Material mat; //points to the current material

   //--- INTERACTIVE VIEW ROTATION EVENT CALLBACKS

   java.util.List mouseListener = new ArrayList();
   java.util.List mouseMotionListener = new ArrayList();
   java.util.List mouseWheelListener = new ArrayList();
   java.util.List keyListener = new ArrayList();

   public void addMouseListener(MouseListener l) {
      mouseListener.add(l);
   }

   public void removeMouseListener(MouseListener l) {
      mouseListener.remove(l);
   }

   public void addMouseMotionListener(MouseMotionListener l) {
      mouseMotionListener.add(l);
   }

   public void removeMouseMotionListener(MouseMotionListener l) {
      mouseMotionListener.remove(l);
   }

   public void addMouseWheelListener(MouseWheelListener l) {
      mouseWheelListener.add(l);
   }

   public void removeMouseWheelListener(MouseWheelListener l) {
      mouseWheelListener.remove(l);
   }

   public void addKeyListener(KeyListener l) {
      keyListener.add(l);
   }

   public void removeKeyListener(KeyListener l) {
      keyListener.remove(l);
   }

   public void mouseEvent() {

      while (mouseEventQueue.size() > 0) {
         MouseEvent e = (MouseEvent) mouseEventQueue.get(0);
         mouseEventQueue.remove(0);
         int x = e.getX();
         int y = e.getY();
         MouseListener l;
         //MouseMotionListener m;
         MouseWheelListener wl;
         switch (e.getID()) {
            case MouseEvent.MOUSE_CLICKED:
               mouseClicked(e, x, y);
               for (int i = 0; i < mouseListener.size(); i++) {
                  l = (MouseListener) mouseListener.get(i);
                  l.mouseClicked(e);
               }
               break;
            /*case MouseEvent.MOUSE_DRAGGED:
             mouseDragged(e,x,y);
             for (int i=0; i<mouseMotionListener.size();i++) {
             m = (MouseMotionListener) mouseMotionListener.get(i);
             m.mouseDragged(e);
             }
             break;*/
            case MouseEvent.MOUSE_ENTERED:
               mouseEntered(e, x, y);
               for (int i = 0; i < mouseListener.size(); i++) {
                  l = (MouseListener) mouseListener.get(i);
                  l.mouseEntered(e);
               }
               break;
            case MouseEvent.MOUSE_EXITED:
               mouseExited(e, x, y);
               for (int i = 0; i < mouseListener.size(); i++) {
                  l = (MouseListener) mouseListener.get(i);
                  l.mouseExited(e);
               }
               break;
            /*				case MouseEvent.MOUSE_MOVED:
             mouseMoved(e,x,y);
             for (int i=0; i<mouseMotionListener.size();i++) {
             m = (MouseMotionListener) mouseMotionListener.get(i);
             m.mouseMoved(e);
             }					
             break;*/
            case MouseEvent.MOUSE_PRESSED:
               mousePressed(e, x, y);
               for (int i = 0; i < mouseListener.size(); i++) {
                  l = (MouseListener) mouseListener.get(i);
                  l.mousePressed(e);
               }
               break;
            case MouseEvent.MOUSE_RELEASED:
               mouseReleased(e, x, y);
               for (int i = 0; i < mouseListener.size(); i++) {
                  l = (MouseListener) mouseListener.get(i);
                  l.mouseReleased(e);
               }
               break;
            case MouseEvent.MOUSE_WHEEL:
               mouseWheelMoved((MouseWheelEvent) e, ((MouseWheelEvent) e).getWheelRotation());
               for (int i = 0; i < mouseWheelListener.size(); i++) {
                  wl = (MouseWheelListener) mouseWheelListener.get(i);
                  wl.mouseWheelMoved((MouseWheelEvent) e);
               }
               break;

         }
      }

      if (lastDragged != null) {
         MouseEvent e = lastDragged;
         int x = e.getX();
         int y = e.getY();
         mouseDragged(e, x, y);
         for (int i = 0; i < mouseMotionListener.size(); i++) {
            MouseMotionListener m = (MouseMotionListener) mouseMotionListener.get(i);
            m.mouseDragged(e);
         }
         lastDragged = null;
      }

      if (lastMoved != null) {
         //System.out.println("got a moved event "+lastMoved);
         MouseEvent e = lastMoved;
         int x = e.getX();
         int y = e.getY();
         mouseMoved(e, x, y);
         for (int i = 0; i < mouseMotionListener.size(); i++) {
            MouseMotionListener m = (MouseMotionListener) mouseMotionListener.get(i);
            m.mouseMoved(e);
         }
         lastMoved = null;
      }

   }

   public void mouseEntered(MouseEvent e, int x, int y) {
      Event event = new Event(this, e.getWhen(), e.getID(), x, y, 0, e.getModifiers());
      mouseEntered(event, x, y);
   }

   public void mouseExited(MouseEvent e, int x, int y) {
      Event event = new Event(this, e.getWhen(), e.getID(), x, y, 0, e.getModifiers());
      mouseExited(event, x, y);
   }

   public void mousePressed(MouseEvent e, int x, int y) {
      Event event = new Event(this, e.getWhen(), e.getID(), x, y, 0, e.getModifiers());
      mousePressed(event, x, y);
   }

   public void mouseReleased(MouseEvent e, int x, int y) {
      Event event = new Event(this, e.getWhen(), e.getID(), x, y, 0, e.getModifiers());
      mouseReleased(event, x, y);
   }

   public void mouseClicked(MouseEvent e, int x, int y) {
      Event event = new Event(this, e.getWhen(), e.getID(), x, y, 0, e.getModifiers());
      mouseClicked(event, x, y);
   }

   // Methods required for the implementation of MouseMotionListener
   public void mouseDragged(MouseEvent e, int x, int y) {
      Event event = new Event(this, e.getWhen(), e.getID(), x, y, 0, e.getModifiers());
      mouseDragged(event, x, y);
   }

   public void mouseMoved(MouseEvent e, int x, int y) {
      Event event = new Event(this, e.getWhen(), e.getID(), x, y, 0, e.getModifiers());
      mouseMoved(event, x, y);
   }

   public void mouseWheelMoved(MouseWheelEvent e, int r) {}

   // IF MOUSE MOVES TO LOWER LEFT CORNER, THEN DISPLAY FRAME RATE
   //	Methods required for the implementation of MouseListener
   public void mouseEntered(MouseEvent e) {
      mouseEventQueue.add(e);
   }

   public void mouseExited(MouseEvent e) {
      mouseEventQueue.add(e);
   }

   double dx = 0;
   double dy = 0;

   public void mousePressed(MouseEvent e) {
      mouseEventQueue.add(e);
   }

   public void mouseReleased(MouseEvent e) {
      mouseEventQueue.add(e);
   }

   public void mouseClicked(MouseEvent e) {
      mouseEventQueue.add(e);
   }

   // Methods required for the implementation of MouseMotionListener
   public void mouseDragged(MouseEvent e) {
      //mouseEventQueue.add(e);
      lastDragged = e;
   }

   public void mouseMoved(MouseEvent e) {
      //mouseEventQueue.add(e);
      //System.out.println("mouse moved listener");
      lastMoved = e;
   }

   public void mouseWheelMoved(MouseWheelEvent e) {
      mouseEventQueue.add(e);
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#mouseEntered(java.awt.Event, int, int)
    */
   public void mouseEntered(Event e, int x, int y) {}

   /* (non-Javadoc)
    * @see render.RenderablePanel#mouseExited(java.awt.Event, int, int)
    */
   public void mouseExited(Event e, int x, int y) {}

   /* (non-Javadoc)
    * @see render.RenderablePanel#mousePressed(java.awt.Event, int, int)
    */
   public void mousePressed(Event e, int x, int y) {
      Renderer.setDragging(true);
      mx = x;
      my = y;
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#mouseReleased(java.awt.Event, int, int)
    */
   public void mouseReleased(Event e, int x, int y) {
      Renderer.setDragging(false);
      if (x < 35 && y < 35) {
         Renderer.tableMode = !Renderer.tableMode;
      }
      if (x > W - 35 && y < 35) {
         seeMaterial = !seeMaterial;
         renderer.bufferg = true; //!renderer.bufferg;
         damage();
      }
      if (x > W - 35 && y > H - 35) {
         renderer.showMesh = !renderer.showMesh;
         damage();
      }
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#mouseClicked(java.awt.Event, int, int)
    */
   public void mouseClicked(Event e, int x, int y) {}

   /* (non-Javadoc)
    * @see render.RenderablePanel#mouseDragged(java.awt.Event, int, int)
    */
   public void mouseDragged(Event e, int x, int y) {
      if (Renderer.isDragging()) {
         theta += .03 * (double) (x - mx);
         // HORIZONTAL VIEW ROTATION
         phi += .03 * (double) (y - my); // VERTICAL VIEW ROTATION
         mx = x;
         my = y;
      }
      if (frameRate < 10 && renderer.lod < 4)
         if (enableLod)
            renderer.lod++;

      isDamage = true;
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#mouseMoved(java.awt.Event, int, int)
    */
   public void mouseMoved(Event e, int x, int y) {
      showFPS = x < 70 && y > H - 21;
   }

   // only interested in keyPressed and keyReleased
   public void keyEvent() {
      while (keyEventQueue.size() > 0) {
         KeyEvent e = (KeyEvent) keyEventQueue.get(0);
         keyEventQueue.remove(0);
         KeyListener l;
         int k = e.getKeyCode();
         switch (e.getID()) {
            case KeyEvent.KEY_PRESSED:
               keyPressed(e, k);
               for (int i = 0; i < keyListener.size(); i++) {
                  l = (KeyListener) keyListener.get(i);
                  l.keyPressed(e);
               }

               break;
            case KeyEvent.KEY_RELEASED:
               keyReleased(e, k);
               for (int i = 0; i < keyListener.size(); i++) {
                  l = (KeyListener) keyListener.get(i);
                  l.keyReleased(e);
               }
               break;
            case KeyEvent.KEY_TYPED:
               keyTyped(e, k);
               for (int i = 0; i < keyListener.size(); i++) {
                  l = (KeyListener) keyListener.get(i);
                  l.keyTyped(e);
               }
               break;
         }
      }
   }

   public void keyPressed(KeyEvent e) {
      keyEventQueue.add(e);
   }

   public void keyReleased(KeyEvent e) {
      keyEventQueue.add(e);
   }

   public void keyTyped(KeyEvent e) {
      keyEventQueue.add(e);
   }

   public void keyPressed(KeyEvent e, int key) {}

   public void keyReleased(KeyEvent e, int key) {}

   public void keyTyped(KeyEvent e, int key) {}

   /*private WritableRaster overlayRaster = null;
    private ComponentColorModel overlayColorModel = null;
    private BufferedImage overlayImage = null;
    private Graphics2D overlayGraphics = null;*/
   public Menus menus = new Menus();

   public Animator getAnimator() {
      return animator;
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#refresh()
    */
   public void refresh() {
      renderer.refresh();

   }

   public Geometry getWorld() {
      return world;
   }

   public GLDrawable getDrawable() {
      return drawable;
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#rotateView(double, double)
    */
   public void rotateView(double theta, double phi) {
      renderer.rotateView(theta, phi);
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#getLod()
    */
   public int getLod() {
      return renderer.lod;
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#setLod(int)
    */
   public void setLod(int value) {
      renderer.lod = value;
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#recalculateSize(int, int)
    */
   public void recalculateSize(int width, int height) {
      if (width != W || height != H)
         queuedReshape = new Dimension(width, height);

   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#getGeometryBuffer()
    */
   public boolean getGeometryBuffer() {
      return renderer.bufferg;
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#setGeometryBuffer(boolean)
    */
   public void setGeometryBuffer(boolean value) {
      renderer.bufferg = value;
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#getPoint(int, int, double[])
    */
   public boolean getPoint(int x, int y, double[] xyz) {
      return renderer.getPoint(x, y, xyz);
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#setDragging(boolean)
    */
   public void setDragging(boolean value) {
      Renderer.setDragging(value);
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#isDragging()
    */
   public boolean isDragging() {
      return Renderer.isDragging();
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#setTableMode(boolean)
    */
   public void setTableMode(boolean value) {
      Renderer.tableMode = false;
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#getTableMode()
    */
   public boolean getTableMode() {
      return false;
   }

   /* (non-Javadoc)
    * @see render.RenderablePanel#showMesh(boolean)
    */
   public void showMesh(boolean value) {
      renderer.showMesh = value;
   }

   /* 	can't do this right now
    * 	 * @see render.RenderablePanel#setOutline(boolean)
    */
   public void setOutline(boolean value) {

   }

   /* can't do this right now, bla bla bla
    * @see render.RenderablePanel#getOutline()
    */
   public boolean getOutline() {
      return false;
   }

   public void init() {}

   /**
    * @return Returns the startTime.
    */
   public double getStartTime() {
      return startTime;
   }

   /**
    * @return root world Geometry
    */
   public Geometry getWorldGeometry() {
      return this.world;
   }

   public Widget addMenu(String label, int x, int y) {
      // TODO Auto-generated method stub
      return null;
   }

   /** 
    * @see render.RenderablePanel#addMenu(render.Widget)
    */
   public void addMenu(Widget menu) {
   // TODO Auto-generated method stub

   }

   /** 
    * @see render.RenderablePanel#getFL()
    */
   public double getFL() {
      return renderer.getFL();
   }

   /** 
    * @see render.RenderablePanel#getMatrix()
    */
   public Matrix[] getMatrix() {
      return matrix;
   }

   /** 
    * @see render.RenderablePanel#getPix()
    */
   public int[] getPix() {
      return pix;
   }

   /** 
    * @see render.RenderablePanel#menu(int)
    */
   public Widget menu(int i) {
      // TODO Auto-generated method stub
      return null;
   }

   /** 
    * @see render.RenderablePanel#processCommand(int)
    */
   public boolean processCommand(int key) {
      // TODO Auto-generated method stub
      return false;
   }

   /** 
    * @see render.RenderablePanel#removeMenu(render.Widget)
    */
   public void removeMenu(Widget menu) {
   // TODO Auto-generated method stub

   }

   /** 
    * @see render.RenderablePanel#widgetAt(int, int)
    */
   public Widget widgetAt(int x, int y) {
      // TODO Auto-generated method stub
      return null;
   }
}

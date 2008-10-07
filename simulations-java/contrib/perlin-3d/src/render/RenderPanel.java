// <pre>
// Copyright 2001 Ken Perlin

package render;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

/**
   Provides a panel interface to the {@link Renderer}.<p>
   It also implements control features dealing with mouse and 
   keyboard interaction.<p>
   You can add this as a Component to your application /applet
   @see Renderer
*/

public class RenderPanel extends Panel implements Runnable {

   private String notice = "Copyright 2001 Ken Perlin. All rights reserved.";

   //--- PUBLIC DATA FIELDS

   /**
    * {@link Renderer} object
    */
   public Renderer renderer;

   /**
    * root of the scene {@link Geometry}
    */

   public Geometry world;

   /** 
    * Flag that determines whether to display current frame rate.
    */
   public boolean showFPS = false;

   /** 
    * Enables level of detail computation for meshes.
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

   public void setOpaque(boolean opaque) {
      //super.setOpaque(opaque);
      if (renderer != null) {
         if (opaque) // Make the alpha value of the bgColor 255
            {
            renderer.setBgColor(renderer.getBgColor() | 0xff000000);
         } else // Make the alpha value of the bgColor 0
            {
            renderer.setBgColor(renderer.getBgColor() & 0x00ffffff);
         }
      }
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
   	Rotates the top matrix around any axis by angle t (radians).<p>       
   	@param t angle in radians
   	@param x
   	@param y
   	@param z
   */
   public void rotate(double t, double x, double y, double z) {
      m().rotate(t, x, y, z);
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
      @param x0 location of the beginning of deformation along the x axis
      @param x1 location of the middle of deformation along the x axis
      @param x2 location of the end of deformation along the x axis
      @param y0 location of the beginning of deformation along the y axis
      @param y1 location of the middle of deformation along the y axis
      @param y2 location of the end of deformation along the y axis
      @param z0 location of the beginning of deformation along the z axis
      @param z1 location of the middle of deformation along the z axis
      @param z2 location of the end of deformation along the z axis
      @return 1 if pull operation was successful, 0 otherwise 
      @see Geometry#pull
   */
   public int pull(Geometry s, double x0, double x1, double x2, double y0, double y1, double y2, double z0, double z1, double z2) {
      return s.pull(m(), x0, x1, x2, y0, y1, y2, z0, z1, z2);
   }

   //--- SYSTEM LEVEL PUBLIC METHODS ---

   Image bufferIm;
   int pix[];

   public RenderPanel() {

      MouseHandler mh = new MouseHandler(this);
      addMouseListener(mh);
      addMouseMotionListener(mh);
      KeyHandler kh = new KeyHandler(this);
      addKeyListener(kh);
   }

   /**
      Initializes the applet and internal variables. To initialize components of
      the application program use {@link #initialize()}.<p>
      @see #initialize()
   */
   public synchronized void init() {
      W = getBounds().width;
      H = getBounds().height;
      renderer = new Renderer();
      pix = renderer.init(W, H);
      mis = new MemoryImageSource(W, H, pix, 0, W);
      mis.setAnimated(true);
      mis.setFullBufferUpdates(true);
      im = createImage(mis);
      bufferIm = createImage(W, H);
      startTime = getCurrentTime();
      world = renderer.getWorld(); // GET ROOT OF GEOMETRY
      for (int i = 0; i < matrix.length; i++)
         matrix[i] = new Matrix();
      identity();
      initialize();
      if (world.child != null)
         for (int n = 0; n < world.child.length; n++)
            if (world.child[n] != null && ((Geometry) world.child[n]).material != null)
               mat = ((Geometry) world.child[n]).material;
      damage();

   }

   /**
      Override this to initialize the application program.
   */
   public void initialize() {
   }

   /**
      Starts the renderer thread.
   */
   public synchronized void start() {
      if (t == null) {
         t = new Thread(this);
         running = true;
         t.start();
      }
   }

   /**
      Stops the renderer thread.
   */
   public synchronized void stop() {
      if (t != null) {
         running = false;
      }
   }

   /**
      Renderer thread.<p>
   */
   public void run() {

      while (running) {
         renderFrame();

         try {
            Thread.sleep(20);
         } catch (InterruptedException ie) {
            ;
         }
      }
   }

   public void renderFrame() {
      // MEASURE ELAPSED TIME AND FRAMERATE

      elapsed += getCurrentTime() - currentTime;
      currentTime = getCurrentTime();

      if (isDamage) {

         frameRate = .9 * frameRate + .1 / elapsed;
         elapsed = 0;

         // LET THE APPLICATION PROGRAMMER MOVE THINGS INTO PLACE

         identity(); // APPLIC. MATRIX STARTS UNTRANSFORMED
         isDamage = true;

         renderer.rotateView(theta, phi);
         theta = phi = 0;

         animate(currentTime - startTime); // APPLICATION ANIMATES THINGS

         // SHADE AND SCAN CONVERT GEOMETRY INTO FRAME BUFFER

         renderer.render();

         // KEEP REFINING LEVEL OF DETAIL UNTIL PERFECT (WHEN LOD=1)

         if (renderer.lod > 1) {
            isDamage = true;
            renderer.lod--;
         }
         // WRITE RESULTS TO THE SCREEN
         if (mat != null)
            if (seeMaterial) {
               for (int x = 0; x < 128; x++)
                  for (int y = 0; y < 128; y++) {
                     int i = y * W + x;
                     pix[i] = mat.table[(x << mat.resP) | y];
                     if (i + (W << 7) < pix.length)
                        pix[i + (W << 7)] = mat.table[(1 << mat.resP + mat.resP) | (x << mat.resP) | y];
                  }
            }
         mis.newPixels(0, 0, W, H, true);
         repaint(0, 0, bounds().width, bounds().height);
         //repaint();
      }
   }

   private synchronized void recalculateSize(int currentWidth, int currentHeight) {

      // Change the size
      W = currentWidth;
      H = currentHeight;

      // Reinitialize the renderer
      pix = renderer.reinit(W, H);

      mis = new MemoryImageSource(W, H, pix, 0, W);
      mis.setFullBufferUpdates(true);
      mis.setAnimated(true);
      im = createImage(mis);
      bufferIm = createImage(W, H);
      damage();
   }

   /**
      Updates the image buffer to output device.
      @param g Specifies the output device.
   */

   public void update(Graphics g) {

      int currentWidth = bounds().width; //getWidth() - insets.left - insets.right;
      int currentHeight = bounds().height; //getHeight() - insets.top - insets.bottom;

      if (currentHeight != H || currentWidth != W) {
         recalculateSize(currentWidth, currentHeight);
         renderFrame();
      }

      g.drawImage(im, 0, 0, null);
   }

   /**
   	Returns the Geometry of the frontmost object at the point (x, y) in
   	the image (like a z-buffer value of geometries).
   	@param x x coordinate in the image
   	@param y y coordinate in the image
   	@return the geometry of the foremost object at that location
   */
   public Geometry getGeometry(int x, int y) {
      if (renderer.bufferg == false) {
         renderer.bufferg = true;
         isDamage = true;
      }
      return renderer.getGeometry(x, y);
   }

   /**
   	Returns the location in world space of the point (x, y) on the screen.
   	@param x x coordinate in the image
   	@param y y coordinate in the image
   	@return true if there is an object at x, y , false otherwise
   */
   public boolean getPoint(int x, int y, double xyz[]) {
      if (renderer.bufferg == false) {
         renderer.bufferg = true;
         isDamage = true;
      }
      return renderer.getPoint(x, y, xyz);
   }

   //--- INTERACTIVE VIEW ROTATION EVENT CALLBACKS

   // IF MOUSE MOVES TO LOWER LEFT CORNER, THEN DISPLAY FRAME RATE

   // legacy functions to parallel original design of the RenderApplet

   /**
   			Listener for mouse movement.<p>
   			If mouse is placed in the lower left cornder it displays the framerate.<p>
   			@return true
   		*/
   public boolean mouseMove(Event event, int x, int y) {
      if (seeMaterial) {
         Geometry tmp = getGeometry(x, y);
         if (tmp != null)
            mat = tmp.material;
      }
      showFPS = x < 35 && y > H - 14;
      return true;
   }

   /**
   	Listener for mouse down.<p>
   	Mouse down starts a view rotation.<p>
   	@return true
   */
   public boolean mouseDown(Event event, int x, int y) {
      Renderer.setDragging(true);
      mx = x;
      my = y;
      return true;
   }

   /**
   	Dragging the mouse causes gradual view rotation in the phi and theta directions.<p>
   	@param event Event
   	@param x - new x coordinate
   	@param y - new y coordinate
   */
   public boolean mouseDrag(Event event, int x, int y) {

      if (Renderer.isDragging()) {
         theta += .03 * (double) (x - mx); // HORIZONTAL VIEW ROTATION
         phi += .03 * (double) (y - my); // VERTICAL VIEW ROTATION
         mx = x;
         my = y;
      }
      if (frameRate < 10 && renderer.lod < 4)
         if (enableLod)
            renderer.lod++;

      isDamage = true;
      return true;
   }

   /** 
   	 Listens for mouse release and controls aspects of the renderer.<p>
   	 A release in the upper left corner toggles {@link Renderer#tableMode}.<p>
   	 A release in the upper right corner toggle visibility of the {@link Material#table} display. When true, the current material table is displayed in the upper left corner of the window. Position of the mouse determines current material.<p>
   	 A release in the lower right toggles {@link Renderer#showMesh}<p>
   	 @param event Event
   	 @param x current x coordinate
   	 @param y current y coordinate
   	 @return true
   */
   public boolean mouseUp(Event event, int x, int y) {
      Renderer.setDragging(false);
      if (x < 35 && y < 35) {
         Renderer.tableMode = !Renderer.tableMode;
      }
      if (x > W - 35 && y < 35) {
         seeMaterial = !seeMaterial;
         renderer.bufferg = !renderer.bufferg;
         damage();
      }
      if (x > W - 35 && y > H - 35) {
         renderer.showMesh = !renderer.showMesh;
         damage();
      }
      return true;
   }

   /**
   	Handles commands received (generally for unicode commands from the KeyListener, 
   	but also for commands from any other sources, like buttons from webpages)
   	: various default control keys to modify render style (Use CTRL + key).<p>
   	'e' - toggles {@link Renderer#showMesh}, that just displays 
   	the shapes as mesh wireframes<br> 
   	'l' - toggles {@link Renderer#getOutline()} which produces a 
   	sketch-line drawing rendition of the scene<br>
   	'm' - toggles {@link Renderer#seeMesh} which determines mesh 
   	visibility <br>
   	't' - toggles global texture manipulation method (MIP on/off) (@link Texture#useMIP)<p> 
   	@param event Event
   	@param key value of the key released
   	@return true if one of the above keys was just released, false otherwise.
   */
   public boolean processCommand(int key) {
      switch (key) {
         case KeyEvent.VK_E :
            renderer.showMesh = !renderer.showMesh;
            damage();
            return true;
         case KeyEvent.VK_L :
            renderer.outline(-renderer.getOutline());
            damage();
            return true;
         case KeyEvent.VK_M :
            renderer.seeMesh = !renderer.seeMesh;
            damage();
            return true;
         case KeyEvent.VK_T :
            Texture.useMIP = !Texture.useMIP;
            damage();
            return true;

      }
      return false;
   }

   //--- PRIVATE METHODS

   // GET THE CURRENT TIME IN SECONDS

   private double getCurrentTime() {
      return System.currentTimeMillis() / 1000.;
   }

   //--- PRIVATE DATA FIELDS

   /**
      Current mouse position
   */
   protected int mx, my;

   /**
      Image memory source object
   */
   private MemoryImageSource mis;

   /**
      Rendering thread
   */
   private Thread t;

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

   private boolean running = false;
   private Matrix matrix[] = new Matrix[10]; // THE MATRIX STACK
   private int top = 0; // MATRIX STACK POINTER
   private boolean seeMaterial = false;
   private Material mat; //points to the current material

   /****************************************************************************************************
    * mouse and keyboard handlers
    * 
    */

   protected class MouseHandler extends MouseAdapter implements MouseMotionListener {
      RenderPanel rp;
      public MouseHandler(RenderPanel r) {
         rp = r;
      }

      public void mouseMoved(MouseEvent me) {
         rp.mouseMove(null, me.getX(), me.getY());
      }

      public void mousePressed(MouseEvent me) {
         rp.mouseDown(null, me.getX(), me.getY());
      }

      public void mouseDragged(MouseEvent me) {
         rp.mouseDrag(null, me.getX(), me.getY());
      }

      public void mouseReleased(MouseEvent me) {
         rp.mouseUp(null, me.getX(), me.getY());
      }
   }

   protected class KeyHandler extends KeyAdapter {
      RenderPanel rp;

      public KeyHandler(RenderPanel r) {
         rp = r;
      }

      public void keyReleased(KeyEvent ke) {
         rp.processCommand(ke.getKeyCode());
      }
   }

}

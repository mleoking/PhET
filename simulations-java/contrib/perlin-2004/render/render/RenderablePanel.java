package render;

import java.awt.Event;
import java.awt.Image;

import java.awt.event.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.ImageProducer;

public interface RenderablePanel {

   public void addKeyListener(KeyListener l);

   /**
    * Adds light source with direction (x, y, z) & color (r, g, b).
    * Arguments x,y,z indicate light direction. Arguments r,g,b indicate light
    * direction.
    * @see Renderer#addLight(double x,double y,double z, double r,double
    *      g,double b)
    */
   public void addLight(double x, double y, double z, // ADD A LIGHT SOURCE
         double r, double g, double b);

   /**
    * adds a menu widget
    * @param label
    * @param x
    * @param y
    * @return
    */
   public Widget addMenu(String label, int x, int y);

   /**
    * adds a menu 
    * @param menu
    */
   public void addMenu(Widget menu);

   public void addMouseListener(MouseListener l);

   public void addMouseMotionListener(MouseMotionListener l);
   
   public void addMouseWheelListener(MouseWheelListener l);

   public Image createImage(ImageProducer producer);

   public Image createImage(int width, int height);

   /**
    * Returns the Geometry of the frontmost object at the point (x, y) in the
    * image (like a z-buffer value of geometries).
    * 
    * @param x  x coordinate in the image
    * @param y  y coordinate in the image
    * @return the geometry of the foremost object at that location
    */
   public Geometry getGeometry(int x, int y);

   /**
    * returns the current system time in seconds
    * @return 
    */
   public double getCurrentTime();

   /**
    * field of view 
    * @return
    */
   public double getFOV();

   /**
    * focal length
    * @return
    */
   public double getFL();

   /**
    * status of the geometry z-buffer, for object picking
    * @return
    */
   public boolean getGeometryBuffer();

   /**
    * returns the height of the component
    * @return
    */
   public int getHeight();

   /**
    * level of detail
    * @return
    */
   public int getLod();

   /**
    * the matrix stack
    * @return
    */
   public Matrix[] getMatrix();

   /**
    * the pixel array
    * @return
    */
   public int[] getPix();

   /**
    * get status of flag which produces the sketch-line drawing rendition of the scene
    * @return
    */
   public boolean getOutline();

   /** 
    * Returns the location in world space of the point (x, y) on the screen.
    * @param x x coordinate in the image
    * @param y y coordinate in the image
    * @return true if there is an object at x, y , false otherwise
    */
   public boolean getPoint(int x, int y, double[] xyz);

   /**
    * (@link Renderable} Renderable 
    * @return
    */
   public Renderable getRenderable();

   /**
    * {@link Renderer}object
    */
   public Renderer getRenderer();

   /**
    * flag that determines whether the normal tables should beprecomputed 
    * @return
    */
   public boolean getTableMode();

   /**
    * the Component's width
    * @return
    */
   public int getWidth();

   /**
    * the root world geometry
    * @return
    */
   public Geometry getWorld();

   /**
    * Sets current matrix to the identity matrix.
    */
   public void identity();

   /**
    * Initializes the renderer and internal variables; to initialize your model 
    * see {@link Renderable.initialize() }
    * 
    * @see #Renderable.initialize()    
    */
   public void init();

   /**
    *  Returns whether dragging is active or not.
    *  @return true when dragging is active, false otherwise    
    */
   public boolean isDragging();

   /**
    * Returns the matrix at the top of the stack.<p>
    * @return the top matrix on the stack
    */
   public Matrix m();

   /**
    * returns the menu at index i
    * @param i
    * @return
    */
   public Widget menu(int i);

   public void mouseClicked(Event e, int x, int y);

   // Methods required for the implementation of MouseMotionListener
   public void mouseDragged(Event e, int x, int y);

   public void mouseEntered(Event e, int x, int y);

   public void mouseExited(Event e, int x, int y);

   public void mouseMoved(Event e, int x, int y);

   public void mousePressed(Event e, int x, int y);

   public void mouseReleased(Event e, int x, int y);   
   
   public void mouseWheelMoved(MouseWheelEvent e, int rotation);

   /**
    * pauses the renderer thread
    */
   public void pause();

   /**
    *  Pops the top matrix from the stack.
    */
   public void pop();

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
         double z2);

   /**
    * Pushes a copy of the top matrix onto the stack.
    */
   public void push();

   /**
    Handles commands received (generally for unicode commands from the KeyListener, 
    but also for commands from any other sources, like buttons from webpages)
    : various default control keys to modify render style (Use CTRL + key).<p>
    'e' - toggles {@link #showMesh(boolean)}, that just displays 
    the shapes as mesh wireframes<br> 
    'l' - toggles {@link #getOutline()} which produces a 
    sketch-line drawing rendition of the scene<br>
    'm' - toggles {@link #seeMesh} which determines mesh 
    visibility <br>
    't' - toggles global texture manipulation method (MIP on/off) (@link Texture#useMIP)<p> 
    @param event Event
    @param key value of the key released
    @return true if one of the above keys was just released, false otherwise.
    */
   public boolean processCommand(int key);

   /**
    * removes a particular menu
    * @param menu
    */
   public void removeMenu(Widget menu);

   /**
    * called upon a resize of the component
    * @param width
    * @param height
    */
   public void recalculateSize(int width, int height);

   /**
    * Forces a refresh of the renderer. Sets isDamage true.
    */
   public void refresh();

   public void removeKeyListener(KeyListener l);

   public void removeMouseListener(MouseListener l);

   public void removeMouseMotionListener(MouseMotionListener l);

   /**
    * repaint the component
    */
   public void repaint();

   /**
    * Rotate angle of view.
    * @param theta
    * @param phi
    */
   public void rotateView(double theta, double phi);

   /**
    * Rotates the top matrix around the X axis by angle t (radians).
    * @param t angle in radians
    *
    */
   public void rotateX(double t);

   /**
    * Rotates the top matrix around the Y axis by angle t (radians).
    * @param t angle in radians
    *
    */
   public void rotateY(double t);

   /**
    * Rotates the top matrix around the Z axis by angle t (radians).
    * @param t angle in radians
    *
    */
   public void rotateZ(double t);

   /**
    * Scales the top matrix by x, y, z in their respective dimensions.
    @param x x scale factor
    @param y y scale factor
    @param z z scale factor
    */
   public void scale(double x, double y, double z);

   /**
    * Sets the background color ( RGB values range: 0..1).
    * 
    * @param r   red component 0..1
    * @param g   green component 0..1
    * @param b   blue component 0..1
    */
   public void setBgColor(double r, double g, double b);

   /**
    If the user is interactively dragging the mouse, we want the
    renderer to know about it, so that any other background process
    (eg: a material which is building a lookup table) can ask the
    renderer, and thereby avoid consuming scarce CPU resources
    simultaneously.
    @param tf dragging true or false
    */
   public void setDragging(boolean value);

   /**
    * Sets the camera's focal length.
    * @param value  focal length
    */
   public void setFL(double value);

   /**
    * Sets the field of view value. 
    * @param value
    */
   public void setFOV(double value);

   /**
    * enables/disables the geometry z-buffer, for object picking
    * @param value on/off
    */
   public void setGeometryBuffer(boolean value);

   /**
    * set the level of detail
    * @param value
    */
   public void setLod(int value);

   /**
    * toggle outline mode
    * @param value
    */
   public void setOutline(boolean value);

   /**
    * set the renderable object
    * @param renderable
    */
   public void setRenderable(Renderable renderable);

   /**
    * toggle table lookup mode and precomputation
    * @param value
    */
   public void setTableMode(boolean value);

   /**
    * toggle the wireframe view of the world
    * @param value
    */
   public void showMesh(boolean value);

   /**
    * starts the renderer thread
    */
   public void start();

   /**
    * Applies the top transformation matrix to {@link Geometry} s.<p>
    * @param s Geometry object
    */
   public void transform(Geometry s);

   /**
    * Translates the top matrix by vector v.<p>
    * @param x translation along the x axis
    * @param y translation along the y axis
    * @param z translation along the z axis
    */
   public void translate(double x, double y, double z);

   /**
    *  Translates the top matrix by vector v.<p>
    *  @param v an array of three doubles representing translations 
    *  in the x,y,z directions.
    */
   public void translate(double[] v);

   /**
    * returns the widget at x, y 
    * @param x
    * @param y
    * @return
    */
   public Widget widgetAt(int x, int y);

}
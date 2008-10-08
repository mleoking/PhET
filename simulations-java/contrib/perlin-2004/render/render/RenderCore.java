// <pre>
// Copyright 2001 Ken Perlin

package render;

//import java.applet.*;
import java.awt.*;
import java.awt.image.*;

public class RenderCore implements Runnable {

	/**
	 * {@link Renderer}object
	 */
	Renderer renderer;

	/**
	 * root of the scene {@link Geometry}
	 */
	Geometry world;

	/**
	 * Flag that determines whether to display current frame rate.
	 */
	boolean showFPS = false;

	/**
	 * Enables level of detail computation for meshes.
	 */
	boolean enableLod = false;

	/**
	 * Euler angle for camera positioning (horizontal view rotation).
	 */
	private double theta = 0;

	/**
	 * Euler angle for camera positioning (vertical view rotation).
	 */
	private double phi = 0;

	/**
	 * The matrix stack
	 */
	private Matrix matrix[] = new Matrix[10];

	/**
	 * Matrix stack pointer
	 */
	private int top = 0;

	/**
	 * enables display of material normals table
	 */
	private boolean seeMaterial = false;

	//TODO do not display material if hasTexture() or show image

	/**
	 * points to the current material to be potentially displayed via
	 * seeMaterial
	 */
	private Material mat;

	/**
	 * the menus
	 */
	public Menus menus = new Menus();

	/**
	 * Current mouse position
	 */
	protected int mx, my;

	/**
	 * Image memory source object
	 */
	private MemoryImageSource mis;

	/**
	 * Rendering thread
	 */
	private Thread t;

	/**
	 * flag used for thread flow control
	 */
	protected boolean isRunning;

	/**
	 * Image width
	 */
	protected int W;

	/**
	 * Image height
	 */
	protected int H;

	/**
	 * Image framebuffer
	 */
	protected Image im; // IMAGE OF MEMORY SOURCE OBJECT

	/**
	 * Secondary frambuffer for displaying additional/overlay information
	 */
	protected Image bufferIm;

	/**
	 * Flag to force a renderer refresh when true.
	 */
	private boolean isDamage = true; // WHETHER WE NEED TO RECOMPUTE IMAGE

	/**
	 * Holds actual time of initialization.
	 */
	protected double startTime = 0;

	/**
	 * Holds current system time. Used to compute time elapsed between frames.
	 */
	protected double currentTime = 0;

	/**
	 * Measures time elapsed from initialization.
	 */
	protected double elapsed = 0;

	/**
	 * Contains current frame rate of the renderer
	 */
	protected double frameRate = 10;

	/**
	 * the packed pixels array
	 */
	int pix[];

	/**
	 * the component that provides gui interface
	 */
	RenderablePanel renderablePanel;

	/**
	 * the main renderable class
	 */
	Renderable renderable;

	/***************************************************************************
	 * METHODS
	 **************************************************************************/

	public RenderCore(RenderablePanel parentComponent) {
		this.renderablePanel = parentComponent;
	}

	/**
	 * Initializes the applet and internal variables. To initialize components
	 * of the application program use {@link #initialize()}.
	 * <p>
	 * 
	 * @see #initialize()
	 */
	public void init() {
		System.out.println("initializing renderCore");
		renderer = new Renderer();
		recalculateSize(renderablePanel.getWidth(), renderablePanel.getHeight());
		startTime = getCurrentTime();
		world = renderer.getWorld(); // GET ROOT OF GEOMETRY
		for (int i = 0; i < matrix.length; i++)
			matrix[i] = new Matrix();
		identity();
		renderable.initialize();
		if (world != null && world.child != null)
			for (int n = 0; n < world.child.length; n++)
				if (world.child[n] != null && world.child[n].material != null)
					mat = world.child[n].material;
	}

	/**
	 * reinitializes the renderer for new dimensions
	 * 
	 * @param currentWidth
	 * @param currentHeight
	 */
	synchronized void recalculateSize(int currentWidth, int currentHeight) {
		// Change the size
		W = currentWidth;
		H = currentHeight;

		// Reinitialize the renderer
		if (renderer == null) //TODO when applet mode, crashes when setSize is
			// called too early
			return;
		//throw new RuntimeException("renderer not initialized. must call
		// init() first");
		pix = renderer.init(W, H);
		mis = new MemoryImageSource(W, H, pix, 0, W);
		mis.setAnimated(true);
		im = renderablePanel.createImage(mis);
		bufferIm = renderablePanel.createImage(W, H);
		damage();
	}

	/**
	 * Get the current system time in seconds
	 */
	public double getCurrentTime() {
		return System.currentTimeMillis() / 1000.;
	}

	/**
	 * prevents the renderer from redrawing the scene.
	 */
	public void pause() {
		isDamage = false;
	}

	/**
	 * Forces a refresh of the renderer. Sets isDamage true.
	 */
	public void damage() {
		renderer.refresh();
		isDamage = true;
	}

	/**
	 * Sets the field of view value.
	 * <p>
	 * 
	 * @param value
	 * @see Renderer#setFOV(double value)
	 */
	public void setFOV(double value) {
		renderer.setFOV(value);
	}

	/**
	 * Sets the camera's focal length.
	 * <p>
	 * 
	 * @param value
	 *            focal length
	 * @see Renderer#setFL(double value)
	 */
	public void setFL(double value) {
		renderer.setFL(value);
	}

	/**
	 * gets the field of view value.
	 * <p>
	 * 
	 * @see Renderer#getFOV(double value)
	 */
	public double getFOV() {
		return renderer.getFOV();
	}

	/**
	 * gets the camera's focal length.
	 * <p>
	 * 
	 * @see Renderer#setFL(double value)
	 */
	public double getFL() {
		return renderer.getFL();
	}

	/**
	 * returns the pix array
	 * 
	 * @see #pix
	 * @return
	 */
	public int[] getPix() {
		return pix;
	}

	/**
	 * Returns xyz world coords of the frontmost object at pixel (x,y)
	 * 
	 * @param x
	 *            x pixel coordinate
	 * @param y
	 *            y pixel coordinate
	 * @param xyz
	 *            output point in world coords
	 * @return true iff not a background pixel
	 */
	public boolean getPoint(int x, int y, double xyz[]) {
		return renderer.getPoint(x, y, xyz);
	}

	/**
	 * Returns the Geometry of the frontmost object at the point (x, y) in the
	 * image (like a z-buffer value of geometries).
	 * 
	 * @param x
	 *            x coordinate in the image
	 * @param y
	 *            y coordinate in the image
	 * @return the geometry of the foremost object at that location
	 */
	public Geometry getGeometry(int x, int y) {
		if (renderer.bufferg == false) {
			renderer.bufferg = true;
			isDamage = true;
		}
		return renderer.getGeometry(x, y);
	}

	/**
	 * Find the widget at the mouse.
	 * <p>
	 * 
	 * @return widget at mouse or null
	 */
	public Widget widgetAt(int x, int y) {
		return menus.widgetAt(x, y);
	}

	/**
	 * Adds light source with direction (x, y, z) & color (r, g, b). Arguments
	 * x,y,z indicate light direction. Arguments r,g,b indicate light direction.
	 * 
	 * @see Renderer#addLight(double x,double y,double z, double r,double
	 *      g,double b)
	 */
	public void addLight(double x, double y, double z, // ADD A LIGHT SOURCE
			double r, double g, double b) {
		renderer.addLight(x, y, z, r, g, b);
	}

	// PUBLIC METHODS TO LET THE PROGRAMMER MANIPULATE A MATRIX STACK

	/**
	 * @return matrix stack
	 */
	public Matrix[] getMatrix() {
		return this.matrix;
	}

	/**
	 * Sets current matrix to the identity matrix.
	 */
	public void identity() {
		m().identity();
	}

	/**
	 * Returns the matrix at the top of the stack.
	 * <p>
	 * 
	 * @return the top matrix on the stack
	 */
	public Matrix m() {
		return matrix[top];
	}

	/**
	 * Pops the top matrix from the stack.
	 */
	public void pop() {
		top--;
	}

	/**
	 * Pushes a copy of the top matrix onto the stack.
	 */
	public void push() {
		matrix[top + 1].copy(matrix[top]);
		top++;
	}

	/**
	 * Rotates the top matrix around the X axis by angle t (radians).
	 * <p>
	 * 
	 * @param t
	 *            angle in radians
	 */
	public void rotateX(double t) {
		m().rotateX(t);
	}

	/**
	 * Rotates the top matrix around the Y axis by angle t (radians).
	 * <p>
	 * 
	 * @param t
	 *            angle in radians
	 */
	public void rotateY(double t) {
		m().rotateY(t);
	}

	/**
	 * Rotates the top matrix around the Z axis by angle t (radians).
	 * <p>
	 * 
	 * @param t
	 *            angle in radians
	 */
	public void rotateZ(double t) {
		m().rotateZ(t);
	}

	/**
	 * Scales the top matrix by x, y, z in their respective dimensions.
	 * <p>
	 * 
	 * @param x
	 *            x scale factor
	 * @param y
	 *            y scale factor
	 * @param z
	 *            z scale factor
	 */
	public void scale(double x, double y, double z) {
		m().scale(x, y, z);
	}

	/**
	 * Applies the top transformation matrix to {@link Geometry}s.
	 * <p>
	 * 
	 * @param s
	 *            Geometry object
	 */
	public void transform(Geometry s) {
		s.setMatrix(m());
	}

	/**
	 * Translates the top matrix by vector v.
	 * <p>
	 * 
	 * @param v
	 *            an array of three doubles representing translations in the
	 *            x,y,z directions.
	 */
	public void translate(double v[]) {
		translate(v[0], v[1], v[2]);
	}

	/**
	 * Translates the top matrix by x, y, z.
	 * <p>
	 * 
	 * @param x -
	 *            translation in the x direction.
	 * @param y -
	 *            translation in the y direction.
	 * @param z -
	 *            translation in the z direction.
	 */
	public void translate(double x, double y, double z) {
		m().translate(x, y, z);
	}

	// PUBLIC METHODS TO LET THE PROGRAMMER DEFORM AN OBJECT

	/**
	 * Deforms a geometric shape according to the beginning, middle, and end
	 * parameters in each dimension. For each dimesion the three parameters
	 * indicate the amount of deformation at each position.
	 * <p>
	 * 0 - beginning, 1 - middle, 2 - end. To indicate infinity (a constant
	 * transformation) set two adjacent parameters to the same value. Setting
	 * all three parameters to the same value transforms the shape geometry
	 * consistently across the entire axis of the parameters.
	 * 
	 * @param s
	 *            shape object to be deformed
	 * @param x0
	 *            location of beginning of deformation along the x axis
	 * @param x1
	 *            location of beginning of deformation along the x axis
	 * @param x2
	 *            location of beginning of deformation along the x axis
	 * @param y0
	 *            location of beginning of deformation along the y axis
	 * @param y1
	 *            location of beginning of deformation along the y axis
	 * @param y2
	 *            location of beginning of deformation along the y axis
	 * @param z0
	 *            location of beginning of deformation along the z axis
	 * @param z1
	 *            location of beginning of deformation along the z axis
	 * @param z2
	 *            location of beginning of deformation along the z axis
	 * @return 1 if pull operation was successful, 0 otherwise
	 * @see Geometry#pull
	 */
	public int pull(Geometry s, double x0, double x1, double x2, double y0,
			double y1, double y2, double z0, double z1, double z2) {
		return s.pull(m(), x0, x1, x2, y0, y1, y2, z0, z1, z2);
	}

	/**
	 * @return root world #Geometry
	 */
	public Geometry getWorld() {
		return this.world;
	}

	public Widget addMenu(String label, int x, int y) {
		return menus.add(label, x, y);
	}

	public void addMenu(Widget menu) {
		menus.add(menu);
	}

	public Widget menu(int i) {
		return menus.menu(i);
	}

	public void removeMenu(Widget menu) {
		menus.remove(menu);
	}

	/**
	 * Starts the renderer thread.
	 */
	public void start() {
		if (t == null) {
			t = new Thread(this);
			isRunning = true;
			t.start();
		}
	}

	/**
	 * Stops the renderer thread.
	 */
	public void stop() {
		if (t != null)
			isRunning = false;
	}

	/**
	 * Renderer thread
	 */
	public void run() {

		while (isRunning) {

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

				// APPLICATION ANIMATES THINGS
				renderable.animate(currentTime - startTime);

				// SHADE AND SCAN CONVERT GEOMETRY INTO FRAME BUFFER
				renderer.render();

				// KEEP REFINING LEVEL OF DETAIL UNTIL PERFECT (WHEN LOD=1)
				if (renderer.lod > 1) {
					isDamage = true;
					renderer.lod--;
				}

				// WRITE RESULTS TO THE SCREEN
				if (mat != null && mat.table.length > 0)
					if (seeMaterial) {
						for (int x = 0; x < 128; x++)
							for (int y = 0; y < 128; y++) {
								int i = y * W + x;
								pix[i] = mat.table[(x << mat.resP) | y];
								if (i + (W << 7) < pix.length)
									pix[i + (W << 7)] = mat.table[(1 << mat.resP
											+ mat.resP)
											| (x << mat.resP) | y];
							}
					}
				mis.newPixels(0, 0, W, H, true);
				renderablePanel.repaint();
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException ie) {
				;
			}
		}
	}

	/**
	 * Updates the image buffer to output device.
	 * 
	 * @param g
	 *            Specifies the output device.
	 */
	public synchronized void update(Graphics g) {

		int currentWidth = renderablePanel.getWidth();
		int currentHeight = renderablePanel.getHeight();

		// allow for dynamic resizing of the applet
		if (currentWidth != W || currentHeight != H) {
			recalculateSize(currentWidth, currentHeight);
		}

		if ((showFPS || menus.size() > 0) && bufferIm!= null) {
			Graphics G = bufferIm.getGraphics();
			G.drawImage(im, 0, 0, null);

			renderable.drawOverlay(G);
			menus.render(G);

			if (showFPS) {
				G.setColor(Color.white);
				G.fillRect(0, H - 14, 47, 14);
				G.setColor(Color.black);
				G.drawString((int) frameRate + "."
						+ ((int) (frameRate * 10) % 10) + " fps", 2, H - 2);
			}
			g.drawImage(bufferIm, 0, 0, null);
		} else
			g.drawImage(im, 0, 0, null);
	}

	/***************************************************************************
	 * EVENT INTERACTION HANDLING
	 **************************************************************************/

	/**
	 * Listener for mouse movement. If mouse is placed in the lower left cornder
	 * it displays the framerate.
	 * 
	 * @return true
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
	 * Listener for mouse down. Mouse down starts a view rotation.
	 * 
	 * @return true
	 */
	public boolean mouseDown(Event event, int x, int y) {
		if (menus.mouseDown(x, y)) {
			damage();
			return true;
		}
		Renderer.setDragging(true); //TODO remove static access to renderer
		mx = x;
		my = y;
		return true;
	}

	/**
	 * Listens for mouse release and controls aspects of the renderer.
	 * <p>
	 * A release in the upper left corner toggles {@link Renderer#tableMode}.
	 * <p>
	 * A release in the upper right corner toggle visibility of the
	 * {@link Material#table}display. When true, the current material table is
	 * displayed in the upper left corner of the window. Position of the mouse
	 * determines current material.
	 * <p>
	 * A release in the lower right toggles {@link Renderer#showMesh}
	 * <p>
	 * 
	 * @param event
	 *            Event
	 * @param x
	 *            current x coordinate
	 * @param y
	 *            current y coordinate
	 * @return true
	 */
	public boolean mouseUp(Event event, int x, int y) {
		if (menus.mouseUp(x, y)) {
			damage();
			return true;
		}

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
	 * Dragging the mouse causes gradual view rotation in the phi and theta
	 * directions.
	 * <p>
	 * 
	 * @param event
	 *            Event
	 * @param x -
	 *            new x coordinate
	 * @param y -
	 *            new y coordinate
	 */
	public boolean mouseDrag(Event event, int x, int y) {
		if (menus.mouseDrag(x, y)) {
			damage();
			return true;
		}

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
	 * Keyboard listener: various default control keys to modify render style
	 * (Use CTRL + key).
	 * <p>
	 * 'e' - toggles {@link Renderer#showMesh}, that just displays the shapes
	 * as mesh wireframes <br>
	 * 'l' - toggles {@link Renderer#getOutline()}which produces a sketch-line
	 * drawing rendition of the scene <br>
	 * 'm' - toggles {@link Renderer#seeMesh}which determines mesh visibility
	 * <br>
	 * 't' - toggles global texture manipulation method (MIP on/off) (@link
	 * Texture#useMIP)
	 * <p>
	 * 
	 * @param key
	 *            value of the key released
	 * @return true if one of the above keys was just released, false otherwise.
	 */
	public boolean processCommand(int key) {
		switch (key) {
		case 'a' - ('a' - 1):
			renderer.isAnaglyph = !renderer.isAnaglyph;
			damage();
			return true;
		case 'e' - ('a' - 1):
			renderer.showMesh = !renderer.showMesh;
			damage();
			return true;
		case 'l' - ('a' - 1):
			renderer.outline(-renderer.getOutline());
			damage();
			return true;
		case 'm' - ('a' - 1):
			renderer.seeMesh = !renderer.seeMesh;
			damage();
			return true;
		case 't' - ('a' - 1):
			Texture.useMIP = !Texture.useMIP;
			damage();
			return true;
		}
		return false;
	}

	public boolean keyUp(Event event, int key) {
		return processCommand(key);
	}

	/**
	 * @return Returns the renderable.
	 */
	public Renderable getRenderable() {
		return renderable;
	}

	/**
	 * @param renderable
	 *            The renderable to set.
	 */
	public void setRenderable(Renderable renderable) {
		this.renderable = renderable;
	}

	/**
	 * @return Returns the renderer.
	 */
	public Renderer getRenderer() {
		return renderer;
	}

	/**
	 * @param renderer
	 *            The renderer to set.
	 */
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	public void setLod(int value) {
		renderer.setLod(value);
	}

	public int getLod() {
		return renderer.getLod();
	}
	
	/**
	 * @return Returns the status of the geometry z-buffer.
	 */
	public boolean getGeometryBuffer() {
		return renderer.getGeometryBuffer();
	}
	/**
	 * @param value enables or disables the geometry z-buffer.
	 */
	public void setGeometryBuffer(boolean value) {
		renderer.setGeometryBuffer(value);
	}
}
/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import org.opensourcephysics.controls.XML;
import org.opensourcephysics.controls.XMLControl;
import org.opensourcephysics.display.axes.CoordinateStringBuilder;
import org.opensourcephysics.display.dialogs.*;
import org.opensourcephysics.tools.*;

/**
 * DrawingPanel renders drawable objects on its canvas.
 * DrawingPanel provides drawable objects with methods that transform from world
 * coordinates to pixel coordinates. World coordinates are defined by xmin, xmax,
 * ymin, and ymax.  These values are recaclucated on-the-fly from preferred
 * values if the aspect ratio is unity; otherwise, preferred values are used.
 *
 * If xmax>xmin then the coordinate scale increases from right to left.
 * If xmax<xmin then the coordinate scale increases from left to right.
 * If ymax>ymin then the coordinate scale increases from bottom to top.
 * If ymax<ymin then the coordinate scale increases from top to bottom.
 *
 * @author Wolfgang Christian
 * @author Joshua Gould
 * @version 1.0
 */
public class DrawingPanel extends JPanel implements Printable, ActionListener,Renderable {

  /** Message box location */
  public static final int BOTTOM_LEFT = 0;

  /** Message box location */
  public static final int BOTTOM_RIGHT = 1;

  /** Message box location */
  public static final int TOP_RIGHT = 2;

  /** Message box location */
  public static final int TOP_LEFT = 3;
  protected JPopupMenu popupmenu = new JPopupMenu(); // right mouse click popup menu
  protected JMenuItem propertiesItem; // the menu item for the properites dialog box
  protected int leftGutter = 0, topGutter = 0, rightGutter = 0, bottomGutter = 0;
  protected int width, height; // the size of the panel the last time it was painted.
  protected Color bgColor = new Color(239, 239, 255); // background color
  protected boolean squareAspect = false;             // adjust xAspect and yAspect so the drawing aspect ratio is unity
  protected boolean autoscaleX = true;
  protected boolean autoscaleY = true;
  protected double autoscaleMargin = 0.0; // used to increase the autoscale range
  // x and y scale in world units

  protected double xminPreferred = -10, xmaxPreferred = 10;
  protected double yminPreferred = -10, ymaxPreferred = 10;
  protected double xfloor = Double.NaN, xceil = Double.NaN;
  protected double yfloor = Double.NaN, yceil = Double.NaN;
  protected double xmin = xminPreferred, xmax = xmaxPreferred;
  protected double ymin = yminPreferred, ymax = xmaxPreferred;
  // pixel scale parameters  These are set every time paintComponent is called using the size of the panel

  protected boolean fixedPixelPerUnit = false;
  protected double xPixPerUnit = 1;                                 // the x scale in pixels per unit
  protected double yPixPerUnit = 1;                                 // the y scale in pixels per unit
  protected AffineTransform pixelTransform = new AffineTransform(); // transform from world to pixel coodinates.
  protected double[] pixelMatrix = new double[6]; // 6 values in the 3x3 pixel transformation
  protected ArrayList drawableList = new ArrayList(); // list of Drawable objects
  protected boolean validImage = false; // true if the current image is valid, false otherwise
  protected BufferedImage offscreenImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
  protected BufferedImage workingImage = offscreenImage;
  private boolean buffered = false; // true will draw this component using an off-screen image
  protected TextPanel trMessageBox = new TextPanel(); // text box in top right hand corner for message
  protected TextPanel tlMessageBox = new TextPanel(); // text box in top left hand corner for message
  protected TextPanel brMessageBox = new TextPanel(); // text box in lower right hand corner for message
  protected TextPanel blMessageBox = new TextPanel(); // text box in lower left hand corner for mouse coordinates
  protected DecimalFormat scientificFormat = new DecimalFormat("0.###E0"); // coordinate display format for message box.
  protected DecimalFormat decimalFormat = new DecimalFormat("0.00"); // coordinate display format for message box.
  protected MouseController mouseController = new CMController(); // handles the coodinate display on mouse actions
  protected boolean showCoordinates = false; // set to true when mouse listener is added
  protected OptionController optionController = new OptionController(); // handles optional mouse actions
  protected ZoomBox zoomBox = new ZoomBox();
  protected boolean enableZoom = false;  // scale can be set via a mouse drag
  protected boolean zoomMode = false;    // a zoom is in progress
  protected Window customInspector;      // optional custom inspector for this panel
  protected boolean clipAtGutter = true; // clips the drawing at the gutter if true
  protected Dimensioned dimensionSetter = null;
  protected Rectangle viewRect = null; // the clipping rectangle within a scroll pane viewport
  // CoordinateStringBuilder converts a mouse event into a string that displays world coordiantes.

  protected CoordinateStringBuilder coordinateStrBuilder = CoordinateStringBuilder.createCartesian();
  protected GlassPanel glassPanel = new GlassPanel();
  protected OSPLayout glassPanelLayout = new OSPLayout();
  int refreshDelay = 100;                                                     // time in ms to delay refresh events
  javax.swing.Timer refreshTimer = new javax.swing.Timer(refreshDelay, this); // delay before for refreshing panel
  VideoCaptureTool vidCap;

  /**
   * DrawingPanel constructor.
   */
  public DrawingPanel() {
    glassPanel.setLayout(glassPanelLayout);
    super.setLayout(new BorderLayout());
    glassPanel.add(trMessageBox, OSPLayout.TOP_RIGHT_CORNER);
    glassPanel.add(tlMessageBox, OSPLayout.TOP_LEFT_CORNER);
    glassPanel.add(brMessageBox, OSPLayout.BOTTOM_RIGHT_CORNER);
    glassPanel.add(blMessageBox, OSPLayout.BOTTOM_LEFT_CORNER);
    glassPanel.setOpaque(false);
    super.add(glassPanel, BorderLayout.CENTER);
    setBackground(bgColor);
    setPreferredSize(new Dimension(300, 300));
    showCoordinates = true; // show coordinates by default
    addMouseListener(mouseController);
    addMouseMotionListener(mouseController);
    addMouseListener(optionController);
    addMouseMotionListener(optionController);
    // invalidate the buffered image if the size changes
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        validImage = false;
      }
    });
    buildPopupmenu();
    refreshTimer.setRepeats(false);
    refreshTimer.setCoalesce(true);
  }

  protected void buildPopupmenu() {
    popupmenu.setEnabled(true);
    ActionListener listener = new PopupmenuListener();
    JMenuItem item = new JMenuItem("Snapshot");
    item.addActionListener(listener);
    popupmenu.add(item);
    item = new JMenuItem("Scale");
    item.addActionListener(listener);
    popupmenu.add(item);
    item = new JMenuItem("Zoom In");
    item.addActionListener(listener);
    popupmenu.add(item);
    item = new JMenuItem("Zoom Out");
    item.addActionListener(listener);
    popupmenu.add(item);
    propertiesItem = new JMenuItem("Inspect");
    propertiesItem.addActionListener(listener);
    popupmenu.add(propertiesItem);
  }

  /**
   * Sets the size of the margin during an autoscale operation.
   *
   * @param _autoscaleMargin
   */
  public void setAutoscaleMargin(double _autoscaleMargin) {
    autoscaleMargin = _autoscaleMargin;
  }

  /**
   * Sets the panel to exclude the gutter from the drawing.
   *
   * @param clip <code>true<\code> to clip; <code>false<\code> otherwise
   */
  public void setClipAtGutter(boolean clip) {
    clipAtGutter = clip;
  }

  /**
   * Gets the clip at gutter flag.
   *
   * @return  <code>true<\code> if drawing is clipped at the gutter; <code>false<\code> otherwise
   */
  public boolean isClipAtGutter() {
    return clipAtGutter;
  }

  /**
   * Sets gutters around the drawing area.
   * @param left
   * @param top
   * @param right
   * @param bottom
   */
  public void setGutters(int left, int top, int right, int bottom) {
    leftGutter = left;
    topGutter = top;
    rightGutter = right;
    bottomGutter = bottom;
  }

  /**
   * Sets the mouse cursor.
   * @param cursor
   */
  public void setMouseCursor(Cursor cursor) {
    Container c = getTopLevelAncestor();
    setCursor(cursor);
    if(c!=null) {
      c.setCursor(cursor);
    }
  }

  /**
   * Checks the image to see if the working image has the correct Dimension.
   *
   * @return <code>true <\code> if the offscreen image matches the panel;  <code>false <\code> otherwise
   */
  protected synchronized boolean checkWorkingImage() {
    Rectangle r = getBounds();
    int width = (int) r.getWidth();
    int height = (int) r.getHeight();
    if((width<=2)||(height<=2)) {
      return false; // panel is too small to draw anything useful
    }
    if((workingImage==null)||(width!=workingImage.getWidth())||(height!=workingImage.getHeight())) {
      this.workingImage = getGraphicsConfiguration().createCompatibleImage(width, height);
      validImage = false; // buffer image is not valid
    }
    if(this.workingImage==null) { // image could not be created
      validImage = false;         // buffer image is not valid
      return false;
    }
    return true; // the buffered image has been created and is the correct size
  }

  /**
   *  Performs the action for the refresh timer by refreshing the data in the DataTable.
   *
   * @param  evt
   */
  public void actionPerformed(ActionEvent evt) {
    if(!validImage) {
      render();
    }
  }

  /**
   * Gets the iconified flag from the top level frame.
   *
   * @return boolean true if frame is iconified; false otherwise
   */
  public boolean isIconified() {
    Component c = getTopLevelAncestor();
    if(c instanceof Frame) {
      return(((Frame) c).getExtendedState()&Frame.ICONIFIED)==1;
    } else {
      return false;
    }
  }

  /**
   * Paints all drawables onto an offscreen image buffer and copies this image onto the screen.
   *
   * @return the image buffer
   */
  public BufferedImage render() {
    if(!isShowing()||isIconified()) {
      return offscreenImage; // no need to draw if the frame is not visible
    }
    if(buffered&&checkWorkingImage()) {
      validImage = true; // drawing into the working image will produce a valid image
      render(workingImage);
      // swap the images
      BufferedImage temp = offscreenImage;
      offscreenImage = workingImage;
      workingImage = temp;
    }
    // always update a Swing component from the event thread
    Runnable doNow = new Runnable() {
      public void run() {
        paintImmediately(getVisibleRect());
      }
    };
    try {
      if(SwingUtilities.isEventDispatchThread()) {
        paintImmediately(getVisibleRect());
      } else { // paint within the event thread
        SwingUtilities.invokeAndWait(doNow);
      }
    } catch(InvocationTargetException ex1) {}
    catch(InterruptedException ex1) {}
    if(buffered&&vidCap!=null) {
      vidCap.addFrame(offscreenImage);
    }
    return offscreenImage;
  }

  /**
   * Paints all drawables onto an image.
   *
   * @param image
   * @return the image buffer
   */
  public synchronized Image render(Image image) {
    Graphics osg = image.getGraphics();
    if(osg!=null) {
      paintEverything(osg);
      if(image==workingImage) {
        zoomBox.paint(osg);               // paint the zoom
      }
      Rectangle viewRect = this.viewRect; // reference for thread safety
      if(viewRect!=null) {
        Rectangle r = new Rectangle(0, 0, image.getWidth(null), image.getHeight(null));
        glassPanel.setBounds(r);
        glassPanelLayout.checkLayoutRect(glassPanel, r);
        glassPanel.render(osg);
        glassPanel.setBounds(viewRect);
        glassPanelLayout.checkLayoutRect(glassPanel, viewRect);
      } else {
        glassPanel.render(osg);
      }
      osg.dispose();
    }
    return image;
  }

  /**
   * Invalidate the offscreen image so that it is rendered during the next repaint operation if buffering is enabled.
   */
  public void invalidateImage() {
    validImage = false;
  }

  /**
   * Paints this component.
   * @param g
   */
  public void paintComponent(Graphics g) {
    // find the clipping rectangle within a scroll pane viewport
    viewRect = null;
    Container c = getParent();
    while(c!=null) {
      if(c instanceof JViewport) {
        viewRect = ((JViewport) c).getViewRect();
        glassPanel.setBounds(viewRect);
        glassPanelLayout.checkLayoutRect(glassPanel, viewRect);
        break;
      }
      c = c.getParent();
    }
    if(buffered) { // paint bufferImage onto screen
      if(!validImage||(getWidth()!=offscreenImage.getWidth())||(getHeight()!=offscreenImage.getHeight())) {
        if((getWidth()!=offscreenImage.getWidth())||(getHeight()!=offscreenImage.getHeight())) {
          g.setColor(Color.WHITE);
          g.fillRect(0, 0, getWidth(), getHeight());
        } else {
          g.drawImage(offscreenImage, 0, 0, null); // copy old image to the screen for now
        }
        refreshTimer.start();                    // image is not valid so start refresh timer
      } else {                                   // current image is valid and has correct size
        g.drawImage(offscreenImage, 0, 0, null); // copy image to the screen
      }
    } else {                                     // paint directly onto the screen
      paintEverything(g);
    }
    if(enableZoom) { // zoom box is always painted on top
      zoomBox.paint(g);
    }
  }

  /**
   * Gets the clipping rectance within a scroll pane viewport.
   *
   * @return the clipping rectangle
   */
  protected Rectangle getViewRect() {
    return viewRect;
  }

  /**
   * Paints all objects inside this component.
   *
   * @param g
   */
  protected void paintEverything(Graphics g) {
    if(dimensionSetter!=null) {
      Dimension interiorDimension = dimensionSetter.getInterior(this);
      if(interiorDimension!=null) {
        squareAspect = false;
        leftGutter = rightGutter = Math.max(0, getWidth()-interiorDimension.width)/2;
        topGutter = bottomGutter = Math.max(0, getHeight()-interiorDimension.height)/2;
      }
    }
    ArrayList tempList = getDrawables();
    scale(tempList);                           // set the x and y scale based on the autoscale values
    setPixelScale();
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight()); // fill the component with the background color
    g.setColor(Color.black); // restore the default drawing color
    paintDrawableList(g, tempList);
  }

  /**
   * Autoscale the x axis using min and max values.
   * from measurable objects.
   * @param autoscale
   */
  public void setAutoscaleX(boolean autoscale) {
    autoscaleX = autoscale;
  }

  /**
   * Determines if the x axis autoscale property is true.
   * @return <code>true<\code> if autoscaled.
   */
  public boolean isAutoscaleX() {
    return autoscaleX;
  }

  /**
   * Autoscale the y axis using min and max values.
   * from measurable objects.
   * @param autoscale
   */
  public void setAutoscaleY(boolean autoscale) {
    autoscaleY = autoscale;
  }

  /**
   * Determines if the y axis autoscale property is true.
   * @return <code>true<\code> if autoscaled.
   */
  public boolean isAutoscaleY() {
    return autoscaleY;
  }

  /**
   * Moves and resizes this component. The new location of the top-left
   * corner is specified by <code>x</code> and <code>y</code>, and the
   * new size is specified by <code>width</code> and <code>height</code>.
   * @param x The new <i>x</i>-coordinate of this component.
   * @param y The new <i>y</i>-coordinate of this component.
   * @param width The new <code>width</code> of this component.
   * @param height The new <code>height</code> of this
   * component.
   */
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    validImage = false;
  }

  public void setBounds(Rectangle r) {
    super.setBounds(r);
    validImage = false;
  }

  /**
   * Sets the buffered image option.
   *
   * Buffered panels copy the offscreen image into the panel during a repaint unless the image
   * has been invalidated.  Use the render() method to draw the image immediately.
   *
   * @param _buffered
   */
  public void setBuffered(boolean _buffered) {
    buffered = _buffered;
    if(buffered) {             // turn off Java buffering because we are doing our own
      setDoubleBuffered(false);
    } else {                   // small default image is not used
      workingImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
      offscreenImage = workingImage;
      setDoubleBuffered(true); // use Java's buffer
    }
    validImage = false;
  }

  public boolean isBuffered() {
    return buffered;
  }

  /**
   * Makes the component visible or invisible.
   * Overrides <code>JComponent.setVisible</code>.
   *
   * @param isVisible  true to make the component visible; false to
   *            make it invisible
   */
  public void setVisible(boolean isVisible) {
    super.setVisible(isVisible);
    if(isVisible&&buffered) {
      validImage = false;
    }
  }

  /**
   * Limits the xmin and xmax values during autoscaling so that the mininimum value
   * will be no greater than the floor and the maximum value will be no
   * smaller than the ceil.
   *
   * Setting a floor or ceil value to <code>Double.NaN<\code> will disable that limit.
   *
   * @param floor the xfloor value
   * @param ceil the xceil value
   */
  public void limitAutoscaleX(double floor, double ceil) {
    xfloor = floor;
    xceil = ceil;
  }

  /**
   * Limits ymin and ymax values during autoscaling so that the mininimum value
   * will be no greater than the floor and the maximum value will be no
   * smaller than the ceil.
   *
   * Setting a floor or ceil value to <code>Double.NaN<\code> will disable that limit.
   *
   * @param floor the yfloor value
   * @param ceil the yceil value
   */
  public void limitAutoscaleY(double floor, double ceil) {
    yfloor = floor;
    yceil = ceil;
  }

  /**
   * Sets the scale using pixels per unit.
   *
   * @param enable boolean enable fixed pixels per unit
   * @param xPixPerUnit double
   * @param yPixPerUnit double
   */
  public void setPixelsPerUnit(boolean enable, double xPixPerUnit, double yPixPerUnit) {
    fixedPixelPerUnit = enable;
    this.xPixPerUnit = xPixPerUnit;
    this.yPixPerUnit = yPixPerUnit;
  }

  /**
   * Sets the preferred scale in the vertical and horizontal direction.
   * @param xmin
   * @param xmax
   * @param ymin
   * @param ymax
   */
  public void setPreferredMinMax(double xmin, double xmax, double ymin, double ymax) {
    if(!Double.isNaN(xmin)&&!Double.isNaN(xmax)) {
      autoscaleX = false;
      if(xmin==xmax) {
        xmin = 0.9*xmin-0.5;
        xmax = 1.1*xmax+0.5;
      }
      this.xminPreferred = xmin;
      this.xmaxPreferred = xmax;
    }
    if(!Double.isNaN(ymin)&&!Double.isNaN(ymax)) {
      autoscaleY = false;
      if(ymin==ymax) {
        ymin = 0.9*ymin-0.5;
        ymax = 1.1*ymax+0.5;
      }
      this.yminPreferred = ymin;
      this.ymaxPreferred = ymax;
    }
  }

  /**
   * Sets the preferred scale in the horizontal direction.
   * @param xmin the minimum value
   * @param xmax the maximum value
   */
  public void setPreferredMinMaxX(double xmin, double xmax) {
    autoscaleX = false;
    if(xmin==xmax) {
      xmin = 0.9*xmin-0.5;
      xmax = 1.1*xmax+0.5;
    }
    this.xminPreferred = xmin;
    this.xmaxPreferred = xmax;
  }

  /**
   * Sets the preferred scale in the vertical direction.
   * @param ymin
   * @param ymax
   */
  public void setPreferredMinMaxY(double ymin, double ymax) {
    autoscaleY = false;
    if(ymin==ymax) {
      ymin = 0.9*ymin-0.5;
      ymax = 1.1*ymax+0.5;
    }
    this.yminPreferred = ymin;
    this.ymaxPreferred = ymax;
  }

  /**
   * Sets the aspect ratio for horizontal to vertical to unity when <code>true<\code>.
   * @param val
   */
  public void setSquareAspect(boolean val) {
    if(squareAspect==val) {
      return;
    }
    squareAspect = val;
    validImage = false;
    repaint();
  }

  /**
   * Determines if the number of pixels per unit is the same for both x and y.
   * @return <code>true<\code> if squareAspect
   */
  public boolean isSquareAspect() {
    return squareAspect;
  }

  /**
   * Determines if the x and y point is inside.
   *
   * @param x the coordinate in world units
   * @param y the coordinate in world units
   *
   * @return <code>true<\code> if point is inside; <code>false<\code> otherwise
   */
  public boolean isPointInside(double x, double y) {
    if(xmin<xmax) {
      if(x<xmin) {
        return false;
      }
      if(x>xmax) {
        return false;
      }
    } else { // max is less than min so scale decreases to the right
      if(x>xmin) {
        return false;
      }
      if(x<xmax) {
        return false;
      }
    }
    if(ymin<ymax) {
      if(y<ymin) {
        return false;
      }
      if(y>ymax) {
        return false;
      }
    } else { // max is less than min so scale decreases to the right
      if(y>ymin) {
        return false;
      }
      if(y<ymax) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines if the user can change scale by dragging the mouse.
   * @return <code>true<\code> if zoom is enabled
   */
  public boolean isZoom() {
    return enableZoom;
  }

  /**
   * Sets the zoom option to allow the user to change scale by dragging the mouse while pressing the shift.
   * @param _enableZoom <code>true<\code> if zoom is enabled; <code>false<\code> otherwise
   */
  public void setZoom(boolean _enableZoom) {
    enableZoom = _enableZoom;
  }

  /**
   * Zooms out by increasing the preferred min/max values by a factor of two.
   */
  protected void zoomOut() {
    double dx = xmax-xmin;
    double dy = ymax-ymin;
    setPreferredMinMax(xmin-dx/2, xmax+dx/2, ymin-dy/2, ymax+dy/2);
    validImage = false;
    if(!getIgnoreRepaint()) {
      repaint();
    }
  }

  /**
   * Zooms in on next click-drag.
   */
  protected void zoomIn() {
    zoomMode = true;
  }

  /**
   * Creates a snapshot using an image of the content.
   */
  public void snapshot() {
    DrawingPanel panel = new DrawingPanel();
    DrawingFrame frame = new DrawingFrame(panel);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setKeepHidden(false);
    panel.setSquareAspect(false);
    int w = (isVisible()) ? getWidth() : getPreferredSize().width;
    int h = (isVisible()) ? getHeight() : getPreferredSize().height;
    if((w==0)||(h==0)) {
      return;
    }
    BufferedImage snapimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    render(snapimage);
    // MeasuredImage mi = new MeasuredImage(snapimage, xmin, xmax, ymin, ymax);
    MeasuredImage mi = new MeasuredImage(snapimage, pixToX(0), pixToX(w), pixToY(h), pixToY(0));
    panel.addDrawable(mi);
    panel.setPreferredMinMax(pixToX(0), pixToX(w), pixToY(h), pixToY(0));
    panel.setPreferredSize(new Dimension(w, h));
    frame.setTitle("Snapshot");
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Determines if the user can examine and change the scale at run-time by right-clicking.
   * @return <code>true<\code> if inspector is enabled
   */
  public boolean hasInspector() {
    return(popupmenu!=null)&&popupmenu.isEnabled();
  }

  /**
   * Enables the popup inspector option.
   * The default inspector shows a popup menu by right-clicking.
   *
   * @param isEnabled <code>true<\code> if the inspector option is enabled; <code>false<\code> otherwise
   */
  public void enableInspector(boolean isEnabled) {
    popupmenu.setEnabled(isEnabled);
  }

  /**
   * Gets the popup menu.
   */
  public JPopupMenu getPopupMenu() {
    return popupmenu;
  }

  /**
   * Sets the popup menu.
   */
  public void setPopupMenu(JPopupMenu menu) {
    popupmenu = menu;
  }

  /**
   * Shows the drawing panel properties inspector.
   */
  public void showInspector() {
    if(customInspector==null) {
      // DrawingPanelInspector.getInspector(this);  // this static inspector is the same for all drawing panels.
      XMLDrawingPanelInspector.getInspector(this); // this static inspector is the same for all drawing panels.
    } else {
      customInspector.setVisible(true);
    }
  }

  /**
   * Hides the drawing panel properties inspector.
   */
  public void hideInspector() {
    if(customInspector==null) {
      DrawingPanelInspector.hideInspector(); // this static inspector is the same for all drawing panels.
    } else {
      customInspector.setVisible(false);
    }
  }

  /**
   * Sets a custom  properties inspector window.
   *
   * @param w the new inspector window
   */
  public void setCustomInspector(Window w) {
    if(customInspector!=null) {
      customInspector.setVisible(false); // hide the current inspector window
    }
    customInspector = w;
  }

  /**
   * Sets the video capture tool. May be set to null.
   *
   * @param videoCap the video capture tool
   */
  public void setVideoCaptureTool(VideoCaptureTool videoCap) {
    if(vidCap!=null) {
      vidCap.setVisible(false); // hide the current video capture tool
    }
    vidCap = videoCap;
    if(vidCap!=null) {
      setBuffered(true);
    }
  }

  /**
   * Gets the video capture tool. May be null.
   *
   * @return the video capture tool
   */
  public VideoCaptureTool getVideoCaptureTool() {
    return vidCap;
  }

  /**
   * Gets the ratio of pixels per unit in the x and y directions.
   * @return the aspect ratio
   */
  public double getAspectRatio() {
    return(pixelMatrix[3]==1) ? 1 : Math.abs(pixelMatrix[0]/pixelMatrix[3]);
  }

  /**
   * Gets the number of pixels per world unit in the x direction.
   * @return pixels per unit
   */
  public double getXPixPerUnit() {
    return pixelMatrix[0];
  }

  /**
   * Gets the number of pixels per world unit in the y direction.
   * Y pixels per unit is positive if y increases from bottom to top.
   *
   * @return pixels per unit
   */
  public double getYPixPerUnit() {
    return -pixelMatrix[3];
  }

  /**
   * Gets the larger of x or y pixels per world unit.
   * @return pixels per unit
   */
  public double getMaxPixPerUnit() {
    return Math.max(Math.abs(pixelMatrix[0]), Math.abs(pixelMatrix[3]));
  }

  /**
   * Gets the x world coordinate for the left-hand side of the drawing area.
   * @return xmin
   */
  public double getXMin() {
    return xmin;
  }

  /**
   * Gets the preferred x world coordinate for the left-hand side of the drawing area.
   * @return xmin
   */
  public double getPreferredXMin() {
    return xminPreferred;
  }

  /**
   * Gets the x world coordinate for the right-hand side of the drawing area.
   * @return xmax
   */
  public double getXMax() {
    return xmax;
  }

  /**
   * Gets the preferred x world coordinate for the right-hand side of the drawing area.
   * @return xmin
   */
  public double getPreferredXMax() {
    return xmaxPreferred;
  }

  /**
   * Gets the y world coordinate for the top of the drawing area.
   * @return ymax
   */
  public double getYMax() {
    return ymax;
  }

  /**
   * Gets the preferred y world coordinate for the top of the drawing area.
   * @return xmin
   */
  public double getPreferredYMax() {
    return ymaxPreferred;
  }

  /**
   * Gets the y world coordinate for the bottom of the drawing area.
   * @return ymin
   */
  public double getYMin() {
    return ymin;
  }

  /**
   * Gets the preferred y world coordinate for the bottom of the drawing area.
   * @return xmin
   */
  public double getPreferredYMin() {
    return yminPreferred;
  }

  /**
   * Gets the CoordinateStringBuilder that converts mouse events into a string showing world coordinates.
   * @return CoordinateStringBuilder
   */
  public CoordinateStringBuilder getCoordinateStringBuilder() {
    return coordinateStrBuilder;
  }

  /**
   * Sets the CoordinateStringBuilder that converts mouse events into a string showing world coordinates.
   */
  public void setCoordinateStringBuilder(CoordinateStringBuilder builder) {
    coordinateStrBuilder = builder;
  }

  /**
   * Gets the scale that will be used when the panel is drawn.
   * @return Rectangle2D
   */
  public Rectangle2D getScale() {
    setPixelScale();
    return new Rectangle2D.Double(xmin, ymin, xmax-xmin, ymax-ymin);
  }

  /**
   * Gets the rectangle that bounds all measurable objects.
   *
   * @return Rectangle2D
   */
  public Rectangle2D getMeasure() {
    double xmin = Double.MAX_VALUE;
    double xmax = -Double.MAX_VALUE;
    double ymin = Double.MAX_VALUE;
    double ymax = -Double.MAX_VALUE;
    boolean measurableFound = false;
    ArrayList tempList = getDrawables(); // this clones the list of drawables
    Iterator it = tempList.iterator();
    while(it.hasNext()) {
      Object obj = it.next();
      if(!(obj instanceof Measurable)||!((Measurable) obj).isMeasured()) {
        continue; // object is not measurable or measure is not set
      }
      Measurable measurable = (Measurable) obj;
      if(!Double.isNaN(measurable.getXMax())&&!Double.isNaN(measurable.getXMin())&&!Double.isNaN(measurable.getYMax())&&!Double.isNaN(measurable.getYMin())) {
        xmin = Math.min(xmin, measurable.getXMin());
        xmax = Math.max(xmax, measurable.getXMax());
        ymin = Math.min(ymin, measurable.getYMin());
        ymax = Math.max(ymax, measurable.getYMax());
        measurableFound = true; // we have at least one valid min-max measure
      }
    }
    if(measurableFound) {
      return new Rectangle2D.Double(xmin, ymin, xmax-xmin, ymax-ymin);
    } else {
      return new Rectangle2D.Double(0, 0, 0, 0);
    }
  }

  /**
   * Gets the affine transformation that converts from world to pixel coordinates.
   * @return the affine transformation
   */
  public AffineTransform getPixelTransform() {
    return(AffineTransform) pixelTransform.clone();
  }

  /**
   * Retrieves the 6 specifiable values in the pixel transformation
   * matrix and places them into an array of double precisions values.
   * The values are stored in the array as
   * {&nbsp;m00&nbsp;m10&nbsp;m01&nbsp;m11&nbsp;m02&nbsp;m12&nbsp;}.
   *
   * @return the transformation matrix
   */
  public double[] getPixelMatrix() {
    return pixelMatrix;
  }

  /**
   *  Calculates min and max values and the affine transformation based on the
   *  current size of the panel and the squareAspect boolean.
   */
  public void setPixelScale() {
    xmin = xminPreferred; // start with the preferred values.
    xmax = xmaxPreferred;
    ymin = yminPreferred;
    ymax = ymaxPreferred;
    width = getWidth();
    height = getHeight();
    if(fixedPixelPerUnit) { // the user has specified a fixed pixel scale
      xmin = (xmaxPreferred+xminPreferred)/2-Math.max(width-leftGutter-rightGutter-1, 1)/xPixPerUnit/2;
      xmax = (xmaxPreferred+xminPreferred)/2+Math.max(width-leftGutter-rightGutter-1, 1)/xPixPerUnit/2;
      ymin = (ymaxPreferred+yminPreferred)/2-Math.max(height-bottomGutter-topGutter-1, 1)/yPixPerUnit/2;
      ymax = (ymaxPreferred+yminPreferred)/2+Math.max(height-bottomGutter-topGutter-1, 1)/yPixPerUnit/2;
      pixelTransform = new AffineTransform(xPixPerUnit, 0, 0, -yPixPerUnit, -xmin*xPixPerUnit+leftGutter, ymax*yPixPerUnit+topGutter);
      pixelTransform.getMatrix(pixelMatrix); // puts the transformation into the pixel matrix
      return;
    }
    xPixPerUnit = Math.max(width-leftGutter-rightGutter-1, 1)/(xmax-xmin);
    yPixPerUnit = Math.max(height-bottomGutter-topGutter-1, 1)/(ymax-ymin); // the y scale in pixels
    if(squareAspect) {
      double stretch = Math.abs(xPixPerUnit/yPixPerUnit);
      if(stretch>=1) {                                                         // make the x range bigger so that aspect ratio is one
        stretch = Math.min(stretch, width);                                    // limit the stretch
        xmin = xminPreferred-(xmaxPreferred-xminPreferred)*(stretch-1)/2.0;
        xmax = xmaxPreferred+(xmaxPreferred-xminPreferred)*(stretch-1)/2.0;
        xPixPerUnit = Math.max(width-leftGutter-rightGutter-1, 1)/(xmax-xmin); // the x scale in pixels per unit
      } else {                                   // make the y range bigger so that aspect ratio is one
        stretch = Math.max(stretch, 1.0/height); // limit the stretch
        ymin = yminPreferred-(ymaxPreferred-yminPreferred)*(1.0/stretch-1)/2.0;
        ymax = ymaxPreferred+(ymaxPreferred-yminPreferred)*(1.0/stretch-1)/2.0;
        yPixPerUnit = Math.max(height-bottomGutter-topGutter-1, 1)/(ymax-ymin); // the y scale in pixels per unit
      }
    }
    pixelTransform = new AffineTransform(xPixPerUnit, 0, 0, -yPixPerUnit, -xmin*xPixPerUnit+leftGutter, ymax*yPixPerUnit+topGutter);
    pixelTransform.getMatrix(pixelMatrix); // puts the transformation into the pixel matrix
  }

  /**
   * Projects a 2D or 3D world coordinate to a pixel coordinate.
   *
   * An (x, y) point will project to (xpix, ypix).
   * An (x, y, z) point will project to (xpix, ypix).
   * An (x, y, delta_x, delta_y) point will project to (xpix, ypix, delta_xpix, delta_ypix).
   * An (x, y, z, delta_x, delta_y, delta_z) point will project to (xpix, ypix, delta_xpix, delta_ypix).
   *
   * @param coordinate
   * @param pixel
   * @return pixel
   */
  public double[] project(double[] coordinate, double[] pixel) {
    switch(coordinate.length) {
    case 2 :                                // input is x,y
    case 3 :                                // input is x,y,z
      pixel[0] = xToPix(coordinate[0]);     // x
      pixel[1] = yToPix(coordinate[1]);     // y
      break;
    case 4 :                                // input is x,y,dx,dy
      pixel[0] = xToPix(coordinate[0]);     // x
      pixel[1] = yToPix(coordinate[1]);     // y
      pixel[2] = xPixPerUnit*coordinate[2]; // dx
      pixel[3] = yPixPerUnit*coordinate[3]; // dy
      break;
    case 6 :                                // input is x,y,z,dx,dy,dz
      pixel[0] = xToPix(coordinate[0]);     // x
      pixel[1] = yToPix(coordinate[1]);     // y
      pixel[2] = xPixPerUnit*coordinate[3]; // dx
      pixel[3] = yPixPerUnit*coordinate[4]; // dy
      break;
    default :
      throw new IllegalArgumentException("Method project not supported for this length.");
    }
    return pixel;
  }

  /**
   * Converts pixel to x world units.
   * @param pix
   * @return x panel units
   */
  public double pixToX(int pix) {
    return xmin+(pix-leftGutter)/xPixPerUnit;
  }

  /**
   * Converts x from world to pixel units.
   * @param x
   * @return the pixel value of the x coordinate
   */
  public int xToPix(double x) {
    double pix = pixelMatrix[0]*x+pixelMatrix[4];
    if(pix>Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    if(pix<Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }
    // return  (int)Math.round(pix);
    return(int) Math.floor((float) pix); // gives better registration with affine transformation
  }

  /**
   * Converts pixel to x world units.
   * @param pix
   * @return x panel units
   */
  public double pixToY(int pix) {
    return ymax-(pix-topGutter)/yPixPerUnit;
  }

  /**
   * Converts y from world to pixel units.
   * @param y
   * @return the pixel value of the y coordinate
   */
  public int yToPix(double y) {
    // double pix = (ymax - y) * yPixPerUnit + topGutter;
    double pix = pixelMatrix[3]*y+pixelMatrix[5];
    if(pix>Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    if(pix<Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }
    // return  (int)Math.round(pix);
    return(int) Math.floor((float) pix); // gives better registration with affine transformation
  }

  /**
   * Sets axis scales if autoscale is true using the max and min values of the measurable objects.
   */
  public void scale() {
    ArrayList tempList = getDrawables(); // this clones the list of drawables
    scale(tempList);
  }

  /**
   * Sets axis scales if autoscale is true using the max and min values of the objects in the given list.
   */
  protected void scale(ArrayList tempList) {
    if(autoscaleX) {
      scaleX(tempList);
    }
    if(autoscaleY) {
      scaleY(tempList);
    }
  }

  /**
   * Sets the scale based on the max and min values of all measurable objects.
   *
   * Autoscale flags are not respected.
   */
  public void measure() {
    ArrayList tempList = getDrawables(); // this clones the list of drawables
    scaleX(tempList);
    scaleY(tempList);
    setPixelScale();
    validImage = false;
  }

  /**
   * Sets the x axis scale based on the max and min values of all measurable objects.  Autoscale flag is not respected.
   */
  protected void scaleX() {
    ArrayList tempList = getDrawables(); // this clones the list of drawables
    scaleX(tempList);
  }

  /**
   * Sets the x axis scale based on the max and min values of all measurable objects.  Autoscale flag is not respected.
   */
  private void scaleX(ArrayList tempList) {
    double newXMin = Double.MAX_VALUE;
    double newXMax = -Double.MAX_VALUE;
    boolean measurableFound = false;
    Iterator it = tempList.iterator();
    while(it.hasNext()) {
      Object obj = it.next();
      if(!(obj instanceof Measurable)) {
        continue;               // object is not measurable
      }
      Measurable measurable = (Measurable) obj;
      if(!measurable.isMeasured()) {
        continue;               // objects' measure not yet set
      }
      double xmi = measurable.getXMin(), xma = measurable.getXMax();
      if(!Double.isNaN(xmi)&&!Double.isNaN(xma)) {
        newXMin = Math.min(newXMin, xmi);
        newXMin = Math.min(newXMin, xma);
        newXMax = Math.max(newXMax, xma);
        newXMax = Math.max(newXMax, xmi);
        measurableFound = true; // we have at least one valid min-max measure
      }
    }
    // do not change change values unless there is at least one measurable object.
    if(measurableFound) {
      if(newXMax-newXMin<Float.MIN_VALUE) {  // range is too small to be meaningful for plotting; newXMax-newXMin is always positive
        if(Double.isNaN(xfloor)) {
          newXMin = 0.9*newXMin-0.5;
        } else {
          newXMin = xfloor;
        }
        if(Double.isNaN(xceil)) {
          newXMax = 1.1*newXMax+0.5;
        } else {
          newXMax = xceil;
        }
      }
      double range = newXMax-newXMin;  //range will always be positive
      while(Math.abs((newXMax+range)/range)>1e5) { // limit autoscale to 5 decimal places
          range *= 2;  // increase the range
          newXMin -= range;
          newXMax += range;
      }
      xminPreferred = newXMin-autoscaleMargin*range;
      xmaxPreferred = newXMax+autoscaleMargin*range;
    } else { // we don't have any measurables
      if(!Double.isNaN(xfloor)) {
        xminPreferred = xfloor;
      }
      if(!Double.isNaN(xceil)) {
        xmaxPreferred = xceil;
      }
    }
    if(!Double.isNaN(xfloor)) {
      xminPreferred = Math.min(xfloor, xminPreferred);
    }
    if(!Double.isNaN(xceil)) {
      xmaxPreferred = Math.max(xceil, xmaxPreferred);
    }
  }

  /**
   * Sets the y axis scale based on the max and min values of all measurable objects. Autoscale flag is not respected.
   */
  protected void scaleY() {
    ArrayList tempList = getDrawables(); // this clones the list of drawables
    scaleY(tempList);
  }

  /**
   * Sets the y axis scale based on the max and min values of all measurable objects. Autoscale flag is not respected.
   */
  private void scaleY(ArrayList tempList) {
    double newYMin = Double.MAX_VALUE;
    double newYMax = -Double.MAX_VALUE;
    boolean measurableFound = false;
    Iterator it = tempList.iterator();
    while(it.hasNext()) {
      Object obj = it.next();
      if(!(obj instanceof Measurable)) {
        continue; // object is not measurable
      }
      Measurable measurable = (Measurable) obj;
      if(!measurable.isMeasured()) {
        continue; // objects' measure not yet set
      }
      double ymi = measurable.getYMin(), yma = measurable.getYMax();
      if(!Double.isNaN(ymi)&&!Double.isNaN(yma)) {
        newYMin = Math.min(newYMin, ymi);
        newYMin = Math.min(newYMin, yma);
        newYMax = Math.max(newYMax, yma);
        newYMax = Math.max(newYMax, ymi);
        measurableFound = true;
      }
    }
    // do not change change values unless there is at least one measurable object.
    if(measurableFound) {
      if(newYMax-newYMin<Float.MIN_VALUE) {
        if(Double.isNaN(yfloor)) {
          newYMin = 0.9*newYMin-0.5;
        } else {
          newYMin = yfloor;
        }
        if(Double.isNaN(yceil)) {
          newYMax = 1.1*newYMax+0.5;
        } else {
          newYMax = yceil;
        }
      }
      double range = newYMax-newYMin;
      while(Math.abs((newYMax+range)/range)>1e5) { // limit autoscale to 5 decimal places
          range *= 2;  // increase the range
          newYMin -= range;
          newYMax += range;
      }
      yminPreferred = newYMin-autoscaleMargin*range;
      ymaxPreferred = newYMax+autoscaleMargin*range;
    } else { // we don't have any measurables
      if(!Double.isNaN(yfloor)) {
        yminPreferred = yfloor;
      }
      if(!Double.isNaN(yceil)) {
        ymaxPreferred = yceil;
      }
    }
    if(!Double.isNaN(yfloor)) {
      yminPreferred = Math.min(yfloor, yminPreferred);
    }
    if(!Double.isNaN(yceil)) {
      ymaxPreferred = Math.max(yceil, ymaxPreferred);
    }
  }

  /**
   * Paints all the drawable objects in the panel.
   * @param g
   */
  protected void paintDrawableList(Graphics g, ArrayList tempList) {
    Graphics2D g2 = (Graphics2D) g;
    Iterator it = tempList.iterator();
    Shape clipShape = g2.getClip();
    int w = getWidth()-leftGutter-rightGutter;
    int h = getHeight()-bottomGutter-topGutter;
    if((w<0)||(h<0)) {
      return;
    }
    if(clipAtGutter) {
      g2.clipRect(leftGutter, topGutter, w, h);
    }
    if((tempList!=null)&&!tempList.isEmpty()&&(tempList.get(0) instanceof False3D)) {
      ((Drawable) tempList.get(0)).draw(this, g2);
    } else {
      while(it.hasNext()) {
        Drawable drawable = (Drawable) it.next();
        drawable.draw(this, g2);
      }
    }
    g2.setClip(clipShape);
  }

  /**
   * Gets the glass panel.
   *
   * The glass panel is a trasparent panel that contians the messages boxes and other compotnents.
   * @return JPanel
   */
  public JPanel getGlassPanel() {
    return glassPanel;
  }

  public void setIgnoreRepaint(boolean ignoreRepaint) {
    super.setIgnoreRepaint(ignoreRepaint);
    glassPanel.setIgnoreRepaint(ignoreRepaint);
  }

  /**
   * Gets the object that sets the gutters for this panel.
   * @return Dimensioned
   */
  public Dimensioned getDimensionSetter() {
    return dimensionSetter;
  }

  /**
   * Adds a drawable object to the drawable list.
   * @param drawable
   */
  public synchronized void addDrawable(Drawable drawable) {
    if((drawable!=null)&&!drawableList.contains(drawable)) {
      drawableList.add(drawable);
      validImage = false;
    }
    if(drawable instanceof Dimensioned) {
      dimensionSetter = ((Dimensioned) drawable);
    }
  }

  /**
   * Adds a collection of drawable objects to the drawable list.
   * @param drawables
   */
  public synchronized void addDrawables(Collection drawables) {
    Iterator it = drawables.iterator();
    while(it.hasNext()) {
      Object obj = it.next();
      if(obj instanceof Drawable) {
        addDrawable((Drawable) obj);
      }
    }
  }

  /**
   * Replaces a drawable object with another drawable.
   *
   * @param oldDrawable Drawable
   * @param newDrawable Drawable
   */
  public synchronized void replaceDrawable(Drawable oldDrawable, Drawable newDrawable) {
    if((oldDrawable!=null)&&drawableList.contains(oldDrawable)) {
      int i = drawableList.indexOf(oldDrawable);
      drawableList.set(i, newDrawable);
      if(newDrawable instanceof Dimensioned) {
        dimensionSetter = ((Dimensioned) newDrawable);
      }
    } else {
      addDrawable(newDrawable); // oldDrawable does not exist
    }
  }

  /**
   * Removes a drawable object from the drawable list.
   * @param drawable
   */
  public synchronized void removeDrawable(Drawable drawable) {
    drawableList.remove(drawable);
    if(drawable instanceof Dimensioned) {
      dimensionSetter = null;
    }
  }

  /**
   * Removes all objects of the given class from the drawable list.
   *
   * Assignable subclasses are NOT removed.  Interfaces CANNOT be specified.
   *
   * @param c the class
   * @see #removeDrawables(Class c)
   */
  public synchronized void removeObjectsOfClass(Class c) {
    Iterator it = drawableList.iterator();
    while(it.hasNext()) {
      Object element = it.next();
      if(element.getClass()==c) {
        it.remove();
        if(element instanceof Dimensioned) {
          dimensionSetter = null;
        }
      }
    }
  }

  /**
   * Removes all objects assignable to the given class from the drawable list.
   * Interfaces can be specified.
   *
   * @param c the class
   * @see #removeObjectsOfClass(Class c)
   */
  public synchronized void removeDrawables(Class c) {
    Iterator it = drawableList.iterator();
    while(it.hasNext()) {
      Object element = it.next();
      if(c.isInstance(element)) {
        it.remove();
        if(element instanceof Dimensioned) {
          dimensionSetter = null;
        }
      }
    }
  }

  /**
   * Removes the option controller.
   *
   * The option controller may interfere with other mouse ac
   */
  public void removeOptionController() {
    removeMouseListener(optionController);
    removeMouseMotionListener(optionController);
  }

  /**
   * Removes all drawable objects from the drawable list.
   */
  public synchronized void clear() {
    drawableList.clear();
    dimensionSetter = null;
  }

  /**
   * Gets the cloned list of Drawable objects.
   *
   * This is a shallow clone.  The same objects will be in both the drawable list and the
   * cloned list.
   * @return cloned list
   */
  public synchronized ArrayList getDrawables() {
    return(ArrayList) drawableList.clone();
  }

  /**
   * Gets Drawable objects of an assignable type. The list contains
   * objects that are assignable from the class or interface.
   *
   * Returns a shallow clone.  The same objects will be in the drawable list and the
   * cloned list.
   *
   * @param c the type of Drawable object
   *
   * @return the cloned list
   *
   * @see #getObjectOfClass(Class c)
   */
  public synchronized ArrayList getDrawables(Class c) {
    ArrayList newList = (ArrayList) drawableList.clone();
    Iterator it = newList.iterator();
    while(it.hasNext()) { // copy only the obejcts of the correct type
      Object obj = it.next();
      if(!c.isInstance(obj)) {
        it.remove();
      }
    }
    return newList;
  }

  /**
   * Gets objects of a specific class from the drawables list.
   *
   * Assignable subclasses are NOT returned.  Interfaces CANNOT be specified.
   * The same objects will be in the drawable list and the cloned list.
   *
   * @param c the class of the object
   *
   * @return the list
   * @see #getDrawables(Class c)
   */
  public synchronized ArrayList getObjectOfClass(Class c) {
    ArrayList newList = (ArrayList) drawableList.clone();
    Iterator it = newList.iterator();
    while(it.hasNext()) { // copy only the obejcts of the correct type
      Object obj = it.next();
      if(obj.getClass()!=c) {
        it.remove();
      }
    }
    return newList;
  }

  /**
   * Draws the panel into a graphics object suitable for printing.
   * @param g
   * @param pageFormat
   * @param pageIndex
   * @return status code
   * @exception PrinterException
   */
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
    if(pageIndex>=1) { // only one page available
      return Printable.NO_SUCH_PAGE;
    }
    if(g==null) {
      return Printable.NO_SUCH_PAGE;
    }
    Graphics2D g2 = (Graphics2D) g;
    double scalex = pageFormat.getImageableWidth()/(double) getWidth();
    double scaley = pageFormat.getImageableHeight()/(double) getHeight();
    double scale = Math.min(scalex, scaley);
    g2.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
    g2.scale(scale, scale);
    paintEverything(g2);
    return Printable.PAGE_EXISTS;
  }

  /**
   * Gets the gutters.
   */
  public int[] getGutters() {
    return new int[] {leftGutter, topGutter, rightGutter, bottomGutter};
  }

  /**
   * Sets the gutters using the given array.
   * @param gutters int[]
   */
  public void setGutters(int[] gutters) {
    leftGutter = gutters[0];
    topGutter = gutters[1];
    rightGutter = gutters[2];
    bottomGutter = gutters[3];
  }

  /**
   * Gets the bottom gutter of this DrawingPanel.
   *
   * @return bottom gutter
   */
  public int getBottomGutter() {
    return bottomGutter;
  }

  /**
   * Gets the bottom gutter of this DrawingPanel.
   *
   * @return right gutter
   */
  public int getTopGutter() {
    return topGutter;
  }

  /**
   * Gets the left gutter of this DrawingPanel.
   *
   * @return left gutter
   */
  public int getLeftGutter() {
    return leftGutter;
  }

  /**
   * Gets the right gutter of this DrawingPanel.
   *
   * @return right gutter
   */
  public int getRightGutter() {
    return rightGutter;
  }

  /**
   * Shows a message in a yellow text box in the lower right hand corner.
   *
   * @param msg
   */
  public void setMessage(String msg) {
    brMessageBox.setText(msg); // the default message box
  }

  /**
   * Shows a message in a yellow text box.
   *
   * location 0=bottom left
   * location 1=bottom right
   * location 2=top right
   * location 3=top left
   *
   * @param msg
   * @param location
   */
  public void setMessage(String msg, int location) {
    switch(location) {
    case 0 : // usually used for mouse coordiantes
      blMessageBox.setText(msg);
      break;
    case 1 :
      brMessageBox.setText(msg);
      break;
    case 2 :
      trMessageBox.setText(msg);
      break;
    case 3 :
      tlMessageBox.setText(msg);
      break;
    }
  }

  /**
   * Show the coordinates in the text box in the lower left hand corner.
   *
   * @param show
   */
  public void setShowCoordinates(boolean show) {
    if(showCoordinates&&!show) {
      this.removeMouseListener(mouseController);
      this.removeMouseMotionListener(mouseController);
    } else if(!showCoordinates&&show) {
      this.addMouseListener(mouseController);
      this.addMouseMotionListener(mouseController);
    }
    showCoordinates = show;
  }

  /**
   * The CMController class handles mouse related events in order to display
   * coordinates in the mouse box.
   */
  private class CMController extends MouseController {
    protected DecimalFormat scientificFormat = new DecimalFormat("0.###E0");
    protected DecimalFormat decimalFormat = new DecimalFormat("0.00");

    /**
     * Handle the mouse pressed event.
     * @param e
     */
    public void mousePressed(MouseEvent e) {
      String s = coordinateStrBuilder.getCoordinateString(DrawingPanel.this, e);
      blMessageBox.setText(s);
    }

    /**
     * Handle the mouse released event.
     * @param e
     */
    public void mouseReleased(MouseEvent e) {
      blMessageBox.setText(null);
    }

    /**
     * Handle the mouse entered event.
     * @param e
     */
    public void mouseEntered(MouseEvent e) {
      if(showCoordinates) {
        setMouseCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      }
    }

    /**
     * Handle the mouse exited event.
     * @param e
     */
    public void mouseExited(MouseEvent e) {
      setMouseCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Handle the mouse clicked event.
     * @param e
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Handle the mouse dragged event.
     * @param e
     */
    public void mouseDragged(MouseEvent e) {
      String s = coordinateStrBuilder.getCoordinateString(DrawingPanel.this, e);
      blMessageBox.setText(s);
    }

    /**
     * Handle the mouse moved event.
     * @param e
     */
    public void mouseMoved(MouseEvent e) {}
  }

  /**
   * ZoomBox creates an on-screen rectangle using XORMode for fast redrawing.
   */
  public class ZoomBox {
    boolean visible = false;
    int xstart, ystart; // start of mouse dowm
    int xstop, ystop;   // most recent mouse drag
    int xlast, ylast;   // corner postion during last drawing

    /**
     * Starts the zoom by saving the corner location.
     *
     * @param xpix
     * @param ypix
     */
    public void startZoom(int xpix, int ypix) {
      xlast = xstop = xstart = xpix;
      ylast = ystop = ystart = ypix;
      visible = true;
    }

    /**
     * Ends the zoom by setting a new scale.
     */
    public void endZoom(int xpix, int ypix) {
      zoomMode = false;
      if(!visible) {
        return; // startZoom must be called first
      }
      xstop = xpix;
      ystop = ypix;
      visible = false;
      if((xstart==xstop)||(ystart==ystop)) {
        return; // the user clicked but did not drag
      }
      double xmin = pixToX(xstart);
      double xmax = pixToX(xstop);
      double ymax = pixToY(ystart);
      double ymin = pixToY(ystop);
      if(!autoscaleX&&!autoscaleY) {
        setPreferredMinMax(xmin, xmax, ymin, ymax); // zoom both axes
      } else if(!autoscaleX) {
        setPreferredMinMaxX(xmin, xmax);
      } else if(!autoscaleY) {
        setPreferredMinMaxY(ymin, ymax);
      }
      xlast = xstop = xstart = 0; // clear the old values
      ylast = ystop = ystart = 0;
      validImage = false;
      if(!getIgnoreRepaint()) {
        DrawingPanel.this.repaint(); // repaint the panel with the new scale
      }
    }

    /**
     * Drags the corner of the Zoombox.
     *
     * Drag uses XORMode drawing to first erase and then repaint the box.
     *
     * @param xpix
     * @param ypix
     */
    synchronized public void drag(int xpix, int ypix) {
      if(!visible) {
        return; // startZoom must be called first
      }
      xstop = xpix;
      ystop = ypix;
      Graphics g = DrawingPanel.this.getGraphics();
      if(g==null) {
        return;
      }
      g.setXORMode(Color.green);
      g.drawRect(Math.min(xstart, xlast), Math.min(ystart, ylast), Math.abs(xlast-xstart), Math.abs(ylast-ystart));
      xlast = xstop;
      ylast = ystop;
      g.drawRect(Math.min(xstart, xlast), Math.min(ystart, ylast), Math.abs(xlast-xstart), Math.abs(ylast-ystart));
      g.setPaintMode();
      g.dispose();
    }

    /**
     * Paints the ZoomBox if it is visible.
     *
     * The ZoomBox is painted in XORMode so that it can be quickly erased during a drag operation.
     *
     * @param g
     */
    synchronized void paint(Graphics g) {
      if(!visible) {
        return;
      }
      g.setXORMode(Color.green);
      xlast = xstop;
      ylast = ystop;
      g.drawRect(Math.min(xstart, xlast), Math.min(ystart, ylast), Math.abs(xlast-xstart), Math.abs(ylast-ystart));
      g.setPaintMode();
    }
  }

  class PopupmenuListener implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      zoomMode = false;
      String cmd = evt.getActionCommand();
      if(cmd.equals("Inspect")) {
        DrawingPanel.this.showInspector();
      } else if(cmd.equals("Snapshot")) {
        snapshot();
      } else if(cmd.equals("Zoom In")) {
        if(autoscaleX||autoscaleY) {
          AutoScaleInspector plotInspector = new AutoScaleInspector(DrawingPanel.this);
          plotInspector.setLocationRelativeTo(DrawingPanel.this);
          plotInspector.updateDisplay();
          plotInspector.setVisible(true);
        }
        zoomIn(); // sets zoomMode to true
      } else if(cmd.equals("Zoom Out")) {
        if(autoscaleX||autoscaleY) {
          AutoScaleInspector plotInspector = new AutoScaleInspector(DrawingPanel.this);
          plotInspector.setLocationRelativeTo(DrawingPanel.this);
          plotInspector.updateDisplay();
          plotInspector.setVisible(true);
        }
        zoomOut();
      } else if(cmd.equals("Scale")) {
        ScaleInspector plotInspector = new ScaleInspector(DrawingPanel.this);
        plotInspector.setLocationRelativeTo(DrawingPanel.this);
        plotInspector.updateDisplay();
        plotInspector.setVisible(true);
      }
    }
  }

  /**
   * OptionController handles mouse actions including zoom.
   */
  class OptionController extends MouseController {

    /**
     * Handles the mouse pressed event.
     * @param e
     */
    public void mousePressed(MouseEvent e) {
      // if( e.getModifiers() == MouseEvent.BUTTON3_MASK &&popupmenu!=null && popupmenu.isEnabled()) {  // right click to show menu
      if(e.isPopupTrigger()&&(popupmenu!=null)&&popupmenu.isEnabled()) {
        popupmenu.show(e.getComponent(), e.getX(), e.getY());
        return;
      } else if(e.isPopupTrigger()&&(popupmenu==null)&&(customInspector!=null)) {
        customInspector.setVisible(true);
        return;
      }
      if(enableZoom&&e.isControlDown()) { // signal for zoom out
        zoomBox.endZoom(e.getX(), e.getY());
        if(!autoscaleX&&!autoscaleY) {    // zoom only those axes that do not autoscale
          scaleX(); // reset the scale on both axes
          scaleY(); // reset the scale on both axes
        } else if(!autoscaleX) {
          scaleX(); // reset the scale on x axis
        } else if(!autoscaleY) {
          scaleY(); // reset the scale on y axis
        }
        setPixelScale();
        if(customInspector==null) {
          DrawingPanelInspector.updateValues(DrawingPanel.this);
        }
        return;
      }
      if((enableZoom&&e.isShiftDown())||zoomMode) { // signal for zoom in
        zoomBox.startZoom(e.getX(), e.getY());
      }
    }

    /**
     * Handles the mouse dragged event.
     * @param e
     */
    public void mouseDragged(MouseEvent e) {
      if(zoomBox.visible) {
        zoomBox.drag(e.getX(), e.getY());
      }
    }

    /**
     * Handles the mouse released event.
     *
     * @param e
     */
    public void mouseReleased(MouseEvent e) {
      if(e.isPopupTrigger()&&(popupmenu!=null)&&popupmenu.isEnabled()) {
        popupmenu.show(e.getComponent(), e.getX(), e.getY());
        return;
      } else if(e.isPopupTrigger()&&(popupmenu==null)&&(customInspector!=null)) {
        customInspector.setVisible(true);
        return;
      }
      if(zoomBox.visible) {
        zoomBox.endZoom(e.getX(), e.getY());
        if(customInspector==null) {
          DrawingPanelInspector.updateValues(DrawingPanel.this);
        }
      }
    }

    /**
     * Method mouseClicked
     *
     * @param e
     */
    public void mouseClicked(MouseEvent e) {
      if(e.isPopupTrigger()&&(popupmenu!=null)&&popupmenu.isEnabled()) {
        popupmenu.show(e.getComponent(), e.getX(), e.getY());
        return;
      } else if(e.isPopupTrigger()&&(popupmenu==null)&&(customInspector!=null)) {
        customInspector.setVisible(true);
      }
    }

    /**
     * Method mouseEntered
     *
     * @param e
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Method mouseExited
     *
     * @param e
     */
    public void mouseExited(MouseEvent e) {}

    /**
     * Method mouseMoved
     *
     * @param e
     */
    public void mouseMoved(MouseEvent e) {}
  }

  class GlassPanel extends JPanel {
    public void render(Graphics g) {
      Component[] c = glassPanelLayout.getComponents();
      for(int i = 0, n = c.length;i<n;i++) {
        if(c[i]==null) {
          continue;
        }
        g.translate(c[i].getX(), c[i].getY());
        c[i].print(g);
        g.translate(-c[i].getX(), -c[i].getY());
      }
    }
  }

  /**
   * Returns an XML.ObjectLoader to save and load object data.
   *
   * @return the XML.ObjectLoader
   */
  public static XML.ObjectLoader getLoader() {
    return new DrawingPanelLoader();
  }

  /**
   * A class to save and load DrawingPanel data.
   */
  static class DrawingPanelLoader implements XML.ObjectLoader {

    /**
     * Saves DrawingPanel data in an XMLControl.
     *
     * @param control the control
     * @param obj the DrawingPanel to save
     */
    public void saveObject(XMLControl control, Object obj) {
      DrawingPanel panel = (DrawingPanel) obj;
      control.setValue("preferred x min", panel.getPreferredXMin());
      control.setValue("preferred x max", panel.getPreferredXMax());
      control.setValue("preferred y min", panel.getPreferredYMin());
      control.setValue("preferred y max", panel.getPreferredYMax());
      control.setValue("autoscale x", panel.isAutoscaleX());
      control.setValue("autoscale y", panel.isAutoscaleY());
      control.setValue("square aspect", panel.isSquareAspect());
      control.setValue("drawables", panel.getDrawables());
    }

    /**
     * Creates a DrawingPanel.
     *
     * @param control the control
     * @return the newly created panel
     */
    public Object createObject(XMLControl control) {
      DrawingPanel panel = new DrawingPanel();
      double xmin = control.getDouble("preferred x min");
      double xmax = control.getDouble("preferred x max");
      double ymin = control.getDouble("preferred y min");
      double ymax = control.getDouble("preferred y max");
      panel.setPreferredMinMax(xmin, xmax, ymin, ymax); // this sets autoscale to false
      if(control.getBoolean("autoscale x")) {
        panel.setAutoscaleX(true);
      }
      if(control.getBoolean("autoscale y")) {
        panel.setAutoscaleY(true);
      }
      return panel;
    }

    /**
     * Loads a DrawingPanel with data from an XMLControl.
     *
     * @param control the control
     * @param obj the object
     * @return the loaded object
     */
    public Object loadObject(XMLControl control, Object obj) {
      DrawingPanel panel = (DrawingPanel) obj;
      double xmin = control.getDouble("preferred x min");
      double xmax = control.getDouble("preferred x max");
      double ymin = control.getDouble("preferred y min");
      double ymax = control.getDouble("preferred y max");
      panel.setPreferredMinMax(xmin, xmax, ymin, ymax); // this sets autoscale to false
      panel.squareAspect = control.getBoolean("square aspect");
      if(control.getBoolean("autoscale x")) {
        panel.setAutoscaleX(true);
      }
      if(control.getBoolean("autoscale y")) {
        panel.setAutoscaleY(true);
      }
      // load the drawables
      Collection drawables = (Collection) control.getObject("drawables");
      if(drawables!=null) {
        panel.clear();
        Iterator it = drawables.iterator();
        while(it.hasNext()) {
          panel.addDrawable((Drawable) it.next());
        }
      }
      return obj;
    }
  }
}

/*
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */

/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.graphics.mousecontrols.CompositeMouseInputListener;
import edu.colorado.phet.common.view.graphics.mousecontrols.CursorControl;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationHandler;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.RectangleUtils;
//import edu.colorado.phet.common.util.persistence.PersistentAffineTransform;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Stack;

/**
 * PhetGraphic is the base class for all PhET graphics.
 * <p/>
 * This graphic class auto-magically repaints itself in the appropriate bounds,
 * using component.paint(int x,int y,int width,int height).
 * This class manages the current and previous bounds for painting, and whether
 * the region is dirty.
 *
 * @author ?
 * @version $Revision$
 */
public abstract class PhetGraphic {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Point location = new Point();
    private Point registrationPoint = new Point();
    private AffineTransform transform = new AffineTransform();
    private Rectangle lastBounds = new Rectangle();
    private Rectangle bounds = new Rectangle();
    private Component component;
    private boolean visible = true;
    private boolean boundsDirty = true;
    private RenderingHints savedRenderingHints;
    private RenderingHints renderingHints;
    private Stack graphicsStates = new Stack();
    private GraphicLayerSet parent;

    /*A bit of state to facilitate interactivity.*/
    protected CompositeMouseInputListener mouseInputListener = new CompositeMouseInputListener();//delegate
    protected CompositeKeyListener keyListener = new CompositeKeyListener();//delegate
    private CursorControl cursorControl;
    private MouseInputAdapter popupHandler;
    private ArrayList listeners = new ArrayList();
    private boolean ignoreMouse = false;
    private boolean autorepaint = true;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a PhetGraphic on the specified component.
     *
     * @param component The component in which the PhetGraphic will be drawn.
     */
    protected PhetGraphic( Component component ) {
        this.component = component;
    }

    /**
     * Provided for Java Beans conformance
     */
    protected PhetGraphic() {
        //noop
    }
    
    //----------------------------------------------------------------------------
    // Accessor methods
    //----------------------------------------------------------------------------
    
    /**
     * Returns the Component within which this PhetGraphic is contained.
     *
     * @return the component
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Set the Component within which this PhetGraphic is contained
     *
     * @param component
     */
    public void setComponent( Component component ) {
        this.component = component;
    }

    /**
     * Sets the parent of this Graphic. Public for Java Beans conformance
     *
     * @param parent the Parent that contains this graphic.
     */
    public void setParent( GraphicLayerSet parent ) {
        this.parent = parent;
    }

    /**
     * Provided for Java Beans conformance
     * @return
     */
    public GraphicLayerSet getParent() {
        return parent;
    }

    public boolean isAutorepaint() {
        return autorepaint;
    }

    public void setAutorepaint( boolean autorepaint ) {
        this.autorepaint = autorepaint;
    }

    public ArrayList getListeners() {
        return listeners;
    }

    public void setListeners( ArrayList listeners ) {
        this.listeners = listeners;
    }

    public void setBounds( Rectangle bounds ) {
        this.bounds = bounds;
    }

    public Rectangle getLastBounds() {
        return lastBounds;
    }

    public void setLastBounds( Rectangle lastBounds ) {
        this.lastBounds = lastBounds;
    }


    //----------------------------------------------------------------------------
    // Graphics Context methods
    //----------------------------------------------------------------------------
    
    /**
     * Saves the graphics context by pushing it onto a stack.
     *
     * @param g2 the graphics context
     */
    protected void saveGraphicsState( Graphics2D g2 ) {
        graphicsStates.push( new GraphicsState( g2 ) );
    }

    /**
     * Restores the graphics context that is on top of the stack.
     * The context is popped off of the stack.  If this stack is
     * empty, calling this method does nothing.
     */
    protected void restoreGraphicsState() {
        if( !graphicsStates.empty() ) {
            GraphicsState gs = (GraphicsState)graphicsStates.pop();
            gs.restoreGraphics();
        }
    }

    /**
     * Saves the rendering hints that are associated with a graphics context.
     * The name of the method is a misnomer; there is no stack involved,
     * and exactly one set of rendering hints can be restored.
     *
     * @param g2 the graphics context
     */
    protected void pushRenderingHints( Graphics2D g2 ) {
        savedRenderingHints = g2.getRenderingHints();
    }

    /**
     * Restores the rendering hints.
     * The rendering hints that were saved are applied to the supplied
     * graphics context. If no hints were saved, calling this method
     * does nothing.
     *
     * @param g2 the graphics context
     */
    protected void popRenderingHints( Graphics2D g2 ) {
        if( savedRenderingHints != null ) {
            g2.setRenderingHints( savedRenderingHints );
        }
    }

    /**
     * Sets the rendering hints for this graphic.
     * These hints should be used when rendering the graphic.
     *
     * @param hints the rendering hints
     */
    public void setRenderingHints( RenderingHints hints ) {
        renderingHints = hints;
    }

    /**
     * Gets the rendering hints for this graphic.
     *
     * @return the rendering hints, possibly null
     */
    public RenderingHints getRenderingHints() {
        return renderingHints;
    }

    //----------------------------------------------------------------------------
    // Visibility methods
    //----------------------------------------------------------------------------
   
    /**
     * Sets this graphic visible or invisible.
     *
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {
        if( visible != this.visible ) {
            this.visible = visible;
            forceRepaint();//if we just turned invisible, we need to paint over ourselves, and vice versa.
            for( int i = 0; i < listeners.size(); i++ ) {
                PhetGraphicListener phetGraphicListener = (PhetGraphicListener)listeners.get( i );
                phetGraphicListener.phetGraphicVisibilityChanged( this );
            }
            if( !visible ) {
                //see if we have a parent, and tell them to lose mouse and key focus.
                if( parent != null ) {
                    parent.childBecameInvisible( this );
                }

                //TODO even if there is no parent, we may want to fire off a MouseExited event anyways.
                //TODO make sure this is coordinated with the childBecameInvisible call above.
            }
        }
    }

    /**
     * Determines whether this graphic and all its parents are visible.
     *
     * @return whether this component and all its parents are visible.
     */
    public boolean isVisible() {
        // If we have a parent, check to see if it is visible
        if( parent != null ) {
            return parent.isVisible() && this.visible;
        }
        else {
            return visible;
        }
    }

    /**
     * Determines whether this graphic (independent of its parents) would be visible.
     *
     * @return the visible flag on this graphic.
     */
    protected boolean getVisibilityFlag() {
        return visible;
    }
    
    //----------------------------------------------------------------------------
    // Registration Point methods
    //----------------------------------------------------------------------------
    
    /**
     * Sets the graphic's registration point.
     * The registration point is the point about which transformations are applied.
     * It is relative to the graphic's bounding box, prior to applying any transforms.
     *
     * @param registrationPoint the registration point
     */
    public void setRegistrationPoint( Point registrationPoint ) {
        setRegistrationPoint( registrationPoint.x, registrationPoint.y );
    }

    /**
     * Sets the graphic's registration point.
     * The registration point is the point about which transformations are applied.
     * It is relative to the graphic's bounding box, prior to applying any transforms.
     *
     * @param x X coordinate of the registration point
     * @param y Y coordinate of the registration point
     */
    public void setRegistrationPoint( int x, int y ) {
        registrationPoint.setLocation( x, y );
        setBoundsDirty();
        autorepaint();
    }

    /**
     * Gets a copy of the registration point.
     * The registration point is the point about which transformations are applied.
     * It is relative to the graphic's bounding box, prior to applying any transforms.
     * The default is (0,0), which is the upper-left corner of the bounding box.
     *
     * @return the registration point
     */
    public Point getRegistrationPoint() {
        return new Point( registrationPoint );
    }
    
    //----------------------------------------------------------------------------
    // Transform methods
    //----------------------------------------------------------------------------

    /**
     * Sets this graphic's local transform.
     * The local transform is applied relative to the registration point.
     *
     * @param transform the transform
     */
    public void setTransform( AffineTransform transform ) {
        if( !transform.equals( this.transform ) ) {
            this.transform = new AffineTransform( transform );
            setBoundsDirty();
            autorepaint();
        }
    }

    /**
     * Gets a copy of the local transform.
     *
     * @return the transform
     */
    public AffineTransform getTransform() {
        return new AffineTransform( transform );
    }

    /**
     * Pre-concatenates the local transform with a specified transform.
     *
     * @param transform the transform to preconcatenate
     */
    public void transform( AffineTransform transform ) {
        // Can send actual parameter to preConcatenateTransform(), because it does
        // not alter its argument
        preConcatenateTransform( transform );
    }

    /**
     * Concatenates a transform to the local transform.
     *
     * @param transform the transform to concatenate.
     */
    protected void concatenateTransform( AffineTransform transform ) {
        if( !transform.isIdentity() ) {
            this.transform.concatenate( transform );
            setBoundsDirty();
            autorepaint();
        }
    }

    /**
     * Pre-concatenates a transform to the local transform.
     *
     * @param transform the transform to pre-concatenate.
     */
    protected void preConcatenateTransform( AffineTransform transform ) {
        if( !transform.isIdentity() ) {
            this.transform.preConcatenate( transform );
            setBoundsDirty();
            autorepaint();
        }
    }

    /**
     * Gets the "net" transform.  The net transform is the result of applying
     * the local transform relative to the registration point, then translating
     * to the location, then applying the parent's net transform (if a parent exists).
     * <p/>
     * This method should be used in methods involving painting and bounds calculations.
     *
     * @return the net AffineTransform of this graphic
     */
    protected AffineTransform getNetTransform() {
        AffineTransform net = new AffineTransform();
        
        // Use preConcatenate, so that transforms are shown in the order that they will occur.
        
        // Translate to registration point

        // todo: why are there minus signs on the parameters here?
        net.preConcatenate( AffineTransform.getTranslateInstance( -registrationPoint.x, -registrationPoint.y ) );
        // Apply local transform
        net.preConcatenate( transform );
        // Translate to location
        // todo: moved this to doing the translation as a completely separate step. See GraphicLayerSet.paint(), contains(), and determine bounds()
        net.preConcatenate( AffineTransform.getTranslateInstance( location.x, location.y ) );

        // todo: Not needed, because GraphicLayerSets apply their transforms to the graphics before we get here
        // Apply parent's net transform - rjl
        if( parent != null ) {
            AffineTransform parentTransform = parent.getNetTransform();
            net.preConcatenate( parentTransform );
        }

        return net;
    }
    
    //----------------------------------------------------------------------------
    // Transform convenience methods.
    //
    // All of these pre-concatenate an AffineTransform to the local transform,
    // so that transforms will occur in the order that you call these methods.
    // Note that this is the opposite of how similar methods in Graphics2D behave.
    //----------------------------------------------------------------------------
    
    /**
     * Pre-concatenates the current local transform with a translation transform.
     *
     * @param tx the distance to translate along the x-axis
     * @param ty the distance to translate along the y-axis
     */
    public void translate( double tx, double ty ) {
        preConcatenateTransform( AffineTransform.getTranslateInstance( tx, ty ) );
    }

    /**
     * Pre-concatenates the current local transform with a rotation transform.
     *
     * @param theta angle of rotation in radians
     */
    public void rotate( double theta ) {
        preConcatenateTransform( AffineTransform.getRotateInstance( theta ) );
    }

    /**
     * Pre-concatenates the current local transform with a translated rotation transform.
     * Rotation is performed about the supplied origin of rotation.
     *
     * @param theta the angle of rotation in radians
     * @param x     the x coordinate of the origin of the rotation
     * @param y     the y coordinate of the origin of the rotation
     */
    public void rotate( double theta, double x, double y ) {
        preConcatenateTransform( AffineTransform.getRotateInstance( theta, x, y ) );
    }

    /**
     * Pre-concatenates the current local transform with a scale transform.
     *
     * @param sx the X scaling multiplier
     * @param sy the Y scaling multiplier
     */
    public void scale( double sx, double sy ) {
        preConcatenateTransform( AffineTransform.getScaleInstance( sx, sy ) );
    }

    /**
     * Pre-concatenates the current local transform with a uniform scale transform.
     *
     * @param s the scale multiplier, applied to both axes
     */
    public void scale( double s ) {
        preConcatenateTransform( AffineTransform.getScaleInstance( s, s ) );
    }

    /**
     * Pre-concatenates the current local transform with a scale transform.
     *
     * @param shx the X shear multiplier
     * @param shy the Y shear multiplier
     */
    public void shear( double shx, double shy ) {
        preConcatenateTransform( AffineTransform.getShearInstance( shx, shy ) );
    }

    /**
     * Pre-concatenates the current local transform with a uniform shear transform.
     *
     * @param sh the shear multiplier, applied to both axes
     */
    public void shear( double sh ) {
        preConcatenateTransform( AffineTransform.getShearInstance( sh, sh ) );
    }

    /**
     * Sets the local transform to the identity matrix.
     */
    public void clearTransform() {
        setTransform( new AffineTransform() );
    }
    
    //----------------------------------------------------------------------------
    // Bounds methods
    //----------------------------------------------------------------------------

    /**
     * Computes the Rectangle in which this graphic resides.
     * This is only called if the shape is dirty.
     * <p/>
     * Subclasses of PhetGraphic must implement this method.
     * Proper computation of the bounds often involves application
     * of the graphic's transform.  See PhetShapeGraphic.determineBounds
     * for an example.
     *
     * @return the Rectangle that contains this graphic.
     */
    protected abstract Rectangle determineBounds();

    /**
     * Gets the rectangle within which this PhetGraphic lies.
     *
     * @return the rectangle within which this PhetGraphic lies.
     */
    public Rectangle getBounds() {
        syncBounds();
        return bounds;
    }

    /**
     * Flags the bounds for recomputation when applicable.
     */
    public void setBoundsDirty() {
        boundsDirty = true;
    }

    /**
     * Determines whether this phetGraphic contains the appropriate point.
     *
     * @param x
     * @param y
     * @return true if the point is contained by this graphic.
     */
    public boolean contains( int x, int y ) {
        if( isVisible() ) {
            syncBounds();
            return bounds != null && bounds.contains( x, y );
        }
        else {
            return false;
        }
    }

    /**
     * If the bounds are dirty, they are recomputed.
     */
    protected void syncBounds() {
        if( boundsDirty ) {
            rebuildBounds();
            boundsDirty = false;
            if( !RectangleUtils.areEqual( lastBounds, bounds ) ) {
                notifyChanged();
            }
        }
    }

    /*
     * Used by syncBounds, this method sets changes the value of the bounds
     * member only if the bounds have actually changed.
     */
    private void rebuildBounds() {
        Rectangle newBounds = determineBounds();
        if( newBounds != null ) {
            lastBounds.setBounds( bounds );
            bounds.setBounds( newBounds );
        }
    }

    //----------------------------------------------------------------------------
    // Location methods
    //----------------------------------------------------------------------------
    
    /**
     * Sets the location of this graphic.
     * <p/>
     * The location is a translation that is applied after the local transform,
     * but before the parent's net transform.  This effectively moves the
     * graphic's registration point to the specified location, relative
     * to the parent container.
     *
     * @param p the location
     */
    public void setLocation( Point p ) {
        setLocation( p.x, p.y );
    }

    /**
     * Convenience method, sets the location.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @see edu.colorado.phet.common.view.phetgraphics.PhetGraphic#setLocation(java.awt.Point)
     */
    public void setLocation( int x, int y ) {
        if( location.x != x || location.y != y ) {
            location.setLocation( x, y );
            setBoundsDirty();
            repaint();
        }
    }

    /**
     * Gets the location.
     *
     * @return the location
     * @see edu.colorado.phet.common.view.phetgraphics.PhetGraphic#setLocation(java.awt.Point)
     */
    public Point getLocation() {
        return new Point( location );
    }

    /**
     * Convenience method, gets the X coordinate of the location.
     *
     * @return X coordinate
     */
    public int getX() {
        return location.x;
    }

    /**
     * Convenience method, gets the Y coordinate of the location.
     *
     * @return Y coordinate
     */
    public int getY() {
        return location.y;
    }

    //----------------------------------------------------------------------------
    // Size methods
    //----------------------------------------------------------------------------

    /**
     * Gets the size of the graphic.
     * The size is the dimension of the bounding rectangle.
     *
     * @return the size
     */
    public Dimension getSize() {
        return new Dimension( getWidth(), getHeight() );
    }

    /**
     * Gets the width of the graphics.
     * The width is the width of the bounding rectangle.
     *
     * @return the width
     */
    public int getWidth() {
        return getBounds().width;
    }

    /**
     * Gets the height of the graphics.
     * The height is the height of the bounding rectangle.
     *
     * @return the height
     */
    public int getHeight() {
        return getBounds().height;
    }
    
    //----------------------------------------------------------------------------
    // Change Notification methods
    //----------------------------------------------------------------------------
    
    /**
     * Adds an Observer for changes in this PhetGraphic.
     *
     * @param phetGraphicListener
     */
    public void addPhetGraphicListener( PhetGraphicListener phetGraphicListener ) {
        listeners.add( phetGraphicListener );
    }

    /**
     * Notifies registered Observers that this PhetGraphic has changed.
     */
    protected void notifyChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            PhetGraphicListener phetGraphicListener = (PhetGraphicListener)listeners.get( i );
            phetGraphicListener.phetGraphicChanged( this );
        }
    }
    
    //----------------------------------------------------------------------------
    // Translation interactivity methods
    //----------------------------------------------------------------------------
    
    /**
     * Adds a listener for translations (dragging).
     *
     * @param translationListener the listener
     */
    public void addTranslationListener( TranslationListener translationListener ) {
        addMouseInputListener( new TranslationHandler( translationListener ) );
    }
    
    //----------------------------------------------------------------------------
    // Mouse Input interactivity methods
    //----------------------------------------------------------------------------

    /**
     * Adds a mouse input listener.
     *
     * @param listener the listener add
     */
    public void addMouseInputListener( MouseInputListener listener ) {
        mouseInputListener.addMouseInputListener( listener );
    }

    /**
     * Removes a mouse input listener.
     *
     * @param listener the listener to remove
     */
    private void removeMouseInputListener( MouseInputListener listener ) {
        mouseInputListener.removeMouseInputListener( listener );
    }

    /**
     * Gets the delegate that manages mouse input listeners.
     *
     * @return the delegate
     */
    public CompositeMouseInputListener getMouseInputListener() {
        return mouseInputListener;
    }

    public void setMouseInputListener( CompositeMouseInputListener mouseInputListener ) {
        this.mouseInputListener = mouseInputListener;
    }

    /**
     * Sets an internal state subclasses can use to determine whether to
     * ignore the mouse.
     *
     * @param ignoreMouse true or false
     */
    protected void setIgnoreMouse( boolean ignoreMouse ) {
        this.ignoreMouse = ignoreMouse;
    }

    /**
     * Gets the current state of ignore mouse.
     *
     * @return true or false
     */
    protected boolean getIgnoreMouse() {
        return ignoreMouse;
    }

    /**
     * Passes a "mouse clicked" event to the mouse input delegate,
     * who in turn sends it to all registered mouse input listeners.
     *
     * @param e the event
     */
    public void fireMouseClicked( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseClicked( e );
        }
    }

    /**
     * Passes a "mouse pressed" event to the mouse input delegate,
     * who in turn sends it to all registered mouse input listeners.
     *
     * @param e the event
     */
    public void fireMousePressed( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mousePressed( e );
        }
    }

    /**
     * Passes a "mouse released" event to the mouse input delegate,
     * who in turn sends it to all registered mouse input listeners.
     *
     * @param e the event
     */
    public void fireMouseReleased( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseReleased( e );
        }
    }

    /**
     * Passes a "mouse entered" event to the mouse input listener delegate,
     * who in turn sends it to all registered mouse input listeners.
     *
     * @param e the event
     */
    public void fireMouseEntered( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseEntered( e );
        }
    }

    /**
     * Passes a "mouse exited" event to the mouse input listener delegate,
     * who in turn sends it to all registered mouse input listeners.
     *
     * @param e the event
     */
    public void fireMouseExited( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseExited( e );
        }
    }

    /**
     * Fires a "mouse exited" event because this component became invisible.
     * This explicitly avoids the visibility condition for firing the event.
     */
    public void fireMouseExitedBecauseInvisible( MouseEvent e ) {
        mouseInputListener.mouseExited( e );
    }

    /**
     * Passes a "mouse dragged" event to the mouse input listener delegate,
     * who in turn sends it to all registered mouse input listeners.
     *
     * @param e the event
     */
    public void fireMouseDragged( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseDragged( e );
        }
    }

    /**
     * Passes a "mouse moved" event to the mouse input listener delegate,
     * who in turn sends it to all registered mouse input listeners.
     *
     * @param e the event
     */
    public void fireMouseMoved( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseMoved( e );
        }
    }

    //----------------------------------------------------------------------------
    // Cursor interactivity methods
    //----------------------------------------------------------------------------

    public CursorControl getCursorControl() {
        return cursorControl;
    }

    public void setCursorControl( CursorControl cursorControl ) {
        this.cursorControl = cursorControl;
    }

    /**
     * Sets the mouse cursor to look like the specified cursor.
     *
     * @param cursor the cursor
     */
    public void setCursor( Cursor cursor ) {
        if( cursor == null && cursorControl != null ) {
            removeCursor();
        }
        if( cursorControl == null ) {
            cursorControl = new CursorControl( cursor );
            addMouseInputListener( cursorControl );
        }
    }

    /**
     * Sets the mouse cursor to use the standard hand cursor, Cursor.HAND_CURSOR.
     */
    public void setCursorHand() {
        setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    /**
     * Removes any custom cursor set on this graphic.
     */
    private void removeCursor() {
        removeMouseInputListener( cursorControl );
    }
    
    //----------------------------------------------------------------------------
    // Popup Menu methods
    //----------------------------------------------------------------------------


    /**
     * Sets a popup menu, displayed when the right mouse button is pressed.
     *
     * @param menu the menu
     */
    public void setPopupMenu( final JPopupMenu menu ) {
        if( popupHandler != null ) {
            removeMouseInputListener( popupHandler );
        }
        popupHandler = new MouseInputAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( SwingUtilities.isRightMouseButton( e ) ) {
                    menu.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        addMouseInputListener( popupHandler );
    }

    //----------------------------------------------------------------------------
    // Paint related methods
    //----------------------------------------------------------------------------
    
    /**
     * Paints the graphic. Subclasses must implement this method.
     * For a good example, see PhetShapeGraphic.paint.
     * <p/>
     * In general, a well-behaved paint method should:
     * <ul>
     * <li>leave the graphics context as it found it, by either explicitly
     * saving and restoring state information, or by calling saveGraphicsState
     * and restoreGraphicsState
     * <li>use getTransform to get the transform information it needs to
     * correctly draw the graphic.  This transform should be passed to
     * g2.transform.
     * </ul>
     *
     * @param g2 the 2D graphics context
     */
    public abstract void paint( Graphics2D g2 );

    /**
     * Set the autorepaint value.
     *
     * @param autorepaint true or false
     */
    public void setAutoRepaint( boolean autorepaint ) {
        this.autorepaint = autorepaint;
    }

    /**
     * Repaints the graphic if autorepaint is set to true.
     */
    public void autorepaint() {
        if( autorepaint ) {
            repaint();
        }
    }

    /**
     * Repaints the visible rectangle on this graphic's Component.
     */
    public void repaint() {
        if( isVisible() ) {
            forceRepaint();
        }
    }

    /**
     * Forces a repaint of this graphic.
     */
    protected void forceRepaint() {
        syncBounds();
        if( lastBounds != null ) {
            component.repaint( lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height );
        }
        if( bounds != null ) {
            component.repaint( bounds.x, bounds.y, bounds.width, bounds.height );
        }
    }

    /**
     * Determine the Local Bounds of this PhetGraphic, ie, the bounds of this PhetGraphic without accounting for any parent transforms.
     *
     * @return the bounds of this PhetGraphic without accounting for parent transforms.
     */

    public Rectangle getLocalBounds() {
        if( parent == null ) {
            return getBounds();
        }
        else {
            Rectangle global = getBounds();
            AffineTransform parentTransform = parent.getNetTransform();
            try {
                AffineTransform inverse = parentTransform.createInverse();
                Rectangle localBounds = inverse.createTransformedShape( global ).getBounds();
                return localBounds;
            }
            catch( Exception e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }
    }

    ////////////////////////////////////////////////////
    ////////
    /////////       Key Listener code

    public void addKeyListener( KeyListener keyListener ) {
        this.keyListener.addKeyListener( keyListener );
    }

    public void removeKeyListener( KeyListener keyListener ) {
        this.keyListener.removeKeyListener( keyListener );
    }

    public int numKeyListeners() {
        return keyListener.numKeyListeners();
    }

    /**
     * Not for client use.
     *
     * @param e
     */
    public void fireKeyTyped( KeyEvent e ) {
        if( isVisible() ) {
            keyListener.keyTyped( e );
        }
    }

    /**
     * Not for client use.
     *
     * @param e
     */
    public void fireKeyPressed( KeyEvent e ) {
        if( isVisible() ) {
            keyListener.keyPressed( e );
        }
    }

    /**
     * Not for client use.
     *
     * @param e
     */
    public void fireKeyReleased( KeyEvent e ) {
        if( isVisible() ) {
            keyListener.keyReleased( e );
        }
    }

    /**
     * No-op by default (override for behavior), when it gains key focus.
     */
    public void gainedKeyFocus() {
    }

    public void lostKeyFocus() {
    }
}
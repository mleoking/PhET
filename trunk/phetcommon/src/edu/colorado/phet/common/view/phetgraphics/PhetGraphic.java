/** University of Colorado, PhET*/
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.graphics.mousecontrols.CompositeMouseInputListener;
import edu.colorado.phet.common.view.graphics.mousecontrols.CursorControl;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationHandler;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Stack;

/**
 * This graphic class auto-magically repaints itself in the appropriate bounds,
 * using component.paint(int x,int y,int width,int height).
 * This class manages the current and previous bounds for painting, and whether the region is dirty.
 */
public abstract class PhetGraphic {
    private AffineTransform transform = new AffineTransform();
    private Rectangle lastBounds = null;
    private Rectangle bounds = null;
    private Component component;
    private boolean visible = true;
    private boolean boundsDirty = true;
    private RenderingHints savedRenderingHints;
    private RenderingHints renderingHints;
    private Stack graphicsStates = new Stack();
    private GraphicLayerSet parent;
//    private boolean autoRepaint = true;//TODO we may want this functionality in the near future.

    /*A bit of state to facilitate interactivity.*/
    protected CompositeMouseInputListener mouseInputListener = new CompositeMouseInputListener();//delegates to
    private CursorControl cursorControl;
    private MouseInputAdapter popupHandler;
    private ArrayList listeners = new ArrayList();
    private boolean ignoreMouse = false;
    private boolean autorepaint = true;

    /**
     * Construct a PhetGraphic on the specified component.
     *
     * @param component The component in which the PhetGraphic will be drawn.
     */
    protected PhetGraphic( Component component ) {
        this.component = component;
    }

    /**
     * Set the parent of this Graphic.
     *
     * @param parent the Parent that contains this graphic.
     */
    protected void setParent( GraphicLayerSet parent ) {
        this.parent = parent;
    }

    /**
     * Adds a Listener for changes in this PhetGraphic.
     *
     * @param phetGraphicListener
     */
    public void addPhetGraphicListener( PhetGraphicListener phetGraphicListener ) {
        listeners.add( phetGraphicListener );
    }

    /**
     * Get the rectangle within which this PhetGraphic lies.
     *
     * @return the rectangle within which this PhetGraphic lies.
     */
    public Rectangle getBounds() {
        syncBounds();
        return bounds;
    }

    protected void saveGraphicsState( Graphics2D graphics2D ) {
        graphicsStates.push( new GraphicsState( graphics2D ) );
    }

    protected void restoreGraphicsState() {
        GraphicsState gs = (GraphicsState)graphicsStates.pop();
        gs.restoreGraphics();
    }

    protected void pushRenderingHints( Graphics2D g ) {
        savedRenderingHints = g.getRenderingHints();
    }

    protected void popRenderingHints( Graphics2D g ) {
        if( savedRenderingHints != null ) {
            g.setRenderingHints( savedRenderingHints );
        }
    }

    public void setRenderingHints( RenderingHints hints ) {
        renderingHints = hints;
    }

    public RenderingHints getRenderingHints() {
        return renderingHints;
    }

    /**
     * If the bounds are dirty, they are recomputed.
     */
    protected void syncBounds() {
        if( boundsDirty ) {
            Rectangle before = bounds == null ? null : new Rectangle( bounds );
            rebuildBounds();
            Rectangle after = bounds == null ? null : new Rectangle( bounds );
            boundsDirty = false;
            if( !RectangleUtils.areEqual( before, after ) ) {
                notifyChanged();
            }
        }
    }

    /**
     * Flags the bounds for recomputation when applicable.
     */
    public void setBoundsDirty() {
        boundsDirty = true;
    }

    /**
     * Returns the Component within which this PhetGraphic is contained.
     *
     * @return
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Determines whether this graphic (independent of its parents) would be visible.
     *
     * @return the visible flag on this graphic.
     */
    protected boolean getVisibilityFlag() {
        return visible;
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

    public Dimension getSize() {
        return new Dimension( getWidth(), getHeight() );
    }

    /**
     * Returns the AffineTransform of this graphic, modified by all parents (if applicable).
     *
     * @return the complete AffineTransform of this graphic.
     */
    protected AffineTransform getTransform() {
        if( parent != null ) {
            AffineTransform parentTransform = parent.getTransform();
            AffineTransform net = new AffineTransform( parentTransform );
            net.concatenate( transform );
            return net;
        }
        else {
            return transform;
        }
    }

    /**
     * Set this graphic visible or invisible.
     *
     * @param visible
     */
    public void setVisible( boolean visible ) {
        if( visible != this.visible ) {
            this.visible = visible;
            forceRepaint();//if we just turned invisible, we need to paint over ourselves, and vice versa.
        }
    }

    /**
     * Determine whether this phetGraphic contains the appropriate point.
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

    private void rebuildBounds() {
        Rectangle newBounds = determineBounds();
        if( newBounds != null ) {
            if( this.bounds == null ) {
                this.bounds = new Rectangle( newBounds );
            }
            else {
                this.bounds.setBounds( newBounds );
            }
            if( lastBounds == null ) {
                lastBounds = new Rectangle( bounds );
            }
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

    protected void forceRepaint() {
        syncBounds();
        if( lastBounds != null ) {
            component.repaint( lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height );
        }
        if( bounds != null ) {
            component.repaint( bounds.x, bounds.y, bounds.width, bounds.height );
        }
        if( bounds != null ) {
            lastBounds.setBounds( bounds );
        }
    }

    /**
     * Modifies the AffineTransform on this PhetGraphic so that the top-left corner lies at the specified coordinates.
     *
     * @param p the new top left corner of this PhetGraphic.
     */
    public void setLocation( Point p ) {
        setLocation( p.x, p.y );
    }

    /**
     * Modifies the AffineTransform on this PhetGraphic so that the top-left corner lies at the specified coordinates.
     *
     * @param x
     * @param y
     */
    public void setLocation( int x, int y ) {
        //In order to respect our global transformation the best,
        //this should translate our current location to the new location.
        Point currentLocation = getLocation();
        if( currentLocation == null ) {
            currentLocation = new Point( 0, 0 );
        }
        int dx = x - currentLocation.x;
        int dy = y - currentLocation.y;
        transform( AffineTransform.getTranslateInstance( dx, dy ) );
    }

    /**
     * Preconcatenates the specified transform with this object's transform.
     *
     * @param transform
     */
    public void transform( AffineTransform transform ) {
        if( !transform.isIdentity() ) {//does this work properly?
            this.transform.preConcatenate( transform );
            setBoundsDirty();
            autorepaint();
        }
    }

    /**
     * Get the top left corner of the boundary of this PhetGraphic.
     *
     * @return
     */
    public Point getLocation() {
        return getBounds() == null ? null : getBounds().getLocation();
    }

    public int getWidth() {
        return getBounds().width;
    }

    public int getHeight() {
        return getBounds().height;
    }

    public int getX() {
        return getBounds().x;
    }

    public int getY() {
        return getBounds().y;
    }

    /**
     * Interactivity methods.
     */
    public void fireMouseClicked( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseClicked( e );
        }
    }

    public void fireMousePressed( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mousePressed( e );
        }
    }

    public void fireMouseReleased( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseReleased( e );
        }
    }

    public void fireMouseEntered( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseEntered( e );
        }
    }

    public void fireMouseExited( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseExited( e );
        }
    }

    public void fireMouseDragged( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseDragged( e );
        }
    }

    public void fireMouseMoved( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseMoved( e );
        }
    }

    /**
     * Causes the mouse cursor to look like the specified cursor.
     *
     * @param cursor
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
     * Sets the mouse to look like a hand over this graphic.
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

    private void removeMouseInputListener( MouseInputListener listener ) {
        mouseInputListener.removeMouseInputListener( listener );
    }

    public void addMouseInputListener( MouseInputListener listener ) {
        this.mouseInputListener.addMouseInputListener( listener );
    }

    public void addTranslationListener( TranslationListener translationListener ) {
        addMouseInputListener( new TranslationHandler( translationListener ) );
    }

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

    protected CompositeMouseInputListener getMouseInputListener() {
        return mouseInputListener;
    }

    protected boolean getIgnoreMouse() {
        return ignoreMouse;
    }

    protected void setIgnoreMouse( boolean ignoreMouse ) {
        this.ignoreMouse = ignoreMouse;
    }

    protected void notifyChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            PhetGraphicListener phetGraphicListener = (PhetGraphicListener)listeners.get( i );
            phetGraphicListener.phetGraphicChanged( this );
        }
    }

    /**
     * Computes the Rectangle in which this graphic resides.  This is only called if the shape is dirty.
     *
     * @return the Rectangle that contains this graphic.
     */
    protected abstract Rectangle determineBounds();

    public abstract void paint( Graphics2D g );

    public void autorepaint() {
        if( autorepaint ) {
            repaint();
        }
    }

    public void setAutoRepaint( boolean autorepaint ) {
        this.autorepaint = autorepaint;
    }
}

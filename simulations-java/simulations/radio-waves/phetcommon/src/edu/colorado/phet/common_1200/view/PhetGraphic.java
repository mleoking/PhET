package edu.colorado.phet.common_1200.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Stack;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import edu.colorado.phet.common.phetgraphics.view.util.CompositeMouseInputListener;
import edu.colorado.phet.common.phetgraphics.view.util.CursorControl;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsState;

/**
 * This graphic class auto-magically repaints itself in the appropriate bounds,
 * using component.paint(int x,int y,int width,int height).
 * This class manages the current and previous bounds for painting, and whether the region is dirty.
 * Testing.
 */
public abstract class PhetGraphic implements InteractiveGraphic {
    private Point location = new Point();
    private Rectangle lastBounds = null;
    private Rectangle bounds = null;
    private Component component;
    protected boolean visible = true;
    private boolean boundsDirty = true;
    private RenderingHints savedRenderingHints;
    private RenderingHints renderingHints;
    private Stack graphicsStates = new Stack();

    /*A bit of state to facilitate interactivity.*/
    private CompositeMouseInputListener mouseInputListener = new CompositeMouseInputListener();//delegates to
    private CursorControl cursorControl;
    private MouseInputAdapter popupHandler;

    protected PhetGraphic( Component component ) {
        this.component = component;
    }

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

    protected void syncBounds() {
        if( boundsDirty ) {
            rebuildBounds();
            boundsDirty = false;
        }
    }

    protected void setBoundsDirty() {
        boundsDirty = true;
    }

    public Component getComponent() {
        return component;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        if( visible != this.visible ) {
            this.visible = visible;
            forceRepaint();//if we just turned invisible, we need to paint over ourselves, and vice versa.
        }
    }

    public boolean contains( int x, int y ) {
        if( visible ) {
            syncBounds();
            return bounds.contains( x, y );
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

    protected abstract Rectangle determineBounds();

    public void setLocation( Point p ) {
        setLocation( p.x, p.y );
    }

    public void setLocation( int x, int y ) {
        this.location.setLocation( x, y );
    }

    public Point getLocation() {
        return location;
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

    public void mouseClicked( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseClicked( e );
        }
    }

    public void mousePressed( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mousePressed( e );
        }
    }

    public void mouseReleased( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseReleased( e );
        }
    }

    public void mouseEntered( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseEntered( e );
        }
    }

    public void mouseExited( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseExited( e );
        }
    }

    public void mouseDragged( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseDragged( e );
        }
    }

    public void mouseMoved( MouseEvent e ) {
        if( isVisible() ) {
            mouseInputListener.mouseMoved( e );
        }
    }

    public void setCursor( Cursor cursor ) {
        if( cursor == null && cursorControl != null ) {
            removeCursor();
        }
        if( cursorControl == null ) {
            cursorControl = new CursorControl( cursor );
            addMouseInputListener( cursorControl );
        }
    }

    public void setCursorHand() {
        setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    private void removeCursor() {
        removeMouseInputListener( cursorControl );
    }

    private void removeMouseInputListener( MouseInputListener listener ) {
        mouseInputListener.removeMouseInputListener( listener );
    }

    private void addMouseInputListener( MouseInputListener listener ) {
        this.mouseInputListener.addMouseInputListener( listener );
    }

    public void addTranslationBehavior( Translatable target ) {
        addMouseInputListener( new TranslationControl( target ) );
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

}

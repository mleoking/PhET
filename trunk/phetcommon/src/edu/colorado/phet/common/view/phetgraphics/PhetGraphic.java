/** Sam Reid*/
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.graphics.BoundedGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;

import java.awt.*;
import java.util.Stack;

/**
 * This graphic class auto-magically repaints itself in the appropriate bounds,
 * using component.paint(int x,int y,int width,int height).
 * This class manages the current and previous bounds for painting, and whether the region is dirty.
 * Testing.
 */
public abstract class PhetGraphic implements BoundedGraphic {
    private Point location = new Point();
    private Rectangle lastBounds = null;
    private Rectangle bounds = null;
    private Component component;
    protected boolean visible = true;
    private boolean boundsDirty = true;
    private RenderingHints savedRenderingHints;
    private RenderingHints renderingHints;
    private Stack graphicsStates = new Stack();
    private CompositePhetGraphic parent;

    protected PhetGraphic( Component component ) {
        this.component = component;
    }

    protected void setParent( CompositePhetGraphic parent ) {
        this.parent = parent;
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
        // If we have a parent, check to see if it is visible
        if( parent != null ) {
            return parent.isVisible() && this.visible;
        }
        else {
            return visible;
        }
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
}

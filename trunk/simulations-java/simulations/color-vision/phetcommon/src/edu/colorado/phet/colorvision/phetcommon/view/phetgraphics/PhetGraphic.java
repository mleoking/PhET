/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14443 $
 * Date modified : $Date:2007-04-12 23:10:41 -0600 (Thu, 12 Apr 2007) $
 */
package edu.colorado.phet.colorvision.phetcommon.view.phetgraphics;

import edu.colorado.phet.colorvision.phetcommon.view.graphics.BoundedGraphic;
import edu.colorado.phet.colorvision.phetcommon.view.util.GraphicsState;

import java.awt.*;
import java.util.Stack;

/**
 * This graphic class auto-magically repaints itself in the appropriate bounds,
 * using component.paint(int x,int y,int width,int height).
 * This class manages the current and previous bounds for painting, and whether the region is dirty.
 * Testing.
 * 
 * @author Sam Reid
 * @version $Revision:14443 $
 */
public abstract class PhetGraphic implements BoundedGraphic {
    private Rectangle lastBounds = new Rectangle();
    private Rectangle bounds = new Rectangle();
    private Component component;
//    private boolean visible = false;
    protected boolean visible = true;
    private boolean boundsDirty = true;
    private RenderingHints savedRenderingHints;
    private RenderingHints renderingHints;
    private Stack graphicsStates = new Stack();

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

    /**
     * Provided to support object pooling in concrete subclasses
     * @param component
     */
    protected void setComponent( Component component ) {
        this.component = component;
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
                this.bounds.setRect( newBounds );
            }
            else {
                this.bounds.setBounds( newBounds );
            }
            if( lastBounds == null ) {
                lastBounds.setRect( bounds );
            }
        }
    }

    public void repaint() {
        if( visible ) {
            forceRepaint();
        }
    }

    private void forceRepaint() {
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

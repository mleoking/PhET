/** Sam Reid*/
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.graphics.BoundedGraphic;

import java.awt.*;

/**
 * This graphic class auto-magically repaints itself in the appropriate bounds,
 * using component.paint(int x,int y,int width,int height).
 * This class manages the current and previous bounds for painting, and whether the region is dirty.
 * Testing.
 */
public abstract class PhetGraphic implements BoundedGraphic {
    private Rectangle lastBounds = null;
    private Rectangle bounds = null;
    private Component component;
//    private boolean visible = false;
    protected boolean visible = true;
    private boolean boundsDirty = true;
    private RenderingHints savedRenderingHints;
    private GraphicsState graphicsState;

    protected PhetGraphic( Component component ) {
        this.component = component;
    }

    public Rectangle getBounds() {
        syncBounds();
        return bounds;
    }

    protected void pushGraphicsState( Graphics2D graphics2D ) {
        graphicsState = new GraphicsState( graphics2D );
    }

    protected void popGraphicsState() {
        graphicsState.restoreGraphics();
    }

    protected void pushRenderingHints( Graphics2D g ) {
        savedRenderingHints = g.getRenderingHints();
    }

    protected void popRenderingHints( Graphics2D g ) {
        if( savedRenderingHints != null ) {
            g.setRenderingHints( savedRenderingHints );
        }
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


    //
    // Inner Classes
    //

    /**
     * A utilitye class for saving and restoring the state of Graphics2D objects
     */
    private class GraphicsState {
        private Graphics2D graphics2D;
        private RenderingHints renderingHints;
        private Paint paint;
        private Color color;
        private Stroke stroke;

        GraphicsState( Graphics2D graphics2D ) {
            this.graphics2D = graphics2D;
            renderingHints = graphics2D.getRenderingHints();
            paint = graphics2D.getPaint();
            color = graphics2D.getColor();
            stroke = graphics2D.getStroke();
        }

        void restoreGraphics() {
            graphics2D.setRenderingHints( graphicsState.renderingHints );
            graphics2D.setPaint( graphicsState.paint );
            graphics2D.setColor( graphicsState.color );
            graphics2D.setStroke( graphicsState.stroke );
        }
    }
}

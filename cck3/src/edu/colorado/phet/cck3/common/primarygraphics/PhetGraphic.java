/** Sam Reid*/
package edu.colorado.phet.cck3.common.phetgraphics;

import edu.colorado.phet.common.view.graphics.BoundedGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 5:59:20 PM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public abstract class PhetGraphic implements BoundedGraphic {
    private Rectangle lastBounds = null;
    private Rectangle bounds = null;
    private Component component;
    private boolean visible = false;
    private boolean boundsDirty = true;

    protected PhetGraphic( Component component ) {
        this.component = component;
    }

    public Rectangle getBounds() {
        syncBounds();
        return bounds;
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
        this.visible = visible;
        forceRepaint();//if we just turned invisible, we need to paint over ourselves, and vice versa.
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

}

/** Sam Reid*/
package edu.colorado.phet.cck3.common.primarygraphics;

import edu.colorado.phet.common.view.graphics.BoundedGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 5:59:20 PM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public abstract class PrimaryGraphic implements BoundedGraphic {
    private Rectangle lastBounds = null;
    private Rectangle bounds = null;
    private Component component;
    private boolean visible = true;
    private boolean boundsDirty = true;

    protected PrimaryGraphic( Component component ) {
        this.component = component;
    }

    protected void setBounds( Rectangle bounds ) {
        this.bounds.setBounds( bounds );
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

    public void repaint() {
        if( visible ) {
            syncBounds();
            component.repaint( lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height );
            component.repaint( bounds.x, bounds.y, bounds.width, bounds.height );
            lastBounds.setBounds( bounds );
        }
    }

    protected abstract Rectangle determineBounds();

}

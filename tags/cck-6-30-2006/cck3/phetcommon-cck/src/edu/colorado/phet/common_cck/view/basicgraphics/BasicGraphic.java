package edu.colorado.phet.common_cck.view.basicgraphics;

import edu.colorado.phet.common_cck.view.graphics.Graphic;

import java.awt.*;
import java.util.ArrayList;

/**
 * This kind of graphic manages issues of how to repaint,
 * and what view bounds it is in.
 * Visibility, and repainting are left to someone else.
 */
public abstract class BasicGraphic implements Graphic {
    private ArrayList graphicListeners = new ArrayList();

    public void addGraphicListener( BasicGraphicListener gl ) {
        graphicListeners.add( gl );
    }

    public abstract Rectangle getBounds();

    public void boundsChanged() {
        for( int i = 0; i < graphicListeners.size(); i++ ) {
            BasicGraphicListener basicGraphicListener = (BasicGraphicListener)graphicListeners.get( i );
            basicGraphicListener.boundsChanged( this );
        }
    }

    public void appearanceChanged() {
        for( int i = 0; i < graphicListeners.size(); i++ ) {
            BasicGraphicListener basicGraphicListener = (BasicGraphicListener)graphicListeners.get( i );
            basicGraphicListener.appearanceChanged( this );
        }
    }

    public void removeGraphicListener( BasicGraphicListener basicGraphicListener ) {
        graphicListeners.remove( basicGraphicListener );
    }
}

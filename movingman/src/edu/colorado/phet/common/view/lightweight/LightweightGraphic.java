package edu.colorado.phet.common.view.lightweight;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.util.ArrayList;

/**
 * This kind of graphic manages issues of how to repaint,
 * and what view bounds it is in.
 * Visibility, and repainting are left to someone else.
 */
public abstract class LightweightGraphic implements Graphic {
    public ArrayList graphicListeners = new ArrayList();

    public void addGraphicListener( GraphicListener gl ) {
        graphicListeners.add( gl );
    }

    public abstract Rectangle getBounds();

    public void boundsChanged() {
        for( int i = 0; i < graphicListeners.size(); i++ ) {
            GraphicListener graphicListener = (GraphicListener)graphicListeners.get( i );
            graphicListener.boundsChanged( this );
        }
    }

    public void paintChanged() {
        for( int i = 0; i < graphicListeners.size(); i++ ) {
            GraphicListener graphicListener = (GraphicListener)graphicListeners.get( i );
            graphicListener.paintChanged( this );
        }
    }
}

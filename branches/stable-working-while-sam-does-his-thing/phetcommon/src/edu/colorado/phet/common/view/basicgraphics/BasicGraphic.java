/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.basicgraphics;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.util.ArrayList;

/**
 * This kind of graphic manages issues of how to repaint,
 * and what view bounds it is in.
 * Visibility, and repainting are left to someone else.
 * 
 * @author ?
 * @version $Revision$
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

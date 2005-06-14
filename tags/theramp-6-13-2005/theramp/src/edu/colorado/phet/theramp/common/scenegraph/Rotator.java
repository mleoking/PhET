/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 2, 2005
 * Time: 3:34:38 AM
 * Copyright (c) Jun 2, 2005 by Sam Reid
 */

public class Rotator extends SceneGraphMouseAdapter {
    public void mouseDragged( SceneGraphMouseEvent event ) {
        if( event.getMouseEvent().isControlDown() ) {
            AbstractGraphic graphic = event.getAbstractGraphic();
//            graphic.rotate( Math.PI / 64, graphic.getLocalWidth() / 2, graphic.getLocalHeight() / 2 );
            Point2D center = RectangleUtils.getCenter2D( graphic.getLocalBounds() );
//            graphic.rotate( Math.PI / 64, graphic.getLocalWidth() / 2, graphic.getLocalHeight() / 2 );
            graphic.rotate( Math.PI / 64, center.getX(), center.getY() );
        }
    }
}

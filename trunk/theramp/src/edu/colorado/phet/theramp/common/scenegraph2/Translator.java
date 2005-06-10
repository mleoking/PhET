/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph2;

/**
 * User: Sam Reid
 * Date: Jun 2, 2005
 * Time: 3:34:38 AM
 * Copyright (c) Jun 2, 2005 by Sam Reid
 */

public class Translator extends SceneGraphMouseAdapter {
    public void mouseDragged( SceneGraphMouseEvent event ) {
        if( !event.getMouseEvent().isControlDown() ) {
            AbstractGraphic graphic = event.getAbstractGraphic();
            graphic.translate( event.getDx(), event.getDy() );
        }
    }
}

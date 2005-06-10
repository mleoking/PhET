/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph2;


/**
 * User: Sam Reid
 * Date: Jun 8, 2005
 * Time: 3:05:06 AM
 * Copyright (c) Jun 8, 2005 by Sam Reid
 */

public class SeparatorLayerNode extends GraphicLayerNode {
    public void paint( RenderEvent graphics2D ) {
        if( isVisible() ) {
            super.render( graphics2D );
            restore( graphics2D );
        }
    }
}

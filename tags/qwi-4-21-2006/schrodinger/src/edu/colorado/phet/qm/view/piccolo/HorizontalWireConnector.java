/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.umd.cs.piccolo.PNode;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Mar 2, 2006
 * Time: 11:21:42 PM
 * Copyright (c) Mar 2, 2006 by Sam Reid
 */

public class HorizontalWireConnector extends HorizontalConnector {
    public HorizontalWireConnector( PNode source, PNode dest ) {
        super( source, dest );
        BufferedImage txtr = null;
        try {
            txtr = ImageLoader.loadBufferedImage( "images/wire.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        setTexture( txtr );
    }
}

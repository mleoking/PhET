// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.view.piccolo;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.umd.cs.piccolo.PNode;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Mar 2, 2006
 * Time: 11:21:42 PM
 */

public class HorizontalWireConnector extends HorizontalConnector {
    public HorizontalWireConnector( PNode source, PNode dest ) {
        super( source, dest );
        BufferedImage txtr = null;
        try {
            txtr = ImageLoader.loadBufferedImage( "quantum-wave-interference/images/wire.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        setTexture( txtr );
    }
}

// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 8:15:07 AM
 */

public class TestPiccoloRendering {

    public static void main( String[] args ) {
        PCanvas pCanvas = new PCanvas();
        pCanvas.setPanEventHandler( null );

        final PText pText = new PText( "Testing Piccolo Rendering" );
        pText.setFont( new PhetFont( Font.BOLD, 32 ) );
        pText.setOffset( 22.96045684814453, 19.954608917236328 );
        pCanvas.getLayer().addChild( pText );

        PPath path = new PPath( new Rectangle( 50, 50 ) );
        path.setPaint( new Color( 0, 0, 0, 0 ) );
        path.addInputEventListener( new PDragEventHandler() );
        pCanvas.getLayer().addChild( path );

        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( pCanvas );
        frame.setSize( 400, 600 );
        frame.setVisible( true );

        pCanvas.setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        pCanvas.setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        pCanvas.setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
    }
}

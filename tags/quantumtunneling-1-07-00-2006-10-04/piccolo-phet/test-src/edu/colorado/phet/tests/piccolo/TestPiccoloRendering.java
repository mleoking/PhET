/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 8:15:07 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class TestPiccoloRendering {

    public static void main( String[] args ) {
        PCanvas pCanvas = new PCanvas();
        pCanvas.setPanEventHandler( null );

        final PText pText = new PText( "Testing Piccolo Rendering" );
        pText.setFont( new Font( "Lucida Sans", Font.BOLD, 32 ) );
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

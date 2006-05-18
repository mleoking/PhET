/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.BoundGraphic;
import edu.colorado.phet.piccolo.nodes.HTMLGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 19, 2005
 * Time: 12:40:15 AM
 * Copyright (c) Sep 19, 2005 by Sam Reid
 */

public class SwoopHelpItem extends PNode {
    public SwoopHelpItem( String str ) {
        HTMLGraphic text = new HTMLGraphic( str );
        BoundGraphic boundGraphic = new BoundGraphic( text, 10, 10 );
        boundGraphic.setPaint( new Color( 100, 200, 120, 128 ) );
        Arrow arrow = new Arrow( new Point2D.Double( boundGraphic.getXOffset(), boundGraphic.getYOffset() ),
                                 new Point2D.Double( boundGraphic.getXOffset() - 40, boundGraphic.getYOffset() - 0 ), 10, 10, 4 );
        PPath arrowShape = new PPath( arrow.getShape() );
        arrowShape.setPaint( Color.blue );
        addChild( arrowShape );
        addChild( boundGraphic );
        addChild( text );
    }

    public static void main( String[] args ) {
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        PPath path = new PPath( new Rectangle( 100, 100, 10, 10 ) );
        path.setPaint( Color.red );
        phetPCanvas.addWorldChild( path );
        SwoopHelpItem graphic = new SwoopHelpItem( "Test Help Item 1." );
        phetPCanvas.addWorldChild( graphic );

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( phetPCanvas );
        frame.setSize( 400, 400 );

        graphic.setScale( 0.3 );
        graphic.setOffset( frame.getWidth() * 1.1, frame.getHeight() / 2 );
        frame.setVisible( true );

        graphic.animateToPositionScaleRotation( path.getFullBounds().getMaxX() + path.getFullBounds().getWidth() + 50, path.getFullBounds().getMaxY(), 1.0, 0, 3000 );
    }
}

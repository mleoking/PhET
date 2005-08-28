/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo;

import edu.colorado.phet.piccolo.ConnectorGraphic;
import edu.colorado.phet.piccolo.HTMLGraphic;
import edu.colorado.phet.piccolo.WiggleMe;
import edu.colorado.phet.piccolo.ArrowConnectorGraphic;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.P3DRect;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 8:15:07 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class TestPiccolo2 {
    static long lastElapsedTime = 0;
    static double totalAngle = 0;

    public static void main( String[] args ) {
        PCanvas pCanvas = new PCanvas();
        PLayer layer = pCanvas.getLayer();
        final PText pText = new PText( "Hello PhET\nTesting" );

        layer.addChild( pText );
//        pText.addInputEventListener( new DragBehavior() );
        JFrame frame = new JFrame( "Test Piccolo" );
        pCanvas.getCamera().translateView( 50, 50 );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( pCanvas );
        frame.setSize( 400, 600 );
        frame.setVisible( true );

        PText text2 = new PText( "Text2" );
        text2.setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
        layer.addChild( text2 );
        text2.translate( 100, 100 );
        text2.addInputEventListener( new PZoomEventHandler() );
//        pCanvas.removeInputEventListener( pCanvas.getZoomEventHandler() );
//        pCanvas.removeInputEventListener( pCanvas.getPanEventHandler() );

        P3DRect child = new P3DRect( 0, 0, 30, 30 );
        child.setRaised( true );
        child.setPaint( Color.green );

        pText.addChild( child );

//        PNode wiggleRoot = new PNode();
        HTMLGraphic htmlGraphic = new HTMLGraphic( "<html> MY HTML<br>so there<sup>2</html>" );
//        BoundGraphic htmlBound = new BoundGraphic( htmlGraphic );
//        htmlBound.setPaint( Color.yellow );
//        wiggleRoot.addChild( htmlBound );
//        wiggleRoot.addChild( htmlGraphic );
//        layer.addChild( wiggleRoot );
//        htmlGraphic.translate( 5, 100 );
//        htmlGraphic.repaint();
        pCanvas.getLayer().addChild( htmlGraphic );
        htmlGraphic.setOffset( 200, 150 );

        PPath p3 = new PPath( new Ellipse2D.Double( 0, 0, 45, 65 ) );
        p3.setPaint( Color.blue );
        p3.setStroke( new BasicStroke( 2 ) );
        p3.setStrokePaint( Color.black );
        p3.setOffset( 200, 10 );
        layer.addChild( p3 );
//
//        Oscillate oscillate = new Oscillate( wiggleRoot );
//        oscillate.setStartTime( System.currentTimeMillis() );
//        p3.getRoot().addActivity( oscillate );
//        layer.getRoot().addActivity( oscillate );

        WiggleMe wiggleMe = new WiggleMe( "Wiggle This!", 0, 0 );
        layer.addChild( wiggleMe );
        wiggleMe.setOscillating( true );

//        ConnectorGraphic connectorGraphic = new ConnectorGraphic( text2, wiggleMe );
        ConnectorGraphic connectorGraphic=new ArrowConnectorGraphic( wiggleMe,text2 );
//        connectorGraphic.setStroke( new BasicStroke( 2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 2, new float[]{10, 5}, 0 ) );
        layer.addChild( 0, connectorGraphic );

//        BoundGraphic boundGraphic = new BoundGraphic( htmlGraphic );
//        layer.addChild( boundGraphic );

        pCanvas.invalidate();
        pCanvas.validate();
        pCanvas.repaint();

//        PDebug.debugRegionManagement = true;
    }

}

/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;

import edu.colorado.phet.piccolo.help.WiggleMe;
import edu.colorado.phet.piccolo.nodes.ArrowConnectorGraphic;
import edu.colorado.phet.piccolo.nodes.ConnectorGraphic;
import edu.colorado.phet.piccolo.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.P3DRect;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 8:15:07 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class TestWiggleMe {
    static long lastElapsedTime = 0;
    static double totalAngle = 0;

    public static void main( String[] args ) {
        PCanvas pCanvas = new PCanvas();
        PLayer layer = pCanvas.getLayer();
        final PText pText = new PText( "Hello PhET\nTesting" );

        layer.addChild( pText );
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

        P3DRect child = new P3DRect( 0, 0, 30, 30 );
        child.setRaised( true );
        child.setPaint( Color.green );

        pText.addChild( child );

        HTMLNode htmlNode = new HTMLNode( "<html>MY HTML<br>is awesome<sup>2</html>" );
        pCanvas.getLayer().addChild( htmlNode );
        htmlNode.setOffset( 200, 150 );

        PPath p3 = new PPath( new Ellipse2D.Double( 0, 0, 45, 65 ) );
        p3.setPaint( Color.blue );
        p3.setStroke( new BasicStroke( 2 ) );
        p3.setStrokePaint( Color.black );
        p3.setOffset( 200, 10 );
        layer.addChild( p3 );

//        TargetedWiggleMe targetedWiggleMe=new TargetedWiggleMe( "Wiggle This!",0,0,text2 );
//        layer.addChild( targetedWiggleMe);
        WiggleMe wiggleMe = new WiggleMe( "Wiggle This!", 0, 0 );
        layer.addChild( wiggleMe );
//
//        ConnectorGraphic connectorGraphic = new ConnectorGraphic( text2, wiggleMe );
        ConnectorGraphic connectorGraphic = new ArrowConnectorGraphic( wiggleMe, text2 );
        layer.addChild( 0, connectorGraphic );

        pCanvas.invalidate();
        pCanvas.validate();
        pCanvas.repaint();

//        new DebugPiccoloTree().printTree( pCanvas.getRoot() );
    }

}

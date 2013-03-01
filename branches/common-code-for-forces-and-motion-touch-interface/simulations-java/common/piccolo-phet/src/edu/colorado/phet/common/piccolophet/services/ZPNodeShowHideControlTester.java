// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.services;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ZPNodeShowHideControlTester {

    public static void main( String[] args ) {

        PhetPCanvas canvas = new PhetPCanvas();

        PPath pathNode = new PPath( new Rectangle2D.Double( 0, 0, 200, 200 ) );
        pathNode.setPaint( null );
        pathNode.setStrokePaint( Color.RED );
        pathNode.setStroke( new BasicStroke( 3f ) );

        PText textNode = new PText( "Blah blah blah" );

        PNode parentNode = new PComposite(); // breaks this test case! PComposite nodes don't forward events to children
        parentNode.addChild( pathNode );
        parentNode.addChild( textNode );
        textNode.setOffset( 0, 100 );
        parentNode.setOffset( 100, 100 );
        canvas.getLayer().addChild( parentNode );

        PNodeShowHideControl showHideControl = new PNodeShowHideControl( parentNode, "Press me" );

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( canvas );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }

}

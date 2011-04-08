// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Test for HTMLNode.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestHTMLNode extends JFrame {

    public TestHTMLNode() {
        super( "TestHTMLNode" );

        PCanvas canvas = new PCanvas();
        getContentPane().add( canvas );

        String html1 = "<html>H<sub>3</sub>O<sup>+</sup></html>";
        HTMLNode htmlNode1 = new HTMLNode( html1 );
        htmlNode1.setFont( new PhetFont( 24 ) );
        canvas.getLayer().addChild( htmlNode1 );
        htmlNode1.setOffset( 100, 100 );

        // Draw a red outline around the HTMLNode's bounds.
        double w1 = htmlNode1.getFullBoundsReference().getWidth();
        double h1 = htmlNode1.getFullBoundsReference().getHeight();
        PPath boundsNode1 = new PPath( new Rectangle2D.Double( 0, 0, w1, h1 ) );
        boundsNode1.setStrokePaint( Color.RED );
        canvas.getLayer().addChild( boundsNode1 );
        boundsNode1.setOffset( htmlNode1.getOffset() );

        String html2 = "<html>This is some HTML text<br>that contains a <b>line</b> break.</html>";
        HTMLNode htmlNode2 = new HTMLNode( html2 );
        htmlNode2.setFont( new PhetFont( 24 ) );
        canvas.getLayer().addChild( htmlNode2 );
        htmlNode2.setOffset( 100, 200 );

        // Draw a red outline around the HTMLNode's bounds.
        double w2 = htmlNode2.getFullBoundsReference().getWidth();
        double h2 = htmlNode2.getFullBoundsReference().getHeight();
        PPath boundsNode2 = new PPath( new Rectangle2D.Double( 0, 0, w2, h2 ) );
        boundsNode2.setStrokePaint( Color.RED );
        canvas.getLayer().addChild( boundsNode2 );
        boundsNode2.setOffset( htmlNode2.getOffset() );
    }

    public static void main( String args[] ) {
        TestHTMLNode frame = new TestHTMLNode();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 640, 480 ) );
        frame.setVisible( true );
    }
}


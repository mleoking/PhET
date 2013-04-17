// Copyright 2002-2012, University of Colorado
package com.pixelzoom.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Tests the accuracy of PText bounds.
 * This is intended for comparison with HTML Canvas text bounds,
 * as demonstrated in canvas-text-bounds-test.html
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PTextBoundsTest {

    private static class BoundedText extends PComposite {
        public BoundedText( String string, Font font ) {

            PText textNode = new PText( string );
            textNode.setFont( font );
            addChild( textNode );

            PPath boundsNode = new PPath( textNode.getFullBoundsReference() );
            boundsNode.setStroke( new BasicStroke( 1f ) );
            boundsNode.setStrokePaint( Color.RED );
            addChild( boundsNode );
        }
    }

    public static void main( String[] args ) {

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 800, 600 ) );

        PNode test1 = new BoundedText( "The quick brown fox jumps over the lazy dog",new PhetFont( Font.BOLD, 28 )  );
        PNode test2 = new BoundedText( "\u0627\u0644\u062D\u0631\u0627\u0631\u064A\u0629",new PhetFont( Font.BOLD, 28 )  ); // Arabic (ar)
        PNode test3 = new BoundedText( "\u80FD\u91CF\u6ED1\u677F\u7AF6\u6280\u5834\uFF1A\u57FA\u790E" ,new PhetFont( Font.BOLD, 28 )  ); // Traditional Chinese (zh_TW)

        canvas.getLayer().addChild( test1 );
        canvas.getLayer().addChild( test2 );
        canvas.getLayer().addChild( test3 );

        // layout
        double x = 50;
        double y = 50;
        test1.setOffset( x, y );
        test2.setOffset( x, test1.getFullBoundsReference().getMaxY() + 20 );
        test3.setOffset( x, test2.getFullBoundsReference().getMaxY() + 20 );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

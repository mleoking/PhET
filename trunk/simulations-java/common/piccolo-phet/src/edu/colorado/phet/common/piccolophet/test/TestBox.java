// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Demonstrates the problem reported in #3191:
 * layout is messed up if child bounds change after adding to Box (HBox, VBox).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestBox extends JFrame {

    public TestBox() {
        final VBox boxNode = new VBox( 1, VBox.LEFT_ALIGNED ) {{
            // create some components, to be initialized (for example) by observing properties
            PText one = new PText();
            PText two = new PText();
            PText three = new PText();
            // add the nodes in their desired top-to-bottom order in the VBox
            addChild( one );
            addChild( two );
            addChild( three );
            // simulate what would happen when (for example) observing properties
            one.setText( "one" );
            two.setText( "two" );
            three.setText( "three" );
            setOffset( 50, 50 );
        }};
        PCanvas canvas = new PCanvas() {{
            setPreferredSize( new Dimension( 200, 200 ) );
            getLayer().addChild( boxNode );
        }};
        setContentPane( canvas );
        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new TestBox() {{
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}

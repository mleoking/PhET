// Copyright 2002-2011, University of Colorado
package edu.colorado.phet;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author John Blanco
 */
public class NodeScalingTest {

    /**
     * Test scaling of a piccolo node.  This was created to help work out some
     * scaling issue in the Nuclear Physics family of simulations.
     *
     * @param args
     */
    public static void main( String[] args ) {

        PhetPCanvas canvas = new PhetPCanvas();

        PPath box = new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 100 ), Color.PINK );
        PText text = new PText( "This is the Label" );
        text.setOffset( 3, 3 );
        box.addChild( text );
        box.setOffset( 100, 100 );

//        box.transformBy( AffineTransform.getScaleInstance( 2, 1 ) );
//        box.transformBy( AffineTransform.getScaleInstance( 1, 1.5 ) );
        canvas.getLayer().addChild( box );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 400, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

}

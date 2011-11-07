// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.test;

import java.awt.geom.Dimension2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This is a class for testing out how patterns related to right-to-left
 * languages work.  I have messed some of these up in the past, so I created
 * a little harness for testing some out.
 * <p/>
 * To make this run as an RTL language, choose one and set the appropriate
 * value as a parameter to the VM.  For example:
 * //REVIEW where's the example?...
 *
 * @author John Blanco
 */
public class PatternTest {
    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 300, 200 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        PText patternLabeledPText = new PText() {{
            setText( "Blah" );
            setFont( new PhetFont( 20 ) );
            setOffset( 50, 50 );
        }};
        canvas.getLayer().addChild( patternLabeledPText );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

}

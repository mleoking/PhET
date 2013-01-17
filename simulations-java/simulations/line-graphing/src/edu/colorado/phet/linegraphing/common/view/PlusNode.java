// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Plus sign, created using PPath because PText("+") cannot be accurately centered.
 * Origin at upper left.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlusNode extends PPath {

    public PlusNode( PDimension size, Paint paint ) {
        this( size.getWidth(), size.getHeight(), paint );
    }

    /**
     * Constructor
     *
     * @param width  width of the horizontal bar
     * @param height height of the horizontal bar
     * @param paint  paint used to fill the plus sign
     */
    private PlusNode( double width, double height, Paint paint ) {
        assert ( width > height );

        // + shape, starting from top left and moving clockwise
        final double c1 = ( width / 2 ) - ( height / 2 );
        final double c2 = ( width / 2 ) + ( height / 2 );
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( c1, 0 );
        path.lineTo( c2, 0 );
        path.lineTo( c2, c1 );
        path.lineTo( width, c1 );
        path.lineTo( width, c2 );
        path.lineTo( c2, c2 );
        path.lineTo( c2, width ); // yes, use width for y param
        path.lineTo( c1, width ); // yes, use width for y param
        path.lineTo( c1, c2 );
        path.lineTo( 0, c2 );
        path.lineTo( 0, c1 );
        path.lineTo( c1, c1 );
        path.closePath();

        setPathTo( path.getGeneralPath() );
        setStroke( null );
        setPaint( paint );
    }

    public static void main( String[] args ) {

        PNode plusNode = new PlusNode( 60, 5, Color.BLACK );
        plusNode.setOffset( 100, 100 );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 800, 600 ) );
        canvas.getLayer().addChild( plusNode );

        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

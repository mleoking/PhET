// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.test;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.ShapeCreationUtils;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Tests some of the shape creation utilities.  This exists primarily to get a
 * look at a bunch of the random shapes at the same time in order to decide
 * which ones look best.
 *
 * @author John Blanco
 */
public class ShapeCreationTest {
    private static final double WIDTH = 60;
    private static final double HEIGHT = 40;

    public static void main( String[] args ) {

        PCanvas canvas = new PCanvas();
        int startSeed = 0;
        Shape shape;
        for ( int i = 0; i < 10; i++ ) {
            for ( int j = 0; j < 10; j++ ) {

                shape = ShapeCreationUtils.createRandomShape( new PDimension( WIDTH, HEIGHT ), startSeed );
                // Invert the shape vertically, since in model space we usually have up mean positive Y.
                shape = AffineTransform.getScaleInstance( 1, -1 ).createTransformedShape( shape );
                PhetPPath shapeNode = new PhetPPath( AffineTransform.getTranslateInstance( ( j + 1 ) * ( WIDTH * 1.5 ), ( i + 1 ) * ( HEIGHT * 1.5 ) ).createTransformedShape( shape ), Color.ORANGE );
                canvas.getLayer().addChild( shapeNode );
                PText textNode = new PText( Integer.toString( startSeed ) );
                textNode.centerFullBoundsOnPoint( shapeNode.getFullBoundsReference().getCenterX(),
                                                  shapeNode.getFullBoundsReference().getCenterY() );
                canvas.getLayer().addChild( textNode );
                startSeed++;
            }
        }

        // Boiler plate app stuff.
        JFrame frame = new JFrame( "Shape Util Testing" );
        frame.setContentPane( canvas );
        frame.setSize( (int) ( WIDTH + 2 ) * 10 * 2, (int) ( HEIGHT + 2 ) * 10 * 2 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

}

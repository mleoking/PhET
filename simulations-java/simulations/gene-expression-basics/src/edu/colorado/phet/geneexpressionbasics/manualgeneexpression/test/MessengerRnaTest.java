// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.test;

import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ProteinA;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.StubGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MobileBiomoleculeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Test harness for the MessengerRna class.
 *
 * @author John Blanco
 */
public class MessengerRnaTest {

    private static final Dimension2D STAGE_SIZE = new PDimension( 800, 480 );

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window and puts
     * the biomolecule(s) on it.
     *
     * @param args
     */
    public static void main( String[] args ) {

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, STAGE_SIZE ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.50 ) ),
                0.1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        MessengerRna messengerRna = new MessengerRna( new StubGeneExpressionModel(), new ProteinA(), new Point2D.Double( 0, 0 ) );
        messengerRna.addLength( 100 );
        messengerRna.addLength( 100 );
        messengerRna.addLength( 100 );
        messengerRna.addLength( 100 );
        canvas.addWorldChild( new MobileBiomoleculeNode( mvt, messengerRna ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame( "RNA Polymerase Shape Testing" );
        frame.setContentPane( canvas );
        frame.setSize( (int) STAGE_SIZE.getWidth(), (int) STAGE_SIZE.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}

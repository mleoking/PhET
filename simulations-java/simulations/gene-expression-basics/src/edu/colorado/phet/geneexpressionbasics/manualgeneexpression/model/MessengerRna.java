// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.BiomoleculeShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.DetachingState;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.IdleState;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MobileBiomoleculeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents messenger ribonucleic acid, or mRNA, in the model.
 *
 * @author John Blanco
 */
public class MessengerRna extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Color used by this molecule.  Since mRNA is depicted as a line and not
    // as a closed shape, a transparent color is used.  This enables reuse of
    // generic biomolecule classes.
    private static final Color NOMINAL_COLOR = new Color( 0, 0, 0, 0 );

    // Minimum distance between points that define the shape.  This is done so
    // that this doesn't end up defined by so many points that the shape is
    // strange looking.
    private static final double MIN_DISTANCE_BETWEEN_POINTS = 50; // In picometers, empirically determined.

    // Random number generator used for creating "curviness" in the shape of
    // the RNA.
    private static final Random RAND = new Random();

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final List<Point2D> shapeDefiningPoints = new ArrayList<Point2D>();
    private final Map<Point2D, Double> distanceToPreviousPointMap = new HashMap<Point2D, Double>();

    // This vector controls the way in which the mRNA drifts as it grows.  Each
    // time growth occurs, the scaler that represents the amount of growth is
    // multiplied by this vector, and the vector is then applied to the
    // positions of all of the shape-defining points.  At the time of this
    // writing, there is no need to have anything other than one way of
    // drifting while growing, but this could easily be made settable or into
    // a constructor param.
    private ImmutableVector2D driftWhileGrowingVector = new Vector2D( 0.15, 0.4 );

    // Rectangle in which all the points that define the mRNA strand should be
    // contained.  This is used to guide the algorithm that twists up the mRNA.
    // Note that sometimes - such as when the mRNA is being translated - that
    // this rectangle is ignored.
    private Rectangle2D containingRect = new Rectangle2D.Double();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.  This creates the mRNA as a single point, with the intention
     * of growing it.
     *
     * @param position
     */
    public MessengerRna( GeneExpressionModel model, Point2D position ) {
        super( model, new DoubleGeneralPath( position ).getGeneralPath(), NOMINAL_COLOR );
        setPosition( position );
        // Explicitly set the state to idle, so that this won't move (other
        // than growing) until it is released.
        behaviorState = new IdleState( this );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Add a length to the mRNA from its current end point to the specified end
     * point.  This is usually done in small amounts, and is likely to look
     * weird if an attempt is made to grow to a distant point.
     */
    public void addLength( Point2D newEndPoint ) {
        double diagonalLength = 0;
        double currentUnfurledLength = getLength();
        if ( currentUnfurledLength < MIN_DISTANCE_BETWEEN_POINTS ) {
            if ( shapeDefiningPoints.size() > 0 ) {
                diagonalLength = shapeDefiningPoints.get( 0 ).distance( newEndPoint );
            }
        }
        else {
            // The diagonal length gets a little larger with added points, but
            // the longer it gets the more slowly the overall size grows.
            diagonalLength = MIN_DISTANCE_BETWEEN_POINTS * ( Math.log( currentUnfurledLength / MIN_DISTANCE_BETWEEN_POINTS ) + 1 );
        }
        if ( shapeDefiningPoints.size() >= 2 ) {
            // If the current last point is less than the min distance from
            // the 2nd to last point, remove the current last point.  This
            // prevents having zillions of shape-defining points, which is
            // harder to work with.
            Point2D lastPoint = shapeDefiningPoints.get( shapeDefiningPoints.size() - 1 );
            Point2D secondToLastPoint = shapeDefiningPoints.get( shapeDefiningPoints.size() - 2 );
            if ( lastPoint.distance( secondToLastPoint ) < MIN_DISTANCE_BETWEEN_POINTS ) {
                shapeDefiningPoints.remove( lastPoint );
                distanceToPreviousPointMap.remove( lastPoint );
            }
        }

        // Add the new point.
        shapeDefiningPoints.add( newEndPoint );
        if ( shapeDefiningPoints.size() > 1 ) {
            // Retain the original distance between this new end point and
            // the previous one.  This is used when twisting up the mRNA.
            distanceToPreviousPointMap.put( newEndPoint, newEndPoint.distance( shapeDefiningPoints.get( shapeDefiningPoints.size() - 1 ) ) );
        }

        // Reposition the existing points such that the overall shape looks
        // like a curled up line.
        if ( shapeDefiningPoints.size() > 0 ) {
            Point2D lastPoint = shapeDefiningPoints.get( shapeDefiningPoints.size() - 1 );
            // Move the first point to be at the end of the diagonal from this
            // newly added point.  This is up and to the left of the new point,
            // so the mRNA will seem to grow in this direction.
            ImmutableVector2D diagonalVector = new ImmutableVector2D( diagonalLength, 0 ).getRotatedInstance( Math.PI * 0.75 );
            shapeDefiningPoints.get( 0 ).setLocation( newEndPoint.getX() + diagonalVector.getX(), newEndPoint.getY() + diagonalVector.getY() );
            // TODO: Temp - randomize the position of all but the first and
            // last points to be somewhere in the square defined by the diagonal.
            double squareWidth = Math.abs( diagonalVector.getX() );
            Rectangle2D enclosingSquare = new Rectangle2D.Double( newEndPoint.getX() + diagonalVector.getX(),
                                                                  newEndPoint.getY(),
                                                                  squareWidth,
                                                                  squareWidth );
            for ( int i = 1; i < shapeDefiningPoints.size() - 1; i++ ) {
                shapeDefiningPoints.get( i ).setLocation( enclosingSquare.getMinX() + RAND.nextDouble() * squareWidth,
                                                          enclosingSquare.getMinY() + RAND.nextDouble() * squareWidth );
            }
        }

        // Update the shape to reflect the newly added point.
        shapeProperty.set( BiomoleculeShapeUtils.createCurvyLineFromPoints( shapeDefiningPoints ) );
    }

    public void addLength( double x, double y ) {
        addLength( new Point2D.Double( x, y ) );
    }

    /**
     * Add a length to the mRNA from its current end point to the specified end
     * point.  This is usually done in small amounts, and is likely to look
     * weird if an attempt is made to grow to a distant point.
     */
    public void addLengthOld( Point2D p ) {
        if ( shapeDefiningPoints.size() > 0 ) {
            Point2D lastPoint = shapeDefiningPoints.get( shapeDefiningPoints.size() - 1 );
            double growthAmount = lastPoint.distance( p );
            // Cause all existing points to "drift" so that this doesn't just
            // create a straight line.
            ImmutableVector2D driftVector = driftWhileGrowingVector.getScaledInstance( growthAmount );
            for ( Point2D point : shapeDefiningPoints ) {
                point.setLocation( point.getX() + driftVector.getX(),
                                   point.getY() + driftVector.getY() );
            }
            if ( shapeDefiningPoints.size() >= 2 ) {
                // If the current last point is less than the min distance from
                // the 2nd to last point, replace the current last point.  This
                // prevents having zillions of shape-defining points, which is
                // harder to work with.
                Point2D secondToLastPoint = shapeDefiningPoints.get( shapeDefiningPoints.size() - 2 );
                if ( lastPoint.distance( secondToLastPoint ) < MIN_DISTANCE_BETWEEN_POINTS ) {
                    shapeDefiningPoints.remove( lastPoint );
                }
                else {
                    // Add a random offset to the second-to-last point, which
                    // is far enough away to keep.  This is done in order to
                    // make the shape of the mRNA a bit curvy.
                    ImmutableVector2D randomizationVector2 = new ImmutableVector2D( 1, 0 ).getScaledInstance( MIN_DISTANCE_BETWEEN_POINTS / 2 ).getRotatedInstance( RAND.nextDouble() * Math.PI * 2 );
                    secondToLastPoint.setLocation( secondToLastPoint.getX() + driftVector.getX() + randomizationVector2.getX(),
                                                   secondToLastPoint.getY() + driftVector.getY() + randomizationVector2.getY() );
                }
            }
        }
        // Add the new point.
        shapeDefiningPoints.add( p );
        // Update the shape to reflect the newly added point.
        shapeProperty.set( BiomoleculeShapeUtils.createCurvyLineFromPoints( shapeDefiningPoints ) );
    }

    public void release() {
        // Set the state to just be drifting around in the cytoplasm.
        behaviorState = new DetachingState( this, new ImmutableVector2D( 0, 1 ) );
    }

    /**
     * Get the length of the strand.  The length is calculated by adding up
     * the distances between the points, and does not account for curvature,
     * so is somewhat inaccurate.
     *
     * @return
     */
    private double getLength() {
        double length = 0;
        for ( int i = 1; i < shapeDefiningPoints.size(); i++ ) {
            length += shapeDefiningPoints.get( i ).distance( shapeDefiningPoints.get( i - 1 ) );
        }
        return length;
    }

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 500, 400 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.5 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                0.1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        canvas.getLayer().addChild( new PhetPPath( new Rectangle2D.Double( -5, -5, 10, 10 ), Color.PINK ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );

//                canvas.addWorldChild( new PhetPPath( mvt.modelToView( new Rectangle2D.Double( -100, -100, 200, 200 ) ), Color.RED ) );
        // Add the mRNA and then grow it a little at a time.
//                RnaPolymerase rnaPolymerase = new RnaPolymerase( new ManualGeneExpressionModel(), new Point2D.Double( 0, 0 ) );
//                canvas.addWorldChild( new MobileBiomoleculeNode( mvt, rnaPolymerase ) );
        MessengerRna messengerRna = new MessengerRna( new ManualGeneExpressionModel(), new Point2D.Double( 0, 0 ) );
        canvas.addWorldChild( new MobileBiomoleculeNode( mvt, messengerRna ) );
        for ( int i = 0; i < 200; i++ ) {
            messengerRna.addLength( mvt.modelToView( i * 50, 0 ) );
            try {
                Thread.sleep( 50 );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            canvas.repaint();
        }
    }


}

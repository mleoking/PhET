// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.model;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Primary model class for the Multiple Cells tab.
 *
 * @author John Blanco
 */
public class MultipleCellsModel implements Resettable {

    public static final int MAX_CELLS = 90;

    // Seeds for the random number generators.  Values chosen empirically.
    private static final long POSITION_RANDOMIZER_SEED = 226;

    private static final long SIZE_AND_ORIENTATION_RANDOMIZER_SEED = 5;

    // Threshold used to prevent floating point errors from not correctly
    // identifying overlap.
    private static double FLOATING_POINT_THRESHOLD = 1E-10;

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // List of all cells that are being simulated.  Some of these cells will be
    // visible to the user at any given time, but some may not.  All are
    // clocked and their parameters are kept the same in order to keep them
    // "in sync" with the visible cells.  This prevents large discontinuities
    // in the protein level when the user adds or removes cells.
    private final List<Cell> cellList = new ArrayList<Cell>();

    // List of cells in the model that should be visible to the user and that
    // are being used in the average protein level calculation.  It is
    // observable so that the view can track them coming and going.
    public final ObservableList<Cell> visibleCellList = new ObservableList<Cell>();

    // Property that controls the number of cells that are visible and that are
    // being included in the calculation of the average protein level.  This is
    // intended to be set by clients, such as the view.
    public final Property<Integer> numberOfVisibleCells = new Property<Integer>( 1 );

    // Properties used to control the rate at which protein is synthesized and
    // degraded in the cells.  These are intended to be set by clients, such as
    // the view.
    public final Property<Integer> transcriptionFactorLevel = new Property<Integer>( CellProteinSynthesisSimulator.DEFAULT_TRANSCRIPTION_FACTOR_COUNT );
    public final Property<Double> proteinDegradationRate = new Property<Double>( CellProteinSynthesisSimulator.DEFAULT_PROTEIN_DEGRADATION_RATE );
    public final Property<Double> transcriptionFactorAssociationProbability = new Property<Double>( CellProteinSynthesisSimulator.DEFAULT_TF_ASSOCIATION_PROBABILITY );
    public final Property<Double> polymeraseAssociationProbability = new Property<Double>( CellProteinSynthesisSimulator.DEFAULT_POLYMERASE_ASSOCIATION_PROBABILITY );
    public final Property<Double> mRnaDegradationRate = new Property<Double>( CellProteinSynthesisSimulator.DEFAULT_MRNA_DEGRADATION_RATE );

    // Property that tracks the average protein level of all the cells.  This
    // should not be set externally, only internally.  From the external
    // perspective, it is intended for monitoring and displaying by view
    // components.
    public final Property<Double> averageProteinLevel = new Property<Double>( 0.0 );

    /**
     * Constructor.
     */
    public MultipleCellsModel() {

        // Hook up the clock.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Random number generators, used to vary the shape and position of the
        // cells.  Seeds are chosen based on experimentation.
        Random sizeAndRotationRandomizer = new Random( SIZE_AND_ORIENTATION_RANDOMIZER_SEED );
        Random positionRandomizer = new Random( POSITION_RANDOMIZER_SEED );

        // Add the max number of cells to the list of invisible cells.
        while ( cellList.size() < MAX_CELLS ) {
            Cell newCell;
            if ( cellList.isEmpty() ) {
                // The first cell is centered and level.
                newCell = new Cell( Cell.DEFAULT_CELL_SIZE, new Point2D.Double( 0, 0 ), 0, 0 );
            }
            else {
                // Do some randomization of the cell's size and rotation angle.
                double cellWidth = Math.max( Cell.DEFAULT_CELL_SIZE.getWidth() * ( sizeAndRotationRandomizer.nextDouble() / 2 + 0.75 ),
                                             Cell.DEFAULT_CELL_SIZE.getHeight() * 2 );
                // Note that the index is used as the seed for the shape in
                // order to make the cell appearance vary, but be deterministic.
                newCell = new Cell( new PDimension( cellWidth, Cell.DEFAULT_CELL_SIZE.getHeight() ), new Point2D.Double( 0, 0 ), Math.PI * 2 * sizeAndRotationRandomizer.nextDouble(), cellList.size() );
                placeCellInOpenLocation( newCell, cellList, positionRandomizer );
            }
            cellList.add( newCell );
        }

        // Hook up the property that controls the number of visible cells.
        numberOfVisibleCells.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer numVisibleCells ) {
                assert numVisibleCells >= 1 && numVisibleCells <= MAX_CELLS;
                setNumVisibleCells( numVisibleCells );
            }
        } );

        // Hook up the cell property parameters to the individual cells so
        // that changes are propagated.
        transcriptionFactorLevel.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer transcriptionFactorLevel ) {
                for ( Cell cell : cellList ) {
                    cell.setTranscriptionFactorCount( transcriptionFactorLevel );
                }
            }
        } );
        polymeraseAssociationProbability.addObserver( new VoidFunction1<Double>() {
            public void apply( Double polymeraseAssociationProbability ) {
                for ( Cell cell : cellList ) {
                    cell.setPolymeraseAssociationRate( polymeraseAssociationProbability );
                }
            }
        } );
        transcriptionFactorAssociationProbability.addObserver( new VoidFunction1<Double>() {
            public void apply( Double transcriptionFactorAssociationProbability ) {
                for ( Cell cell : cellList ) {
                    cell.setGeneTranscriptionFactorAssociationRate( transcriptionFactorAssociationProbability );
                }
            }
        } );
        proteinDegradationRate.addObserver( new VoidFunction1<Double>() {
            public void apply( Double proteinDegradationRate ) {
                for ( Cell cell : cellList ) {
                    cell.setProteinDegradationRate( proteinDegradationRate );
                }
            }
        } );
        mRnaDegradationRate.addObserver( new VoidFunction1<Double>() {
            public void apply( Double mRnaDegradationRate ) {
                for ( Cell cell : cellList ) {
                    cell.setMRnaDegradationRate( mRnaDegradationRate );
                }
            }
        } );
    }

    private void stepInTime( double dt ) {
        // Step each of the cells.
        for ( Cell cell : cellList ) {
            cell.stepInTime( dt );
        }

        // Update the average protein level.  Note that only the visible cells
        // are used for this calculation.  This helps convey the concept that
        // the more cells there are, the more even the average level is.
        int totalProteinCount = 0;
        for ( Cell cell : visibleCellList ) {
            cell.stepInTime( dt );
            totalProteinCount += cell.proteinCount.get();
        }
        averageProteinLevel.set( (double) totalProteinCount / visibleCellList.size() );
    }

    public IClock getClock() {
        return clock;
    }

    /**
     * Restore model to initial conditions.  Should be called at least once
     * during initialization of the module.
     */
    public void reset() {

        // Reset all the cell control parameters.
        numberOfVisibleCells.reset();
        transcriptionFactorLevel.reset();
        proteinDegradationRate.reset();
        transcriptionFactorAssociationProbability.reset();
        polymeraseAssociationProbability.reset();
        mRnaDegradationRate.reset();

        // Start with one visible cell.
        setNumVisibleCells( 1 );

        // Step the model a bunch of times in order to allow it to reach a
        // steady state.  The number of times that are needed to reach steady
        // state was empirically determined.
        for ( int i = 0; i < 1000; i++ ) {
            stepInTime( clock.getDt() );
        }
    }

    /**
     * Set the number of cells that should be visible to the user and that are
     * included in the calculation of average protein level.
     *
     * @param numCells - target number of cells.
     */
    private void setNumVisibleCells( int numCells ) {

        assert numCells > 0 && numCells <= MAX_CELLS;  // Bounds checking.
        numCells = MathUtil.clamp( 1, numCells, MAX_CELLS ); // Defensive programming.

        if ( visibleCellList.size() < numCells ) {
            // Add cells to the visible list.
            while ( visibleCellList.size() < numCells ) {
                visibleCellList.add( cellList.get( visibleCellList.size() ) );
            }
        }
        else if ( visibleCellList.size() > numCells ) {
            // Remove cells from the visible list.  Take them off the end.
            while ( visibleCellList.size() > numCells ) {
                visibleCellList.remove( visibleCellList.size() - 1 );
            }
        }
    }

    // Find a location for the given cell that doesn't overlap with any of the
    // other cells on the list.
    private static void placeCellInOpenLocation( Cell cell, List<Cell> cellList, Random positionRandomizer ) {
        // Loop, randomly generating positions of increasing distance from the
        // center, until the cell is positioned in a place that does not
        // overlap with the existing cells.  The overall bounding shape of the
        // collection of cells is elliptical, not circular.
        double boundingShapeWidth = Cell.DEFAULT_CELL_SIZE.getWidth() * 20;
        double boundingShapeHeight = boundingShapeWidth * 0.35;
        Shape boundingShape = new Ellipse2D.Double( -boundingShapeWidth / 2, -boundingShapeHeight / 2, boundingShapeWidth, boundingShapeHeight );
        for ( int i = 0; i < (int) Math.ceil( Math.sqrt( cellList.size() ) ); i++ ) {
            double radius = ( i + 1 ) * Cell.DEFAULT_CELL_SIZE.getWidth() * ( positionRandomizer.nextDouble() / 2 + .75 );
            for ( int j = 0; j < radius * Math.PI / ( Cell.DEFAULT_CELL_SIZE.getHeight() * 2 ); j++ ) {
                double angle = positionRandomizer.nextDouble() * 2 * Math.PI;
                cell.setPosition( radius * Math.cos( angle ), radius * Math.sin( angle ) );
                if ( !boundingShape.contains( cell.getPosition() ) ) {
                    // Not in bounds.
                    continue;
                }
                boolean overlapDetected = false;
                for ( Cell existingCell : cellList ) {
                    if ( rectanglesOverlap( cell.getEnclosingRectVertices(), existingCell.getEnclosingRectVertices() ) ) {
                        overlapDetected = true;
                        break;
                    }
                }
                if ( !overlapDetected ) {
                    // Found an open spot.
                    return;
                }
            }
        }
        System.out.println( "Warning: Exiting placement loop without having found open location." );
    }

    // Find a location for the given cell that doesn't overlap with any of the
    // other cells on the list.
    private static void placeCellInOpenLocationOldCircular( Cell cell, List<Cell> cellList, Random positionRandomizer ) {
        // Loop, randomly generating positions of increasing distance from the
        // center, until the cell is positioned in a place that does not
        // overlap with the existing cells.
        for ( int i = 0; i < (int) Math.ceil( Math.sqrt( cellList.size() ) ); i++ ) {
            double radius = ( i + 1 ) * Cell.DEFAULT_CELL_SIZE.getWidth() * ( positionRandomizer.nextDouble() / 2 + .75 );
            for ( int j = 0; j < radius * Math.PI / ( Cell.DEFAULT_CELL_SIZE.getHeight() * 2 ); j++ ) {
                double angle = positionRandomizer.nextDouble() * 2 * Math.PI;
                cell.setPosition( radius * Math.cos( angle ), radius * Math.sin( angle ) );
                boolean overlapDetected = false;
                for ( Cell existingCell : cellList ) {
                    if ( rectanglesOverlap( cell.getEnclosingRectVertices(), existingCell.getEnclosingRectVertices() ) ) {
                        overlapDetected = true;
                        break;
                    }
                }
                if ( !overlapDetected ) {
                    // Found an open spot.
                    return;
                }
            }
        }
        System.out.println( "Warning: Exiting placement loop without having found open location." );
    }

    /**
     * Get a rectangle in model space that is centered at coordinates (0, 0)
     * and that is large enough to contain all of the visible cells.
     *
     * @return
     */
    public Rectangle2D getVisibleCellCollectionBounds() {
        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;
        for ( Cell cell : visibleCellList ) {
            if ( cell.getShape().getBounds2D().getMinX() < minX ) {
                minX = cell.getShape().getBounds2D().getMinX();
                maxX = -minX;
            }
            if ( cell.getShape().getBounds2D().getMinY() < minY ) {
                minY = cell.getShape().getBounds2D().getMinY();
                maxY = -minY;
            }
            if ( cell.getShape().getBounds2D().getMaxX() > maxX ) {
                maxX = cell.getShape().getBounds2D().getMaxX();
                minX = -maxX;
            }
            if ( cell.getShape().getBounds2D().getMaxY() > maxY ) {
                maxY = cell.getShape().getBounds2D().getMaxY();
                minY = -maxY;
            }
        }
        return new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
    }

    /**
     * Sets the number of polymerases for all cells in this population
     *
     * @param polymeraseCount number of polymerases
     */
    public void setPolymeraseCount( int polymeraseCount ) {
        for ( Cell cell : visibleCellList ) {
            cell.setPolymeraseCount( polymeraseCount );
        }
    }

    /**
     * Sets the rate that transcription factors associate with genes for all
     * cells in this population
     *
     * @param newRate
     */
    public void setGeneTranscriptionFactorAssociationRate( double newRate ) {
        for ( Cell cell : visibleCellList ) {
            cell.setGeneTranscriptionFactorAssociationRate( newRate );
        }
    }

    /**
     * Sets the rate constant for the polymerase to bind to the gene for all cells
     * in this population
     *
     * @param newRate the rate for polymerase binding
     */
    public void setPolymeraseAssociationRate( double newRate ) {
        for ( Cell cell : visibleCellList ) {
            cell.setPolymeraseAssociationRate( newRate );
        }
    }

    /**
     * Sets the rate constant for RNA/ribosome association for all cells in
     * this population
     *
     * @param newRate the rate at which RNA binds to a ribosome
     */
    public void setRNARibosomeAssociationRate( double newRate ) {
        for ( Cell cell : visibleCellList ) {
            cell.setRNARibosomeAssociationRate( newRate );
        }
    }

    /**
     * Determine whether or not two rectangles that are positioned at arbitrary
     * rotations overlap.  The built-in facilities in Swing and Piccolo are
     * insufficient for this since they do not handle rotation.
     * <p/>
     * This method implements an algorithm called a "separating axis test".
     * Search the internet on this if you need to know more.
     *
     * @param r1 - Set of vertices for rectangle 1
     * @param r2 - Set of vertices for rectangle 2
     * @return true if the rectangles overlap, false if not.
     */
    private static boolean rectanglesOverlap( List<Point2D> r1, List<Point2D> r2 ) {

        // Parameter checking.
        if ( r1.size() != 4 || r2.size() != 4 ) {
            throw new IllegalArgumentException( "Rectangles must have exactly four vertices." );
        }

        boolean separatingEdgeFound = false;

        for ( int i = 0; i < 4; i++ ) {
            Vector2D edge = new Vector2D( r1.get( i ).getX() - r1.get( ( i + 1 ) % 4 ).getX(),
                                          r1.get( i ).getY() - r1.get( ( i + 1 ) % 4 ).getY() );
            // Rotate the vector by 90 degrees.
            edge.setComponents( -edge.getY(), edge.getX() );

            double r2AccumulatedSideSigns = 0;
            for ( int j = 0; j < 4; j++ ) {
                r2AccumulatedSideSigns += sideSign( edge, r1.get( i ), r2.get( j ) );
            }

            if ( Math.abs( r2AccumulatedSideSigns ) == 4 ) {
                // All points of r2 are on one side of this edge.  Are all
                // points of r1 on the other side?
                double r1AccumulatedSideSigns = 0;
                for ( int j = 0; j < 4; j++ ) {
                    r1AccumulatedSideSigns += sideSign( edge, r1.get( i ), r1.get( j ) );
                }

                // This should always come out to have an absolute value of
                // two.  I think.
                assert Math.abs( r1AccumulatedSideSigns ) == 2;
                if ( Math.signum( r2AccumulatedSideSigns ) == -Math.signum( r1AccumulatedSideSigns ) ) {
                    // All points of r1 are on the opposite side of the edge.
                    // A separating edge has been found.
                    separatingEdgeFound = true;
                    break;
                }
            }
        }

        return !separatingEdgeFound;
    }

    private static double sideSign( Vector2D edge, Point2D pointOnEdge, Point2D testPoint ) {
        double value = edge.getX() * ( testPoint.getX() - pointOnEdge.getX() ) + edge.getY() * ( testPoint.getY() - pointOnEdge.getY() );
        if ( value < FLOATING_POINT_THRESHOLD ) {
            value = 0;
        }
        return Math.signum( edge.getX() * ( testPoint.getX() - pointOnEdge.getX() ) + edge.getY() * ( testPoint.getY() - pointOnEdge.getY() ) );
    }

    public static void main( String[] args ) {
        List<Point2D> referenceRect = new ArrayList<Point2D>() {{
            add( new Point2D.Double( 1, 1 ) );
            add( new Point2D.Double( 4, 1 ) );
            add( new Point2D.Double( 4, 3 ) );
            add( new Point2D.Double( 1, 3 ) );
        }};

        // Same rect.
        List<Point2D> identicalRect = new ArrayList<Point2D>( referenceRect );

        // Non overlapping rect.
        List<Point2D> nonOverlapNonRotatedRect = new ArrayList<Point2D>() {{
            add( new Point2D.Double( 1, 4 ) );
            add( new Point2D.Double( 4, 4 ) );
            add( new Point2D.Double( 4, 7 ) );
            add( new Point2D.Double( 1, 7 ) );
        }};

        List<Point2D> slightlyOverlappingRect = new ArrayList<Point2D>() {{
            add( new Point2D.Double( 3, 2 ) );
            add( new Point2D.Double( 7, 2 ) );
            add( new Point2D.Double( 7, 9 ) );
            add( new Point2D.Double( 3, 9 ) );
        }};

        List<Point2D> nonOverlappingNonRotatedNegativeXRect = new ArrayList<Point2D>() {{
            add( new Point2D.Double( -4, 1 ) );
            add( new Point2D.Double( -1, 1 ) );
            add( new Point2D.Double( -1, 3 ) );
            add( new Point2D.Double( -4, 3 ) );
        }};

        List<Point2D> nonOverlappingRotatedRect = new ArrayList<Point2D>() {{
            add( new Point2D.Double( 2, -3 ) );
            add( new Point2D.Double( 3, -4 ) );
            add( new Point2D.Double( 5, -2 ) );
            add( new Point2D.Double( 4, -1 ) );
        }};

        List<Point2D> overlappingRotatedRect = new ArrayList<Point2D>() {{
            add( new Point2D.Double( 1, 0 ) );
            add( new Point2D.Double( 2, -1 ) );
            add( new Point2D.Double( 6, 3 ) );
            add( new Point2D.Double( 5, 4 ) );
        }};

        System.out.println( "identicalRect overlap = " + rectanglesOverlap( referenceRect, identicalRect ) );
        System.out.println( "nonOverlapNonRotatedRect overlap = " + rectanglesOverlap( referenceRect, nonOverlapNonRotatedRect ) );
        System.out.println( "slightlyOverlappingRect overlap = " + rectanglesOverlap( referenceRect, slightlyOverlappingRect ) );
        System.out.println( "nonOverlappingNonRotatedNegativeXRect overlap = " + rectanglesOverlap( referenceRect, nonOverlappingNonRotatedNegativeXRect ) );
        System.out.println( "nonOverlappingRotatedRect overlap = " + rectanglesOverlap( referenceRect, nonOverlappingRotatedRect ) );
        System.out.println( "overlappingRotatedRect overlap = " + rectanglesOverlap( referenceRect, overlappingRotatedRect ) );
    }
}

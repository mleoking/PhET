// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
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
    private static final long POSITION_RANDOMIZER_SEED = 9;
    private static final long SIZE_AND_ORIENTATION_RANDOMIZER_SEED = 1;

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

    // Locations where cells are placed.  This is initialized at construction
    // so that placements are consistent as cells come and go.
    public final List<Point2D> cellLocations = new ArrayList<Point2D>();

    // Property that controls the number of cells that are visible and that are
    // being included in the calculation of the average protein level.  This is
    // intended to be set by clients, such as the view.
    public final Property<Integer> numberOfVisibleCells = new Property<Integer>( 1 );

    // Properties used to control the rate at which protein is synthesized and
    // degraded in the cells.  These are intended to be set by clients, such as
    // the view.
    public final Property<Integer> transcriptionFactorLevel = new Property<Integer>( CellProteinSynthesisSimulator.DEFAULT_TRANSCRIPTION_FACTOR_COUNT );
    public final Property<Double> proteinDegradationRate = new Property<Double>( CellProteinSynthesisSimulator.DEFAULT_PROTEIN_DEGRADATION_RATE );
    // TODO: I'm not sure how to actually reconcile the following two parameters
    // between what the spec requires and what the model provides.  For now,
    // I've gone with two properties that seem related, but need to work with
    // George and possibly MK to finalize this.
    public final Property<Double> transcriptionFactorAssociationProbability = new Property<Double>( CellProteinSynthesisSimulator.DEFAULT_TF_ASSOCIATION_PROBABILITY );
    public final Property<Double> polymeraseAssociationProbability = new Property<Double>( CellProteinSynthesisSimulator.DEFAULT_POLYMERASE_ASSOCIATION_PROBABILITY );

    // Property that tracks the average protein level of all the cells.  This
    // should not be set externally, only internally.  From the external
    // perspective, it is intended for monitoring and displaying by view
    // components.
    public final Property<Double> averageProteinLevel = new Property<Double>( 0.0 );

    // Random number generators, used to vary the shape and position of the
    // cells.  Seeds are chosen based on experimentation.
    private Random sizeAndRotationRandomizer = new Random( SIZE_AND_ORIENTATION_RANDOMIZER_SEED );
    private Random positionRandomizer = new Random( POSITION_RANDOMIZER_SEED );

    /**
     * Constructor.
     */
    public MultipleCellsModel() {
        initializeCellLocations();

        // Hook up the clock.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add and position the cells.
        long entryTime = System.nanoTime();
        System.out.println( "Sytem time before adding and positioning cells: " + entryTime );
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
                placeCellInOpenLocation( newCell, cellList );
            }
            cellList.add( newCell );
        }
        System.out.println( "Duration of creation and placing: " + ( ( System.nanoTime() - entryTime ) / 1E9 ) );


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
    private void placeCellInOpenLocation( Cell cell, List<Cell> cellList ) {
        for ( int i = 0; i < (int) Math.ceil( Math.sqrt( cellList.size() ) ); i++ ) {
            double radius = ( i + 1 ) * Cell.DEFAULT_CELL_SIZE.getWidth() * ( positionRandomizer.nextDouble() / 2 + .75 );
            for ( int j = 0; j < radius * Math.PI / ( Cell.DEFAULT_CELL_SIZE.getHeight() * 2 ); j++ ) {
                double angle = positionRandomizer.nextDouble() * 2 * Math.PI;
                cell.setPosition( radius * Math.cos( angle ), radius * Math.sin( angle ) );
                boolean overlapDetected = false;
                for ( Cell existingCell : cellList ) {
                    if ( shapesOverlap( cell.getShape(), existingCell.getShape() ) ) {
                        overlapDetected = true;
                        break;
                    }
                }
                if ( overlapDetected == false ) {
                    // Found an open spot.
                    return;
                }
            }
        }
        System.out.println( "Warning: Exiting placement loop without having found open location." );
    }

    private static boolean shapesOverlap( Shape shape1, Shape shape2 ) {
        Area area1 = new Area( shape1 );
        Area area2 = new Area( shape2 );
        area1.intersect( area2 );
        return !area1.getBounds2D().isEmpty();
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

    private void initializeCellLocations() {
        assert cellLocations.size() == 0; // Should only be called once.

        // Transform for converting from unit-circle based locations to
        // locations that will work for the elliptical cells.
        AffineTransform transform = AffineTransform.getScaleInstance( Cell.DEFAULT_CELL_SIZE.getWidth(), Cell.DEFAULT_CELL_SIZE.getHeight() );

        // Set the first location to be at the origin.
        cellLocations.add( new Point2D.Double( 0, 0 ) );

        // Create the list of potential locations for cells.  This algorithm
        // is based on a unit circle, and the resulting positions are
        // transformed based on the dimensions of the cells.
        int layer = 1;
        int placedCells = 1;
        while ( placedCells < MAX_CELLS ) {
            List<Point2D> preTransformLocations = new ArrayList<Point2D>();
            int numCellsOnThisLayer = (int) Math.floor( layer * 2 * Math.PI );
            double angleIncrement = 2 * Math.PI / numCellsOnThisLayer;
            Vector2D nextLocation = new Vector2D();
            nextLocation.setMagnitudeAndAngle( layer, positionRandomizer.nextDouble() * 2 * Math.PI );
            for ( int i = 0; i < numCellsOnThisLayer && placedCells < MAX_CELLS; i++ ) {
                preTransformLocations.add( nextLocation.toPoint2D() );
                nextLocation.rotate( angleIncrement );
                placedCells++;
            }
            // Shuffle the locations.
            Collections.shuffle( preTransformLocations );
            // Transform the locations and add them to the final list.
            for ( Point2D preTransformLocation : preTransformLocations ) {
                cellLocations.add( transform.transform( preTransformLocation, null ) );
            }
            // Next layer.
            layer++;
        }
    }
}

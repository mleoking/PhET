// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.model;

import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.ShapeChangingModelElement;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model element that represents a cell in the multiple-cells tab.  The cell
 * has a shape, a protein level, and a number of parameters that control how
 * it synthesized a protein.  Only one protein is synthesized.
 *
 * @author John Blanco
 */
public class Cell extends ShapeChangingModelElement {

    // Bounding size for cells
    public static final Dimension2D DEFAULT_CELL_SIZE = new PDimension( 2E-6, 0.5E-6 ); // In meters.

    // Protein level at which the cell color starts to change.  This is meant
    // to make the cell act as though the protein being produced is florescent.
    public static final double PROTEIN_LEVEL_WHERE_COLOR_CHANGE_STARTS = 75;

    // Protein level at which the color change (towards the florescent color)
    // is complete.
    public static final double PROTEIN_LEVEL_WHERE_COLOR_CHANGE_COMPLETES = 200;

    // This is a separate object in which the protein synthesis is simulated.
    // The reason that this is broken out into a separate class is that it was
    // supplied by someone outside of the PhET project, and this keeps it
    // encapsulated and thus easier for the original author to help maintain.
    // TODO: I have no idea what the original ribosome count should be, so I'm just taking a wild guess here.
    private CellProteinSynthesisSimulator proteinSynthesisSimulator = new CellProteinSynthesisSimulator( 100 );

    // Property that indicates the current protein count in the cell.  This
    // should not be set by external users, only monitored.
    public Property<Integer> proteinCount = new Property<Integer>( 0 );

    /**
     * Constructor.
     *
     * @param seed - seed for random number generator, used to make the shape
     *             of the cell be somewhat unique.
     */
    public Cell( long seed ) {
        this( new Point2D.Double( 0, 0 ), 0, seed );
    }

    /**
     * Constructor.
     *
     * @param initialPosition - Initial location of this cell in model space.
     * @param seed            - Seed for the random number generator, used to give the
     *                        cell a somewhat unique shape.
     */
    public Cell( Point2D initialPosition, double rotationAngle, long seed ) {
        super( createShape( initialPosition, rotationAngle, seed ) );
    }

    // Static function for creating the shape of the cell.
    private static Shape createShape( Point2D initialPosition, double rotationAngle, long seed ) {
        return BioShapeUtils.createEColiLikeShape( initialPosition, DEFAULT_CELL_SIZE.getWidth(), DEFAULT_CELL_SIZE.getHeight(), rotationAngle, seed );
    }

    public void stepInTime( double dt ) {
        // TODO: Multiplying time step, because the example used a large number.  Need to talk to George E to figure out units.
        proteinSynthesisSimulator.stepInTime( dt * 1000 );
        proteinCount.set( proteinSynthesisSimulator.getProteinCount() );
    }

    //-------------------------------------------------------------------------
    // The following methods are essentially "pass through" methods to the
    // protein synthesis simulation.  This is kept separate for now.  At some
    // point, once the protein synthesis stuff is fully debugged, it may make
    // sense to pull the protein synthesis model into this class.
    //-------------------------------------------------------------------------

    public void setTranscriptionFactorCount( int tfCount ) {
        proteinSynthesisSimulator.setTranscriptionFactorCount( tfCount );
    }

    // TODO: Temp for debug, remove eventually.
    public int getTranscriptionFactorCount() {
        return proteinSynthesisSimulator.getTranscriptionFactorCount();
    }

    // TODO: Temp for debug, remove eventually.
    public void printCellDebugInfo() {
        proteinSynthesisSimulator.printDebugInfo();
    }


    public void setPolymeraseCount( int polymeraseCount ) {
        proteinSynthesisSimulator.setPolymeraseCount( polymeraseCount );
    }

    public void setGeneTranscriptionFactorAssociationRate( double newRate ) {
        proteinSynthesisSimulator.setGeneTranscriptionFactorAssociationRate( newRate );
    }

    public void setPolymeraseAssociationRate( double newRate ) {
        proteinSynthesisSimulator.setPolymeraseAssociationRate( newRate );
    }

    public void setRNARibosomeAssociationRate( double newRate ) {
        proteinSynthesisSimulator.setRNARibosomeAssociationRate( newRate );
    }

    public void setProteinDegradationRate( double newRate ) {
        proteinSynthesisSimulator.setProteinDegradationRate( newRate );
    }

    //----------- End of pass-through methods ---------------------------------

}

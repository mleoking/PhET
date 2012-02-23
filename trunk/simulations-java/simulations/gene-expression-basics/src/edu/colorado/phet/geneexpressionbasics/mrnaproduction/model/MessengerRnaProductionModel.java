// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MotionBounds;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.GeneA;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.GeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;

/**
 * Primary model for the manual gene expression tab.
 * <p/>
 * Dimensions in this model (and all sub-elements of the model) are in nano-
 * meters, i.e. 10E-9 meters.
 * <p/>
 * The point (0,0) in model space is at the leftmost edge of the DNA strand, and
 * at the vertical center of the strand.
 *
 * @author John Blanco
 */
public class MessengerRnaProductionModel extends GeneExpressionModel implements Resettable {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Stage size for the mobile biomolecules, which is basically the area in
    // which the molecules can move.  These are empirically determined such
    // that the molecules don't move off of the screen when looking at a given
    // gene.
    private static final double BIOMOLECULE_STAGE_WIDTH = 10000; // In picometers.
    private static final double BIOMOLECULE_STAGE_HEIGHT = 5250; // In picometers.

    // Length, in terms of base pairs, of the DNA molecule.
    private static final int NUM_BASE_PAIRS_ON_DNA_STRAND = 500;

    // Configurations for the transcriptions factors used within this model.
    public static final TranscriptionFactorConfig POSITIVE_TRANSCRIPTION_FACTOR_CONFIG = TranscriptionFactor.TRANSCRIPTION_FACTOR_CONFIG_GENE_1_POS;
    public static final TranscriptionFactorConfig NEGATIVE_TRANSCRIPTION_FACTOR_CONFIG = TranscriptionFactor.TRANSCRIPTION_FACTOR_CONFIG_GENE_1_NEG;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // DNA strand, which is where the genes reside, where the polymerase does
    // its transcription, and where a lot of the action takes place.
    protected final DnaMolecule dnaMolecule = new DnaMolecule( this,
                                                               NUM_BASE_PAIRS_ON_DNA_STRAND,
                                                               -NUM_BASE_PAIRS_ON_DNA_STRAND * DnaMolecule.DISTANCE_BETWEEN_BASE_PAIRS / 2 );

    // List of mobile biomolecules in the model, excluding mRNA.
    public final ObservableList<MobileBiomolecule> mobileBiomoleculeList = new ObservableList<MobileBiomolecule>();

    // List of mRNA molecules in the sim.  These are kept separate because they
    // are treated a bit differently than the other mobile biomolecules.
    public final ObservableList<MessengerRna> messengerRnaList = new ObservableList<MessengerRna>();

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // List of areas where biomolecules should not be allowed.  These are
    // generally populated by the view in order to keep biomolecules from
    // wandering over the tool boxes and such.
    private final List<Shape> offLimitsMotionSpaces = new ArrayList<Shape>();

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public MessengerRnaProductionModel() {
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add gene to DNA molecule.
        dnaMolecule.addGene( new GeneA( dnaMolecule, (int)Math.round( NUM_BASE_PAIRS_ON_DNA_STRAND * 0.45 ) ) );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }

    public DnaMolecule getDnaMolecule() {
        return dnaMolecule;
    }

    public void addMobileBiomolecule( final MobileBiomolecule mobileBiomolecule, boolean interactsWithDna ) {
        mobileBiomoleculeList.add( mobileBiomolecule );
        mobileBiomolecule.setMotionBounds( getMotionBounds( interactsWithDna ) );

        // Hook up an observer that will activate and deactivate placement
        // hints for this molecule.
        mobileBiomolecule.userControlled.addObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean isUserControlled, Boolean wasUserControlled ) {
                if ( isUserControlled ) {
                    dnaMolecule.activateHints( mobileBiomolecule );
                    for ( MessengerRna messengerRna : messengerRnaList ) {
                        messengerRna.activateHints( mobileBiomolecule );
                    }
                }
                else {
                    dnaMolecule.deactivateAllHints();
                    for ( MessengerRna messengerRna : messengerRnaList ) {
                        messengerRna.deactivateAllHints();
                    }
                }
            }
        } );

        // Hook up an observer that will remove this biomolecule from the
        // model if its existence strength reaches zero.
        mobileBiomolecule.existenceStrength.addObserver( new VoidFunction1<Double>() {
            public void apply( Double existenceStrength ) {
                if ( existenceStrength == 0 ) {
                    removeMobileBiomolecule( mobileBiomolecule );
                    mobileBiomolecule.existenceStrength.removeObserver( this );
                }
            }
        } );
    }

    /**
     * Get a list of all mobile biomolecules that overlap with the provided
     * shape.
     *
     * @param testShape - Shape, in model coordinate, to test for overlap.
     * @return List of molecules that overlap with the provided shape.
     */
    public List<MobileBiomolecule> getOverlappingBiomolecules( Shape testShape ) {

        List<MobileBiomolecule> overlappingBiomolecules = new ArrayList<MobileBiomolecule>();

        // Since it is computationally expensive to test overlap with every
        // shape, we do a fast bounds test first, and then the more expensive
        // test when necessary.
        Rectangle2D testShapeBounds = testShape.getBounds2D();
        for ( MobileBiomolecule mobileBiomolecule : mobileBiomoleculeList ) {
            if ( mobileBiomolecule.getShape().getBounds2D().intersects( testShapeBounds ) ) {
                // Bounds overlap, see if actual shapes overlap.
                Area testShapeArea = new Area( testShape );
                Area biomoleculeArea = new Area( mobileBiomolecule.getShape() );
                biomoleculeArea.intersect( testShapeArea );
                if ( !biomoleculeArea.isEmpty() ) {
                    // The biomolecule overlaps with the test shape, so add it
                    // to the list.
                    overlappingBiomolecules.add( mobileBiomolecule );
                }
            }
        }

        return overlappingBiomolecules;
    }

    public void removeMobileBiomolecule( MobileBiomolecule mobileBiomolecule ) {
        mobileBiomoleculeList.remove( mobileBiomolecule );
    }

    public void addMessengerRna( final MessengerRna messengerRna ) {
        messengerRnaList.add( messengerRna );
    }

    @Override public void removeMessengerRna( MessengerRna messengerRnaBeingDestroyed ) {
        messengerRnaList.remove( messengerRnaBeingDestroyed );
    }

    @Override public List<MessengerRna> getMessengerRnaList() {
        return messengerRnaList;
    }

    public void reset() {
        mobileBiomoleculeList.clear();
        messengerRnaList.clear();
        dnaMolecule.reset();
    }

    /**
     * Add a space where the biomolecules should not be allowed to wander. This
     * is generally used by the view to prevent biomolecules from moving over
     * tool boxes and such.
     *
     * @param newOffLimitsSpace
     */
    public void addOffLimitsMotionSpace( Shape newOffLimitsSpace ) {
        for ( Shape offLimitsMotionSpace : offLimitsMotionSpaces ) {
            if ( offLimitsMotionSpace.equals( newOffLimitsSpace ) ) {
                // An equivalent space already exists, so don't bother adding
                // this one.
                return;
            }
        }
        // Add the new one to the list.
        offLimitsMotionSpaces.add( newOffLimitsSpace );
    }

    private void stepInTime( double dt ) {
        for ( MobileBiomolecule mobileBiomolecule : new ArrayList<MobileBiomolecule>( mobileBiomoleculeList ) ) {
            mobileBiomolecule.stepInTime( dt );
        }
        for ( MessengerRna messengerRna : messengerRnaList ) {
            messengerRna.stepInTime( dt );
        }
        dnaMolecule.stepInTime( dt );
    }

    /**
     * Get the motion bounds for the biomolecules that are interacting with
     * this DNA strand.
     *
     * @param includeDnaStrand - Flag to signify whether these bounds should
     *                         include the DNA strand.  This should be true for molecules that
     *                         interact with the DNA, false for those that don't.
     */
    public MotionBounds getMotionBounds( boolean includeDnaStrand ) {

        // The bottom of the bounds depends on whether or not the DNA strand
        // is included.
        double bottomYPos = includeDnaStrand ? DnaMolecule.Y_POS - DnaMolecule.DIAMETER * 3 : DnaMolecule.Y_POS + DnaMolecule.DIAMETER / 2;

        // Get the nominal bounds.
        Area bounds = new Area( new Rectangle2D.Double( -BIOMOLECULE_STAGE_WIDTH / 2,
                                                        bottomYPos,
                                                        BIOMOLECULE_STAGE_WIDTH,
                                                        BIOMOLECULE_STAGE_HEIGHT ) );

        // Subtract off any overlapping areas.
        for ( Shape offLimitMotionSpace : offLimitsMotionSpaces ) {
            if ( bounds.intersects( offLimitMotionSpace.getBounds2D() ) ) {
                bounds.subtract( new Area( offLimitMotionSpace ) );
            }
        }

        return new MotionBounds( bounds );
    }
}

// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MotionBounds;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.GeneA;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.GeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.RnaPolymerase;
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
    // that the molecules move around primarily in the viewable area, but are
    // able to move somewhat outside the area too.
    private static final double BIOMOLECULE_STAGE_WIDTH = 5000; // In picometers.
    private static final double BIOMOLECULE_STAGE_HEIGHT = 2600; // In picometers.

    // Length, in terms of base pairs, of the DNA molecule.
    private static final int NUM_BASE_PAIRS_ON_DNA_STRAND = 500;

    // Configurations for the transcriptions factors used within this model.
    public static final TranscriptionFactorConfig POSITIVE_TRANSCRIPTION_FACTOR_CONFIG = TranscriptionFactor.TRANSCRIPTION_FACTOR_CONFIG_GENE_1_POS;
    public static final TranscriptionFactorConfig NEGATIVE_TRANSCRIPTION_FACTOR_CONFIG = TranscriptionFactor.TRANSCRIPTION_FACTOR_CONFIG_GENE_1_NEG;

    // Maximum number of transcription factor molecules.  The pertains to both
    // positive and negative transcription factors.
    public static final int MAX_TRANSCRIPTION_FACTOR_COUNT = 30;

    // Number of RNA polymerase molecules present.
    public static final int RNA_POLYMERASE_COUNT = 20;

    // etc.
    private static final Random RAND = new Random();

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

    // Properties that control the quantity of transcription factors.
    public final IntegerProperty positiveTranscriptionFactorCount = new IntegerProperty( 0 ){{
        addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer count ) {
                setTranscriptionFactorCount( TranscriptionFactor.TRANSCRIPTION_FACTOR_CONFIG_GENE_1_POS, count );
            }
        } );
    }};

    public final IntegerProperty negativeTranscriptionFactorCount = new IntegerProperty( 0 );

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

        // Create the clock and hook it up.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add the gene to the DNA molecule.  Only one gene in this model.
        dnaMolecule.addGene( new GeneA( dnaMolecule, (int) Math.round( NUM_BASE_PAIRS_ON_DNA_STRAND * 0.45 ) ) );

        // Add the polymerase.  This doesn't come and go, the concentration
        // of these remains constant in this model.
        for ( int i = 0; i < RNA_POLYMERASE_COUNT; i++ ){
            addMobileBiomolecule( new RnaPolymerase( this, generateInitialLocation() ), true );
        }
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

        // Set the motion bounds such that the molecules move around above and
        // on top of the DNA.
        mobileBiomolecule.setMotionBounds( getMotionBounds( mobileBiomolecule ) );

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

    // Generate a random initial location for a biomolecule.
    private Point2D generateInitialLocation() {
        double xPos = BIOMOLECULE_STAGE_WIDTH * ( -0.5 + RAND.nextDouble() );
        double yPos = DnaMolecule.Y_POS + BIOMOLECULE_STAGE_HEIGHT * RAND.nextDouble();
        return new Point2D.Double( xPos, yPos );
    }

    private void setTranscriptionFactorCount( TranscriptionFactorConfig tcConfig, int targetCount ) {
        // Count the transcription factors that match this configuration.
        int currentCount = 0;
        for ( MobileBiomolecule mobileBiomolecule : mobileBiomoleculeList ) {
            if ( mobileBiomolecule instanceof TranscriptionFactor ){
                if (((TranscriptionFactor)mobileBiomolecule).getConfig().equals( tcConfig )){
                    currentCount++;
                }
            }
        }

        if ( targetCount > currentCount ){
            // Add some.
            for ( int i = currentCount; i < targetCount; i++ ){
                addMobileBiomolecule( new TranscriptionFactor( this, tcConfig, generateInitialLocation() ), true );
            }
        }
        else if ( targetCount < currentCount ){
            // Remove some.
            for ( MobileBiomolecule mobileBiomolecule : new ArrayList<MobileBiomolecule>( mobileBiomoleculeList ) ) {
                if ( mobileBiomolecule instanceof TranscriptionFactor ){
                    if (((TranscriptionFactor)mobileBiomolecule).getConfig().equals( tcConfig )){
                        removeMobileBiomolecule( mobileBiomolecule );
                        currentCount--;
                        if ( currentCount == targetCount ){
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the motion bounds for the biomolecules in this model.  These
     * bounds are set up to stay above and around the DNA molecule, and to
     * go in and out of the area that the user is likely to be viewing.
     * However, there is no direct interaction with the view to determine the
     * bounds - they are fixed based on the expected view.
     */
    public MotionBounds getMotionBounds( MobileBiomolecule mobileBiomolecule ) {

        // The bottom of the bounds depends on the size of the biomolecule.  A
        // little bit of margin is added.
        double bottomYPos = DnaMolecule.Y_POS - mobileBiomolecule.getShape().getBounds2D().getHeight() / 2 * 1.1;

        // Get the nominal bounds.
        Area bounds = new Area( new Rectangle2D.Double( -BIOMOLECULE_STAGE_WIDTH / 2,
                                                        bottomYPos,
                                                        BIOMOLECULE_STAGE_WIDTH,
                                                        BIOMOLECULE_STAGE_HEIGHT ) );

        // Subtract off any off limits areas.
        for ( Shape offLimitMotionSpace : offLimitsMotionSpaces ) {
            if ( bounds.intersects( offLimitMotionSpace.getBounds2D() ) ) {
                bounds.subtract( new Area( offLimitMotionSpace ) );
            }
        }

        return new MotionBounds( bounds );
    }
}

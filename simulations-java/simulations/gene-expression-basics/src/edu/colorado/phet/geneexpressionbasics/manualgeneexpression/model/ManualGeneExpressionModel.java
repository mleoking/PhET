// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MotionBounds;

import static edu.colorado.phet.common.phetcommon.math.MathUtil.clamp;

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
public class ManualGeneExpressionModel extends GeneExpressionModel implements Resettable {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Stage size for the mobile biomolecules, which is basically the area in
    // which the molecules can move.  These are empirically determined such
    // that the molecules don't move off of the screen when looking at a given
    // gene.
    private static final double BIOMOLECULE_STAGE_WIDTH = 10000; // In picometers.
    private static final double BIOMOLECULE_STAGE_HEIGHT = 6700; // In picometers.

    // Size of the DNA strand.
    private static final int NUM_BASE_PAIRS_ON_DNA_STRAND = 2000;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // DNA strand, which is where the genes reside, where the polymerase does
    // its transcription, and where a lot of the action takes place.
    private final DnaMolecule dnaMolecule;

    // List of mobile biomolecules in the model, excluding mRNA.
    public final ObservableList<MobileBiomolecule> mobileBiomoleculeList = new ObservableList<MobileBiomolecule>();

    // List of mRNA molecules in the sim.  These are kept separate because they
    // are treated a bit differently than the other mobile biomolecules.
    public final ObservableList<MessengerRna> messengerRnaList = new ObservableList<MessengerRna>();

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // The gene that the user is focusing on, other gene activity is
    // suspended.  Start with the 0th gene in the DNA (leftmost).
    public final Property<Gene> activeGene;

    // Properties that keep track of whether the first or last gene is
    // currently active, which means that the user is viewing it.
    public final ObservableProperty<Boolean> isFirstGeneActive;
    public final ObservableProperty<Boolean> isLastGeneActive;

    // List of areas where biomolecules should not be allowed.  These are
    // generally populated by the view in order to keep biomolecules from
    // wandering over the tool boxes and such.
    private final List<Shape> offLimitsMotionSpaces = new ArrayList<Shape>();

    // Properties that track how many of the various proteins have been collected.
    public final Property<Integer> proteinACollected = new Property<Integer>( 0 );
    public final Property<Integer> proteinBCollected = new Property<Integer>( 0 );
    public final Property<Integer> proteinCCollected = new Property<Integer>( 0 );

    // Map of the protein collection count properties to the protein types,
    // used to obtain the count property based on the type of protein.
    private final Map<Class<? extends Protein>, Property<Integer>> mapProteinClassToCollectedCount = new HashMap<Class<? extends Protein>, Property<Integer>>() {{
        put( ProteinA.class, proteinACollected );
        put( ProteinB.class, proteinBCollected );
        put( ProteinC.class, proteinCCollected );
    }};

    // Rectangle that describes the "protein capture area".  When a protein is
    // dropped by the user over this area, it is considered to be captured.
    private final Rectangle2D proteinCaptureArea = new Rectangle2D.Double();

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public ManualGeneExpressionModel() {
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Initialize the DNA molecule.
        dnaMolecule = new DnaMolecule( this, NUM_BASE_PAIRS_ON_DNA_STRAND, -NUM_BASE_PAIRS_ON_DNA_STRAND * DnaMolecule.DISTANCE_BETWEEN_BASE_PAIRS / 4, false );
        dnaMolecule.addGene( new GeneA( dnaMolecule, NUM_BASE_PAIRS_ON_DNA_STRAND / 4 - GeneA.NUM_BASE_PAIRS / 2 ) );
        dnaMolecule.addGene( new GeneB( dnaMolecule, NUM_BASE_PAIRS_ON_DNA_STRAND / 2 - GeneB.NUM_BASE_PAIRS / 2 ) );
        dnaMolecule.addGene( new GeneC( dnaMolecule, NUM_BASE_PAIRS_ON_DNA_STRAND * 3 / 4 - GeneC.NUM_BASE_PAIRS / 2 ) );

        // Initialize variables that are dependent upon the DNA.
        activeGene = new Property<Gene>( dnaMolecule.getGenes().get( 0 ) );
        isFirstGeneActive = activeGene.valueEquals( dnaMolecule.getGenes().get( 0 ) );
        isLastGeneActive = activeGene.valueEquals( dnaMolecule.getLastGene() );
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

    public void previousGene() {
        switchToGeneRelative( -1 );
    }

    public void nextGene() {
        switchToGeneRelative( +1 );
    }

    public void setProteinCaptureArea( Rectangle2D newCaptureAreaBounds ) {
        proteinCaptureArea.setFrame( newCaptureAreaBounds );
    }

    public Property<Integer> getCollectedCounterForProteinType( Class<? extends Protein> proteinType ) {
        assert mapProteinClassToCollectedCount.containsKey( proteinType );
        return mapProteinClassToCollectedCount.get( proteinType );
    }

    private void switchToGeneRelative( int i ) {
        final ArrayList<Gene> genes = dnaMolecule.getGenes();
        int index = clamp( 0, genes.indexOf( activeGene.get() ) + i, genes.size() - 1 );
        activeGene.set( genes.get( index ) );
    }

    private void activateGene( int i ) {
        activeGene.set( dnaMolecule.getGenes().get( i ) );
    }

    public void addMobileBiomolecule( final MobileBiomolecule mobileBiomolecule ) {
        mobileBiomoleculeList.add( mobileBiomolecule );
        mobileBiomolecule.setMotionBounds( getBoundsForActiveGene() );

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
                    if ( wasUserControlled ) {
                        // The user dropped this biomolecule.
                        if ( proteinCaptureArea.contains( mobileBiomolecule.getPosition() ) && mobileBiomolecule instanceof Protein ) {
                            // The user has dropped this protein in the
                            // capture area.  So, like, capture it.
                            captureProtein( (Protein) mobileBiomolecule );
                        }
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

    // Capture the specified protein, which means that it is actually removed
    // from the model and the associated capture count property is incremented.
    private void captureProtein( Protein protein ) {
        if ( protein instanceof ProteinA ) {
            proteinACollected.set( proteinACollected.get() + 1 );
        }
        if ( protein instanceof ProteinB ) {
            proteinBCollected.set( proteinBCollected.get() + 1 );
        }
        if ( protein instanceof ProteinC ) {
            proteinCCollected.set( proteinCCollected.get() + 1 );
        }
        mobileBiomoleculeList.remove( protein );
    }

    public int getProteinCount( Class<? extends Protein> proteinClass ) {
        int count = 0;
        for ( MobileBiomolecule mobileBiomolecule : mobileBiomoleculeList ) {
            if ( mobileBiomolecule.getClass() == proteinClass ) {
                count++;
            }
        }
        return count;
    }

    public void removeMobileBiomolecule( MobileBiomolecule mobileBiomolecule ) {
        mobileBiomoleculeList.remove( mobileBiomolecule );
    }

    public void addMessengerRna( final MessengerRna messengerRna ) {
        messengerRnaList.add( messengerRna );
        messengerRna.setMotionBounds( getBoundsForActiveGene() );
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
        proteinACollected.reset();
        proteinBCollected.reset();
        proteinCCollected.reset();
        activateGene( 0 );
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
     * Get the motion bounds for any biomolecule that is going to be associated
     * with the currently active gene.  This is used to keep the biomolecules
     * from wandering outside of the area that the user can see.
     */
    public MotionBounds getBoundsForActiveGene() {

        // The bottom bounds are intended to be roughly at the bottom of the
        // viewport.  The value was empirically determined.
        double bottomYPos = DnaMolecule.Y_POS - 2000;

        // Get the nominal bounds for this gene.
        Area bounds = new Area( new Rectangle2D.Double( activeGene.get().getCenterX() - BIOMOLECULE_STAGE_WIDTH / 2,
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

// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MotionBounds;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
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

    // Length, in terms of base pairs, of the DNA molecule.
    private static final int NUM_BASE_PAIRS_ON_DNA_STRAND = 500;

    // Configurations for the transcriptions factors used within this model.
    public static final TranscriptionFactorConfig POSITIVE_TRANSCRIPTION_FACTOR_CONFIG = TranscriptionFactor.TRANSCRIPTION_FACTOR_CONFIG_GENE_1_POS;
    public static final TranscriptionFactorConfig NEGATIVE_TRANSCRIPTION_FACTOR_CONFIG = TranscriptionFactor.TRANSCRIPTION_FACTOR_CONFIG_GENE_1_NEG;

    // Maximum number of transcription factor molecules.  The pertains to both
    // positive and negative transcription factors.
    public static final int MAX_TRANSCRIPTION_FACTOR_COUNT = 8;

    // Number of RNA polymerase molecules present.
    public static final int RNA_POLYMERASE_COUNT = 7;

    // etc.
    private static final Random RAND = new Random();

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // DNA strand, which is where the genes reside, where the polymerase does
    // its transcription, and where a lot of the action takes place.
    protected final DnaMolecule dnaMolecule = new DnaMolecule( this,
                                                               NUM_BASE_PAIRS_ON_DNA_STRAND,
                                                               -NUM_BASE_PAIRS_ON_DNA_STRAND * DnaMolecule.DISTANCE_BETWEEN_BASE_PAIRS / 2,
                                                               true );

    // List of mobile biomolecules in the model, excluding mRNA.
    public final ObservableList<MobileBiomolecule> mobileBiomoleculeList = new ObservableList<MobileBiomolecule>();

    // List of mRNA molecules in the sim.  These are kept separate because they
    // are treated a bit differently than the other mobile biomolecules.
    public final ObservableList<MessengerRna> messengerRnaList = new ObservableList<MessengerRna>();

    // Properties that control the quantity of transcription factors.
    public final IntegerProperty positiveTranscriptionFactorCount = new IntegerProperty( 0 ) {{
        addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer count ) {
                setTranscriptionFactorCount( TranscriptionFactor.TRANSCRIPTION_FACTOR_CONFIG_GENE_1_POS, count );
            }
        } );
    }};

    public final IntegerProperty negativeTranscriptionFactorCount = new IntegerProperty( 0 ) {{
        addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer count ) {
                setTranscriptionFactorCount( TranscriptionFactor.TRANSCRIPTION_FACTOR_CONFIG_GENE_1_NEG, count );
            }
        } );
    }};

    // For debug - node that represents molecule motion bounds.
    public final Shape moleculeMotionBounds;

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // List of areas where biomolecules should not be allowed.  These are
    // generally populated by the view in order to keep biomolecules from
    // wandering over the tool boxes and such.
    private final List<Shape> offLimitsMotionSpaces = new ArrayList<Shape>();

    // The one gene that is on this DNA strand.
    private final Gene gene;

    // The bounds within which polymerase is moved when it is recycled.
    private final Rectangle2D polymeraseRecycleReturnBounds;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public MessengerRnaProductionModel() {

        // Hook up the clock.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add the gene to the DNA molecule.  There is only one gene in this model.
        gene = new GeneA( dnaMolecule, (int) Math.round( NUM_BASE_PAIRS_ON_DNA_STRAND * 0.45 ) );
        dnaMolecule.addGene( gene );

        // Set up a node that depicts motion bounds.  This is for debug.
        moleculeMotionBounds = getMotionBounds( new RnaPolymerase( this, new Point2D.Double( 0, 0 ) ) ).getBounds();

        // Set up the area where RNA polymerase goes when it is recycled.
        // This is near the beginning of the transcribed region in order to
        // make transcription more likely to occur.
        Rectangle2D polymeraseSize = new RnaPolymerase().getShape().getBounds2D();
        double recycleZoneCenterX = dnaMolecule.getBasePairXOffsetByIndex( dnaMolecule.getGenes().get( 0 ).getTranscribedRegion().getMin() ) + ( RAND.nextDouble() - 0.5 ) * 2000;
        double recycleZoneCenterY = DnaMolecule.Y_POS + polymeraseSize.getHeight() * 1.5;
        polymeraseRecycleReturnBounds = new Rectangle2D.Double( recycleZoneCenterX - polymeraseSize.getWidth() * 3,
                                                                recycleZoneCenterY - polymeraseSize.getHeight() * 0.6,
                                                                polymeraseSize.getWidth() * 6,
                                                                polymeraseSize.getHeight() * 1.2 );

        // Reset this model in order to set initial state.
        reset();
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
                }
                else {
                    dnaMolecule.deactivateAllHints();
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

        // Since this will never be translated in this model, make it fade
        // away once it is formed.
        messengerRna.setFadeAwayWhenFormed( true );

        // Remove this from the model once its existence strength reaches
        // zero, which it will do since it is fading out.
        messengerRna.existenceStrength.addObserver( new VoidFunction1<Double>() {
            public void apply( Double existenceStrength ) {
                if ( existenceStrength <= 0 ) {
                    // It's "gone", so remove it from the model.
                    messengerRnaList.remove( messengerRna );
                    messengerRna.existenceStrength.removeObserver( this );
                }
            }
        } );
    }

    @Override public void removeMessengerRna( MessengerRna messengerRnaBeingDestroyed ) {
        messengerRnaList.remove( messengerRnaBeingDestroyed );
    }

    @Override public List<MessengerRna> getMessengerRnaList() {
        return messengerRnaList;
    }

    public void reset() {
        positiveTranscriptionFactorCount.reset();
        negativeTranscriptionFactorCount.reset();
        mobileBiomoleculeList.clear();
        messengerRnaList.clear();
        dnaMolecule.reset();

        // Add the polymerase molecules.  These don't come and go, the
        // concentration of these remains constant in this model.
        for ( int i = 0; i < RNA_POLYMERASE_COUNT; i++ ) {
            RnaPolymerase rnaPolymerase = new RnaPolymerase( this, new Point2D.Double( 0, 0 ) );
            rnaPolymerase.setPosition3D( generateInitialLocation3D( rnaPolymerase ) );
            rnaPolymerase.set3DMotionEnabled( true );
            rnaPolymerase.setRecycleMode( true );
            rnaPolymerase.setRecycleReturnZone( polymeraseRecycleReturnBounds );
            addMobileBiomolecule( rnaPolymerase, true );
        }
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

        // Step all the contained biomolecules.
        for ( MobileBiomolecule mobileBiomolecule : new ArrayList<MobileBiomolecule>( mobileBiomoleculeList ) ) {
            mobileBiomolecule.stepInTime( dt );
        }
        for ( MessengerRna messengerRna : new ArrayList<MessengerRna>( messengerRnaList ) ) {
            messengerRna.stepInTime( dt );
        }
        dnaMolecule.stepInTime( dt );
    }

    // Generate a random, valid, initial location, including the Z dimension.
    private Point3D generateInitialLocation3D( MobileBiomolecule biomolecule ) {
        double xMin = moleculeMotionBounds.getBounds2D().getMinX() + biomolecule.getShape().getBounds2D().getWidth() / 2;
        double yMin = moleculeMotionBounds.getBounds2D().getMinY() + biomolecule.getShape().getBounds2D().getHeight() / 2;
        double xMax = moleculeMotionBounds.getBounds2D().getMaxX() - biomolecule.getShape().getBounds2D().getWidth() / 2;
        double yMax = moleculeMotionBounds.getBounds2D().getMaxY() - biomolecule.getShape().getBounds2D().getHeight() / 2;
        double xPos = xMin + RAND.nextDouble() * ( xMax - xMin );
        double yPos = yMin + RAND.nextDouble() * ( yMax - yMin );
        double zPos = -RAND.nextDouble(); // Valid z values are from -1 to 0.
        return new Point3D.Double( xPos, yPos, zPos );
    }

    private void setTranscriptionFactorCount( TranscriptionFactorConfig tcConfig, int targetCount ) {
        // Count the transcription factors that match this configuration.
        int currentLevel = 0;
        for ( MobileBiomolecule mobileBiomolecule : mobileBiomoleculeList ) {
            if ( mobileBiomolecule instanceof TranscriptionFactor && ( (TranscriptionFactor) mobileBiomolecule ).getConfig().equals( tcConfig ) ) {
                currentLevel++;
            }
        }

        if ( targetCount > currentLevel ) {
            // Add some.
            for ( int i = currentLevel; i < targetCount; i++ ) {
                TranscriptionFactor transcriptionFactor = new TranscriptionFactor( this, tcConfig, new Point2D.Double( 0, 0 ) );
                transcriptionFactor.setPosition3D( generateInitialLocation3D( transcriptionFactor ) );
                transcriptionFactor.set3DMotionEnabled( true );
                addMobileBiomolecule( transcriptionFactor, true );
            }
        }
        else if ( targetCount < currentLevel ) {
            // Remove some.
            for ( MobileBiomolecule mobileBiomolecule : new ArrayList<MobileBiomolecule>( mobileBiomoleculeList ) ) {
                if ( mobileBiomolecule instanceof TranscriptionFactor ) {
                    if ( ( (TranscriptionFactor) mobileBiomolecule ).getConfig().equals( tcConfig ) ) {
                        // Remove this one.
                        mobileBiomolecule.forceDetach();
                        removeMobileBiomolecule( mobileBiomolecule );
                        currentLevel--;
                        if ( currentLevel == targetCount ) {
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
        double minY = DnaMolecule.Y_POS - mobileBiomolecule.getShape().getBounds2D().getHeight() / 2 * 1.4;

        // The max Y position is set to make it so that molecules can move
        // outside of the view port, but not way outside.  Its value was
        // empirically determined.
        double maxY = minY + 1500;

        // Figure out the X bounds based on the length of the gene.  This
        // extends a little less in the -x direction than in the +x, since the
        // beginning of the gene is in the center of the view port.
        double minX = gene.getStartX() - gene.getTranscribedRegionLength() / 2;
        double maxX = gene.getEndX() + 400; // Needs to be long enough to allow the polymerase to get to the end.

        // Get the nominal bounds.
        Area bounds = new Area( new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY ) );

        // Subtract off any off limits areas.
        for ( Shape offLimitMotionSpace : offLimitsMotionSpaces ) {
            if ( bounds.intersects( offLimitMotionSpace.getBounds2D() ) ) {
                bounds.subtract( new Area( offLimitMotionSpace ) );
            }
        }

        return new MotionBounds( bounds );
    }
}

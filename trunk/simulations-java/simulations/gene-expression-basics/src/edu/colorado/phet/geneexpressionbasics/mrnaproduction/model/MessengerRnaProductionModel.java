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
    private static final int RNA_POLYMERASE_COUNT = 7;

    // etc.
    private static final Random RAND = new Random();

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // DNA strand, which is where the genes reside, where the polymerase does
    // its transcription, and where a lot of the action takes place.
    private final DnaMolecule dnaMolecule = new DnaMolecule( this,
                                                             NUM_BASE_PAIRS_ON_DNA_STRAND,
                                                             -NUM_BASE_PAIRS_ON_DNA_STRAND * DnaMolecule.DISTANCE_BETWEEN_BASE_PAIRS / 2,
                                                             true );

    // The one gene that is on this DNA strand.
    private final Gene gene;

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

    // Motion bounds for the mobile biomolecules.
    public final MotionBounds moleculeMotionBounds;

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // List of areas where biomolecules should not be allowed.  These are
    // generally populated by the view in order to keep biomolecules from
    // wandering over the tool boxes and such.
    private final List<Shape> offLimitsMotionSpaces = new ArrayList<Shape>();

    // The bounds within which polymerase may be moved when recycled.
    private final Rectangle2D aboveDnaPolymeraseReturnBounds;
    private final Rectangle2D belowDnaPolymeraseReturnBounds;

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

        // Set up the motion bounds for the biomolecules.
        {
            // The bottom of the bounds, based off center point of the DNA
            // molecule.  Offset was empirically determined.
            double minY = DnaMolecule.Y_POS - 1200;

            // The max Y position is set to make it so that molecules can move
            // outside of the view port, but not way outside.  Its value was
            // empirically determined.
            double maxY = DnaMolecule.Y_POS + 1100;

            // Figure out the X bounds based on the length of the gene.  This
            // extends a little less in the -x direction than in the +x, since the
            // beginning of the gene is in the center of the view port.
            double minX = gene.getStartX() - 1300;
            double maxX = gene.getEndX() + 400; // Needs to be long enough to allow the polymerase to get to the end.

            // Create the nominal rectangular bounds.
            Area bounds = new Area( new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY ) );

            // Subtract off any off limits areas.
            for ( Shape offLimitMotionSpace : offLimitsMotionSpaces ) {
                if ( bounds.intersects( offLimitMotionSpace.getBounds2D() ) ) {
                    bounds.subtract( new Area( offLimitMotionSpace ) );
                }
            }
            moleculeMotionBounds = new MotionBounds( bounds );
        }

        // Set up the area where RNA polymerase goes when it is recycled.
        // This is near the beginning of the transcribed region in order to
        // make transcription more likely to occur.
        Rectangle2D polymeraseSize = new RnaPolymerase().getShape().getBounds2D();
        double recycleZoneCenterX = dnaMolecule.getBasePairXOffsetByIndex( dnaMolecule.getGenes().get( 0 ).getTranscribedRegion().getMin() ) + ( RAND.nextDouble() - 0.5 ) * 2000;
        double recycleZoneHeight = polymeraseSize.getHeight() * 1.2;
        double recycleZoneWidth = polymeraseSize.getWidth() * 4;
        aboveDnaPolymeraseReturnBounds = new Rectangle2D.Double( recycleZoneCenterX - polymeraseSize.getWidth() * 2,
                                                                 DnaMolecule.Y_POS + polymeraseSize.getHeight(),
                                                                 recycleZoneWidth,
                                                                 recycleZoneHeight );
        belowDnaPolymeraseReturnBounds = new Rectangle2D.Double( recycleZoneCenterX - polymeraseSize.getWidth() * 2,
                                                                 DnaMolecule.Y_POS - polymeraseSize.getHeight() - recycleZoneHeight,
                                                                 recycleZoneWidth,
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

    public void addMobileBiomolecule( final MobileBiomolecule mobileBiomolecule ) {
        mobileBiomoleculeList.add( mobileBiomolecule );

        // Set the motion bounds such that the molecules move around above and
        // on top of the DNA.
        mobileBiomolecule.setMotionBounds( moleculeMotionBounds );

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
        gene.getPolymeraseAffinityProperty().reset();
        gene.getTranscriptionFactorAffinityProperty( POSITIVE_TRANSCRIPTION_FACTOR_CONFIG ).reset();
        gene.getTranscriptionFactorAffinityProperty( NEGATIVE_TRANSCRIPTION_FACTOR_CONFIG ).reset();

        // Add the polymerase molecules.  These don't come and go, the
        // concentration of these remains constant in this model.
        for ( int i = 0; i < RNA_POLYMERASE_COUNT; i++ ) {
            RnaPolymerase rnaPolymerase = new RnaPolymerase( this, new Point2D.Double( 0, 0 ) );
            rnaPolymerase.setPosition3D( generateInitialLocation3D( rnaPolymerase ) );
            rnaPolymerase.set3DMotionEnabled( true );
            rnaPolymerase.setRecycleMode( true );
            rnaPolymerase.addRecycleReturnZone( aboveDnaPolymeraseReturnBounds );
            rnaPolymerase.addRecycleReturnZone( belowDnaPolymeraseReturnBounds );
            addMobileBiomolecule( rnaPolymerase );
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
        double xMin = moleculeMotionBounds.getBounds().getBounds2D().getMinX() + biomolecule.getShape().getBounds2D().getWidth() / 2;
        double yMin = moleculeMotionBounds.getBounds().getBounds2D().getMinY() + biomolecule.getShape().getBounds2D().getHeight() / 2;
        double xMax = moleculeMotionBounds.getBounds().getBounds2D().getMaxX() - biomolecule.getShape().getBounds2D().getWidth() / 2;
        double yMax = moleculeMotionBounds.getBounds().getBounds2D().getMaxY() - biomolecule.getShape().getBounds2D().getHeight() / 2;
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
                addMobileBiomolecule( transcriptionFactor );
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
}

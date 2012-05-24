// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;
import edu.colorado.phet.geneexpressionbasics.common.model.WindingBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.MessengerRnaAttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MessengerRnaNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents messenger ribonucleic acid, or mRNA, in the model.
 * This class is fairly complex, due to the need for mRNA to wind up and
 * unwind as it is transcribed, translated, and destroyed.
 *
 * @author John Blanco
 */
public class MessengerRna extends WindingBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Length of the "leader segment", which is the portion of the mRNA that
    // sticks out on the upper left side so that a ribosome can be attached.
    public static final double LEADER_LENGTH = INTER_POINT_DISTANCE * 2;

    // Distance within which this will connect to a ribosome.
    private static final double RIBOSOME_CONNECTION_DISTANCE = 200; // picometers
    private static final double MRNA_DESTROYER_CONNECT_DISTANCE = 200; // picometers

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    public final PlacementHint ribosomePlacementHint;
    public final PlacementHint mRnaDestroyerPlacementHint;

    // Externally visible indicator for whether this mRNA is being synthesized.
    // Assumes that it is being synthesized when created.
    public final BooleanProperty beingSynthesized = new BooleanProperty( true );

    // Map from ribosomes to the shape segment to which they are attached.
    private final Map<Ribosome, ShapeSegment> mapRibosomeToShapeSegment = new HashMap<Ribosome, ShapeSegment>();

    // mRNA destroyer that is destroying this mRNA.  Null until and unless
    // destruction has begun.
    private MessengerRnaDestroyer messengerRnaDestroyer = null;

    // Shape segment where the mRNA destroyer is connected.  This is null until
    // and unless destruction has begun.
    private ShapeSegment segmentWhereDestroyerConnects = null;

    // Protein prototype, used to keep track of protein that should be
    // synthesized from this particular strand of mRNA.
    private final Protein proteinPrototype;

    // Local reference to the non-generic state machine used by this molecule.
    private final MessengerRnaAttachmentStateMachine mRnaAttachmentStateMachine;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.  This creates the mRNA as a single point, with the intention
     * of growing it.
     *
     * @param position
     */
    public MessengerRna( final GeneExpressionModel model, Protein proteinPrototype, Point2D position ) {
        super( model, new DoubleGeneralPath( position ).getGeneralPath(), position );
        this.proteinPrototype = proteinPrototype;
        mRnaAttachmentStateMachine = (MessengerRnaAttachmentStateMachine) super.attachmentStateMachine;

        // Add the first segment to the shape segment list.  This segment will
        // contain the "leader" for the mRNA.
        shapeSegments.add( new ShapeSegment.FlatSegment( position ) {{
            setCapacity( LEADER_LENGTH );
        }} );

        // Add the placement hints for the locations where the user can attach
        // a ribosome or an mRNA destroyer.
        ribosomePlacementHint = new PlacementHint( new Ribosome( model ) );
        mRnaDestroyerPlacementHint = new PlacementHint( new MessengerRnaDestroyer( model ) );
        shapeProperty.addObserver( new SimpleObserver() {
            public void update() {
                // This hint always sits at the beginning of the RNA strand.
                ImmutableVector2D currentMRnaFirstPointPosition = new ImmutableVector2D( firstShapeDefiningPoint.getPosition() );
                ribosomePlacementHint.setPosition( currentMRnaFirstPointPosition.getSubtractedInstance( Ribosome.OFFSET_TO_TRANSLATION_CHANNEL_ENTRANCE ).toPoint2D() );
                mRnaDestroyerPlacementHint.setPosition( currentMRnaFirstPointPosition.toPoint2D() );
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public void translate( ImmutableVector2D translationVector ) {
        // Translate the current shape user the superclass facility.
        super.translate( translationVector );
        // Translate each of the shape segments that define the outline shape.
        for ( ShapeSegment shapeSegment : shapeSegments ) {
            shapeSegment.translate( translationVector );
        }
        // Translate each of the points that define the curly mRNA shape.
        PointMass thisPoint = firstShapeDefiningPoint;
        while ( thisPoint != null ) {
            thisPoint.translate( translationVector );
            thisPoint = thisPoint.getNextPointMass();
        }
    }

    /**
     * Command this mRNA strand to fade away when it has become fully formed.
     * This was created for use in the 2nd tab, where mRNA is never translated
     * once it is produced.
     */
    public void setFadeAwayWhenFormed( boolean fadeAwayWhenFormed ) {
        // Just pass this through to the state machine.
        mRnaAttachmentStateMachine.setFadeAwayWhenFormed( fadeAwayWhenFormed );
    }

    /**
     * Advance the translation of the mRNA through the given ribosome by the
     * specified length.  The given ribosome must already be attached to the
     * mRNA.
     *
     * @param ribosome - The ribosome by which the mRNA is being translated.
     * @param length   - The amount of mRNA to move through the translation channel.
     * @return - true if the mRNA is completely through the channel, indicating,
     *         that transcription is complete, and false if not.
     */
    public boolean advanceTranslation( Ribosome ribosome, double length ) {

        ShapeSegment segmentToAdvance = mapRibosomeToShapeSegment.get( ribosome );

        // Error checking.
        if ( segmentToAdvance == null ) {
            System.out.println( getClass().getName() + " - Warning: Attempt to advance translation by a ribosome that isn't attached." );
            return true;
        }

        // Advance the translation by advancing the position of the mRNA in the
        // segment that corresponds to the translation channel of the ribosome.
        segmentToAdvance.advance( length, shapeSegments );

        // Realign the segments, since they may well have changed shape.  
        if ( shapeSegments.contains( segmentToAdvance ) ) {
            realignSegmentsFrom( segmentToAdvance );
        }

        // Since the sizes and relationships of the segments probably changed,
        // the winding algorithm needs to be rerun.
        windPointsThroughSegments();

        // If there is anything left in this segment, then transcription is not
        // yet complete.
        return segmentToAdvance.getContainedLength() <= 0;
    }

    /**
     * Advance the destruction of the mRNA by the specified length.  This pulls
     * the strand into the lead segment much like translation does, but does
     * not move the points into new segment, it just gets rid of them.
     *
     * @param length
     */
    public boolean advanceDestruction( double length ) {

        // Error checking.
        if ( segmentWhereDestroyerConnects == null ) {
            System.out.println( getClass().getName() + " - Warning: Attempt to advance the destruction of mRNA that has no content left." );
            return true;
        }

        // Advance the destruction by reducing the length of the mRNA.
        reduceLength( length );

        // Realign the segments, since they may well have changed shape.
        if ( shapeSegments.contains( segmentWhereDestroyerConnects ) ) {
            realignSegmentsFrom( segmentWhereDestroyerConnects );
        }

        if ( shapeSegments.size() > 0 ) {
            // Since the sizes and relationships of the segments probably changed,
            // the winding algorithm needs to be rerun.
            windPointsThroughSegments();
        }

        // If there is any length left, then the destruction is not yet
        // complete.  This is a quick way to test this.
        return firstShapeDefiningPoint == lastShapeDefiningPoint;
    }

    // Reduce the length of the mRNA.  This handles both the shape segments and
    // the shape-defining points.
    private void reduceLength( double reductionAmount ) {
        if ( reductionAmount >= getLength() ) {
            // Reduce length to be zero.
            lastShapeDefiningPoint = firstShapeDefiningPoint;
            lastShapeDefiningPoint.setNextPointMass( null );
            shapeSegments.clear();
        }
        else {
            // Remove the length from the shape segments.
            segmentWhereDestroyerConnects.advanceAndRemove( reductionAmount, shapeSegments );
            // Remove the length from the shape defining points.
            for ( double amountRemoved = 0; amountRemoved < reductionAmount; ) {
                if ( lastShapeDefiningPoint.getTargetDistanceToPreviousPoint() <= reductionAmount - amountRemoved ) {
                    // Remove the last point from the list.
                    amountRemoved += lastShapeDefiningPoint.getTargetDistanceToPreviousPoint();
                    lastShapeDefiningPoint = lastShapeDefiningPoint.getPreviousPointMass();
                    lastShapeDefiningPoint.setNextPointMass( null );
                }
                else {
                    // Reduce the distance of the last point from the previous point.
                    lastShapeDefiningPoint.setTargetDistanceToPreviousPoint( lastShapeDefiningPoint.getTargetDistanceToPreviousPoint() - ( reductionAmount - amountRemoved ) );
                    amountRemoved = reductionAmount;
                }
            }
        }
    }

    /**
     * Create a new version of the protein that should result when this strand
     * of mRNA is translated.
     *
     * @return
     */
    public Protein getProteinPrototype() {
        return proteinPrototype;
    }

    /**
     * Get the point in model space where the entrance of the given ribosome's
     * translation channel should be in order to be correctly attached to
     * this strand of messenger RNA.  This allows the ribosome to "follow" the
     * mRNA if it is moving or changing shape.
     * <p/>
     * TODO: Consider making this a property.  Might be cleaner.
     *
     * @param ribosome
     * @return
     */
    public Point2D getRibosomeAttachmentLocation( Ribosome ribosome ) {
        if ( !mapRibosomeToShapeSegment.containsKey( ribosome ) ) {
            System.out.println( getClass().getName() + " Warning: Ignoring attempt to obtain attachment point for non-attached ribosom." );
            return null;
        }
        Point2D attachmentPoint;
        ShapeSegment segment = mapRibosomeToShapeSegment.get( ribosome );
        if ( shapeSegments.getPreviousItem( segment ) == null ) {
            // There is no previous segment, which means that the segment to
            // which this ribosome is attached is the leader segment.  The
            // attachment point is thus the leader length from its rightmost
            // edge.
            attachmentPoint = new Point2D.Double( segment.getLowerRightCornerPos().getX() - LEADER_LENGTH, segment.getLowerRightCornerPos().getY() );
        }
        else {
            // The segment has filled up the channel, so calculate the
            // position based on its left edge.
            attachmentPoint = new Point2D.Double( segment.getUpperLeftCornerPos().getX() + ribosome.getTranslationChannelLength(),
                                                  segment.getUpperLeftCornerPos().getY() );
        }
        return attachmentPoint;
    }

    /**
     * Release this mRNA from a ribosome.  If this is the only ribosome to
     * which the mRNA is connected, the mRNA will start wandering.
     *
     * @param ribosome
     */
    public void releaseFromRibosome( Ribosome ribosome ) {
        assert mapRibosomeToShapeSegment.containsKey( ribosome ); // This shouldn't be called if the ribosome wasn't connected.
        mapRibosomeToShapeSegment.remove( ribosome );
        if ( mapRibosomeToShapeSegment.isEmpty() ) {
            mRnaAttachmentStateMachine.allRibosomesDetached();
        }
    }

    /**
     * Release this mRNA from the polymerase which is, presumably, transcribing
     * it.
     */
    public void releaseFromPolymerase() {
        mRnaAttachmentStateMachine.detach();
    }

    /**
     * Activate the placement hint(s) as appropriate for the given biomolecule.
     *
     * @param biomolecule - And instance of the type of biomolecule for which
     *                    any matching hints should be activated.
     */
    public void activateHints( MobileBiomolecule biomolecule ) {
        ribosomePlacementHint.activateIfMatch( biomolecule );
        mRnaDestroyerPlacementHint.activateIfMatch( biomolecule );
    }

    public void deactivateAllHints() {
        ribosomePlacementHint.active.set( false );
        mRnaDestroyerPlacementHint.active.set( false );
    }

    /**
     * Initiate the translation process by setting up the segments as needed.
     * This should only be called after a ribosome that was moving to attach
     * with this mRNA arrives at the attachment point.
     *
     * @param ribosome
     */
    public void initiateTranslation( Ribosome ribosome ) {
        assert mapRibosomeToShapeSegment.containsKey( ribosome ); // State checking.

        // Set the capacity of the first segment to the size of the channel
        // through which it will be pulled plus the leader length.
        ShapeSegment firstShapeSegment = shapeSegments.get( 0 );
        assert firstShapeSegment.isFlat();
        firstShapeSegment.setCapacity( ribosome.getTranslationChannelLength() + LEADER_LENGTH );
    }

    /**
     * Initiate the destruction of this mRNA strand by setting up the segments
     * as needed.  This should only be called after an mRNA destroyer has
     * attached to the front of the mRNA strand.  Once initiated, destruction
     * cannot be stopped.
     *
     * @param messengerRnaDestroyer
     */
    public void initiateDestruction( MessengerRnaDestroyer messengerRnaDestroyer ) {
        assert this.messengerRnaDestroyer == messengerRnaDestroyer; // Shouldn't get this from unattached destroyers.

        // Set the capacity of the first segment to the size of the channel
        // through which it will be pulled plus the leader length.
        segmentWhereDestroyerConnects = shapeSegments.get( 0 );
        assert segmentWhereDestroyerConnects.isFlat();
        segmentWhereDestroyerConnects.setCapacity( messengerRnaDestroyer.getDestructionChannelLength() + LEADER_LENGTH );
    }

    /**
     * Get the proportion of the entire mRNA that has been translated by the
     * given ribosome.
     *
     * @param ribosome
     * @return
     */
    public double getProportionOfRnaTranslated( Ribosome ribosome ) {
        if ( !mapRibosomeToShapeSegment.containsKey( ribosome ) ) {
            System.out.println( getClass().getName() + " - Warning: Attempt to obtain amount of mRNA translated by a ribosome that isn't attached." );
            return 0;
        }

        double translatedLength = 0;

        ShapeSegment segmentInRibosomeChannel = mapRibosomeToShapeSegment.get( ribosome );
        assert segmentInRibosomeChannel.isFlat(); // Make sure things are as we expect.

        // Add the length for each segment that precedes this ribosome.
        for ( ShapeSegment shapeSegment : shapeSegments ) {
            if ( shapeSegment == segmentInRibosomeChannel ) {
                break;
            }
            translatedLength += shapeSegment.getContainedLength();
        }

        // Add the length for the segment that is inside the translation
        // channel of this ribosome.
        translatedLength += segmentInRibosomeChannel.getContainedLength() - ( segmentInRibosomeChannel.getLowerRightCornerPos().getX() - segmentInRibosomeChannel.attachmentSite.locationProperty.get().getX() );

        return Math.max( translatedLength / getLength(), 0 );
    }

    public AttachmentSite considerProposalFrom( Ribosome ribosome ) {
        assert !mapRibosomeToShapeSegment.containsKey( ribosome ); // Shouldn't get redundant proposals from a ribosome.
        AttachmentSite returnValue = null;

        // Can't consider proposal if currently being destroyed.
        if ( messengerRnaDestroyer == null ) {
            // See if the attachment site at the leading edge of the mRNA is
            // available.
            AttachmentSite leadingEdgeAttachmentSite = shapeSegments.get( 0 ).attachmentSite;
            if ( leadingEdgeAttachmentSite.attachedOrAttachingMolecule.get() == null &&
                 leadingEdgeAttachmentSite.locationProperty.get().distance( ribosome.getEntranceOfRnaChannelPos().toPoint2D() ) < RIBOSOME_CONNECTION_DISTANCE ) {
                // This attachment site is in range and available.
                returnValue = leadingEdgeAttachmentSite;
                // Update the attachment state machine.
                mRnaAttachmentStateMachine.attachedToRibosome();
                // Enter this connection in the map.
                mapRibosomeToShapeSegment.put( ribosome, shapeSegments.get( 0 ) );
            }
        }

        return returnValue;
    }

    public AttachmentSite considerProposalFrom( MessengerRnaDestroyer messengerRnaDestroyer ) {
        assert this.messengerRnaDestroyer != messengerRnaDestroyer; // Shouldn't get redundant proposals from same destroyer.
        AttachmentSite returnValue = null;

        // Make sure that this mRNA is not already being destroyed.
        if ( this.messengerRnaDestroyer == null ) {
            // See if the attachment site at the leading edge of the mRNA is
            // available.
            AttachmentSite leadingEdgeAttachmentSite = shapeSegments.get( 0 ).attachmentSite;
            if ( leadingEdgeAttachmentSite.attachedOrAttachingMolecule.get() == null &&
                 leadingEdgeAttachmentSite.locationProperty.get().distance( messengerRnaDestroyer.getPosition() ) < MRNA_DESTROYER_CONNECT_DISTANCE ) {
                // This attachment site is in range and available.
                returnValue = leadingEdgeAttachmentSite;
                // Update the attachment state machine.
                mRnaAttachmentStateMachine.attachToDestroyer();
                // Keep track of the destroyer.
                this.messengerRnaDestroyer = messengerRnaDestroyer;
            }
        }

        return returnValue;
    }

    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new MessengerRnaAttachmentStateMachine( this );
    }

    public Point2D getDestroyerAttachmentLocation() {
        assert segmentWhereDestroyerConnects != null; // State checking - shouldn't be called before this is set.
        // Avoid null pointer exception.
        if ( segmentWhereDestroyerConnects == null ) {
            return new Point2D.Double( 0, 0 );
        }
        // The attachment location is at the right most side of the segment
        // minus the leader length.
        return new Point2D.Double( segmentWhereDestroyerConnects.getLowerRightCornerPos().getX() - LEADER_LENGTH,
                                   segmentWhereDestroyerConnects.getLowerRightCornerPos().getY() );
    }

    /**
     * Test harness.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 1000, 400 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.2 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                0.2 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );

        MessengerRna messengerRna = new MessengerRna( new ManualGeneExpressionModel(),
                                                      new ProteinA( new StubGeneExpressionModel() ),
                                                      mvt.modelToView( new Point2D.Double( 0, 0 ) ) );
        canvas.addWorldChild( new MessengerRnaNode( mvt, messengerRna ) );
        for ( int i = 0; i < 200; i++ ) {
            messengerRna.addLength( 25 ); // Number derived from what polymerase tends to do.
            try {
                Thread.sleep( 500 );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            canvas.repaint();
        }
    }
}

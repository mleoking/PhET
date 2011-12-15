// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.RibosomeAttachmentStateMachine;

/**
 * Class that represents the a ribosome in the model.
 *
 * @author John Blanco
 */
public class Ribosome extends MobileBiomolecule {

    private static final double WIDTH = 290;                  // In nanometers.
    private static final double OVERALL_HEIGHT = 300;         // In nanometers.
    private static final double TOP_SUBUNIT_HEIGHT_PROPORTION = 0.6;
    private static final double TOP_SUBUNIT_HEIGHT = OVERALL_HEIGHT * TOP_SUBUNIT_HEIGHT_PROPORTION;
    private static final double BOTTOM_SUBUNIT_HEIGHT = OVERALL_HEIGHT * ( 1 - TOP_SUBUNIT_HEIGHT_PROPORTION );

    // Offset from the center position to the entrance of the translation
    // channel.  May require some tweaking of the shape changes.
    public static final ImmutableVector2D OFFSET_TO_TRANSLATION_CHANNEL_ENTRANCE = new ImmutableVector2D( WIDTH / 2, -OVERALL_HEIGHT / 2 + BOTTOM_SUBUNIT_HEIGHT );

    // Offset from the center position to the point from which the protein
    // emerges.  May require some tweaking if the overall shape changes.
    private static final ImmutableVector2D OFFSET_TO_PROTEIN_OUTPUT_CHANNEL = new ImmutableVector2D( WIDTH * 0.4, OVERALL_HEIGHT * 0.6 );

    // Messenger RNA being translated, null if no translation is in progress.
    private MessengerRna messengerRnaBeingTranslated;

    // Protein being synthesized, null if no synthesis in progress.
    private Protein proteinBeingSynthesized = null;

    public Ribosome( GeneExpressionModel model ) {
        this( model, new Point2D.Double( 0, 0 ) );
    }

    public Ribosome( final GeneExpressionModel model, Point2D position ) {
        super( model, createShape(), new Color( 205, 155, 29 ) );
        setPosition( position );
        /* TODO: Shouldn't need this anymore - new state behavior as of 12/2/2011 should handle it.  Keep for a while just in case.
        userControlled.addObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean isUserControlled, Boolean wasUserControlled ) {
                if ( wasUserControlled && !isUserControlled ) {
                    // The user just dropped this ribosome.  If there is an
                    // mRNA nearby, attach to it.
                    for ( MessengerRna messengerRna : model.getMessengerRnaList() ) {
                        if ( messengerRna.getTranslationAttachmentPoint().distance( getEntranceOfRnaChannelPos().toPoint2D() ) < MRNA_CAPTURE_DISTANCE ) {
                            // Move to the appropriate location in order to
                            // look attached to the mRNA.
                            setPosition( new ImmutableVector2D( messengerRna.getTranslationAttachmentPoint() ).getSubtractedInstance( OFFSET_TO_TRANSLATION_CHANNEL_ENTRANCE ).toPoint2D() );

                            // Attach to this mRNA.
                            messengerRna.connectToRibosome( Ribosome.this );

                            // Create the protein associated with this mRNA and
                            // add it to the model.
                            proteinBeingSynthesized = messengerRna.getProteinPrototype().createInstance( model, Ribosome.this );
                            proteinBeingSynthesized.setGrowthFactor( 0 );
                            model.addMobileBiomolecule( proteinBeingSynthesized );

                            // Move into the translating state.
                            behaviorState = new TranslatingMRnaState( messengerRna, Ribosome.this );
                        }
                    }
                }
            }
        } );
        */
    }

    public void setProteinGrowth( double growthFactor ) {
        if ( proteinBeingSynthesized == null ) {
            System.out.println( getClass().getName() + " - Warning - Ignoring attempt to grow non-existent protein." );
            return;
        }
        proteinBeingSynthesized.setFullSizeProportion( growthFactor );
    }

    public MessengerRna getMessengerRnaBeingTranslated() {
        return messengerRnaBeingTranslated;
    }

    /**
     * Scan for mRNA and propose attachments to any that are found.  It is up
     * to the mRNA to accept or refuse based on distance, availability, or
     * whatever.
     * <p/>
     * This method is called from the attachment state machine framework.
     *
     * @return
     */
    @Override public AttachmentSite proposeAttachments() {
        AttachmentSite attachmentSite = null;
        List<MessengerRna> messengerRnaList = model.getMessengerRnaList();
        for ( MessengerRna messengerRna : messengerRnaList ) {
            attachmentSite = messengerRna.considerProposalFrom( this );
            if ( attachmentSite != null ) {
                // Proposal accepted.
                messengerRnaBeingTranslated = messengerRna;
                break;
            }
        }
        return attachmentSite;
    }

    public void releaseProtein() {
        if ( proteinBeingSynthesized == null ) {
            System.out.println( getClass().getName() + " - Warning - Ignoring attempt to release non-existent protein." );
            return;
        }
        proteinBeingSynthesized.release();
    }

    public void releaseMessengerRna() {
        messengerRnaBeingTranslated.releaseFromRibosome( this );
        messengerRnaBeingTranslated = null;
    }

    // Overridden in order to hook up unique attachment state machine for this
    // biomolecule.
    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new RibosomeAttachmentStateMachine( this );
    }

    private static Shape createShape() {
        // Draw the top portion, which in this sim is the larger subunit.  The
        // shape is essentially a lumpy ellipse, and is based on some drawings
        // seen on the web.
        List<Point2D> topSubunitPointList = new ArrayList<Point2D>() {{
            // Define the shape with a series of points.  Starts at top left.
            add( new Point2D.Double( -WIDTH * 0.3, TOP_SUBUNIT_HEIGHT * 0.9 ) );
            add( new Point2D.Double( WIDTH * 0.3, TOP_SUBUNIT_HEIGHT ) );
            add( new Point2D.Double( WIDTH * 0.5, 0 ) );
            add( new Point2D.Double( WIDTH * 0.3, -TOP_SUBUNIT_HEIGHT * 0.4 ) );
            add( new Point2D.Double( 0, -TOP_SUBUNIT_HEIGHT * 0.5 ) ); // Center bottom.
            add( new Point2D.Double( -WIDTH * 0.3, -TOP_SUBUNIT_HEIGHT * 0.4 ) );
            add( new Point2D.Double( -WIDTH * 0.5, 0 ) );
        }};
        Shape topSubunitShape = AffineTransform.getTranslateInstance( 0, OVERALL_HEIGHT / 4 ).createTransformedShape( BioShapeUtils.createRoundedShapeFromPoints( topSubunitPointList ) );
        // Draw the bottom portion, which in this sim is the smaller subunit.
        List<Point2D> bottomSubunitPointList = new ArrayList<Point2D>() {{
            // Define the shape with a series of points.
            add( new Point2D.Double( -WIDTH * 0.45, BOTTOM_SUBUNIT_HEIGHT * 0.5 ) );
            add( new Point2D.Double( 0, BOTTOM_SUBUNIT_HEIGHT * 0.45 ) );
            add( new Point2D.Double( WIDTH * 0.45, BOTTOM_SUBUNIT_HEIGHT * 0.5 ) );
            add( new Point2D.Double( WIDTH * 0.45, -BOTTOM_SUBUNIT_HEIGHT * 0.5 ) );
            add( new Point2D.Double( 0, -BOTTOM_SUBUNIT_HEIGHT * 0.45 ) );
            add( new Point2D.Double( -WIDTH * 0.45, -BOTTOM_SUBUNIT_HEIGHT * 0.5 ) );
        }};
        Shape bottomSubunitShape = AffineTransform.getTranslateInstance( 0, -OVERALL_HEIGHT / 4 ).createTransformedShape( BioShapeUtils.createRoundedShapeFromPoints( bottomSubunitPointList ) );
        // Combine the two subunits into one shape.
        Area combinedShape = new Area( topSubunitShape );
        combinedShape.add( new Area( bottomSubunitShape ) );
        return combinedShape;
    }

    public ImmutableVector2D getEntranceOfRnaChannelPos() {
        return new ImmutableVector2D( getPosition() ).getAddedInstance( OFFSET_TO_TRANSLATION_CHANNEL_ENTRANCE );
    }

    public double getTranslationChannelLength() {
        return WIDTH;
    }

    public void setPositionOfTranslationChannel( Point2D position ) {
        setPosition( new ImmutableVector2D( position ).getSubtractedInstance( OFFSET_TO_TRANSLATION_CHANNEL_ENTRANCE ).toPoint2D() );
    }

    /**
     * Advance the translation of the mRNA.
     *
     * @param amount
     * @return - true if translation is complete, false if not.
     */
    public boolean advanceMessengerRnaTranslation( double amount ) {
        assert messengerRnaBeingTranslated != null; // Verify expected state.
        if ( messengerRnaBeingTranslated != null ) {
            return messengerRnaBeingTranslated.advanceTranslation( this, amount );
        }
        return false;
    }

    /**
     * Get the location in model space of the point at which a protein that is
     * being synthesized by this ribosome should be attached.
     *
     * @return
     */
    public Point2D getProteinAttachmentPoint() {
        return new Point2D.Double( getPosition().getX() + OFFSET_TO_PROTEIN_OUTPUT_CHANNEL.getX(),
                                   getPosition().getY() + OFFSET_TO_PROTEIN_OUTPUT_CHANNEL.getY() );
    }

    public void initiateTranslation() {
        assert messengerRnaBeingTranslated != null;
        if ( messengerRnaBeingTranslated != null ) {
            messengerRnaBeingTranslated.initiateTranslation( this );
        }
    }
}

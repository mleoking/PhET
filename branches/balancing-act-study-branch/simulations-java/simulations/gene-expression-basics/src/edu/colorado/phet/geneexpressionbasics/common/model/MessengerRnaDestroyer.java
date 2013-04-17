// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.RnaDestroyerAttachmentStateMachine;

/**
 * Class that represents the small ribosomal subunit in the model.
 *
 * @author John Blanco
 */
public class MessengerRnaDestroyer extends MobileBiomolecule {

    private static final double WIDTH = 250;   // In nanometers.

    // Reference to the messenger RNA being destroyed.
    private MessengerRna messengerRnaBeingDestroyed;

    public MessengerRnaDestroyer( GeneExpressionModel model ) {
        this( model, new Vector2D( 0, 0 ) );
    }

    public MessengerRnaDestroyer( GeneExpressionModel model, Vector2D position ) {
        super( model, createShape(), new Color( 255, 150, 66 ) );
        setPosition( position );
    }

    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new RnaDestroyerAttachmentStateMachine( this );
    }

    public boolean advanceMessengerRnaDestruction( double amountToDestroy ) {
        return messengerRnaBeingDestroyed.advanceDestruction( amountToDestroy );
    }

    /**
     * Scan for mRNA and propose attachments to any that are found.  It is up
     * to the mRNA to accept or refuse based on distance, availability, or
     * whatever.
     * <p/>
     * This method is called by the attachment state machine framework.
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
                messengerRnaBeingDestroyed = messengerRna;
                break;
            }
        }
        return attachmentSite;
    }

    private static Shape createShape() {
        Shape circle = new Ellipse2D.Double( -WIDTH / 2, -WIDTH / 2, WIDTH, WIDTH );
        DoubleGeneralPath mouthPath = new DoubleGeneralPath( 0, 0 ) {{
            lineTo( WIDTH, WIDTH * 0.8 );
            lineTo( WIDTH, -WIDTH * 0.8 );
            closePath();
        }};
        Area overallShape = new Area( circle );
        overallShape.subtract( new Area( mouthPath.getGeneralPath() ) );
        return overallShape;
    }

    public double getDestructionChannelLength() {
        // Since this looks like a circle with a slice out of it, the channel
        // is half of the width.
        return getShape().getBounds2D().getWidth() / 2;
    }

    public void initiateMessengerRnaDestruction() {
        messengerRnaBeingDestroyed.initiateDestruction( this );
    }

    public MessengerRna getMessengerRnaBeingDestroyed() {
        return messengerRnaBeingDestroyed;
    }

    public void clearMessengerRnaBeingDestroyed() {
        messengerRnaBeingDestroyed = null;
    }
}

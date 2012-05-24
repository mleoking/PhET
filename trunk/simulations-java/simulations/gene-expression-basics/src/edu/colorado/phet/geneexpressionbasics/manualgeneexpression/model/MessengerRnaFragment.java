// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.common.model.WindingBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.MessengerRnaFragmentAttachmentStateMachine;

/**
 * Class that represents a fragment of messenger ribonucleic acid, or mRNA, in
 * the model.  The fragments exist for a short time as mRNA is being destroyed,
 * but can't be translated.
 *
 * @author John Blanco
 */
public class MessengerRnaFragment extends WindingBiomolecule {

    /**
     * Constructor.  This creates the mRNA fragment as a single point, with the
     * intention of growing it.
     *
     * @param position
     */
    public MessengerRnaFragment( final GeneExpressionModel model, Point2D position ) {
        super( model, new DoubleGeneralPath( position ).getGeneralPath(), position );

        // Add the first, and in this case only, segment to the shape segment
        // list.
        shapeSegments.add( new ShapeSegment.SquareSegment( position ) );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Release this mRNA fragment from the destroyer molecule.
     */
    public void releaseFromDestroyer() {
        attachmentStateMachine.detach();
    }

    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new MessengerRnaFragmentAttachmentStateMachine( this );
    }
}

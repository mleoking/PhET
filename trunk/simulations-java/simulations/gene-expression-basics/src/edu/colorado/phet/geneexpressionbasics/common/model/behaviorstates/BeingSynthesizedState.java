// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates;

import java.util.List;

import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Protein;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Ribosome;

/**
 * State that defines the behavior for a biomolecule that is being synthesized.
 * At the time of this writing, this only applies to proteins.
 *
 * @author John Blanco
 */
public class BeingSynthesizedState extends BiomoleculeBehaviorState {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // The protein molecule that is being synthesized.
    private final Protein protein;

    // The ribosome that is creating this protein.
    private final Ribosome parentRibosome;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public BeingSynthesizedState( Protein protein, Ribosome ribosome ) {
        super( protein );
        this.protein = protein;
        this.parentRibosome = ribosome;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public BiomoleculeBehaviorState stepInTime( double dt ) {
        protein.setPositionOfAttachmentPoint( parentRibosome.getProteinAttachmentPoint() );
        // State is changed by protein when it is released, so no state change
        // ever occurs here.
        return this;
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites ) {
        // Does not attach while in this state.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        return new UnattachedAndAvailableState( protein );
    }
}

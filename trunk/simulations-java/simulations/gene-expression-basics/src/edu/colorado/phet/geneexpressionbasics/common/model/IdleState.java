// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.util.List;

/**
 * State that does nothing - no motion, no change, no stimuli.  Be cautious with
 * the use of this state, since a biomolecule will get stuck here unless its
 * state is explicitly set to something else at some point.
 *
 * @author John Blanco
 */
public class IdleState extends BiomoleculeBehaviorState {

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public IdleState( MobileBiomolecule biomolecule ) {
        super( biomolecule );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public BiomoleculeBehaviorState stepInTime( double dt ) {
        // Does nothing.
        return this;
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites ) {
        // Does not attach while in this state.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        return new UnattachedAndAvailableState( biomolecule );
    }
}

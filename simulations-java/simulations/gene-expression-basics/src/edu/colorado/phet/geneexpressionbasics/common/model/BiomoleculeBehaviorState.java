// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

/**
 * The behavior states control the motion and the attachment behavior of the
 * biomolecules.
 *
 * @author John Blanco
 */
public abstract class BiomoleculeBehaviorState {
    public abstract BiomoleculeBehaviorState stepInTime( double dt, MobileBiomolecule biomolecule );

    /**
     * Consider whether to attach to the proposed attachment site.  The contract
     * for this API includes the idea that if the biomolecule chooses to attach,
     * it must update the AttachmentSite accordingly.
     *
     * @param attachmentSite
     * @return
     */
    public abstract BiomoleculeBehaviorState considerAttachment( AttachmentSite attachmentSite, MobileBiomolecule biomolecule );

}

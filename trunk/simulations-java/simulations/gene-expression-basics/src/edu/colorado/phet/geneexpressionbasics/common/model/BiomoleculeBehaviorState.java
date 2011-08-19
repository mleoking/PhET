// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.util.List;

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
     * @param proposedAttachmentSites@return
     */
    public abstract BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites, MobileBiomolecule biomolecule );

    /**
     * The user moved this biomolecule.  If any attachments existed or were
     * developing, they must be cleaned up, and the molecule becomes
     * unattached.
     *
     * @return
     */
    public abstract BiomoleculeBehaviorState movedByUser();
}

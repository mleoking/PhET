// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.util.List;
import java.util.Random;

/**
 * The behavior states control the motion and the attachment behavior of the
 * biomolecules.
 *
 * @author John Blanco
 */
public abstract class BiomoleculeBehaviorState {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    protected static final Random RAND = new Random();

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------
    protected final MobileBiomolecule biomolecule;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected BiomoleculeBehaviorState( MobileBiomolecule biomolecule ) {
        this.biomolecule = biomolecule;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public abstract BiomoleculeBehaviorState stepInTime( double dt );

    /**
     * Consider whether to attach to any of the proposed attachment sites.  The
     * contract for this API includes the idea that if the biomolecule chooses
     * to attach, it must update the AttachmentSite accordingly.
     *
     * @param proposedAttachmentSites
     * @return New state if a state change occurs, previous state if not.
     */
    public abstract BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites );

    /**
     * The user moved this biomolecule.  If any attachments existed or were
     * developing, they must be cleaned up, and the molecule becomes
     * unattached.
     *
     * @return
     */
    public abstract BiomoleculeBehaviorState movedByUser();

    /**
     * Calculate the intensity of the attraction between the biomolecule and the
     * attachment site.  This value is based on a combination of the distance
     * from the site and the site's affinity value.
     *
     * @param biomolecule
     * @param attachmentSite
     * @return
     */
    protected double getAttraction( MobileBiomolecule biomolecule, AttachmentSite attachmentSite ) {
        return ( 1 / Math.pow( biomolecule.getPosition().distance( attachmentSite.locationProperty.get() ), 2 ) ) * attachmentSite.getAffinity();
    }
}

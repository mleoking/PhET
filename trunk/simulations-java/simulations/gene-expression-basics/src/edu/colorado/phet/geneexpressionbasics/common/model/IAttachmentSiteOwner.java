// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

/**
 * Interface that is implemented by model objects that have attachements sites,
 * i.e. sites at which a biomolecule may attach.
 *
 * @author John Blanco
 */
public interface IAttachmentSiteOwner {
    /**
     * Commands the owner of the attachment sites to propose the most
     * appropriate attachments sites, if there are any, to the given
     * biomolecule.
     *
     * @param mobileBiomolecule
     */
    void proposeAttachmentSitesTo( MobileBiomolecule mobileBiomolecule );
}

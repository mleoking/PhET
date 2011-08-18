// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

/**
 * Interface that is implemented by model objects that have attachements sites,
 * i.e. sites at which a biomolecule may attach.
 *
 * @author John Blanco
 */
public interface AttachmentSiteOwner {
    boolean proposeAttachmentSitesTo( MobileBiomolecule mobileBiomolecule );
}

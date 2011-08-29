// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * A site to which one biomolecule may attach to another.  Typically one
 * biomolecule (e.g. a DnaMolecule) owns the attachment site and proposes it to
 * a smaller biomolecule.
 *
 * @author John Blanco
 */
public class AttachmentSite {

    // Location of this attachment site.  It is a property so that it can be
    // followed in the event that the biomolecule upon which it exists is
    // moving.
    public Property<Point2D> locationProperty = new Property<Point2D>( new Point2D.Double( 0, 0 ) );

    // A value between 0 and 1 that represents the strength of the affinity
    // for this attachment.
    private double affinity;

    // A property that tracks which if any biomolecule is attached to this
    // site.
    public Property<Option<MobileBiomolecule>> attachedMolecule = new Property<Option<MobileBiomolecule>>( new Option.None<MobileBiomolecule>() );

    /**
     * Constructor.
     *
     * @param initialLocation
     * @param affinity
     */
    public AttachmentSite( Point2D initialLocation, double affinity ) {
        this.locationProperty.set( new Point2D.Double( initialLocation.getX(), initialLocation.getY() ) );
        this.affinity = affinity;
    }

    public double getAffinity() {
        return affinity;
    }

    public void setAffinity( double affinity ) {
        assert affinity >= 0 && affinity <= 1;
        this.affinity = affinity;
    }

    @Override public boolean equals( Object obj ) {
        if ( this == obj ) { return true; }

        if ( !( obj instanceof AttachmentSite ) ) { return false; }

        AttachmentSite otherAttachmentSite = (AttachmentSite) obj;

        return this.affinity == otherAttachmentSite.affinity &&
               this.locationProperty.get().getX() == otherAttachmentSite.locationProperty.get().getX() &&
               this.locationProperty.get().getY() == otherAttachmentSite.locationProperty.get().getY();

    }
}

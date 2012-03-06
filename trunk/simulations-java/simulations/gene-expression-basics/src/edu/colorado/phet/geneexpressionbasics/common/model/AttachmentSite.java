// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A site to which one biomolecule may attach to another.  Typically one
 * biomolecule (e.g. a DnaMolecule) owns the attachment site, so if the
 * biomolecule that owns it moves, the attachment site moves with it.
 *
 * @author John Blanco
 */
public class AttachmentSite {

    // Location of this attachment site.  It is a property so that it can be
    // followed in the event that the biomolecule upon which it exists is
    // moving.
    public Property<Point2D> locationProperty = new Property<Point2D>( new Point2D.Double( 0, 0 ) );

    // Property that can be used to change the affinity of the attachment site.
    // Valid values are from 0 to 1;
    public final Property<Double> affinityProperty;

    // A property that tracks which if any biomolecule is attached to or moving
    // towards attachment with this site.
    public Property<MobileBiomolecule> attachedOrAttachingMolecule = new Property<MobileBiomolecule>( null );

    /**
     * Constructor.
     *
     * @param initialLocation
     */
    public AttachmentSite( Point2D initialLocation, double initialAffinity ) {
        this.locationProperty.set( new Point2D.Double( initialLocation.getX(), initialLocation.getY() ) );
        affinityProperty = new Property<Double>( initialAffinity );
    }

    public double getAffinity() {
        return affinityProperty.get();
    }

    public void setAffinity( double affinity ) {
        assert affinity >= 0 && affinity <= 1; // Bounds checking.
        affinityProperty.set( affinity );
    }

    @Override public boolean equals( Object obj ) {
        if ( this == obj ) { return true; }

        if ( !( obj instanceof AttachmentSite ) ) { return false; }

        AttachmentSite otherAttachmentSite = (AttachmentSite) obj;

        return this.affinityProperty.get() == otherAttachmentSite.affinityProperty.get() &&
               this.locationProperty.get().getX() == otherAttachmentSite.locationProperty.get().getX() &&
               this.locationProperty.get().getY() == otherAttachmentSite.locationProperty.get().getY();
    }
}

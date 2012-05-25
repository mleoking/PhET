// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * An attachment site is a single point in model space to which a biomolecule
 * may attach.  Typically, one biomolecule (e.g. a DnaMolecule) owns the
 * attachment site, so if the biomolecule that owns it moves, the attachment
 * site should move with it.
 *
 * @author John Blanco
 */
public class AttachmentSite {

    // Threshold used to decide whether or not a biomolecule is attached.
    private static final double ATTACHED_THRESHOLD = 10; // picometers

    // Location of this attachment site.  It is a property so that it can be
    // followed in the event that the biomolecule upon which it exists is
    // moving.
    public final Property<Point2D> locationProperty = new Property<Point2D>( new Point2D.Double( 0, 0 ) );

    // Property that represents the affinity of the attachment site.
    public final Property<Double> affinityProperty;

    // A property that tracks which if any biomolecule is attached to or moving
    // towards attachment with this site.
    public final Property<MobileBiomolecule> attachedOrAttachingMolecule = new Property<MobileBiomolecule>( null );

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

    /**
     * Indicates whether or not a biomolecules is currently attached to this
     * site.
     *
     * @return - true if a biomolecule is fully attached, false if not.  If a
     *         molecule is on its way but not yet at the site, false is returned.
     */
    public boolean isMoleculeAttached() {
        return attachedOrAttachingMolecule.get() != null && locationProperty.get().distance( attachedOrAttachingMolecule.get().getPosition() ) < ATTACHED_THRESHOLD;
    }

    @Override public boolean equals( Object obj ) {
        if ( this == obj ) { return true; }

        if ( !( obj instanceof AttachmentSite ) ) { return false; }

        AttachmentSite otherAttachmentSite = (AttachmentSite) obj;

        return this.affinityProperty.get().equals( otherAttachmentSite.affinityProperty.get() ) &&
               this.locationProperty.get().getX() == otherAttachmentSite.locationProperty.get().getX() &&
               this.locationProperty.get().getY() == otherAttachmentSite.locationProperty.get().getY();
    }
}

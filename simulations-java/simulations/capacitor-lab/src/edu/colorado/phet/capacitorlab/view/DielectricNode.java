// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.ICapacitor;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Visual pseudo-3D representation of a capacitor dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricNode extends BoxNode {

    private final static float TRANSPARENCY = 0.75f;

    private final ICapacitor capacitor;

    public DielectricNode( final ICapacitor capacitor, CLModelViewTransform3D mvt,
                           final Property<DielectricChargeView> dielectricChargeView,
                           double maxExcessDielectricPlateCharge, double maxDielectricEField ) {
        super( mvt, capacitor.getDielectricMaterial().getColor(), capacitor.getDielectricSize() );

        this.capacitor = capacitor;

        final DielectricTotalChargeNode totalChargeNode = new DielectricTotalChargeNode( capacitor, mvt, maxDielectricEField );
        addChild( totalChargeNode );

        final DielectricExcessChargeNode excessChargeNode = new DielectricExcessChargeNode( capacitor, mvt, maxExcessDielectricPlateCharge );
        addChild( excessChargeNode );

        dielectricChargeView.addObserver( new SimpleObserver() {
            public void update() {
                totalChargeNode.setVisible( dielectricChargeView.get() == DielectricChargeView.TOTAL );
                excessChargeNode.setVisible( dielectricChargeView.get() == DielectricChargeView.EXCESS );
            }
        } );

        // change color when dielectric material changes
        capacitor.addDielectricMaterialObserver( new SimpleObserver() {
            public void update() {
                setColor( DielectricNode.this.capacitor.getDielectricMaterial().getColor() );
            }
        } );
    }

    /**
     * Controls the opacity of the dielectric.
     * This is needed because the dielectric must be transparent to see E-field.
     *
     * @param opaque
     */
    public void setOpaque( boolean opaque ) {
        float transparency = ( opaque ) ? 1f : TRANSPARENCY;
        /*
         * Some dielectric materials are naturally transparent.
         * Modify dielectric transparency only if it's not already transparent.
         */
        if ( capacitor.getDielectricMaterial().isOpaque() ) {
            setTransparency( transparency );
        }
    }
}

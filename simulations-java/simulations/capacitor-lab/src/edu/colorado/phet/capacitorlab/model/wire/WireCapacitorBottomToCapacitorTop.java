// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.*;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorToCapacitorWireSegment;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * Wire that connects the bottom plate of one capacitor to the top plate of another capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireCapacitorBottomToCapacitorTop extends Wire {

    private final Capacitor topCapacitor;

    public WireCapacitorBottomToCapacitorTop( CLModelViewTransform3D mvt, final double thickness, final Capacitor topCapacitor, final Capacitor bottomCapacitor ) {
        super( mvt, thickness, new CapacitorToCapacitorWireSegment( topCapacitor, bottomCapacitor ) );
        this.topCapacitor = topCapacitor;

        // adjust when dimensions of capacitor change
        SimpleObserver o = new SimpleObserver() {
            public void update() {
                setShape( createShape() );
            }
        };
        topCapacitor.addPlateSizeObserver( o );
        topCapacitor.addPlateSeparationObserver( o );
        bottomCapacitor.addPlateSizeObserver( o );
        bottomCapacitor.addPlateSeparationObserver( o );
    }

    // Subtract any part of the wire that is occluded by the bottom plate.
    @Override protected Shape createShape() {
        Shape wireShape = super.createShape();
        // HACK: null check required because createShape is called in the superclass constructor.
        if ( topCapacitor != null ) {
            wireShape = ShapeUtils.subtract( wireShape, topCapacitor.getShapeFactory().createBottomPlateShape() );
        }
        return wireShape;
    }
}

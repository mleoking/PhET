// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.ICapacitor;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.BatteryBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorBottomWireSegment;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * Wire that connects the bottom of a battery to the bottom of a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireBatteryBottomToCapacitorBottom extends Wire {

    private final Battery battery;
    private final ICapacitor capacitor;

    public WireBatteryBottomToCapacitorBottom( CLModelViewTransform3D mvt, final double thickness, final Battery battery, final ICapacitor capacitor ) {
        super( mvt, thickness, new Function0<ArrayList<WireSegment>>() {
            public ArrayList<WireSegment> apply() {
                final Point2D.Double leftCorner = new Point2D.Double( battery.getX(), battery.getY() + CLConstants.WIRE_EXTENT );
                final Point2D.Double rightCorner = new Point2D.Double( capacitor.getX(), leftCorner.getY() );
                final double t = ( thickness / 2 ); // for proper connection at corners with CAP_BUTT wire stroke
                ArrayList<WireSegment> segments = new ArrayList<WireSegment>() {{
                    add( new BatteryBottomWireSegment( battery, leftCorner ) );
                    add( new WireSegment( leftCorner.getX() - t, leftCorner.getY() + t, rightCorner.getX() + t, rightCorner.getY() + t ) );
                    add( new CapacitorBottomWireSegment( rightCorner, capacitor ) );
                }};
                return segments;
            }
        } );

        this.battery = battery;
        this.capacitor = capacitor;

        // adjust when dimensions of capacitor change
        SimpleObserver o = new SimpleObserver() {
            public void update() {
                setShape( createShape() );
            }
        };
        capacitor.addPlateSizeObserver( o );
        capacitor.addPlateSeparationObserver( o );
    }

    // Subtract any part of the wire that is occluded by the battery or bottom plate.
    @Override protected Shape createShape() {
        Shape wireShape = super.createShape();
        // HACK: null check required because createShape is called in the superclass constructor.
        if ( battery != null && capacitor != null ) {
            wireShape = ShapeUtils.subtract( wireShape, battery.getShapeFactory().createBodyShape(), capacitor.getShapeFactory().createBottomPlateShape() );
        }
        return wireShape;
    }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.BatteryBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorBottomWireSegment;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * Wire that connects the bottom of a battery to the bottoms of N parallel capacitors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireBatteryBottomToCapacitorBottoms extends Wire {

    private final Battery battery;
    private final ArrayList<Capacitor> capacitors;

    public WireBatteryBottomToCapacitorBottoms( CLModelViewTransform3D mvt, double thickness, Battery battery, final Capacitor capacitor ) {
        this( mvt, thickness, battery, new ArrayList<Capacitor>() {{ add( capacitor ); }} );
    }

    public WireBatteryBottomToCapacitorBottoms( CLModelViewTransform3D mvt, final double thickness, final Battery battery, final ArrayList<Capacitor> capacitors ) {
        super( mvt, thickness, new Function0<ArrayList<WireSegment>>() {
            public ArrayList<WireSegment> apply() {

                // connect battery to the rightmost capacitor
                final Capacitor rightmostCapacitor = capacitors.get( capacitors.size() - 1 );
                final double y = Math.max( battery.getY() + CLConstants.WIRE_EXTENT, rightmostCapacitor.getLocation().getY() + 0.01 );//TODO clean this up
                final Point2D.Double leftCorner = new Point2D.Double( battery.getX(), y );
                final Point2D.Double rightCorner = new Point2D.Double( rightmostCapacitor.getX(), leftCorner.getY() );
                final double t = ( thickness / 2 ); // for proper connection at corners with CAP_BUTT wire stroke
                ArrayList<WireSegment> segments = new ArrayList<WireSegment>() {{
                    add( new BatteryBottomWireSegment( battery, leftCorner ) );
                    add( new WireSegment( leftCorner.getX() - t, leftCorner.getY() + t, rightCorner.getX() + t, rightCorner.getY() + t ) );
                    add( new CapacitorBottomWireSegment( rightCorner, rightmostCapacitor ) );
                }};

                // add segments for all capacitors in between the battery and rightmost capacitor
                for ( int i = 0; i < capacitors.size() - 1; i++ ) {
                    Capacitor capacitor = capacitors.get( i );
                    Point2D.Double startPoint = new Point2D.Double( capacitor.getX(), y );
                    segments.add( new CapacitorBottomWireSegment( startPoint, capacitor ) );
                }

                return segments;
            }
        } );

        this.battery = battery;
        this.capacitors = capacitors;

        // adjust when dimensions of capacitor change
        SimpleObserver o = new SimpleObserver() {
            public void update() {
                setShape( createShape() );
            }
        };
        for ( Capacitor capacitor : capacitors ) {
            capacitor.addPlateSizeObserver( o );
            capacitor.addPlateSeparationObserver( o );
        }
    }

    // Subtract any part of the wire that is occluded by the battery or one of the bottom plates.
    @Override protected Shape createShape() {
        Shape wireShape = super.createShape();
        // HACK: null check required because createShape is called in the superclass constructor.
        if ( battery != null && capacitors != null ) {
            wireShape = ShapeUtils.subtract( wireShape, battery.getShapeFactory().createBodyShape() );
            for ( Capacitor capacitor : capacitors ) {
                wireShape = ShapeUtils.subtract( wireShape, capacitor.getShapeFactory().createBottomPlateShape() );
            }
        }
        return wireShape;
    }
}
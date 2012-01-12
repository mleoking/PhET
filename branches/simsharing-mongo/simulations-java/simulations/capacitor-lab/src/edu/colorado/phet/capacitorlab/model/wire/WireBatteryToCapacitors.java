// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.BatteryBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.BatteryTopWireSegment;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorTopWireSegment;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * Base class for any wire that connects a battery (B) to one of more capacitors (C1...Cn).
 * </p>
 * For the "top" subclass, the wire looks like this:
 * <code>
 * |-----|------|--...--|
 * |     |      |       |
 * B     C1    C2       Cn
 * </code>
 * </p>
 * For the "bottom" subclass, the wire looks like this:
 * <code>
 * B     C1    C2       Cn
 * |     |      |       |
 * |-----|------|--...--|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class WireBatteryToCapacitors extends Wire {

    // possible connection points on the battery and capacitors
    private enum ConnectionPoint {
        TOP, BOTTOM
    }

    /*
     * Connects the top of the battery (B) to the tops of N capacitors (C1...Cn).
     * Constructor args are described in superclass constructor.
     */
    public static class WireBatteryToCapacitorsTop extends WireBatteryToCapacitors {

        public WireBatteryToCapacitorsTop( CLModelViewTransform3D mvt, double thickness, double wireExtent, Battery battery, Capacitor... capacitors ) {
            this( mvt, thickness, wireExtent, battery, new ArrayList<Capacitor>( Arrays.asList( capacitors ) ) );
        }

        public WireBatteryToCapacitorsTop( CLModelViewTransform3D mvt, double thickness, double wireExtent, Battery battery, ArrayList<Capacitor> capacitors ) {
            super( ConnectionPoint.TOP, mvt, thickness, wireExtent, battery, capacitors );
        }
    }

    /*
     * Connects the bottom of the battery (B) to the bottoms of N capacitors (C1...Cn).
     * Constructor args are described in superclass constructor.
     */
    public static class WireBatteryToCapacitorsBottom extends WireBatteryToCapacitors {

        private final Battery battery;
        private final ArrayList<Capacitor> capacitors;

        public WireBatteryToCapacitorsBottom( CLModelViewTransform3D mvt, double thickness, double wireExtent, Battery battery, Capacitor... capacitors ) {
            this( mvt, thickness, wireExtent, battery, new ArrayList<Capacitor>( Arrays.asList( capacitors ) ) );
        }

        public WireBatteryToCapacitorsBottom( CLModelViewTransform3D mvt, double thickness, double wireExtent, Battery battery, ArrayList<Capacitor> capacitors ) {
            super( ConnectionPoint.BOTTOM, mvt, thickness, wireExtent, battery, capacitors );
            this.battery = battery;
            this.capacitors = capacitors;

            // Add plate size observers to all capacitors, so we can handle wire occlusion.
            SimpleObserver plateSizeObserver = new SimpleObserver() {
                public void update() {
                    setShape( createShape() );
                }
            };
            for ( Capacitor capacitor : capacitors ) {
                capacitor.addPlateSizeObserver( plateSizeObserver, false /* notifyOnAdd */ );
            }
            setShape( createShape() ); // call explicitly because notifyOnAdd was false
        }

        public void cleanup() {
            super.cleanup();
            //FUTURE removePlateSizeObserver for all capacitors
        }

        // Subtract any part of the wire that is occluded by the battery or one of the bottom plates.
        @Override protected Shape createShape() {
            Shape wireShape = super.createShape();
            // Null checks required because createShape is called in the superclass constructor.
            if ( battery != null && capacitors != null ) {
                wireShape = ShapeUtils.subtract( wireShape, battery.getShapeCreator().createBodyShape() );
                for ( Capacitor capacitor : capacitors ) {
                    wireShape = ShapeUtils.subtract( wireShape, capacitor.getShapeCreator().createBottomPlateShape() );
                }
            }
            return wireShape;
        }
    }

    /**
     * Constructor
     *
     * @param connectionPoint connection point on the battery and capacitors, TOP or BOTTOM
     * @param mvt             model-view transform
     * @param thickness       thickness of the wire, in meters
     * @param wireExtent      how far the wire extends below the capacitors, in meters
     * @param battery
     * @param capacitors
     */
    public WireBatteryToCapacitors( ConnectionPoint connectionPoint, CLModelViewTransform3D mvt, double thickness, double wireExtent, Battery battery, ArrayList<Capacitor> capacitors ) {
        super( mvt, thickness );

        // y coordinate of the horizontal wire
        double horizontalY = getHorizontalY( connectionPoint, capacitors, wireExtent );

        // horizontal segment connecting battery (B) to the rightmost capacitor (Cn)
        final Capacitor rightmostCapacitor = capacitors.get( capacitors.size() - 1 );
        final Point2D.Double leftCorner = new Point2D.Double( battery.getX(), horizontalY );
        final Point2D.Double rightCorner = new Point2D.Double( rightmostCapacitor.getX(), leftCorner.getY() );
        final double t = getCornerOffset(); // for proper connection at corners with wire stroke end style
        addSegment( getBatteryWireSegment( connectionPoint, battery, getEndOffset(), leftCorner ) );
        addSegment( new WireSegment( leftCorner.getX() - t, leftCorner.getY() + t, rightCorner.getX() + t, rightCorner.getY() + t ) );
        addSegment( getCapacitorWireSegment( connectionPoint, rightmostCapacitor, rightCorner ) );

        // add vertical segments for all capacitors (C1...Cn-1) in between the battery (B) and rightmost capacitor (Cn)
        for ( int i = 0; i < capacitors.size() - 1; i++ ) {
            Capacitor capacitor = capacitors.get( i );
            Point2D.Double startPoint = new Point2D.Double( capacitor.getX(), horizontalY );
            addSegment( getCapacitorWireSegment( connectionPoint, capacitor, startPoint ) );
        }
    }

    /*
     * Gets the Y coordinate of the horizontal wire.
     * It extends wireExtent distance above/below the capacitor that is closest to the wire.
     */
    private static double getHorizontalY( ConnectionPoint connectionPoint, ArrayList<Capacitor> capacitors, double wireExtent ) {
        double y = capacitors.get( 0 ).getY();
        if ( connectionPoint == ConnectionPoint.TOP ) {
            for ( Capacitor capacitor : capacitors ) {
                y = Math.min( y, capacitor.getLocation().getY() - wireExtent );
            }
        }
        else {
            for ( Capacitor capacitor : capacitors ) {
                y = Math.max( y, capacitor.getLocation().getY() + wireExtent );
            }
        }
        return y;
    }

    // Gets a wire segment that attaches to the specified terminal (top or bottom) of a battery.
    private static WireSegment getBatteryWireSegment( ConnectionPoint connectionPoint, Battery battery, double endOffset, Point2D endPoint ) {
        if ( connectionPoint == ConnectionPoint.TOP ) {
            return new BatteryTopWireSegment( battery, endOffset, endPoint );
        }
        else {
            return new BatteryBottomWireSegment( battery, endOffset, endPoint );
        }
    }

    // Gets a wire segment that attaches to the specified plate (top or bottom) of a capacitor.
    private static WireSegment getCapacitorWireSegment( ConnectionPoint connectionPoint, Capacitor capacitor, Point2D endPoint ) {
        if ( connectionPoint == ConnectionPoint.TOP ) {
            return new CapacitorTopWireSegment( capacitor, endPoint );
        }
        else {
            return new CapacitorBottomWireSegment( capacitor, endPoint );
        }
    }
}

// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A straight segment of wire. One or more segments are joined to create a wire.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireSegment {

    // publicly observable properties
    public final Property<Point2D> startPointProperty, endPointProperty;

    public WireSegment( Point2D startPoint, Point2D endPoint ) {
        this( startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY() );
    }

    public WireSegment( double startX, double startY, double endX, double endY ) {
        this.startPointProperty = new Property<Point2D>( new Point2D.Double( startX, startY ) );
        this.endPointProperty = new Property<Point2D>( new Point2D.Double( endX, endY ) );
    }

    public void cleanup() {
        // base class does nothing
    }

    // Any wire segment that is connected to a battery.
    private static abstract class BatteryWireSegment extends WireSegment implements SimpleObserver {

        private final Battery battery;
        private final double startYOffset;

        public BatteryWireSegment( Battery battery, double startYOffset, Point2D startPoint, Point2D endPoint ) {
            super( startPoint, endPoint );
            this.battery = battery;
            this.startYOffset = startYOffset;
            battery.addPolarityObserver( this );
        }

        public void cleanup() {
            super.cleanup();
            battery.removePolarityObserver( this );
        }

        protected Battery getBattery() {
            return battery;
        }

        protected double getStartYOffset() {
            return startYOffset;
        }
    }

    /**
     * Wire segment whose start point is connected to the top terminal of a battery.
     * Adjusts the start point when the battery's polarity changes.
     */
    public static class BatteryTopWireSegment extends BatteryWireSegment {

        public BatteryTopWireSegment( final Battery battery, double startYOffset, Point2D endPoint ) {
            super( battery, startYOffset, new Double( battery.getX(), battery.getY() + battery.getTopTerminalYOffset() ), endPoint );
        }

        public void update() {
            Battery battery = getBattery();
            startPointProperty.set( new Point2D.Double( battery.getX(), battery.getY() + battery.getTopTerminalYOffset() - getStartYOffset() ) );
        }
    }

    /**
     * Wire segment whose start point is connected to the bottom terminal of a battery.
     * Adjusts the start point when the battery's polarity changes.
     */
    public static class BatteryBottomWireSegment extends BatteryWireSegment {

        public BatteryBottomWireSegment( final Battery battery, double startYOffset, Point2D endPoint ) {
            super( battery, startYOffset, new Double( battery.getX(), battery.getY() + battery.getBottomTerminalYOffset() ), endPoint );
        }

        public void update() {
            Battery battery = getBattery();
            startPointProperty.set( new Double( battery.getX(), battery.getY() + battery.getBottomTerminalYOffset() + getStartYOffset() ) );
        }
    }

    // Any wire segment that is connected to one capacitor.
    private static abstract class CapacitorWireSegment extends WireSegment implements SimpleObserver {

        private Capacitor capacitor;

        public CapacitorWireSegment( Capacitor capacitor, Point2D startPoint, Point2D endPoint ) {
            super( startPoint, endPoint );
            this.capacitor = capacitor;
            capacitor.addPlateSeparationObserver( this );
        }

        public void cleanup() {
            super.cleanup();
            capacitor.removePlateSeparationObserver( this );
        }

        protected Capacitor getCapacitor() {
            return capacitor;
        }
    }

    /**
     * Wire segment whose start point is connected to the top plate of a capacitor.
     * Adjusts the start point when the plate separation changes.
     */
    public static class CapacitorTopWireSegment extends CapacitorWireSegment {

        public CapacitorTopWireSegment( Capacitor capacitor, Point2D endPoint ) {
            super( capacitor, new Point2D.Double( capacitor.getTopPlateCenter().getX(), capacitor.getTopPlateCenter().getY() ), endPoint );
        }

        public void update() {
            Capacitor capacitor = getCapacitor();
            startPointProperty.set( new Double( capacitor.getTopPlateCenter().getX(), capacitor.getTopPlateCenter().getY() ) );
        }
    }

    /**
     * Wire segment whose start point is connected to the bottom plate of a capacitor.
     * Adjusts the start point when the plate separation changes.
     */
    public static class CapacitorBottomWireSegment extends CapacitorWireSegment {

        public CapacitorBottomWireSegment( final Capacitor capacitor, Point2D endPoint ) {
            super( capacitor, new Point2D.Double( capacitor.getBottomPlateCenter().getX(), capacitor.getBottomPlateCenter().getY() ), endPoint );
        }

        public void update() {
            Capacitor capacitor = getCapacitor();
            startPointProperty.set( new Double( capacitor.getBottomPlateCenter().getX(), capacitor.getBottomPlateCenter().getY() ) );
        }
    }

    /**
     * Wire segment that connects the bottom plate of one capacitor to the top plate of another capacitor.
     * Adjusts the start and end points when the plate separations change.
     */
    public static class CapacitorToCapacitorWireSegment extends WireSegment implements SimpleObserver {

        private final Capacitor topCapacitor, bottomCapacitor;

        public CapacitorToCapacitorWireSegment( final Capacitor topCapacitor, final Capacitor bottomCapacitor ) {
            super( new Point2D.Double( topCapacitor.getBottomPlateCenter().getX(), topCapacitor.getBottomPlateCenter().getY() ),
                   new Point2D.Double( bottomCapacitor.getTopPlateCenter().getX(), bottomCapacitor.getTopPlateCenter().getY() ) );

            this.topCapacitor = topCapacitor;
            this.bottomCapacitor = bottomCapacitor;

            topCapacitor.addPlateSeparationObserver( this );
            bottomCapacitor.addPlateSeparationObserver( this );
        }

        public void cleanup() {
            super.cleanup();
            topCapacitor.removePlateSeparationObserver( this );
            bottomCapacitor.removePlateSeparationObserver( this );
        }

        public void update() {
            startPointProperty.set( new Double( topCapacitor.getBottomPlateCenter().getX(), topCapacitor.getBottomPlateCenter().getY() ) );
            endPointProperty.set( new Double( bottomCapacitor.getTopPlateCenter().getX(), bottomCapacitor.getTopPlateCenter().getY() ) );
        }
    }
}

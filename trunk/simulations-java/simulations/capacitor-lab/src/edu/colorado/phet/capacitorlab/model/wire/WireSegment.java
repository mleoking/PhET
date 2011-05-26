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
        // do nothing
    }

    /**
     * Wire segment whose start point is connected to the top terminal of a battery.
     * Adjusts the start point when the battery's polarity changes.
     */
    public static class BatteryTopWireSegment extends WireSegment {

        private final Battery battery;
        private final SimpleObserver polarityObserver;

        public BatteryTopWireSegment( final Battery battery, Point2D endPoint ) {
            super( new Point2D.Double( battery.getX(), battery.getY() + battery.getTopTerminalYOffset() ), endPoint );
            polarityObserver = new SimpleObserver() {
                public void update() {
                    startPointProperty.set( new Point2D.Double( battery.getX(), battery.getY() + battery.getTopTerminalYOffset() ) );
                }
            };
            this.battery = battery;
            battery.addPolarityObserver( polarityObserver );
        }

        public void cleanup() {
            super.cleanup();
            battery.removePolarityObserver( polarityObserver );
        }
    }

    /**
     * Wire segment whose start point is connected to the bottom terminal of a battery.
     * Adjusts the start point when the battery's polarity changes.
     */
    public static class BatteryBottomWireSegment extends WireSegment {

        private final Battery battery;
        private final SimpleObserver polarityObserver;

        public BatteryBottomWireSegment( final Battery battery, Point2D endPoint ) {
            super( new Point2D.Double( battery.getX(), battery.getY() + battery.getBottomTerminalYOffset() ), endPoint );
            polarityObserver = new SimpleObserver() {
                public void update() {
                    startPointProperty.set( new Double( battery.getX(), battery.getY() + battery.getBottomTerminalYOffset() ) );
                }
            };
            this.battery = battery;
            battery.addPolarityObserver( polarityObserver );
        }

        public void cleanup() {
            super.cleanup();
            battery.removePolarityObserver( polarityObserver );
        }
    }

    /**
     * Wire segment whose end point is connected to the top plate of a capacitor.
     * Adjusts the end point when the plate separation changes.
     */
    public static class CapacitorTopWireSegment extends WireSegment {

        private final Capacitor capacitor;
        private final SimpleObserver plateSeparationObserver;

        public CapacitorTopWireSegment( Point2D startPoint, final Capacitor capacitor ) {
            super( startPoint, new Point2D.Double( capacitor.getTopPlateCenter().getX(), capacitor.getTopPlateCenter().getY() ) );
            plateSeparationObserver = new SimpleObserver() {
                public void update() {
                    endPointProperty.set( new Double( capacitor.getTopPlateCenter().getX(), capacitor.getTopPlateCenter().getY() ) );
                }
            };
            this.capacitor = capacitor;
            capacitor.addPlateSeparationObserver( plateSeparationObserver );
        }

        public void cleanup() {
            super.cleanup();
            capacitor.removePlateSeparationObserver( plateSeparationObserver );
        }
    }

    /**
     * Wire segment whose end point is connected to the bottom plate of a capacitor.
     * Adjusts the end point when the plate separation changes.
     */
    public static class CapacitorBottomWireSegment extends WireSegment {

        private final Capacitor capacitor;
        private final SimpleObserver plateSeparationObserver;

        public CapacitorBottomWireSegment( Point2D startPoint, final Capacitor capacitor ) {
            super( startPoint, new Point2D.Double( capacitor.getBottomPlateCenter().getX(), capacitor.getBottomPlateCenter().getY() ) );
            plateSeparationObserver = new SimpleObserver() {
                public void update() {
                    endPointProperty.set( new Double( capacitor.getBottomPlateCenter().getX(), capacitor.getBottomPlateCenter().getY() ) );
                }
            };
            this.capacitor = capacitor;
            capacitor.addPlateSeparationObserver( plateSeparationObserver );
        }

        public void cleanup() {
            super.cleanup();
            capacitor.removePlateSeparationObserver( plateSeparationObserver );
        }
    }

    /**
     * Wire segment that connects the bottom plate of one capacitor to the top plate of another capacitor.
     * Adjusts the start and end points when the plate separations change.
     */
    public static class CapacitorToCapacitorWireSegment extends WireSegment {

        private final Capacitor topCapacitor, bottomCapacitor;
        private final SimpleObserver topSeparationObserver, bottomSeparationObserver;

        public CapacitorToCapacitorWireSegment( final Capacitor topCapacitor, final Capacitor bottomCapacitor ) {
            super( new Point2D.Double( topCapacitor.getBottomPlateCenter().getX(), topCapacitor.getBottomPlateCenter().getY() ),
                   new Point2D.Double( bottomCapacitor.getTopPlateCenter().getX(), bottomCapacitor.getTopPlateCenter().getY() ) );

            topSeparationObserver = new SimpleObserver() {
                public void update() {
                    startPointProperty.set( new Double( topCapacitor.getBottomPlateCenter().getX(), topCapacitor.getBottomPlateCenter().getY() ) );
                }
            };
            this.topCapacitor = topCapacitor;
            topCapacitor.addPlateSeparationObserver( topSeparationObserver );

            bottomSeparationObserver = new SimpleObserver() {
                public void update() {
                    endPointProperty.set( new Double( bottomCapacitor.getTopPlateCenter().getX(), bottomCapacitor.getTopPlateCenter().getY() ) );
                }
            };
            this.bottomCapacitor = bottomCapacitor;
            bottomCapacitor.addPlateSeparationObserver( bottomSeparationObserver );
        }

        public void cleanup() {
            super.cleanup();
            topCapacitor.removePlateSeparationObserver( topSeparationObserver );
            bottomCapacitor.removePlateSeparationObserver( bottomSeparationObserver );
        }
    }
}

// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.geom.Point2D;

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

    /**
     * Wire segment whose start point is connected to the top terminal of a battery.
     * Adjusts the start point when the battery's polarity changes.
     */
    public static class BatteryTopWireSegment extends WireSegment {

        public BatteryTopWireSegment( final Battery battery, Point2D endPoint ) {
            super( new Point2D.Double( battery.getX(), battery.getY() + battery.getTopTerminalYOffset() ), endPoint );
            battery.addPolarityObserver( new SimpleObserver() {
                public void update() {
                    startPointProperty.set( new Point2D.Double( battery.getX(), battery.getY() + battery.getTopTerminalYOffset() ) );
                }
            } );
        }
    }

    /**
     * Wire segment whose start point is connected to the bottom terminal of a battery.
     * Adjusts the start point when the battery's polarity changes.
     */
    public static class BatteryBottomWireSegment extends WireSegment {

        public BatteryBottomWireSegment( final Battery battery, Point2D endPoint ) {
            super( new Point2D.Double( battery.getX(), battery.getY() + battery.getBottomTerminalYOffset() ), endPoint );
            battery.addPolarityObserver( new SimpleObserver() {
                public void update() {
                    startPointProperty.set( new Point2D.Double( battery.getX(), battery.getY() + battery.getBottomTerminalYOffset() ) );
                }
            } );
        }
    }

    /**
     * Wire segment whose end point is connected to the top plate of a capacitor.
     * Adjusts the end point when the plate separation changes.
     */
    public static class CapacitorTopWireSegment extends WireSegment {

        public CapacitorTopWireSegment( Point2D startPoint, final Capacitor capacitor ) {
            super( startPoint, new Point2D.Double( capacitor.getTopPlateCenter().getX(), capacitor.getTopPlateCenter().getY() ) );
            capacitor.addPlateSeparationObserver( new SimpleObserver() {
                public void update() {
                    endPointProperty.set( new Point2D.Double( capacitor.getTopPlateCenter().getX(), capacitor.getTopPlateCenter().getY() ) );
                }
            } );
        }
    }

    /**
     * Wire segment whose end point is connected to the bottom plate of a capacitor.
     * Adjusts the end point when the plate separation changes.
     */
    public static class CapacitorBottomWireSegment extends WireSegment {

        public CapacitorBottomWireSegment( Point2D startPoint, final Capacitor capacitor ) {
            super( startPoint, new Point2D.Double( capacitor.getBottomPlateCenter().getX(), capacitor.getBottomPlateCenter().getY() ) );
            capacitor.addPlateSeparationObserver( new SimpleObserver() {
                public void update() {
                    endPointProperty.set( new Point2D.Double( capacitor.getBottomPlateCenter().getX(), capacitor.getBottomPlateCenter().getY() ) );
                }
            } );
        }
    }

    /**
     * Wire segment that connects the bottom plate of one capacitor to the top plate of another capacitor.
     * Adjusts the start and end points when the plate separations change.
     */
    public static class CapacitorToCapacitorWireSegment extends WireSegment {
        public CapacitorToCapacitorWireSegment( final Capacitor topCapacitor, final Capacitor bottomCapacitor ) {
            super( new Point2D.Double( topCapacitor.getBottomPlateCenter().getX(), topCapacitor.getBottomPlateCenter().getY() ),
                   new Point2D.Double( bottomCapacitor.getTopPlateCenter().getX(), bottomCapacitor.getTopPlateCenter().getY() ) );

            topCapacitor.addPlateSeparationObserver( new SimpleObserver() {
                public void update() {
                    startPointProperty.set( new Point2D.Double( topCapacitor.getBottomPlateCenter().getX(), topCapacitor.getBottomPlateCenter().getY() ) );
                }
            } );

            bottomCapacitor.addPlateSeparationObserver( new SimpleObserver() {
                public void update() {
                    endPointProperty.set( new Point2D.Double( bottomCapacitor.getTopPlateCenter().getX(), bottomCapacitor.getTopPlateCenter().getY() ) );
                }
            } );
        }
    }
}

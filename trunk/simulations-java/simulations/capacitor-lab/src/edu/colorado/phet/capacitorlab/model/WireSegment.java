// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A straight segment of wire. One or more segments are joined to create a wire.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireSegment {

    // observable properties
    private final Property<Point2D> startPointProperty, endPointProperty;

    public WireSegment( Point2D startPoint, Point2D endPoint ) {
        this( startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY() );
    }
        
    public WireSegment( double startX, double startY, double endX, double endY ) {
        this.startPointProperty = new Property<Point2D>( new Point2D.Double( startX, startY ) );
        this.endPointProperty = new Property<Point2D>( new Point2D.Double( endX, endY ) );
    }
    
    public void addStartPointObserver( SimpleObserver o ) {
        startPointProperty.addObserver( o );
    }
    
    public void removeStartPointObserver( SimpleObserver o ) {
        startPointProperty.removeObserver( o );
    }
    
    public Point2D getStartPoint() {
        return new Point2D.Double( startPointProperty.getValue().getX(), startPointProperty.getValue().getY() );
    }

    public void setStartPoint( Point2D startPoint ) {
        this.startPointProperty.setValue( new Point2D.Double( startPoint.getX(), startPoint.getY() )  );
    }

    public void addEndPointObserver( SimpleObserver o ) {
        endPointProperty.addObserver( o );
    }
    
    public void removeEndPointObserver( SimpleObserver o ) {
        endPointProperty.removeObserver( o );
    }
    
    public Point2D getEndPoint() {
        return new Point2D.Double( endPointProperty.getValue().getX(), endPointProperty.getValue().getY() );
    }

    public void setEndPoint( Point2D endPoint ) {
        this.endPointProperty.setValue( new Point2D.Double( endPoint.getX(), endPoint.getY() ) );
    }
    
    /**
     * Wire segment whose start point is connected to the top terminal of a battery.
     */
    public static class BatteryTopWireSegment extends WireSegment {
        
        public BatteryTopWireSegment( final Battery battery, Point2D endPoint ) {
            super( new Point2D.Double( battery.getX(), battery.getY() + battery.getTopTerminalYOffset() ), endPoint );
            battery.addPolarityObserver( new SimpleObserver() {
                public void update() {
                    setStartPoint( new Point2D.Double( battery.getX(), battery.getY() + battery.getTopTerminalYOffset() ) );
                }
            } );
        }
    }
    
    /**
     * Wire segment whose start point is connected to the bottom terminal of a battery.
     */
    public static class BatteryBottomWireSegment extends WireSegment {
        
        public BatteryBottomWireSegment( final Battery battery, Point2D endPoint ) {
            super( new Point2D.Double( battery.getX(), battery.getY() + battery.getBottomTerminalYOffset() ), endPoint );
            battery.addPolarityObserver( new SimpleObserver() {
                public void update() {
                    setStartPoint( new Point2D.Double( battery.getX(), battery.getY() + battery.getBottomTerminalYOffset() ) );
                }
            });
        }
    }
    
    /**
     * Wire segment whose end point is connected to the top plate of a capacitor.
     */
    public static class CapacitorTopWireSegment extends WireSegment {
        
        public CapacitorTopWireSegment( Point2D startPoint, final Capacitor capacitor ) {
            super( startPoint, new Point2D.Double( capacitor.getTopPlateCenter().getX(), capacitor.getTopPlateCenter().getY() ) );
            capacitor.addPlateSeparationObserver( new SimpleObserver() {
                public void update() {
                    setEndPoint( new Point2D.Double( capacitor.getTopPlateCenter().getX(), capacitor.getTopPlateCenter().getY() ) );
                }
            } );
        }
    }
    
    /**
     * Wire segment whose end point is connected to the bottom plate of a capacitor.
     */
    public static class CapacitorBottomWireSegment extends WireSegment {
        
        public CapacitorBottomWireSegment( Point2D startPoint, final Capacitor capacitor ) {
            super( startPoint, new Point2D.Double( capacitor.getBottomPlateCenter().getX(), capacitor.getBottomPlateCenter().getY() ) );
            capacitor.addPlateSeparationObserver( new SimpleObserver() {
                public void update() {
                    setEndPoint( new Point2D.Double( capacitor.getBottomPlateCenter().getX(), capacitor.getBottomPlateCenter().getY() ) );
                }
            } );
        }
    }
}

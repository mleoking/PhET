/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.WireSegment.BatteryBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.WireSegment.BatteryTopWireSegment;
import edu.colorado.phet.capacitorlab.model.WireSegment.CapacitorBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.WireSegment.CapacitorTopWireSegment;
import edu.colorado.phet.capacitorlab.util.ShapeUtils;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A wire is a collection of connected wire segments.
 * It has an associated voltage.
 * <p>
 * NOTE: It's the client's responsibility to ensure 
 * that all segments are connected.  No checking is done here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Wire {

    private final ArrayList<WireSegment> wireSegments;
    private final double thickness;

    private final Property<Shape> shapeProperty;
    private final Property<Double> voltageProperty;

    public Wire( ArrayList<WireSegment> wireSegments, double thickness ) {
        assert ( wireSegments != null && wireSegments.size() > 0 );
        assert ( thickness > 0 );

        this.thickness = thickness;
        this.wireSegments = new ArrayList<WireSegment>( wireSegments );
        this.shapeProperty = new Property<Shape>( createShape() );
        this.voltageProperty = new Property<Double>( 0.0 );

        // when any segment changes, update the shape property
        {
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    shapeProperty.setValue( createShape() );
                }
            };
            for ( WireSegment segment : wireSegments ) {
                segment.addStartPointObserver( o );
                segment.addEndPointObserver( o );
            }
        }
    }

    public double getThickness() {
        return thickness;
    }

    public double getVoltage() {
        return voltageProperty.getValue();
    }

    public void setVoltage( double voltage ) {
        voltageProperty.setValue( voltage );
    }

    public void addShapeObserver( SimpleObserver o ) {
        shapeProperty.addObserver( o );
    }

    public Shape getShape() {
        return shapeProperty.getValue();
    }

    private Shape createShape() {
        Area area = new Area();
        for ( WireSegment wireSegment : wireSegments ) {
            area.add( new Area( wireSegment.createShape( thickness ) ) );
        }
        return area;
    }

    public boolean intersects( Shape shape ) {
        return ShapeUtils.intersects( shapeProperty.getValue(), shape );
    }

    /**
     * Wire that connects the tops of a battery and capacitor.
     */
    public static class TopWire extends Wire {

        public TopWire( final Battery battery, final Capacitor capacitor, double thickness ) {
            super( createSegments( battery, capacitor ), thickness );
        }

        private static ArrayList<WireSegment> createSegments( final Battery battery, final Capacitor capacitor ) {
            final Point2D.Double leftCorner = new Point2D.Double( battery.getX(), battery.getY() - CLConstants.WIRE_EXTENT );
            final Point2D.Double rightCorner = new Point2D.Double( capacitor.getX(), leftCorner.getY() );
            ArrayList<WireSegment> segments = new ArrayList<WireSegment>() {{
                add( new BatteryTopWireSegment( battery, leftCorner ) );
                add( new WireSegment( leftCorner, rightCorner ) );
                add( new CapacitorTopWireSegment( rightCorner, capacitor ) );
            }};
            return segments;
        }
    }

    /**
     * Wire that connects the bottoms of a battery and capacitor.
     */
    public static class BottomWire extends Wire {

        public BottomWire( final Battery battery, final Capacitor capacitor, double thickness ) {
            super( createSegments( battery, capacitor ), thickness );
        }

        private static ArrayList<WireSegment> createSegments( final Battery battery, final Capacitor capacitor ) {
            final Point2D.Double leftCorner = new Point2D.Double( battery.getX(), battery.getY() + CLConstants.WIRE_EXTENT );
            final Point2D.Double rightCorner = new Point2D.Double( capacitor.getX(), leftCorner.getY() );
            ArrayList<WireSegment> segments = new ArrayList<WireSegment>() {{
                add( new BatteryBottomWireSegment( battery, leftCorner ) );
                add( new WireSegment( leftCorner, rightCorner ) );
                add( new CapacitorBottomWireSegment( rightCorner, capacitor ) );
            }};
            return segments;
        }
    }
}

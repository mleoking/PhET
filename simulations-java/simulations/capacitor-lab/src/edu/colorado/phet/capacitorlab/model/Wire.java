// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.WireSegment.BatteryBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.WireSegment.BatteryTopWireSegment;
import edu.colorado.phet.capacitorlab.model.WireSegment.CapacitorBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.WireSegment.CapacitorTopWireSegment;
import edu.colorado.phet.capacitorlab.shapes.WireShapeFactory;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

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

    private final ArrayList<WireSegment> segments;
    private final double thickness;
    private final WireShapeFactory shapeFactory;
    private final SimpleObserver segmentObserver;

    // observable properties
    private final Property<Shape> shapeProperty; // Shape in view coordinates!
    private final Property<Double> voltageProperty;

    public Wire( ArrayList<WireSegment> segments, double thickness, CLModelViewTransform3D mvt ) {
        assert ( segments != null && segments.size() > 0 );
        assert ( thickness > 0 );

        this.segments = new ArrayList<WireSegment>( segments );
        this.thickness = thickness;
        this.shapeFactory = new WireShapeFactory( this, mvt );

        this.shapeProperty = new Property<Shape>( createShape() );
        this.voltageProperty = new Property<Double>( 0.0 );

        // when any segment changes, update the shape property
        {
            segmentObserver = new SimpleObserver() {
                public void update() {
                    setShape( createShape() );
                }
            };
            for ( WireSegment segment : segments ) {
                segment.addStartPointObserver( segmentObserver );
                segment.addEndPointObserver( segmentObserver );
            }
        }
    }

    public void addSegment( WireSegment segment ) {
        if ( !segments.contains( segment ) ) {
            segments.add( segment );
            segment.addStartPointObserver( segmentObserver );
            segment.addEndPointObserver( segmentObserver );
        }
    }

    public void removeSegment( WireSegment segment ) {
        if ( segments.contains( segment ) ) {
            segments.remove( segment );
            segment.removeStartPointObserver( segmentObserver );
            segment.removeEndPointObserver( segmentObserver );
        }
    }

    public double getThickness() {
        return thickness;
    }

    public ArrayList<WireSegment> getSegmentsReference() {
        return segments;
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

    protected void setShape( Shape shape ) {
        shapeProperty.setValue( shape );
    }

    public boolean intersects( Shape shape ) {
        return ShapeUtils.intersects( shapeProperty.getValue(), shape );
    }

    protected Shape createShape() {
        return shapeFactory.createWireShape();
    }

    /**
     * Wire that connects the tops of a battery and capacitor.
     */
    public static class TopWire extends Wire {

        public TopWire( final Battery battery, final Capacitor capacitor, double thickness, CLModelViewTransform3D mvt ) {
            super( createSegments( battery, capacitor, thickness ), thickness, mvt );
        }

        private static ArrayList<WireSegment> createSegments( final Battery battery, final Capacitor capacitor, double thickness ) {
            final Point2D.Double leftCorner = new Point2D.Double( battery.location.getX(), battery.location.getY() - CLConstants.WIRE_EXTENT );
            final Point2D.Double rightCorner = new Point2D.Double( capacitor.getX(), leftCorner.getY() );
            final double t = ( thickness / 2 ); // for proper connection at corners with CAP_BUTT wire stroke
            ArrayList<WireSegment> segments = new ArrayList<WireSegment>() {{
                add( new BatteryTopWireSegment( battery, leftCorner ) );
                add( new WireSegment( leftCorner.getX() - t, leftCorner.getY() + t, rightCorner.getX() + t, rightCorner.getY() + t ) );
                add( new CapacitorTopWireSegment( rightCorner, capacitor ) );
            }};
            return segments;
        }
    }

    /**
     * Wire that connects the bottoms of a battery and capacitor.
     */
    public static class BottomWire extends Wire {

        private final Battery battery;
        private final Capacitor capacitor;

        public BottomWire( final Battery battery, final Capacitor capacitor, double thickness, CLModelViewTransform3D mvt ) {
            super( createSegments( battery, capacitor, thickness ), thickness, mvt );

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

        private static ArrayList<WireSegment> createSegments( final Battery battery, final Capacitor capacitor, double thickness ) {
            final Point2D.Double leftCorner = new Point2D.Double( battery.location.getX(), battery.location.getY() + CLConstants.WIRE_EXTENT );
            final Point2D.Double rightCorner = new Point2D.Double( capacitor.getX(), leftCorner.getY() );
            final double t = ( thickness / 2 ); // for proper connection at corners with CAP_BUTT wire stroke
            ArrayList<WireSegment> segments = new ArrayList<WireSegment>() {{
                add( new BatteryBottomWireSegment( battery, leftCorner ) );
                add( new WireSegment( leftCorner.getX() - t, leftCorner.getY() + t, rightCorner.getX() + t, rightCorner.getY() + t ) );
                add( new CapacitorBottomWireSegment( rightCorner, capacitor ) );
            }};
            return segments;
        }

        /*
         * Subtract any part of the wire that is occluded by the battery or bottom plate.
         */
        @Override
        protected Shape createShape() {
            Shape wireShape = super.createShape();
            // HACK: null check required because createShape is called in the superclass constructor.
            if ( battery != null && capacitor != null ) {
                wireShape = ShapeUtils.subtract( wireShape, battery.shapeFactory.createBodyShape(), capacitor.getShapeFactory().createBottomPlateShape() );
            }
            return wireShape;
        }
    }
}

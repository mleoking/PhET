/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.model.WireSegment.BatteryBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.WireSegment.BatteryTopWireSegment;
import edu.colorado.phet.capacitorlab.model.WireSegment.CapacitorBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.WireSegment.CapacitorTopWireSegment;
import edu.colorado.phet.capacitorlab.shapes.CapacitorShapeFactory;
import edu.colorado.phet.capacitorlab.shapes.WireShapeFactory;
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

    private final ArrayList<WireSegment> segments;
    private final double thickness;
    private final WireShapeFactory shapeFactory;

    private final Property<Shape> shapeProperty; // Shape in view coordinates!
    private final Property<Double> voltageProperty;

    public Wire( ArrayList<WireSegment> segments, double thickness, ModelViewTransform mvt ) {
        assert ( segments != null && segments.size() > 0 );
        assert ( thickness > 0 );

        this.segments = new ArrayList<WireSegment>( segments );
        this.thickness = thickness;
        this.shapeFactory = new WireShapeFactory( this, mvt );
        
        this.shapeProperty = new Property<Shape>( createShape() );
        this.voltageProperty = new Property<Double>( 0.0 );

        // when any segment changes, update the shape property
        {
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    setShape( createShape() );
                }
            };
            for ( WireSegment segment : segments ) {
                segment.addStartPointObserver( o );
                segment.addEndPointObserver( o );
            }
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
        return shapeFactory.createShape();
    }

    /**
     * Wire that connects the tops of a battery and capacitor.
     */
    public static class TopWire extends Wire {

        public TopWire( final Battery battery, final Capacitor capacitor, double thickness, ModelViewTransform mvt ) {
            super( createSegments( battery, capacitor ), thickness, mvt );
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

        private final CapacitorShapeFactory capacitorShapeFactory;
        
        public BottomWire( final Battery battery, final Capacitor capacitor, double thickness, ModelViewTransform mvt ) {
            super( createSegments( battery, capacitor ), thickness, mvt );
            this.capacitorShapeFactory = new CapacitorShapeFactory( capacitor, mvt );
            
            capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {
                
                @Override
                public void plateSizeChanged() {
                    setShape( createShape() );
                }
                
                @Override
                public void plateSeparationChanged() {
                    setShape( createShape() );
                }
                
                @Override
                public void dielectricOffsetChanged() {
                    setShape( createShape() );
                }
            });
            
            setShape( createShape() ); // require because of hack in createShape
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
        
        @Override
        /*
         * Subtract any part of the wire that is occluded by the top plate or dielectric.
         * Occlusion by the bottom plate is OK because the wire and bottom plate have the same potential.
         */
        protected Shape createShape() {
            Shape shape = null;
            Shape wireShape = super.createShape();
            if ( capacitorShapeFactory == null ) {
                /*
                 * HACK: Null check required because createShape is called in the superclass constructor.
                 * A call to setShape at the end of the constructor is required for proper construction.
                 */
                shape = wireShape;
            }
            else {
                Area area = new Area( wireShape );
                area.subtract( new Area( capacitorShapeFactory.createTopPlateShape() ) );
                area.subtract( new Area( capacitorShapeFactory.createDielectricShapeOccluded() ) );
                shape = area;
            }
            return shape;
        }
    }
}

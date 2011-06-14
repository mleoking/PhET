// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.shapes.WireShapeCreator;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * A wire is a collection of connected wire segments.
 * It contains a creator object that creates the wire shape.
 * The shape is used to display the wire, and to check continuity when measuring voltage.
 * <p/>
 * Note that strict connectivity of the wire segments is not required. In fact, you'll notice that
 * segment endpoints are often adjusted to accommodate the creation of wire shapes that look convincing
 * in the view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Wire {

    private final ArrayList<WireSegment> segments;
    private final SimpleObserver segmentObserver;
    private final double thickness;
    private final WireShapeCreator shapeCreator;

    // observable properties
    private final Property<Shape> shapeProperty; // Shape in view coordinates!

    public Wire( CLModelViewTransform3D mvt, double thickness, ArrayList<WireSegment> segments ) {
        assert ( segments != null );
        assert ( thickness > 0 );

        this.segments = new ArrayList<WireSegment>( segments );
        this.thickness = thickness;
        this.shapeCreator = new WireShapeCreator( this, mvt );

        this.shapeProperty = new Property<Shape>( createShape() );

        // when any segment changes, update the shape property
        segmentObserver = new SimpleObserver() {
            public void update() {
                setShape( createShape() );
            }
        };
        for ( WireSegment segment : segments ) {
            addSegmentObserver( segment, segmentObserver );
        }
    }

    // For use by subclasses who wish to add their segments via addSegment.
    protected Wire( CLModelViewTransform3D mvt, double thickness ) {
        this( mvt, thickness, new ArrayList<WireSegment>() );
    }

    public void cleanup() {
        for ( WireSegment segment : segments ) {
            segment.cleanup();
        }
    }

    protected void addSegment( WireSegment segment ) {
        segments.add( segment );
        addSegmentObserver( segment, segmentObserver );
    }

    private static void addSegmentObserver( WireSegment segment, SimpleObserver observer ) {
        segment.startPointProperty.addObserver( observer );
        segment.endPointProperty.addObserver( observer );
    }

    public double getThickness() {
        return thickness;
    }

    public ArrayList<WireSegment> getSegments() {
        return new ArrayList<WireSegment>( segments );
    }

    public void addShapeObserver( SimpleObserver o ) {
        shapeProperty.addObserver( o );
    }

    public Shape getShape() {
        return shapeProperty.get();
    }

    protected void setShape( Shape shape ) {
        shapeProperty.set( shape );
    }

    public boolean intersects( Shape shape ) {
        return ShapeUtils.intersects( shapeProperty.get(), shape );
    }

    protected Shape createShape() {
        return shapeCreator.createWireShape();
    }

    protected double getCornerOffset() {
        return shapeCreator.getCornerOffset();
    }

    protected double getEndOffset() {
        return shapeCreator.getEndOffset();
    }
}

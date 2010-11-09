package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A collection of connected wire segments.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireBranch {
    
    private final ArrayList<WireSegment> wireSegments;
    private Property<Shape> shape;

    public WireBranch( ArrayList<WireSegment> wireSegments ) {
        
        this.wireSegments = wireSegments;
        this.shape = new Property<Shape>( createShape() );
        
        // when any segment changes, update the shape property
        {
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    shape.setValue( createShape() );
                }
            };
            for ( WireSegment segment : wireSegments ) {
                segment.addStartPointChangeListener( o );
                segment.addEndPointChangeListener( o );
            }
        }
    }
    
    public double getVoltage() {
        return 0;//XXX
    }
    
    public Shape getShape() {
        return shape.getValue();
    }
    
    public void addShapeObserver( SimpleObserver o ) {
        shape.addObserver( o );
    }

    private Shape createShape() {
        Area area = new Area();
        for ( WireSegment segment : wireSegments ) {
            area.add( new Area( segment.toShape() ));
        }
        return area;
    }
}

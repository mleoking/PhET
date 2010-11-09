/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A wire is a collection of connected wire segments.
 * It has an associated voltage.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Wire {
    
    private final ArrayList<WireSegment> wireSegments;
    private final Property<Shape> shapeProperty;
    private final Property<Double> voltageProperty;

    public Wire( ArrayList<WireSegment> wireSegments ) {
        
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
    
    public double getThickness() {
        return wireSegments.get( 0 ).getThickness();
    }
    
    private Shape createShape() {
        Area area = new Area();
        for ( WireSegment wireSegment : wireSegments ) {
            area.add( new Area( wireSegment.createShape() ) );
        }
        return area;
    }

    public boolean containsPoint(Point3D pt){
        return shapeProperty.getValue().contains( pt.getX(),pt.getY() );
    }
}

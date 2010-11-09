package edu.colorado.phet.capacitorlab.model;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * A collection of connected wire segments.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Wire {
    
    private final ArrayList<WireSegment> wireSegments;
    private Property<Shape> shapeProperty;
    private double thickness;
    private Property<Double> voltageProperty = new Property<Double>( 0.0 );

    public Wire( double thickness, ArrayList<WireSegment> wireSegments ) {
        this.thickness = thickness;

        this.wireSegments = wireSegments;
        this.shapeProperty = new Property<Shape>( createShape() );
        
        // when any segment changes, update the shape property
        {
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    shapeProperty.setValue( createShape() );
                }
            };
            for ( WireSegment segment : wireSegments ) {
                segment.addStartPointChangeListener( o );
                segment.addEndPointChangeListener( o );
            }
        }
    }
    
    public double getVoltage() {
        return voltageProperty.getValue();
    }
    
    public Shape getShape() {
        return shapeProperty.getValue();
    }
    
    public void addShapeObserver( SimpleObserver o ) {
        shapeProperty.addObserver( o );
    }

    private Shape createShape() {
        return new Area( new BasicStroke( (float) getThickness(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ).createStrokedShape( getPath() ) );
    }

    public double getThickness() {
        return thickness;
    }

    public boolean containsPoint(Point3D pt){
        return shapeProperty.getValue().contains( pt.getX(),pt.getY() );
    }

    public GeneralPath getPath(){
        DoubleGeneralPath path = new DoubleGeneralPath( wireSegments.get(0).getStartPoint() );
        for ( WireSegment wireSegment : wireSegments ) {
            path.lineTo( wireSegment.getEndPoint() );
        }
        return path.getGeneralPath();
    }

    public void setVoltage( double voltage ) {
        voltageProperty.setValue( voltage );
    }
}

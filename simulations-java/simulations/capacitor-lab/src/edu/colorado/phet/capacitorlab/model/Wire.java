package edu.colorado.phet.capacitorlab.model;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;

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
    private Property<Shape> shape;
    private double thickness;

    public Wire( double thickness, ArrayList<WireSegment> wireSegments ) {
        this.thickness = thickness;

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
        return new Area( new BasicStroke( (float) getThickness(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ).createStrokedShape( getPath() ) );
    }

    public double getThickness() {
        return thickness;
    }

    public GeneralPath getPath(){
        DoubleGeneralPath path = new DoubleGeneralPath( wireSegments.get(0).getStartPoint() );
        for ( WireSegment wireSegment : wireSegments ) {
            path.lineTo( wireSegment.getEndPoint() );
        }
        return path.getGeneralPath();
    }
}

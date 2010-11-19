/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Line2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Wire;
import edu.colorado.phet.capacitorlab.model.WireSegment;

/**
 * Creates the 2D shape for a wire.
 * All Shapes are in the global view coordinate frame.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireShapeFactory {

    private final Wire wire;
    private final CLModelViewTransform3D mvt;
    
    public WireShapeFactory( Wire wire, CLModelViewTransform3D mvt ) {
        this.wire = wire;
        this.mvt = mvt;
    }
    
    public Shape createWireShape() {
        Area area = new Area();
        for ( WireSegment segment : wire.getSegmentsReference() ) {
            Shape s = createWireSegmentShape( segment, wire.getThickness() );
            area.add( new Area( s ) );
        }
        return area;
    }
    
    private Shape createWireSegmentShape( WireSegment segment, double thickness ) {
        Line2D line = new Line2D.Double( segment.getStartPoint(), segment.getEndPoint() );
        Stroke stroke = new BasicStroke( (float) thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ); // use CAP_BUTT so that ends are accurate
        Shape s = new Area( stroke.createStrokedShape( line ) );
        return mvt.modelToView( s );
    }
}

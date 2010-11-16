/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Line2D;

import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
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
    private final ModelViewTransform mvt;
    
    public WireShapeFactory( Wire wire, ModelViewTransform mvt ) {
        this.wire = wire;
        this.mvt = mvt;
    }
    
    public Shape createShape() {
        Area area = new Area();
        for ( WireSegment segment : wire.getSegmentsReference() ) {
            Shape s = createSegmentShape( segment, wire.getThickness() );
            area.add( new Area( s ) );
        }
        return area;
    }
    
    private Shape createSegmentShape( WireSegment segment, double thickness ) {
        Line2D line = new Line2D.Double( segment.getStartPoint(), segment.getEndPoint() );
        /* TODO:
         * CAP_SQUARE ensures that the joints between segments will look correct.
         * But it makes the termination ends of the wires a tad longer than desired.
         */
        Stroke stroke = new BasicStroke( (float) thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
        Shape s = new Area( stroke.createStrokedShape( line ) );
        return mvt.modelToView( s );
    }
}

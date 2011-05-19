// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment;

/**
 * Creates the 2D shape for a wire.
 * Shapes are in the global view coordinate frame.
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

    // Create the wire shape by using constructive area geometry to add shapes of wire segments.
    public Shape createWireShape() {
        Area area = new Area();
        for ( WireSegment segment : wire.getSegmentsReference() ) {
            Shape s = createWireSegmentShape( segment, wire.getThickness() );
            area.add( new Area( s ) );
        }
        return area;
    }

    // Create the shape for one wire segment.
    private Shape createWireSegmentShape( WireSegment segment, double thickness ) {
        Line2D line = new Line2D.Double( segment.startPointProperty.get(), segment.endPointProperty.get() );
        Stroke stroke = new BasicStroke( (float) thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ); // use CAP_BUTT so that ends are accurate
        Shape s = new Area( stroke.createStrokedShape( line ) );
        return mvt.modelToView( s );
    }
}

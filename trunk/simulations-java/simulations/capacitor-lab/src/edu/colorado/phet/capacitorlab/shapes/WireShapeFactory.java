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

    // Determines how the wires are capped. If you change this, you'll need to fiddle with getEndOffset and getCornerOffset.
    private static final int CAP_STYLE = BasicStroke.CAP_ROUND;

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
        Stroke stroke = new BasicStroke( (float) thickness, CAP_STYLE, BasicStroke.JOIN_MITER );
        Shape s = new Area( stroke.createStrokedShape( line ) );
        return mvt.modelToView( s );
    }

    // Offset required to make 2 segments join seamlessly at a corner. This is specific to CAP_STYLE.
    public double getCornerOffset() {
        return 0;
    }

    // Offset required to make a wire align properly with some endpoint (eg, a battery terminal). This is specific to CAP_STYLE.
    public double getEndOffset() {
        return wire.getThickness() / 2;
    }
}

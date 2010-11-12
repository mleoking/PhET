/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;
import java.awt.geom.Area;

import edu.colorado.phet.capacitorlab.model.Wire;
import edu.colorado.phet.capacitorlab.model.WireSegment;

/**
 * Creates the 2D shape for a wire.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireShapeFactory {

    private final Wire wire;
    
    public WireShapeFactory( Wire wire ) {
        this.wire = wire;
    }
    
    public Shape createShape() {
        Area area = new Area();
        for ( WireSegment segment : wire.getSegmentsReference() ) {
            area.add( new Area( segment.createShape( wire.getThickness() ) ) );
        }
        return area;
    }
}

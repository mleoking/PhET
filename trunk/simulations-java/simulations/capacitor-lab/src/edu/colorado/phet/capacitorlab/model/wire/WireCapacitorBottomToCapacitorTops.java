// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorToCapacitorWireSegment;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorTopWireSegment;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * Wire that connects the bottom plate of one capacitor (c) to the top plates of N other capacitor (c1,c2,...,cn).
 * <code>
 * c
 * |
 * |-----|--...--|
 * |     |       |
 * c1   c2      cn
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireCapacitorBottomToCapacitorTops extends Wire {

    private final Capacitor topCapacitor;

    public WireCapacitorBottomToCapacitorTops( CLModelViewTransform3D mvt, double thickness, Capacitor topCapacitor, Capacitor... bottomCapacitors ) {
        this( mvt, thickness, topCapacitor, new ArrayList<Capacitor>( Arrays.asList( bottomCapacitors ) ) );
    }

    public WireCapacitorBottomToCapacitorTops( CLModelViewTransform3D mvt, double thickness, Capacitor topCapacitor, ArrayList<Capacitor> bottomCapacitors ) {
        super( mvt, thickness );

        this.topCapacitor = topCapacitor;

        // connect top capacitor to leftmost bottom capacitor
        addSegment( new CapacitorToCapacitorWireSegment( topCapacitor, bottomCapacitors.get( 0 ) ) );

        if ( bottomCapacitors.size() > 1 ) {
            // horizontal wire above leftmost to rightmost capacitor
            final double t = getCornerOffset(); // for proper connection at corners with wire stroke end style
            final double xStart = topCapacitor.getX() - t;
            final double xEnd = bottomCapacitors.get( bottomCapacitors.size() - 1 ).getX() + t;
            final double y = topCapacitor.getY() + ( ( bottomCapacitors.get( 0 ).getY() - topCapacitor.getY() ) / 2 );
            addSegment( new WireSegment( xStart, y, xEnd, y ) );

            // vertical wires from horizontal wire down to each bottom capacitor
            for ( int i = 1; i < bottomCapacitors.size(); i++ ) {
                double x = bottomCapacitors.get( i ).getX();
                addSegment( new CapacitorTopWireSegment( bottomCapacitors.get( i ), new Point2D.Double( x, y ) ) );
            }
        }

        setShape( createShape() );
    }

    // Subtract any part of the wire that is occluded by the top capacitor's bottom plate.
    @Override protected Shape createShape() {
        Shape wireShape = super.createShape();
        // Null check required because createShape is called in the superclass constructor.
        if ( topCapacitor != null ) {
            wireShape = ShapeUtils.subtract( wireShape, topCapacitor.getShapeFactory().createBottomPlateShape() );
        }
        return wireShape;
    }
}

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
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * A specialized wire, found in all of our circuits.
 * It connects the bottom plate of one capacitor (C1) to the top plates of N other capacitors (C2,C3,...,Cn).
 * <code>
 * C1
 * |
 * |-----|--...--|
 * |     |       |
 * C2   C3      Cn
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireCapacitorToCapacitors extends Wire {

    private final Capacitor topCapacitor;

    /**
     * Constructor
     *
     * @param mvt              model-view transform
     * @param thickness        wire thickness, in meters
     * @param topCapacitor     C1 in the javadoc diagram
     * @param bottomCapacitors C2...Cn in the javadoc diagram
     */
    public WireCapacitorToCapacitors( CLModelViewTransform3D mvt, double thickness, Capacitor topCapacitor, Capacitor... bottomCapacitors ) {
        this( mvt, thickness, topCapacitor, new ArrayList<Capacitor>( Arrays.asList( bottomCapacitors ) ) );
    }

    // Same as above constructor, but with list of bottom capacitors instead of varargs.
    public WireCapacitorToCapacitors( CLModelViewTransform3D mvt, double thickness, Capacitor topCapacitor, ArrayList<Capacitor> bottomCapacitors ) {
        super( mvt, thickness );

        this.topCapacitor = topCapacitor;

        // vertical segment connecting top capacitor (C1) to leftmost bottom capacitor (C2)
        addSegment( new CapacitorToCapacitorWireSegment( topCapacitor, bottomCapacitors.get( 0 ) ) );

        if ( bottomCapacitors.size() > 1 ) {
            // horizontal segment above leftmost (C2) to rightmost (Cn) bottom capacitors
            final double t = getCornerOffset(); // for proper connection at corners with wire stroke end style
            final double xStart = topCapacitor.getX() - t;
            final double xEnd = bottomCapacitors.get( bottomCapacitors.size() - 1 ).getX() + t;
            final double y = topCapacitor.getY() + ( ( bottomCapacitors.get( 0 ).getY() - topCapacitor.getY() ) / 2 );
            addSegment( new WireSegment( xStart, y, xEnd, y ) );

            // vertical segments from horizontal segment down to each bottom capacitor (C2...Cn)
            for ( int i = 1; i < bottomCapacitors.size(); i++ ) {
                double x = bottomCapacitors.get( i ).getX();
                addSegment( new CapacitorTopWireSegment( bottomCapacitors.get( i ), new Point2D.Double( x, y ) ) );
            }
        }

        // Add plate size observer to top capacitor, so we can handle wire occlusion.
        topCapacitor.addPlateSizeObserver( new SimpleObserver() {
            public void update() {
                setShape( createShape() );
            }
        } );
    }

    public void cleanup() {
        super.cleanup();
        //FUTURE topCapacitor.removePlateSizeObserver
    }

    // Subtract any part of the wire that is occluded by the top capacitor's bottom plate.
    @Override protected Shape createShape() {
        Shape wireShape = super.createShape();
        // Null check required because createShape is called in the superclass constructor.
        if ( topCapacitor != null ) {
            wireShape = ShapeUtils.subtract( wireShape, topCapacitor.getShapeCreator().createBottomPlateShape() );
        }
        return wireShape;
    }
}

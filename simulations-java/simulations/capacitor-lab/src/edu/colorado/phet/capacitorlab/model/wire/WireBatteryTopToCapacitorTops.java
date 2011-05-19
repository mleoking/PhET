// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.BatteryTopWireSegment;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorTopWireSegment;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Wire that connects the top of a battery (B) to the tops of N parallel capacitors (c1,c2,...,cn).
 * <code>
 * |-----|------|--...--|
 * |     |      |       |
 * B     c1    c2       cn
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireBatteryTopToCapacitorTops extends Wire {

    public WireBatteryTopToCapacitorTops( CLModelViewTransform3D mvt, double thickness, Battery battery, final Capacitor capacitor ) {
        this( mvt, thickness, battery, new ArrayList<Capacitor>() {{ add( capacitor ); }} );
    }

    public WireBatteryTopToCapacitorTops( final CLModelViewTransform3D mvt, final double thickness, final Battery battery, final ArrayList<Capacitor> capacitors ) {
        super( mvt, thickness, new Function0<ArrayList<WireSegment>>() {
            public ArrayList<WireSegment> apply() {

                // connect battery to the rightmost capacitor
                final Capacitor rightmostCapacitor = capacitors.get( capacitors.size() - 1 );
                final double y = Math.min( battery.getY() - CLConstants.WIRE_EXTENT, rightmostCapacitor.getLocation().getY() - 0.01 );//TODO clean this up
                final Point2D.Double leftCorner = new Point2D.Double( battery.getX(), y );
                final Point2D.Double rightCorner = new Point2D.Double( rightmostCapacitor.getX(), leftCorner.getY() );
                final double t = ( thickness / 2 ); // for proper connection at corners with CAP_BUTT wire stroke
                ArrayList<WireSegment> segments = new ArrayList<WireSegment>() {{
                    add( new BatteryTopWireSegment( battery, leftCorner ) );
                    add( new WireSegment( leftCorner.getX() - t, leftCorner.getY() + t, rightCorner.getX() + t, rightCorner.getY() + t ) );
                    add( new CapacitorTopWireSegment( rightCorner, rightmostCapacitor ) );
                }};

                // add segments for all capacitors in between the battery and rightmost capacitor
                for ( int i = 0; i < capacitors.size() - 1; i++ ) {
                    Capacitor capacitor = capacitors.get( i );
                    Point2D.Double startPoint = new Point2D.Double( capacitor.getX(), y );
                    segments.add( new CapacitorTopWireSegment( startPoint, capacitor ) );
                }

                return segments;
            }
        } );
    }
}
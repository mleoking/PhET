// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.ICapacitor;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.BatteryTopWireSegment;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorTopWireSegment;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Wire that connects the top of a battery to the top of a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireBatteryTopToCapacitorTop extends Wire {

    public WireBatteryTopToCapacitorTop( final CLModelViewTransform3D mvt, final double thickness, final Battery battery, final ICapacitor capacitor ) {
        super( mvt, thickness, new Function0<ArrayList<WireSegment>>() {
            public ArrayList<WireSegment> apply() {
                final double y = Math.min( battery.getY() - CLConstants.WIRE_EXTENT, capacitor.getLocation().getY() - 0.01 );//TODO clean this up
                final Point2D.Double leftCorner = new Point2D.Double( battery.getX(), y );
                final Point2D.Double rightCorner = new Point2D.Double( capacitor.getX(), leftCorner.getY() );
                final double t = ( thickness / 2 ); // for proper connection at corners with CAP_BUTT wire stroke
                ArrayList<WireSegment> segments = new ArrayList<WireSegment>() {{
                    add( new BatteryTopWireSegment( battery, leftCorner ) );
                    add( new WireSegment( leftCorner.getX() - t, leftCorner.getY() + t, rightCorner.getX() + t, rightCorner.getY() + t ) );
                    add( new CapacitorTopWireSegment( rightCorner, capacitor ) );
                }};
                return segments;
            }
        } );
    }
}
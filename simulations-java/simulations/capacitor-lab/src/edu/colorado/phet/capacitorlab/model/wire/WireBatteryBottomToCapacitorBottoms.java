// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.BatteryBottomWireSegment;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorBottomWireSegment;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * Wire that connects the bottom of a battery (B) to the bottoms of N parallel capacitors (c1,c2,...,cn).
 * <code>
 * B     c1    c2       cn
 * |     |      |       |
 * |-----|------|--...--|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireBatteryBottomToCapacitorBottoms extends Wire {

    private final Battery battery;
    private final ArrayList<Capacitor> capacitors;

    public WireBatteryBottomToCapacitorBottoms( CLModelViewTransform3D mvt, double thickness, Battery battery, Capacitor... capacitors ) {
        this( mvt, thickness, battery, new ArrayList<Capacitor>( Arrays.asList( capacitors ) ) );
    }

    public WireBatteryBottomToCapacitorBottoms( CLModelViewTransform3D mvt, double thickness, Battery battery, ArrayList<Capacitor> capacitors ) {
        super( mvt, thickness );

        this.battery = battery;
        this.capacitors = capacitors;

        // find max Y
        double maxY = battery.getY() + CLConstants.WIRE_EXTENT;
        for ( Capacitor capacitor : capacitors ) {
            maxY = Math.max( maxY, capacitor.getLocation().getY() + 0.01 ); //TODO clean this up
        }

        // connect battery to the rightmost capacitor
        final Capacitor rightmostCapacitor = capacitors.get( capacitors.size() - 1 );
        final Point2D.Double leftCorner = new Point2D.Double( battery.getX(), maxY );
        final Point2D.Double rightCorner = new Point2D.Double( rightmostCapacitor.getX(), leftCorner.getY() );
        final double t = getCornerOffset(); // for proper connection at corners with wire stroke end style
        addSegment( new BatteryBottomWireSegment( battery, getEndOffset(), leftCorner ) );
        addSegment( new WireSegment( leftCorner.getX() - t, leftCorner.getY() + t, rightCorner.getX() + t, rightCorner.getY() + t ) );
        addSegment( new CapacitorBottomWireSegment( rightmostCapacitor, rightCorner ) );

        // add segments for all capacitors in between the battery and rightmost capacitor
        for ( int i = 0; i < capacitors.size() - 1; i++ ) {
            Capacitor capacitor = capacitors.get( i );
            Point2D.Double startPoint = new Point2D.Double( capacitor.getX(), maxY );
            addSegment( new CapacitorBottomWireSegment( capacitor, startPoint ) );
        }

        setShape( createShape() );
    }

    // Subtract any part of the wire that is occluded by the battery or one of the bottom plates.
    @Override protected Shape createShape() {
        Shape wireShape = super.createShape();
        // Null check required because createShape is called in the superclass constructor.
        if ( battery != null && capacitors != null ) {
            wireShape = ShapeUtils.subtract( wireShape, battery.getShapeFactory().createBodyShape() );
            for ( Capacitor capacitor : capacitors ) {
                wireShape = ShapeUtils.subtract( wireShape, capacitor.getShapeFactory().createBottomPlateShape() );
            }
        }
        return wireShape;
    }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorToCapacitorWireSegment;
import edu.colorado.phet.capacitorlab.model.wire.WireSegment.CapacitorTopWireSegment;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
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
public class WireCapacitorBottomToCapacitorTop extends Wire {

    private final Capacitor topCapacitor;

    public WireCapacitorBottomToCapacitorTop( CLModelViewTransform3D mvt, final double thickness, final Capacitor topCapacitor, final Capacitor bottomCapacitor ) {
        this( mvt, thickness, topCapacitor, new ArrayList<Capacitor>() {{ add( bottomCapacitor ); }} );
    }

    public WireCapacitorBottomToCapacitorTop( CLModelViewTransform3D mvt, final double thickness, final Capacitor topCapacitor, final ArrayList<Capacitor> bottomCapacitors ) {
        super( mvt, thickness, new Function0<ArrayList<WireSegment>>() {
            public ArrayList<WireSegment> apply() {

                ArrayList<WireSegment> segments = new ArrayList<WireSegment>();

                // connect top capacitor to leftmost bottom capacitor
                segments.add( new CapacitorToCapacitorWireSegment( topCapacitor, bottomCapacitors.get( 0 ) ) );

                if ( bottomCapacitors.size() > 1 ) {
                    // horizontal wire above leftmost to rightmost capacitor
                    final double xStart = topCapacitor.getX();
                    final double xEnd = bottomCapacitors.get( bottomCapacitors.size() - 1 ).getX();
                    final double y = topCapacitor.getY() + ( bottomCapacitors.get( 0 ).getY() - topCapacitor.getY() / 2 );
                    segments.add( new WireSegment( xStart, y, xEnd, y ) );

                    // vertical wires from horizontal wire down to each bottom capacitor
                    for ( int i = 1; i < bottomCapacitors.size(); i++ ) {
                        double x = bottomCapacitors.get( i ).getX();
                        segments.add( new CapacitorTopWireSegment( new Point2D.Double( x, y ), bottomCapacitors.get( i ) ) );
                    }
                }

                return segments;
            }
        } );

        this.topCapacitor = topCapacitor;

        // adjust when dimensions of any capacitor change
        SimpleObserver o = new SimpleObserver() {
            public void update() {
                setShape( createShape() );
            }
        };
        topCapacitor.addPlateSizeObserver( o );
        topCapacitor.addPlateSeparationObserver( o );
        for ( Capacitor bottomCapacitor : bottomCapacitors ) {
            bottomCapacitor.addPlateSeparationObserver( o );
            bottomCapacitor.addPlateSizeObserver( o );
        }
    }

    // Subtract any part of the wire that is occluded by the bottom plate.
    @Override protected Shape createShape() {
        Shape wireShape = super.createShape();
        // HACK: null check required because createShape is called in the superclass constructor.
        if ( topCapacitor != null ) {
            wireShape = ShapeUtils.subtract( wireShape, topCapacitor.getShapeFactory().createBottomPlateShape() );
        }
        return wireShape;
    }
}

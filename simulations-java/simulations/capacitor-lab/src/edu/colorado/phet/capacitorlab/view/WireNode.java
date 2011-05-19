// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.*;

import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a wire.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireNode extends PComposite {

    private static final Stroke WIRE_STROKE = new BasicStroke( 1f );
    private static final Color WIRE_STROKE_COLOR = Color.BLACK;
    private static final Color WIRE_FILL_COLOR = Color.LIGHT_GRAY;

    private final Wire wire;

    public WireNode( final Wire wire ) {

        this.wire = wire;

        final PPath pathNode = new PhetPPath( wire.getShape(), WIRE_FILL_COLOR, WIRE_STROKE, WIRE_STROKE_COLOR );
        addChild( pathNode );

        wire.addShapeObserver( new SimpleObserver() {
            public void update() {
                pathNode.setPathTo( wire.getShape() );
            }
        } );
    }

    public Wire getWire() {
        return wire;
    }
}

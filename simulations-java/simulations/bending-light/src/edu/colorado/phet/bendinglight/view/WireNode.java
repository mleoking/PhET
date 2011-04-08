// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo graphic that connects a sensor to its body.
 *
 * @author Sam Reid
 */
public class WireNode extends PNode {
    public WireNode( final PNode probe, final PNode body, final Color color ) {
        addChild( new PhetPPath( new BasicStroke( 8, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1f ), color ) {{
            final PropertyChangeListener listener = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    setPathTo( new DoubleGeneralPath( probe.getFullBounds().getCenterX(), probe.getFullBounds().getMaxY() ) {{
                        curveTo( probe.getFullBounds().getCenterX(), probe.getFullBounds().getMaxY() + 60,
                                 body.getFullBounds().getX() - 60, body.getFullBounds().getCenterY(),
                                 body.getFullBounds().getX(), body.getFullBounds().getCenterY() );
                    }}.getGeneralPath() );
                }
            };
            probe.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
            body.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
        }} );
    }
}

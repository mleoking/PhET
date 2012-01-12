// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo graphic that connects a sensor (the probe) to its body (where the readout appears)
 *
 * @author Sam Reid
 */
public class WireNode extends PNode {
    public WireNode( final PNode probe, final PNode body, final Color color ) {
        addChild( new PhetPPath( new BasicStroke( 8, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1f ), color ) {{
            final PropertyChangeListener listener = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    //Use a curve that looks like a real wire, connect to the left side of the body
                    setPathTo( new DoubleGeneralPath( probe.getFullBounds().getCenterX(), probe.getFullBounds().getMaxY() ) {{
                        curveTo( probe.getFullBounds().getCenterX(), probe.getFullBounds().getMaxY() + 60,
                                 body.getFullBounds().getX() - 60, body.getFullBounds().getCenterY(),
                                 body.getFullBounds().getX(), body.getFullBounds().getCenterY() );
                    }}.getGeneralPath() );
                }
            };
            //Update when bounds of the sensor or body changes
            probe.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
            body.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
        }} );
    }
}

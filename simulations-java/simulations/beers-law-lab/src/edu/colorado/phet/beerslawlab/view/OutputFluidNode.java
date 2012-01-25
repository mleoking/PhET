// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.model.Faucet;
import edu.colorado.phet.beerslawlab.model.IFluid;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Fluid coming out of a faucet.
 * Origin is at the top center, to simplify alignment with the center of the faucet's output pipe.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OutputFluidNode extends PPath {

    public OutputFluidNode( final Faucet faucet, final IFluid fluid, final double maxWidth, final double height ) {
        setPickable( false );
        setChildrenPickable( false );

        setStroke( new BasicStroke( 0.25f ) );
        setStrokePaint( fluid.getFluidColor().darker().darker() );

        setOffset( faucet.getLocation().toPoint2D() );

        // match the color of the fluid
        fluid.addFluidColorObserver( new SimpleObserver() {
            public void update() {
                setPaint( fluid.getFluidColor() );
            }
        } );

        // set the width of the shape to match the flow rate
        faucet.flowRate.addObserver( new SimpleObserver() {
            public void update() {
                if ( faucet.flowRate.get() == 0 ) {
                    setPathTo( new Rectangle2D.Double() ); // empty rectangle
                }
                else {
                    double width = maxWidth * faucet.flowRate.get() / faucet.getMaxFlowRate();
                    setPathTo( new Rectangle2D.Double( -width / 2, 0, width, height ) );
                }
            }
        } );
    }
}

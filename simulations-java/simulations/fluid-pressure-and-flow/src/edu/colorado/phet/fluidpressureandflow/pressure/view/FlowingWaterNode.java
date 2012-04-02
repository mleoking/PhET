// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.pressure.model.TrapezoidPool;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the water flowing from the faucet as a solid blue rectangle
 *
 * @author Sam Reid
 */
public class FlowingWaterNode extends PNode {
    public FlowingWaterNode( final TrapezoidPool pool, final Property<Double> flowRatePercentage, final ModelViewTransform transform ) {
        final double distanceToStickUpAboveTheGroundToGoBehindFaucet = 0.5;
        addChild( new PhetPPath( Color.blue ) {{
            flowRatePercentage.addObserver( new VoidFunction1<Double>() {
                @Override public void apply( final Double rate ) {
                    double width = 0.6 * rate;//meters wide of the fluid flow
                    Shape shape = transform.modelToView( new Rectangle2D.Double( pool.centerAtLeftChamberOpening - width / 2, -pool.height, width, pool.height + distanceToStickUpAboveTheGroundToGoBehindFaucet ) );
                    setPathTo( shape );
                }
            } );
        }} );
    }
}
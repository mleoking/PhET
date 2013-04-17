// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.pressure.view.WaterColor.getBottomColor;

/**
 * Shows the water flowing from the faucet as a solid blue rectangle
 *
 * @author Sam Reid
 */
public class OutputFlowingWaterNode extends PNode {
    public OutputFlowingWaterNode( final FaucetPool pool, final Property<Double> flowRate, final ModelViewTransform transform, final ObservableProperty<Double> liquidDensity, final ObservableProperty<Boolean> faucetEnabled ) {
        addChild( new PhetPPath( getBottomColor( liquidDensity.get() ) ) {{
            new RichSimpleObserver() {
                @Override public void update() {
                    double width = 0.6 * flowRate.get() * ( faucetEnabled.get() ? 1 : 0 );//meters wide of the fluid flow
                    double height = 100;
                    final Rectangle2D.Double shape = new Rectangle2D.Double( pool.getWaterOutputCenterX() - width / 2, -pool.getHeight() - height - 0.2, width, height );
                    final Shape viewShape = transform.modelToView( shape );
                    setPathTo( viewShape );

                    setPaint( getBottomColor( liquidDensity.get() ) );
                }
            }.observe( flowRate, pool.getWaterVolume(), liquidDensity, faucetEnabled );
        }} );
    }
}
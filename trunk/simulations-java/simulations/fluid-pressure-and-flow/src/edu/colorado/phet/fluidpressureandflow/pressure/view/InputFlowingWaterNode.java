// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

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
public class InputFlowingWaterNode extends PNode {
    public InputFlowingWaterNode( final FaucetPool pool, final Property<Double> flowRatePercentage, final ModelViewTransform transform, final ObservableProperty<Double> liquidDensity, final ObservableProperty<Boolean> faucetEnabled ) {
        final double distanceToStickUpAboveTheGroundToGoBehindFaucet = 0.5;
        addChild( new PhetPPath( getBottomColor( liquidDensity.get() ) ) {{
            new RichSimpleObserver() {
                @Override public void update() {
                    double width = 0.6 * flowRatePercentage.get() * ( faucetEnabled.get() ? 1 : 0 );//meters wide of the fluid flow

                    //centerAtLeftChamberOpening
                    final Rectangle2D.Double shape = new Rectangle2D.Double( pool.getInputFaucetX() - width / 2, -pool.getHeight() + pool.getWaterHeight(), width, pool.getHeight() + distanceToStickUpAboveTheGroundToGoBehindFaucet - pool.getWaterHeight() );
                    setPathTo( transform.modelToView( shape ) );
                    setPaint( getBottomColor( liquidDensity.get() ) );
                }
            }.observe( flowRatePercentage, pool.getWaterVolume(), liquidDensity, faucetEnabled );
        }} );
    }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view.faucet;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that displays the water behind a faucet
 *
 * @author Sam Reid
 */
public class WaterNode extends PNode {

    //Listeners for the shape of the water that flows out of the faucet
    private ArrayList<VoidFunction1<Rectangle2D>> listeners = new ArrayList<VoidFunction1<Rectangle2D>>();

    public WaterNode( final Property<Double> flowRate, final Option<Double> flowPoint, final double imageWidth, final double imageHeight ) {
        addChild( new PhetPPath( SugarAndSaltSolutionsApplication.WATER_COLOR ) {{
            flowRate.addObserver( new VoidFunction1<Double>() {
                public void apply( Double flow ) {
                    double width = flow * 100 * 0.5;
                    double pipeWidth = 56;

                    //Compute the bottom of the water (e.g. if it collides with the beaker)
                    double bottomY = flowPoint.getOrElse( 1000.0 );
                    double height = bottomY - imageHeight - getOffset().getY();
                    final Rectangle2D.Double waterShape = new Rectangle2D.Double( imageWidth - width / 2 - pipeWidth / 2, imageHeight, width, height );
                    notifyWaterShapeChanged( waterShape );
                    setPathTo( waterShape );
                }
            } );
        }} );
    }

    //Add a listener that will be notified about the shape of the water
    public void addListener( VoidFunction1<Rectangle2D> listener ) {
        listeners.add( listener );
    }

    //Notify listeners that the shape of the output water changed
    private void notifyWaterShapeChanged( Rectangle2D.Double waterShape ) {
        for ( VoidFunction1<Rectangle2D> listener : listeners ) {
            listener.apply( waterShape );
        }
    }
}
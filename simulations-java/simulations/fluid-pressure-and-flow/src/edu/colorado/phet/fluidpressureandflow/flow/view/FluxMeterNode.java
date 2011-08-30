// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.flow.model.FluxMeter;
import edu.umd.cs.piccolo.PNode;

/**
 * Interactive Piccolo graphic for the flux meter node, which attaches to the pipe and shows the flux as a function of position
 * The user can drag it back and forth, and enable/disable it from the control panel.
 * TODO: Split into multiple layers so it looks like it encompasses the water
 *
 * @author Sam Reid
 */
public class FluxMeterNode extends PNode {

    public FluxMeterNode( final ModelViewTransform transform, final FluxMeter fluxMeter ) {
        addChild( new PhetPPath( new BasicStroke( 4 ), Color.yellow ) {{

            //Tuned by hand so it matches the perspective of the pipe graphics
            double ellipseWidth = 0.45;

            //Create a hoop to catch the water flux, have to flip the y-values going from model to view
            final ImmutableVector2D top = fluxMeter.getTop();
            final ImmutableVector2D bottom = fluxMeter.getBottom();
            final Ellipse2D.Double modelShape = new Ellipse2D.Double( top.getX() - ellipseWidth / 2, bottom.getY(), ellipseWidth, -1 * ( bottom.getY() - top.getY() ) );
            final Shape viewShape = transform.modelToView( modelShape );
            setPathTo( viewShape );
        }} );
    }
}
// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * This is a framed "window"-like node that shows the zoomed in water and solute particles.
 * Allows us to model periodic boundary conditions (where the particle leaves one side and comes in the other, without flickering.
 *
 * @author Sam Reid
 */
public class ParticleWindowNode extends PNode {
    private final PNode particleLayer = new PNode();

    //Color to show around the particle window as its border.  Also used for the zoom in box in ZoomIndicatorNode
    public static final Color FRAME_COLOR = Color.orange;

    public ParticleWindowNode( final WaterModel model, final ModelViewTransform transform ) {
        PClip clip = new PClip() {{

            //Show a frame around the particles so they are clipped when they move out of the window
            setPathTo( transform.modelToView( model.particleWindow.toRectangle2D() ) );
            setStroke( new BasicStroke( 2 ) );
            setStrokePaint( FRAME_COLOR );
            addChild( particleLayer );
        }};
        addChild( clip );
    }
}
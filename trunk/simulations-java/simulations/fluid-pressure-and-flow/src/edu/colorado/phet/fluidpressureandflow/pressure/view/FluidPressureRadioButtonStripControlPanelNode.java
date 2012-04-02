// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.RadioButtonStripControlPanelNode.RadioButtonStripNode;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Radio button strip that lets the user choose between different scenes in the pressure tab.
 *
 * @author Sam Reid
 */
public class FluidPressureRadioButtonStripControlPanelNode extends RadioButtonStripNode<IPool> {
    public FluidPressureRadioButtonStripControlPanelNode( final FluidPressureCanvas canvas, FluidPressureModel model ) {
        super( model.pool, Arrays.asList( new Pair<PNode, IPool>( createIcon( canvas, model.pool, model.squarePool ), model.squarePool ),
                                          new Pair<PNode, IPool>( createIcon( canvas, model.pool, model.trapezoidPool ), model.trapezoidPool ) ), 5 );
    }

    //Creates an icon that displays the track.
    //Copied from code in EnergySkatePark's TrackButton
    private static PNode createIcon( final FluidPressureCanvas canvas, SettableProperty<IPool> property, IPool pool ) {
        IPool origPool = property.get();

        //Set the property that we wish to display
        property.set( pool );

        //To create the icon, load the module and render it to an image
        BufferedImage icon = new BufferedImage( 1024, 768, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = icon.createGraphics();
        canvas.setSize( 1024, 768 );

        //Render into the image
        canvas.getRootNode().fullPaint( new PPaintContext( g2 ) );

        //Restore
        property.set( origPool );

        //Resize to a small icon size using multi-scaling so the quality will be high
        return new PImage( BufferedImageUtils.multiScaleToWidth( icon, 100 ) );
    }
}
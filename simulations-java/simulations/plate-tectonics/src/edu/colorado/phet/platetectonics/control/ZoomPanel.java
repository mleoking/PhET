// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;
import edu.colorado.phet.lwjglphet.utils.SwingForwardingProperty;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Displays a zoom control in the 1st tab
 */
public class ZoomPanel extends PNode {

    private final SwingForwardingProperty<Double> swingZoomRatio;
    private final Property<Double> zoomRatio;

    public ZoomPanel( final Property<Double> zoomRatio ) {
        this.zoomRatio = zoomRatio;
        final PText zoomText = new PText( Strings.ZOOM );

        swingZoomRatio = new SwingForwardingProperty<Double>( zoomRatio );
        VSliderNode slider = new VSliderNode( UserComponents.zoomSlider, 0, 1, 6, 100, swingZoomRatio, new Property<Boolean>( true ) ) {{
            setOffset( ( zoomText.getFullBounds().getWidth() - getFullBounds().getWidth() ) / 2,
                       zoomText.getFullBounds().getMaxY() + 10
            );
        }};
        addChild( zoomText );
        addChild( slider );
    }

    public void resetAll() {
        // reset the actual zoom ratio
        zoomRatio.reset();
        final double ratio = zoomRatio.get();

        // and update the slider with the new (reset) value in the Swing EDT
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                swingZoomRatio.set( ratio );
                repaint();
            }
        } );
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;
import edu.colorado.phet.lwjglphet.utils.SwingForwardingProperty;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Displays a zoom control in the 1st tab
 */
public class ZoomPanel extends PNode {
    public ZoomPanel( final Property<Double> zoomRatio ) {
        final PText zoomText = new PText( Strings.ZOOM );

        VSliderNode slider = new VSliderNode( 0, 1, new SwingForwardingProperty<Double>( zoomRatio ), new Property<Boolean>( true ), 100 ) {{
            setOffset( ( zoomText.getFullBounds().getWidth() - getFullBounds().getWidth() ) / 2,
                       zoomText.getFullBounds().getMaxY() + 10
            );
        }};
        addChild( zoomText );
        addChild( slider );
    }
}

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.MeasuringTape;

/**
 * @author Sam Reid
 */
public class GOMeasuringTapeNode extends MeasuringTape {

    public GOMeasuringTapeNode( final Property<Boolean> visible, ModelViewTransform2D modelViewTransform2D ) {
        super( modelViewTransform2D, new Point2D.Double( 0, 0 ) );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
        setTapePaint( Color.lightGray );
    }
}

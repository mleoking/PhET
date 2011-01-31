// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.MeasuringTape;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class FPAFMeasuringTape extends PNode {
    public FPAFMeasuringTape( ModelViewTransform transform, final Property<Boolean> measuringTapeVisible ) {
        final Point2D.Double zero = new Point2D.Double( 0, 0 );
        final Point2D.Double one = new Point2D.Double( 1, 1 );
        final ModelViewTransform2D modelViewTransform2D = new ModelViewTransform2D( zero, one,
                                                                                    transform.modelToView( zero ), transform.modelToView( one ) );
        addChild( new MeasuringTape( modelViewTransform2D, zero, "m" ) );
        measuringTapeVisible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( measuringTapeVisible.getValue() );
            }
        } );
    }
}

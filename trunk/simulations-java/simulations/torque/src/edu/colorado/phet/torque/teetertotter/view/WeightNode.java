// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.torque.teetertotter.model.weights.Weight;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 *
 */
public class WeightNode extends ModelObjectNode {
    public WeightNode( final ModelViewTransform mvt, final Weight weight ) {
        super( mvt, weight, Color.RED );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() {
            @Override
            public void mouseDragged( PInputEvent event ) {
                PDimension viewDelta = event.getDeltaRelativeTo( getParent() );
                ImmutableVector2D modelDelta = mvt.viewToModelDelta( new ImmutableVector2D( viewDelta ) );
                weight.translate( modelDelta );
            }
        } );
    }
}

package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.model.PipePosition;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PipePositionControl extends PNode {
    public PipePositionControl( final ModelViewTransform transform, final PipePosition pipePosition ) {
        ControlPoint2 top = new ControlPoint2( pipePosition.getTopProperty(), true );
        ControlPoint2 bottom = new ControlPoint2( pipePosition.getBottomProperty(), false );

        addChild( new PipeBackNode.GrabHandle( transform, bottom, top ) );
        addChild( new PipeBackNode.GrabHandle( transform, top, bottom ) );
    }
}

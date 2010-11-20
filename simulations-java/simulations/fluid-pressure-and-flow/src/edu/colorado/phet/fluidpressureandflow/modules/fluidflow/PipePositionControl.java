package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.model.PipePosition;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PipePositionControl extends PNode {
    public PipePositionControl( final ModelViewTransform transform, final PipePosition pipePosition ) {
        ControlPoint top = new ControlPoint( pipePosition.getTopProperty(), true );
        ControlPoint bottom = new ControlPoint( pipePosition.getBottomProperty(), false );

        addChild( new PipeBackNode.GrabHandle( transform, bottom, top ) );
        addChild( new PipeBackNode.GrabHandle( transform, top, bottom ) );
    }
}

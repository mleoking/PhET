package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowRulerNode extends PNode {
    public FluidPressureAndFlowRulerNode( ModelViewTransform2D transform, Pool pool, final Property<Boolean> visible ) {
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
        Point2D rulerModelOrigin = new Point2D.Double( pool.getMinX(), pool.getMinY() );
        final RulerNode rulerNode = new RulerNode( Math.abs( transform.modelToViewDifferentialYDouble( 5 ) ),
                                                   50, new String[] { "0", "1", "2", "3", "4", "5" }, "m", 4, 18 );
        rulerNode.rotate( -Math.PI / 2 );
        rulerNode.setOffset( transform.modelToViewXDouble( rulerModelOrigin.getX() ),
                             transform.modelToViewYDouble( rulerModelOrigin.getY() ) + rulerNode.getInsetWidth() );
        addChild( rulerNode );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( getParent() );
                translate( delta.width, delta.height );
            }
        } );
    }
}

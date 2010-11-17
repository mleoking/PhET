package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowRuler extends PNode {
    public FluidPressureAndFlowRuler( ModelViewTransform2D transform, final Property<Boolean> visible, double length, String[] majorTicks, String units, Point2D rulerModelOrigin ) {
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
        final RulerNode rulerNode = new RulerNode( length,
                                                   50, majorTicks, units, 4, 18 );
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

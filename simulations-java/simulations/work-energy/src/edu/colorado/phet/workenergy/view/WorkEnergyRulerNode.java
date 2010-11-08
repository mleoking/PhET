package edu.colorado.phet.workenergy.view;

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
public class WorkEnergyRulerNode extends PNode {
    public WorkEnergyRulerNode( final Property<Boolean> visibleProperty, ModelViewTransform2D transform ) {
        visibleProperty.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visibleProperty.getValue() );
            }
        } );

        final RulerNode rulerNode = new RulerNode( Math.abs( transform.modelToViewDifferentialYDouble( 5 ) ),
                50, new String[] { "0", "1", "2", "3", "4", "5" }, "m", 4, 18 );
        rulerNode.setOffset(300, transform.modelToViewYDouble( 0 ) + rulerNode.getInsetWidth() );
        rulerNode.rotate( -Math.PI / 2 );
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

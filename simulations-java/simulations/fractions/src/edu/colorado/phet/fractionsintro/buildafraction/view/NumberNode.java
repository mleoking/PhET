package edu.colorado.phet.fractionsintro.buildafraction.view;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Node for a number that gets dragged out of the toolbox.
 *
 * @author Sam Reid
 */
public class NumberNode extends PNode {
    public NumberNode( final int number, final DragContext context ) {
        addChild( new PhetPText( number + "", new PhetFont( 64, true ) ) );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                final PDimension delta = event.getDeltaRelativeTo( getParent() );
                translate( delta.width, delta.height );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.endDrag( NumberNode.this, event );
            }
        } );
        addInputEventListener( new CursorHandler() );
    }
}
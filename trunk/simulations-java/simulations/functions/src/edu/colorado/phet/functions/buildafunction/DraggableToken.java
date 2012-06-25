package edu.colorado.phet.functions.buildafunction;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class DraggableToken extends PNode {
    public final Object value;
    private final PhetPText text;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private ArrayList<FunctionBoxWithText> functions = new ArrayList<FunctionBoxWithText>();

    public static interface Listener {
        void functionApplied( DraggableToken t, FunctionBoxWithText function );
    }

    public void applyFunction( final FunctionBoxWithText head ) {
        functions.add( head );
        Object v = value;
        for ( FunctionBoxWithText function : functions ) {
            v = function.evaluate( v );
        }
        text.setText( v.toString() );
        for ( Listener listener : listeners ) {
            listener.functionApplied( this, head );
        }
    }

    public void addListener( Listener listener ) { listeners.add( listener ); }

    public static interface Context {
        void dragged( DraggableToken token, PInputEvent event );
    }

    public DraggableToken( Object value, final Context context ) {
        this.value = value;
        text = new PhetPText( value.toString(), new PhetFont( 64, true ) );
        addChild( text );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                context.dragged( DraggableToken.this, event );
            }
        } );
    }
}
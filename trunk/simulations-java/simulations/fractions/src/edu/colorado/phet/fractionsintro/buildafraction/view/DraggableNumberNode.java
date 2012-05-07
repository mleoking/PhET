package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.data.Option;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableNumber;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableNumberObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState.RELEASE_ALL;

/**
 * Bar node that the user can drag
 *
 * @author Sam Reid
 */
public class DraggableNumberNode extends PNode {
    public DraggableNumberNode( final ObjectID id, final BuildAFractionModel model, final BuildAFractionCanvas canvas ) {
        final DraggableNumberObserver observer = new DraggableNumberObserver( id ) {
            @Override public void applyChange( final Option<DraggableNumber> old, final Option<DraggableNumber> newOne ) {
                removeAllChildren();
                if ( newOne.isSome() ) {
                    addChild( new PNode() {{
                        final DraggableNumber some = newOne.some();
                        addChild( BuildAFractionCanvas.numberGraphic( some.getNumber() ) );
                        setOffset( some.getDraggableObject().position.toPoint2D() );
                        addInputEventListener( new CursorHandler() );
                        addInputEventListener( new PBasicInputEventHandler() {
                            @Override public void mousePressed( final PInputEvent event ) {
                                model.startDraggingNumber( id );
                            }

                            @Override public void mouseDragged( final PInputEvent event ) {
                                model.dragNumber( event.getDeltaRelativeTo( canvas.rootNode ) );
                            }

                            @Override public void mouseReleased( final PInputEvent event ) {
                                model.update( RELEASE_ALL );
                            }
                        } );
                    }} );
                }
                else {
                    //If removed from model, remove this view class
                    getParent().removeChild( DraggableNumberNode.this );
                    model.removeChangeObserver( this );
                }
            }
        };
        model.addChangeObserver( observer );
    }
}
package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.data.Option;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.Container;
import edu.colorado.phet.fractionsintro.buildafraction.model.ContainerObserver;
import edu.colorado.phet.fractionsintro.buildafraction.model.ObjectID;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState.RELEASE_ALL;

/**
 * Bar node that the user can drag
 *
 * @author Sam Reid
 */
public class DraggablePieceNode extends PNode {
    public DraggablePieceNode( final ObjectID id, final BuildAFractionModel model, final BuildAFractionCanvas canvas ) {
        model.addObserver( new ContainerObserver( id ) {
            @Override public void applyChange( final Option<Container> old, final Option<Container> newOne ) {
                removeAllChildren();
                if ( newOne.isSome() ) {
                    addChild( new PNode() {{
                        addChild( BuildAFractionCanvas.pieceGraphic( newOne.some() ) );
                        addInputEventListener( new CursorHandler() );
                        addInputEventListener( new PBasicInputEventHandler() {
                            @Override public void mousePressed( final PInputEvent event ) {
                                model.startDraggingContainer( id );
                            }

                            @Override public void mouseDragged( final PInputEvent event ) {
                                model.dragContainer( event.getDeltaRelativeTo( canvas.rootNode ) );
                            }

                            @Override public void mouseReleased( final PInputEvent event ) {
                                model.update( RELEASE_ALL );
                            }
                        } );
                    }} );
                }
                else {
                    //If removed from model, remove this view class
                    getParent().removeChild( DraggablePieceNode.this );
                    model.removeObserver( this );
                }
            }
        } );
    }
}
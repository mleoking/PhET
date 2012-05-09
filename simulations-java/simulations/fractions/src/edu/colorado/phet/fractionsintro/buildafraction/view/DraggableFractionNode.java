package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.data.Option;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableFraction;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableFractionObserver;
import edu.colorado.phet.fractionsintro.buildafraction.model.FractionID;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState.RELEASE_ALL;

/**
 * @author Sam Reid
 */
public class DraggableFractionNode extends PNode {
    public final FractionID id;

    public DraggableFractionNode( final FractionID id, final BuildAFractionModel model, final BuildAFractionCanvas canvas ) {
        this.id = id;
        final DraggableFractionObserver observer = new DraggableFractionObserver( id ) {
            @Override public void applyChange( final Option<DraggableFraction> old, final Option<DraggableFraction> newOne ) {
                redraw( newOne, model, canvas, this );
            }

        };
        model.addObserver( observer );
        model.addObserver( new ChangeObserver<BuildAFractionState>() {
            @Override public void update( final BuildAFractionState newValue, final BuildAFractionState oldValue ) {
                if ( oldValue.isUserDraggingZero() != newValue.isUserDraggingZero() ) {
                    redraw( newValue.getDraggableFraction( id ), model, canvas, observer );
                }
            }
        } );
    }


    private void redraw( final Option<DraggableFraction> newOne, final BuildAFractionModel model, final BuildAFractionCanvas canvas, final DraggableFractionObserver draggableFractionObserver ) {
        removeAllChildren();
        if ( newOne.isSome() ) {
            addChild( new PNode() {{
                final DraggableFraction some = newOne.some();
                addChild( BuildAFractionCanvas.emptyFractionGraphic( some.numerator.isNone(), some.denominator.isNone() && !model.state.get().isUserDraggingZero() ) );
                setOffset( some.getDraggableObject().position.toPoint2D() );
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( final PInputEvent event ) {
                        model.startDraggingFraction( id );
                    }

                    @Override public void mouseDragged( final PInputEvent event ) {
                        model.dragFraction( id, event.getDeltaRelativeTo( canvas.rootNode ) );
                    }

                    @Override public void mouseReleased( final PInputEvent event ) {
                        model.update( RELEASE_ALL );
                    }
                } );
            }} );
        }
        else {
            //If removed from model, remove this view class
            getParent().removeChild( DraggableFractionNode.this );
            model.removeObserver( draggableFractionObserver );
        }
    }
}
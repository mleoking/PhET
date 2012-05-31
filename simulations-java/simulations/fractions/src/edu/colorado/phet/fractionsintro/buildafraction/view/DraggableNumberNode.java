package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.data.Option;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableNumber;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableNumberID;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableNumberObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Bar node that the user can drag
 *
 * @author Sam Reid
 */
public class DraggableNumberNode extends PNode {
    public final DraggableNumberID id;

    public DraggableNumberNode( final DraggableNumberID id, final BuildAFractionModel model, final BuildAFractionCanvas canvas ) {
        this.id = id;
        final DraggableNumberObserver observer = new DraggableNumberObserver( id ) {
            @Override public void applyChange( final Option<DraggableNumber> old, final Option<DraggableNumber> newOne ) {
                removeAllChildren();
                if ( newOne.isSome() ) {
                    addChild( new PNode() {{
                        final DraggableNumber some = newOne.some();
                        addChild( NumberScene.numberGraphic( some.getNumber() ) );
                        if ( some.attachment.isSome() ) {
//                            System.out.println( "rendering number node with attachment, some= " + some );
                            final DraggableFractionNode draggableFractionNode = canvas.getDraggableFractionNode( some.attachment.some()._1() );
                            final Point2D center2D = draggableFractionNode.getFractionCenter();
                            double offset = some.attachment.some()._2() ? -getFullBounds().getHeight() / 2 :
                                            getFullBounds().getHeight() / 2;
                            centerFullBoundsOnPoint( center2D.getX(), center2D.getY() + offset );

                            //Disallow interaction when on a fraction, so mouse clicks will fall through to the fraction itself
                            setPickable( false );
                            setChildrenPickable( false );
                        }
                        else {
                            setOffset( some.getDraggableObject().position.toPoint2D() );
                        }
                        addInputEventListener( new CursorHandler() );
                        addInputEventListener( new PBasicInputEventHandler() {
                            @Override public void mousePressed( final PInputEvent event ) {
                                model.startDraggingNumber( id );
                            }

                            @Override public void mouseDragged( final PInputEvent event ) {
                                model.dragNumber( event.getDeltaRelativeTo( canvas.rootNode ) );
                            }

                            @Override public void mouseReleased( final PInputEvent event ) {
                                canvas.draggableNumberNodeReleased( DraggableNumberNode.this );
                            }
                        } );
                    }} );
                }
                else {
                    //If removed from model, remove this view class
                    getParent().removeChild( DraggableNumberNode.this );
                    model.removeObserver( this );
                }
            }
        };
        model.addObserver( observer );
    }
}
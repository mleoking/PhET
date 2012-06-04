package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.data.Option;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableFraction;
import edu.colorado.phet.fractionsintro.buildafraction.model.DraggableFractionObserver;
import edu.colorado.phet.fractionsintro.buildafraction.model.FractionID;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class DraggableFractionNode extends PNode {
    public final FractionID id;
    private PNode fractionGraphic;
    private Point2D storedOffset;

    public DraggableFractionNode( final FractionID id, final BuildAFractionModel model, final BuildAFractionCanvas canvas ) {
        this.id = id;
        final DraggableFractionObserver observer = new DraggableFractionObserver( id ) {
            @Override public void applyChange( final Option<DraggableFraction> old, final Option<DraggableFraction> newOne ) {
                redraw( newOne, model, canvas, this );
            }

        };
        model.addObserver( observer );
        model.addObserver( new ChangeObserver<BuildAFractionState>() {
            public void update( final BuildAFractionState newValue, final BuildAFractionState oldValue ) {
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
                fractionGraphic = BuildAFractionCanvas.emptyFractionGraphic( some.numerator.isNone(), some.denominator.isNone() && !model.state.get().isUserDraggingZero() );
                addChild( fractionGraphic );
                if ( some.numerator.isSome() && some.denominator.isSome() ) {
                    addChild( new PImage( Images.SPLIT_BLUE ) {{
                        translate( fractionGraphic.getFullBounds().getWidth(), fractionGraphic.getFullBounds().getHeight() / 2 - getFullBounds().getHeight() / 2 );
                        addInputEventListener( new CursorHandler() );
                        addInputEventListener( new PBasicInputEventHandler() {
                            @Override public void mousePressed( final PInputEvent event ) {
                                model.splitFraction( id );
                            }
                        } );
                    }} );
                }
                storedOffset = some.getDraggableObject().position.toPoint2D();
                setOffset( storedOffset );
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( final PInputEvent event ) {
                        model.startDraggingFraction( id );
                    }

                    @Override public void mouseDragged( final PInputEvent event ) {
                        model.dragFraction( id, event.getDeltaRelativeTo( canvas.rootNode ) );
                    }

                    @Override public void mouseReleased( final PInputEvent event ) {

                        //Send the message through the view so it can check for score cell hits
                        canvas.numberScene.fractionNodeDropped( id );
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

    //Find the center of the fraction, used to center the numerator and denominator which are rendered by the DraggableNumberNode class
    public Point2D getFractionCenter() {
        return new Point2D.Double( fractionGraphic.getFullBounds().getCenter2D().getX() + storedOffset.getX(), fractionGraphic.getFullBounds().getCenter2D().getY() + storedOffset.getY() );
    }
}
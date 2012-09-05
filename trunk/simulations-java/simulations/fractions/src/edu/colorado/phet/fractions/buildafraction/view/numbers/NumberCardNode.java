// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.SimSharingCanvasBoundedDragHandler;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.view.DisablePickingWhileAnimating;
import edu.colorado.phet.fractions.buildafraction.view.Stackable;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.value;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.numberCard;

/**
 * Card that shows a single number (could be multiple digits).
 *
 * @author Sam Reid
 */
public class NumberCardNode extends Stackable {
    public final int number;
    private final PhetPPath cardShape;
    public final NumberNode numberNode;

    public NumberCardNode( final Dimension2DDouble size, final Integer number, final NumberDragContext context ) {
        this.number = number;
        cardShape = new PhetPPath( new RoundRectangle2D.Double( 0, 0, size.width, size.height, 10, 10 ), Color.white, new BasicStroke( 1 ), Color.black );
        addChild( cardShape );
        numberNode = new NumberNode( number ) {{
            centerFullBoundsOnPoint( size.width / 2, size.height / 2 );
        }};
        addChild( numberNode );

        addInputEventListener( new SimSharingCanvasBoundedDragHandler( chain( numberCard, NumberCardNode.this.hashCode() ), NumberCardNode.this ) {
            @Override protected ParameterSet getParametersForAllEvents( final PInputEvent event ) {
                return super.getParametersForAllEvents( event ).with( value, number );
            }

            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );
                context.startDrag( NumberCardNode.this );
            }

            @Override protected void dragNode( final DragEvent event ) {
                moveToFront();
                setPositionInStack( Option.<Integer>none() );
                translate( event.delta.width, event.delta.height );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                super.mouseReleased( event );
                context.endDrag( NumberCardNode.this );
            }
        } );

        addInputEventListener( new CursorHandler() );
    }

    public void animateToStackLocation( Vector2D v ) {
        animateToPositionScaleRotation( v.x, v.y, 1, 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new DisablePickingWhileAnimating( this, true ) );
    }

    public void setCardShapeVisible( boolean visible ) { cardShape.setVisible( visible ); }

    public void addNumberNodeBackIn( final NumberNode numberNode ) {
        addChild( numberNode );
        numberNode.setOffset( cardShape.getCenterX() - numberNode.getFullWidth() / 2, cardShape.getCenterY() - numberNode.getFullHeight() / 2 );
    }

    @SuppressWarnings("unchecked") public void animateToTopOfStack() { stack.animateToTopOfStack( this ); }
}
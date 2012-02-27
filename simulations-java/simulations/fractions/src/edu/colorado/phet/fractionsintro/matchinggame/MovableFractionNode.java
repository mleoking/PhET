// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame;

import fj.Equal;
import fj.F;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Decorates a pnode with mouse listeners and sets the position properly
 *
 * @author Sam Reid
 */
public class MovableFractionNode extends PNode {
    public MovableFractionNode( final SettableProperty<MatchingGameState> model, final MovableFraction f, PNode node, final PNode rootNode ) {
        addChild( node );
        centerFullBoundsOnPoint( f.position.getX(), f.position.getY() );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {

            //Flag one slice as dragging
            @Override public void mousePressed( PInputEvent event ) {
                MatchingGameState state = model.get();
                model.set( state.fractions( state.fractions.delete( f, Equal.<MovableFraction>anyEqual() ).snoc( f.dragging( true ) ) ) );
            }

            //Set all drag flags to false
            @Override public void mouseReleased( PInputEvent event ) {
                final MatchingGameState state = model.get();
                model.set( state.fractions( state.fractions.map( new F<MovableFraction, MovableFraction>() {
                    @Override public MovableFraction f( MovableFraction f ) {
                        return f.dragging( false );
                    }
                } ) ) );
            }

            //Drag the dragged slice as identified by the model (since nodes will be destroyed as this happens)
            @Override public void mouseDragged( PInputEvent event ) {
                MatchingGameState state = model.get();

                final Dimension2D delta = event.getDeltaRelativeTo( rootNode );
                MatchingGameState newState = state.fractions( state.fractions.map( new F<MovableFraction, MovableFraction>() {
                    @Override public MovableFraction f( MovableFraction f ) {
                        return f.dragging ? f.translate( delta.getWidth(), delta.getHeight() ) : f;
                    }
                } ) );
                model.set( newState );
            }
        } );
    }
}
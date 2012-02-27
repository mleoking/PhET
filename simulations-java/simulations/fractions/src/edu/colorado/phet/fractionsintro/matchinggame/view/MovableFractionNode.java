// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.Equal;
import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;
import fj.data.List;
import fj.data.TreeMap;

import java.awt.geom.Dimension2D;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.UpdateArgs;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.fractionsintro.matchinggame.model.Motions.*;
import static fj.Function.curry;
import static fj.data.TreeMap.fromMutableMap;

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

                //animate to the closest destination
                model.set( state.fractions( state.fractions.map( new F<MovableFraction, MovableFraction>() {
                    @Override public MovableFraction f( final MovableFraction f ) {
                        HashMap<ImmutableVector2D, F<UpdateArgs, MovableFraction>> map = new HashMap<ImmutableVector2D, F<UpdateArgs, MovableFraction>>() {{
                            put( state.rightScale.center(), MoveToRightScale );
                            put( state.leftScale.center(), MoveToLeftScale );
                            put( f.home.position(), MoveToCell( f.home ) );
                        }};
                        final Ord<ImmutableVector2D> ord = Ord.ord( curry( new F2<ImmutableVector2D, ImmutableVector2D, Ordering>() {
                            public Ordering f( final ImmutableVector2D u1, final ImmutableVector2D u2 ) {
                                return Ord.<Comparable>comparableOrd().compare( f.position.distance( u1 ), f.position.distance( u2 ) );
                            }
                        } ) );
                        TreeMap<ImmutableVector2D, F<UpdateArgs, MovableFraction>> tm = fromMutableMap( ord, map );
                        List<ImmutableVector2D> sorted = tm.keys().sort( ord );

                        return f.dragging ? f.dragging( false ).motion( tm.get( sorted.head() ).some() ) : f;
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
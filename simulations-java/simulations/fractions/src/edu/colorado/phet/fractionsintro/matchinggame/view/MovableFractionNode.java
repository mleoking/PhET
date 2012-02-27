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

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
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
                MatchingGameState state = model.get();

                //Find the fraction that the user released:
                final MovableFraction draggingFraction = state.fractions.find( new F<MovableFraction, Boolean>() {
                    @Override public Boolean f( MovableFraction m ) {
                        return m.dragging;
                    }
                } ).some();

                //Determine where to animate it
                final MatchingGameState finalState = state;
                HashMap<Vector2D, F<UpdateArgs, MovableFraction>> map = new HashMap<Vector2D, F<UpdateArgs, MovableFraction>>() {{
                    put( finalState.rightScale.getAttachmentPoint(), MoveToRightScale );
                    put( finalState.leftScale.getAttachmentPoint(), MoveToLeftScale );
                    put( draggingFraction.home.position(), MoveToCell( draggingFraction.home ) );
                }};
                final Ord<Vector2D> ord = Ord.ord( curry( new F2<Vector2D, Vector2D, Ordering>() {
                    public Ordering f( final Vector2D u1, final Vector2D u2 ) {
                        return Ord.<Comparable>comparableOrd().compare( draggingFraction.position.distance( u1 ), draggingFraction.position.distance( u2 ) );
                    }
                } ) );
                final TreeMap<Vector2D, F<UpdateArgs, MovableFraction>> tm = fromMutableMap( ord, map );
                List<Vector2D> sorted = tm.keys().sort( ord );

                final Vector2D selectedAttachmentPoint = sorted.head();

                state = state.jettison( selectedAttachmentPoint );

                //animate to the closest destination
                final List<MovableFraction> newFractions = state.fractions.map( new F<MovableFraction, MovableFraction>() {
                    @Override public MovableFraction f( final MovableFraction f ) {
                        return f.dragging ? f.dragging( false ).motion( tm.get( selectedAttachmentPoint ).some() ) : f;
                    }
                } );
                final MatchingGameState newState = state.fractions( newFractions );
                model.set( newState );
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
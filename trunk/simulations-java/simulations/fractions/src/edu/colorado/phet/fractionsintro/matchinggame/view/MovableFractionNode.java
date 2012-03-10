// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.Equal;
import fj.F;
import fj.Ord;
import fj.data.List;
import fj.data.TreeMap;

import java.awt.geom.Dimension2D;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.matchinggame.model.Cell;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.UpdateArgs;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.fractions.util.FJUtils.ord;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Motions.*;
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
        if ( !f.scored ) {
            attachInputHandlers( model, f, rootNode );
        }
    }

    private void attachInputHandlers( final SettableProperty<MatchingGameState> model, final MovableFraction f, final PNode rootNode ) {
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
                final Vector2D rightScaleAttachmentPoint = finalState.rightScale.getAttachmentPoint( draggingFraction );
                final Vector2D leftScaleAttachmentPoint = finalState.leftScale.getAttachmentPoint( draggingFraction );
                HashMap<Vector2D, F<UpdateArgs, MovableFraction>> map = new HashMap<Vector2D, F<UpdateArgs, MovableFraction>>() {{
                    put( rightScaleAttachmentPoint, MoveToRightScale );
                    put( leftScaleAttachmentPoint, MoveToLeftScale );
                    Cell cell = model.get().getClosestFreeStartCell( draggingFraction );
                    put( cell.position(), MoveToCell( cell ) );
                }};

                final Ord<Vector2D> ord = ord( new F<Vector2D, Double>() {
                    @Override public Double f( final Vector2D u ) {
                        return draggingFraction.position.distance( u );
                    }
                } );
                final TreeMap<Vector2D, F<UpdateArgs, MovableFraction>> tm = fromMutableMap( ord, map );
                List<Vector2D> sorted = tm.keys().sort( ord );

                final Vector2D selectedAttachmentPoint = sorted.head();

                if ( selectedAttachmentPoint.equals( rightScaleAttachmentPoint ) ) { state = state.jettisonFraction( state.rightScale ).rightScaleDropTime( System.currentTimeMillis() ); }
                if ( selectedAttachmentPoint.equals( leftScaleAttachmentPoint ) ) { state = state.jettisonFraction( state.leftScale ).leftScaleDropTime( System.currentTimeMillis() ); }

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
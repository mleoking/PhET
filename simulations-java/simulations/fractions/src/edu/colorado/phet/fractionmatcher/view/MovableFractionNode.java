// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.view;

import fj.Equal;
import fj.F;
import fj.Ord;
import fj.data.List;

import java.awt.geom.Dimension2D;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractionmatcher.model.Cell;
import edu.colorado.phet.fractionmatcher.model.MatchingGameState;
import edu.colorado.phet.fractionmatcher.model.MovableFraction;
import edu.colorado.phet.fractionmatcher.model.UpdateArgs;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.fractionmatcher.model.Mode.USER_IS_MOVING_OBJECTS_TO_THE_SCALES;
import static edu.colorado.phet.fractionmatcher.model.Motions.*;
import static edu.colorado.phet.fractions.util.FJUtils.ord;
import static fj.data.TreeMap.fromMutableMap;

/**
 * Decorates a fraction representation node with mouse listeners and sets the position properly
 *
 * @author Sam Reid
 */
class MovableFractionNode extends PNode {
    public MovableFractionNode( final SettableProperty<MatchingGameState> model, final MovableFraction f, PNode node, final PNode rootNode, boolean pickable ) {
        addChild( node );
        setOffset( f.position.getX(), f.position.getY() );
        centerFullBoundsOnPoint( f.position.getX(), f.position.getY() );

        //Disallow interaction when showing clues
        setPickable( pickable );
        setChildrenPickable( pickable );

        //Attach input handlers if the fraction hasn't already been placed in the target location.
        if ( !f.scored ) {
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new SimSharingDragHandler( f.userComponent, UserComponentTypes.sprite, true ) {

                //Flag one slice as dragging
                @Override protected void startDrag( final PInputEvent event ) {
                    super.startDrag( event );
                    MatchingGameState state = model.get();
                    model.set( state.withFractions( state.fractions.delete( f, Equal.<MovableFraction>anyEqual() ).snoc( f.withDragging( true ) ) ) );
                }

                //Drag the dragged slice as identified by the model (since nodes will be destroyed as this happens)
                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );

                    MatchingGameState state = model.get();
                    final Dimension2D delta = event.getDeltaRelativeTo( rootNode );
                    MatchingGameState newState = state.withFractions( state.fractions.map( new F<MovableFraction, MovableFraction>() {
                        @Override public MovableFraction f( MovableFraction f ) {
                            return f.dragging ? f.translate( delta.getWidth(), delta.getHeight() ) : f;
                        }
                    } ) );
                    model.set( newState );
                }

                //Set all drag flags to false
                @Override protected void endDrag( final PInputEvent event ) {
                    super.endDrag( event );
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
                    final HashMap<Vector2D, F<UpdateArgs, MovableFraction>> map = new HashMap<Vector2D, F<UpdateArgs, MovableFraction>>() {{
                        put( rightScaleAttachmentPoint, MOVE_TO_RIGHT_SCALE );
                        put( leftScaleAttachmentPoint, MOVE_TO_LEFT_SCALE );
                        Cell cell = model.get().getClosestFreeStartCell( draggingFraction );
                        put( cell.getPosition(), moveToCell( cell ) );
                    }};

                    //Create an ordering that will sort based on distance from the dragging fraction.
                    final Ord<Vector2D> ordering = ord( new F<Vector2D, Double>() {
                        @Override public Double f( final Vector2D u ) {
                            return draggingFraction.position.distance( u );
                        }
                    } );

                    //List the keys in order of distance from the dragging fraction.
                    List<Vector2D> sorted = fromMutableMap( ordering, map ).keys().sort( ordering );

                    //Find the closest.
                    final Vector2D selectedAttachmentPoint = sorted.head();

                    //If attached to the right side, then jettison the pre-existing right-side fraction (and vice-versa) so this can take its place.
                    if ( selectedAttachmentPoint.equals( rightScaleAttachmentPoint ) ) {
                        state = state.jettisonFraction( state.rightScale ).withRightScaleDropTime( System.currentTimeMillis() );
                    }
                    if ( selectedAttachmentPoint.equals( leftScaleAttachmentPoint ) ) {
                        state = state.jettisonFraction( state.leftScale ).withLeftScaleDropTime( System.currentTimeMillis() );
                    }

                    //Animate the fraction to the nearest scale
                    final List<MovableFraction> newFractions = state.fractions.map( new F<MovableFraction, MovableFraction>() {
                        @Override public MovableFraction f( final MovableFraction f ) {
                            final F<UpdateArgs, MovableFraction> moveToScale = fromMutableMap( ordering, map ).get( selectedAttachmentPoint ).some();
                            return f.dragging ? f.withDragging( false ).withMotion( moveToScale ) : f;
                        }
                    } );

                    //Update the state with the new list of fractions, new mode and set it back to the model.
                    final MatchingGameState newState = state.withFractions( newFractions ).withMode( USER_IS_MOVING_OBJECTS_TO_THE_SCALES );
                    model.set( newState );
                }
            } );
        }
    }
}
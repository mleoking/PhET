// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.view;

import fj.Equal;
import fj.F;
import fj.Ord;
import fj.data.List;
import fj.data.TreeMap;

import java.awt.geom.Dimension2D;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractionmatcher.model.Cell;
import edu.colorado.phet.fractionmatcher.model.MatchingGameState;
import edu.colorado.phet.fractionmatcher.model.Mode;
import edu.colorado.phet.fractionmatcher.model.MovableFraction;
import edu.colorado.phet.fractionmatcher.model.UpdateArgs;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

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
                    HashMap<Vector2D, F<UpdateArgs, MovableFraction>> map = new HashMap<Vector2D, F<UpdateArgs, MovableFraction>>() {{
                        put( rightScaleAttachmentPoint, MOVE_TO_RIGHT_SCALE );
                        put( leftScaleAttachmentPoint, MOVE_TO_LEFT_SCALE );
                        Cell cell = model.get().getClosestFreeStartCell( draggingFraction );
                        put( cell.getPosition(), moveToCell( cell ) );
                    }};

                    //REVIEW: Needs some doc, hard to understand what's going on here.
                    final Ord<Vector2D> ord = ord( new F<Vector2D, Double>() {
                        @Override public Double f( final Vector2D u ) {
                            return draggingFraction.position.distance( u );
                        }
                    } );
                    final TreeMap<Vector2D, F<UpdateArgs, MovableFraction>> tm = fromMutableMap( ord, map );
                    List<Vector2D> sorted = tm.keys().sort( ord );

                    final Vector2D selectedAttachmentPoint = sorted.head();

                    if ( selectedAttachmentPoint.equals( rightScaleAttachmentPoint ) ) {
                        state = state.jettisonFraction( state.rightScale ).withRightScaleDropTime( System.currentTimeMillis() );
                    }
                    if ( selectedAttachmentPoint.equals( leftScaleAttachmentPoint ) ) {
                        state = state.jettisonFraction( state.leftScale ).withLeftScaleDropTime( System.currentTimeMillis() );
                    }

                    //animate to the closest destination
                    final List<MovableFraction> newFractions = state.fractions.map( new F<MovableFraction, MovableFraction>() {
                        @Override public MovableFraction f( final MovableFraction f ) {
                            return f.dragging ? f.withDragging( false ).withMotion( tm.get( selectedAttachmentPoint ).some() ) : f;
                        }
                    } );
                    // REVIEW: Why is this always being set to WAITING_FOR_USER_TO_CHECK_ANSWER at the end of a drag?  It seems
                    // like both scales need to be loaded first.  Not sure what to suggest, we just were confused by it when
                    // working through the code.
                    final MatchingGameState newState = state.withFractions( newFractions ).withMode( Mode.WAITING_FOR_USER_TO_CHECK_ANSWER );
                    model.set( newState );
                }
            } );
        }
    }
}
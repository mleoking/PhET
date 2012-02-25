// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame;

import fj.Equal;
import fj.F;

import java.awt.BasicStroke;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.matchinggame.model.Cell;
import edu.colorado.phet.fractionsintro.matchinggame.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static java.awt.Color.lightGray;

/**
 * Displays an immutable matching game state
 *
 * @author Sam Reid
 */
public class MatchingGameNode extends FNode {
    public MatchingGameNode( final SettableProperty<MatchingGameState> model, final PNode rootNode ) {
        model.get().cells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.rectangle.toRectangle2D(), new BasicStroke( 1 ), lightGray );
            }
        } ).foreach( addChild );
        model.get().fractions.map( new F<Fraction, PNode>() {
            @Override public PNode f( final Fraction f ) {
                return new PhetPText( f.numerator + "/" + f.denominator ) {{
                    centerFullBoundsOnPoint( f.position );
                    addInputEventListener( new CursorHandler() );

                    addInputEventListener( new PBasicInputEventHandler() {

                        //Flag one slice as dragging
                        @Override public void mousePressed( PInputEvent event ) {
                            MatchingGameState state = model.get();
                            model.set( state.fractions( state.fractions.delete( f, Equal.<Fraction>anyEqual() ).snoc( f.dragging( true ) ) ) );
                        }

                        //Set all drag flags to false
                        @Override public void mouseReleased( PInputEvent event ) {
                            final MatchingGameState state = model.get();
                            model.set( state.fractions( state.fractions.map( new F<Fraction, Fraction>() {
                                @Override public Fraction f( Fraction f ) {
                                    return f.dragging( false );
                                }
                            } ) ) );
                        }

                        //Drag the dragged slice as identified by the model (since nodes will be destroyed as this happens)
                        @Override public void mouseDragged( PInputEvent event ) {
                            MatchingGameState state = model.get();

                            final Dimension2D delta = event.getDeltaRelativeTo( rootNode );
                            MatchingGameState newState = state.fractions( state.fractions.map( new F<Fraction, Fraction>() {
                                @Override public Fraction f( Fraction f ) {
                                    return f.dragging ? f.translate( delta.getWidth(), delta.getHeight() ) : f;
                                }
                            } ) );
                            model.set( newState );
                        }
                    } );


                }};
            }
        } ).foreach( addChild );
    }
}

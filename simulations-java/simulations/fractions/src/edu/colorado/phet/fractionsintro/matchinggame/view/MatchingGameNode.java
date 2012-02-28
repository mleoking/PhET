// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.matchinggame.model.Cell;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Scale;
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
        final MatchingGameState state = model.get();
        final PNode scales = new RichPNode( state.leftScale.toNode(), state.rightScale.toNode() );
        addChild( scales );
        state.scales().map( new F<Scale, PNode>() {
            @Override public PNode f( Scale s ) {
                return s.toNode();
            }
        } ).foreach( addChild );
        state.scoreCells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.rectangle.toRoundedRectangle( 20, 20 ), lightGray );
            }
        } ).foreach( addChild );
        if ( state.getScaleValue( state.leftScale ) > 0 && state.getScaleValue( state.rightScale ) > 0 ) {
            final String string = state.getLeftScaleValue() < state.getRightScaleValue() ? "<" :
                                  state.getLeftScaleValue() > state.getRightScaleValue() ? ">" :
                                  "=";
            PNode sign = state.development.outline ? new PhetPPath( new PhetFont( 160, true ).createGlyphVector( new FontRenderContext( new AffineTransform(), true, true ), string ).getOutline(), Color.yellow, new BasicStroke( 2 ), Color.black ) :
                         new PhetPText( string, new PhetFont( 160, true ) );
            sign.addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    model.set( model.get().development( model.get().development.outline( !model.get().development.outline ) ) );
                }
            } );
            sign.centerFullBoundsOnPoint( scales.getFullBounds().getCenter2D().getX(), scales.getFullBounds().getCenter2D().getY() );
            addChild( sign );
        }

        //Render the cells, costs about 50% of a CPU--could be optimized
        state.cells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.rectangle.toRectangle2D(), new BasicStroke( 1 ), lightGray );
            }
        } ).foreach( addChild );
        state.fractions.map( new F<MovableFraction, PNode>() {
            @Override public PNode f( final MovableFraction f ) {
                return new MovableFractionNode( model, f, f.node.f( f.fraction() ), rootNode );
            }
        } ).foreach( addChild );
    }
}

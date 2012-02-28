// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;

import java.awt.BasicStroke;

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
            addChild( new PhetPText( state.getLeftScaleValue() < state.getRightScaleValue() ? "<" :
                                     state.getLeftScaleValue() > state.getRightScaleValue() ? ">" :
                                     "=", new PhetFont( 180, true ) ) {{
                centerFullBoundsOnPoint( scales.getFullBounds().getCenter2D() );
            }} );
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

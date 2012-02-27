// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame;

import fj.F;

import java.awt.BasicStroke;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
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
        model.get().scales().map( new F<Scale, PNode>() {
            @Override public PNode f( Scale s ) {
                return s.toNode();
            }
        } ).foreach( addChild );
        model.get().cells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.rectangle.toRectangle2D(), new BasicStroke( 1 ), lightGray );
            }
        } ).foreach( addChild );
        model.get().fractions.map( new F<MovableFraction, PNode>() {
            @Override public PNode f( final MovableFraction f ) {
                return new MovableFractionNode( model, f, f.node.f( f.fraction() ), rootNode );
            }
        } ).foreach( addChild );
    }
}

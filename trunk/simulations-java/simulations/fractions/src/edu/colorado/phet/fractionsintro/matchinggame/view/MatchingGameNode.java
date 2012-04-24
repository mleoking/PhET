// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.matchinggame.model.Cell;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Scale;
import edu.colorado.phet.fractionsintro.matchinggame.view.Controller.Next;
import edu.colorado.phet.fractionsintro.matchinggame.view.Controller.ShowAnswer;
import edu.colorado.phet.fractionsintro.matchinggame.view.Controller.TryAgain;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.showAnswerButton;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.tryAgainButton;
import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.STAGE_SIZE;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Mode.*;
import static java.awt.Color.lightGray;
import static java.awt.Color.yellow;

/**
 * Displays an immutable matching game state
 *
 * @author Sam Reid
 */
public class MatchingGameNode extends FNode {

    private final SettableProperty<MatchingGameState> model;

    public MatchingGameNode( final boolean showDeveloperControls, final SettableProperty<MatchingGameState> model, final PNode rootNode, final F<ButtonArgs, Button> buttonFactory ) {
        this.model = model;
        final MatchingGameState state = model.get();

        final PNode scales = new RichPNode( state.leftScale.toNode(), state.rightScale.toNode() );
        addChild( scales );

        final boolean revealClues = state.getMode() == SHOWING_WHY_ANSWER_WRONG ||
                                    state.getMode() == USER_CHECKED_CORRECT_ANSWER ||
                                    state.getMode() == SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS;
        addChild( new ZeroOffsetNode( new BarGraphNode( state.getLeftScaleValue(), state.getRightScaleValue(), revealClues ) ) {{
            setOffset( scales.getFullBounds().getCenterX() - getFullWidth() / 2, scales.getFullBounds().getCenterY() - getFullHeight() - 15 );
        }} );

        final FNode scalesNode = new FNode( state.getScales().map( new F<Scale, PNode>() {
            @Override public PNode f( final Scale s ) {
                return s.toNode();
            }
        } ) );
        addChild( scalesNode );

        final FNode scoreCellsLayer = new FNode( state.scoreCells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.toRoundedRectangle(), lightGray );
            }
        } ) );
        addChild( scoreCellsLayer );

        addChild( new PhetPText( "My Matches", new PhetFont( 18, true ) ) {{

            //Center "my matches" under the top left cell
            setOffset( scoreCellsLayer.getChild( 0 ).getFullBounds().getCenterX() - getFullWidth() / 2, scoreCellsLayer.getMaxY() );
        }} );

        final Vector2D buttonLocation = new Vector2D( state.getLastDroppedScaleRight() ? scalesNode.getFullBounds().getMaxX() + 80 : scalesNode.getFullBounds().getX() - 80,
                                                      scalesNode.getFullBounds().getCenterY() );

        //Show the sign
        if ( revealClues ) {
            addSignNode( state, scales );
        }

        if ( state.getLeftScaleValue() > 0 && state.getRightScaleValue() > 0 ) {
//            System.out.println( "state = " + state.getMode() + ", state.checks = " + state.getChecks() );

            if ( state.getMode() == SHOWING_WHY_ANSWER_WRONG ) {
                if ( state.getChecks() < 2 ) {
                    addChild( buttonFactory.f( new ButtonArgs( tryAgainButton, "Try again", Color.red, buttonLocation, new TryAgain() ) ) );
                }
                else {
                    addChild( buttonFactory.f( new ButtonArgs( showAnswerButton, "Show answer", Color.red, buttonLocation, new ShowAnswer() ) ) );
                }
            }

            //TODO: This shows a flicker of "check answer" after user presses next, needs to be fixed
            else if ( state.getChecks() < 2 && state.getMode() == WAITING_FOR_USER_TO_CHECK_ANSWER ) {
                addChild( buttonFactory.f( new ButtonArgs( Components.checkAnswerButton, "Check answer", Color.orange, buttonLocation, new CheckAnswer() ) ) );
            }

            //If they match, show a "Keep" button. This allows the student to look at the right answer as long as they want before moving it to the scoreboard.
            if ( state.getMode() == USER_CHECKED_CORRECT_ANSWER ) {
                addSignNode( state, scales );

                addChild( new VBox( new FaceNode( 120 ), new PhetPText( state.getChecks() == 1 ? "+2" : "+1", new PhetFont( 18, true ) ) ) {{
                    final Vector2D pt = buttonLocation.plus( 0, -150 );
                    centerFullBoundsOnPoint( pt.getX() - getFullBounds().getWidth() / 2, pt.getY() );
                }} );

                addChild( buttonFactory.f( new ButtonArgs( Components.keepMatchButton, "Next", Color.green, buttonLocation, new Next() ) ) );
            }

            if ( state.getMode() == SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS ) {
                addChild( buttonFactory.f( new ButtonArgs( Components.keepMatchButton, "Next", Color.green, buttonLocation, new Next() ) ) );
            }
        }
        state.scoreCells.take( state.scored ).map( new F<Cell, PNode>() {
            @Override public PNode f( final Cell cell ) {
                return new PhetPText( "=", new PhetFont( 22 ) ) {{centerFullBoundsOnPoint( cell.rectangle.getCenter() );}};
            }
        } ).foreach( addChild );

        //Render the cells, costs about 50% of a CPU--could be optimized
        state.startCells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.rectangle.toRectangle2D(), new BasicStroke( 1 ), lightGray );
            }
        } ).foreach( addChild );
        state.fractions.map( new F<MovableFraction, PNode>() {
            @Override public PNode f( final MovableFraction f ) {
                return new MovableFractionNode( model, f, f.toNode(), rootNode, !revealClues );
            }
        } ).foreach( addChild );

        addChild( new ScoreboardNode( model ) {{
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, scoreCellsLayer.getMaxY() + INSET );
        }} );

        if ( showDeveloperControls ) {
            addChild( buttonFactory.f( new ButtonArgs( null, "Resample", Color.red, new Vector2D( 100, 6 ), new Resample() ) ) );
        }
    }

    //Encapsulates stroke, paint and stroke paint for a sign node like "=", "<", ">"
    public static PhetPPath createSignNode( Shape shape ) { return new PhetPPath( shape, yellow, new BasicStroke( 2 ), Color.black ); }

    private void addSignNode( final MatchingGameState state, final PNode scales ) {
        class TextSign extends PNode {
            TextSign( String text ) {
                final PhetFont textFont = new PhetFont( 100, true );
                final PNode sign = createSignNode( textFont.createGlyphVector( new FontRenderContext( new AffineTransform(), true, true ), text ).getOutline() );
                addChild( sign );
            }
        }

        final PNode sign = state.getLeftScaleValue() < state.getRightScaleValue() ? new TextSign( "<" ) :
                           state.getLeftScaleValue() > state.getRightScaleValue() ? new TextSign( ">" ) :
                           new EqualsSignNode();
        sign.centerFullBoundsOnPoint( scales.getFullBounds().getCenter2D().getX(), scales.getFullBounds().getCenter2D().getY() + 10 );
        addChild( sign );
    }
}

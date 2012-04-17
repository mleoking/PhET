// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.Cell;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Scale;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.*;
import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.initialState;
import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.newLevel;
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

    //Encapsulates stroke, paint and stroke paint for a sign node like "=", "<", ">"
    public static PhetPPath createSignNode( Shape shape ) { return new PhetPPath( shape, yellow, new BasicStroke( 2 ), Color.black ); }

    public MatchingGameNode( final boolean showDeveloperControls, final SettableProperty<MatchingGameState> model, final PNode rootNode ) {
        this.model = model;
        final MatchingGameState state = model.get();
        final PNode scales = new RichPNode( state.leftScale.toNode(), state.rightScale.toNode() );
        addChild( scales );

        final boolean revealClues = state.mode == SHOWING_WHY_ANSWER_WRONG ||
                                    state.mode == USER_CHECKED_CORRECT_ANSWER ||
                                    state.mode == SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS;
        addChild( new ZeroOffsetNode( new BarGraphNode( state.getLeftScaleValue(), state.leftScaleDropTime, state.getRightScaleValue(), state.rightScaleDropTime, revealClues ) ) {{
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

        final ImmutableVector2D buttonLocation = new ImmutableVector2D( state.getLastDroppedScaleRight() ? scalesNode.getFullBounds().getMaxX() + 80 : scalesNode.getFullBounds().getX() - 80,
                                                                        scalesNode.getFullBounds().getCenterY() );

        //Show the sign
        if ( revealClues ) {
            addSignNode( state, scales );
        }

        if ( state.getLeftScaleValue() > 0 && state.getRightScaleValue() > 0 ) {
            System.out.println( "state = " + state.mode + ", state.checks = " + state.checks );

            if ( state.mode == SHOWING_WHY_ANSWER_WRONG ) {
                if ( state.checks < 2 ) {
                    addChild( new Button( tryAgainButton, "Try again", Color.red, buttonLocation, new ActionListener() {
                        @Override public void actionPerformed( final ActionEvent e ) {
                            updateWith( new TryAgain() );
                        }
                    } ) );
                }
                else {
                    addChild( new Button( showAnswerButton, "Show answer", Color.red, buttonLocation, new ActionListener() {
                        @Override public void actionPerformed( final ActionEvent e ) {
                            updateWith( new ShowAnswer() );
                        }
                    } ) );
                }
            }

            //TODO: This shows a flicker of "check answer" after user presses next, needs to be fixed
            else if ( state.checks < 2 && state.mode == WAITING_FOR_USER_TO_CHECK_ANSWER ) {
                addChild( new Button( checkAnswerButton, "Check answer", Color.orange, buttonLocation, new ActionListener() {
                    @Override public void actionPerformed( final ActionEvent e ) {
                        updateWith( new CheckAnswer() );
                    }
                } ) );
            }

            //If they match, show a "Keep" button. This allows the student to look at the right answer as long as they want before moving it to the scoreboard.
            if ( state.mode == USER_CHECKED_CORRECT_ANSWER ) {
                addSignNode( state, scales );

                addChild( new VBox( new FaceNode( 200 ), new PhetPText( state.checks == 1 ? "+2" : "+1", new PhetFont( 18, true ) ) ) {{
                    final ImmutableVector2D pt = buttonLocation.plus( 0, -150 );
                    centerFullBoundsOnPoint( pt.getX(), pt.getY() );
                }} );

                addChild( new Button( Components.keepMatchButton, "Next", Color.green, buttonLocation, new ActionListener() {
                    @Override public void actionPerformed( ActionEvent e ) {
                        updateWith( new Next() );
                    }
                } ) );
            }

            if ( state.mode == SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS ) {
                addChild( new Button( Components.keepMatchButton, "Next", Color.green, buttonLocation, new ActionListener() {
                    @Override public void actionPerformed( ActionEvent e ) {
                        updateWith( new Next() );
                    }
                } ) );
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

        final int newLevel = state.level + 1;
        final ActionListener nextLevel = new ActionListener() {
            @Override public void actionPerformed( ActionEvent e ) {
                model.set( newLevel( newLevel ).withAudio( state.audio ) );
            }
        };

        if ( state.scored == state.scoreCells.length() ) {

            final ActionListener playAgain = new ActionListener() {
                @Override public void actionPerformed( ActionEvent e ) {
                    model.set( newLevel( state.level ).withAudio( state.audio ) );
                }
            };

            addChild( new VBox( new HTMLImageButtonNode( "Play again", Color.orange ) {{ addActionListener( playAgain ); }},
                                new HTMLImageButtonNode( "Level " + newLevel, Color.green ) {{ addActionListener( nextLevel ); }}
            ) {{
                setOffset( AbstractFractionsCanvas.STAGE_SIZE.getWidth() - getFullWidth(), AbstractFractionsCanvas.STAGE_SIZE.getHeight() / 2 - getFullHeight() / 2 );
            }} );
        }

        if ( showDeveloperControls ) {
            addChild( new VBox(
                    new Button( null, "Reset", Color.yellow, ImmutableVector2D.ZERO, new ActionListener() {
                        @Override public void actionPerformed( final ActionEvent e ) {
                            model.set( initialState() );
                        }
                    } ),
                    new Button( null, "Resample", Color.yellow, ImmutableVector2D.ZERO, new ActionListener() {
                        @Override public void actionPerformed( final ActionEvent e ) {
                            model.set( newLevel( model.get().level ) );
                        }
                    } ),
                    new Button( null, "Skip to level " + newLevel, Color.yellow, ImmutableVector2D.ZERO, nextLevel )
            ) {{
                setOffset( 0, 200 );
            }} );
        }
    }

    //Apply the specified function to the model
    private void updateWith( final F<MatchingGameState, MatchingGameState> f ) { model.set( f.f( model.get() ) ); }

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

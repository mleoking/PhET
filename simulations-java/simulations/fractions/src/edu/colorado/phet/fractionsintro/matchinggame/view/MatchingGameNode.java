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

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.view.FractionsIntroCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.Cell;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Scale;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static java.awt.Color.lightGray;
import static java.awt.Color.yellow;

/**
 * Displays an immutable matching game state
 *
 * @author Sam Reid
 */
public class MatchingGameNode extends FNode {

    //Encapsulates stroke, paint and stroke paint for a sign node like "=", "<", ">"
    public static PhetPPath createSignNode( Shape shape ) { return new PhetPPath( shape, yellow, new BasicStroke( 2 ), Color.black ); }

    public MatchingGameNode( final boolean showDeveloperControls, final SettableProperty<MatchingGameState> model, final PNode rootNode ) {
        final MatchingGameState state = model.get();
        final PNode scales = new RichPNode( state.leftScale.toNode(), state.rightScale.toNode() );
        addChild( scales );

        addChild( new ZeroOffsetNode( new BarGraphNode( state.getLeftScaleValue(), state.leftScaleDropTime, state.getRightScaleValue(), state.rightScaleDropTime ) ) {{
            setOffset( scales.getFullBounds().getCenterX() - getFullWidth() / 2, scales.getFullBounds().getCenterY() - getFullHeight() - 15 );
        }} );

        final FNode scalesNode = new FNode() {{
            state.getScales().map( new F<Scale, PNode>() {
                @Override public PNode f( Scale s ) {
                    return s.toNode();
                }
            } ).foreach( addChild );
        }};
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

        if ( state.getLeftScaleValue() > 0 && state.getRightScaleValue() > 0 ) {

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

            //If they match, show a "Keep" button. This allows the student to look at the right answer as long as they want before moving it to the scoreboard.
            if ( state.getLeftScaleValue() == state.getRightScaleValue() ) {
                addChild( new HTMLImageButtonNode( "Keep<br>Match", new PhetFont( 16, true ), Color.orange ) {{
                    centerFullBoundsOnPoint( state.getLastDroppedScaleRight() ? scalesNode.getFullBounds().getMaxX() + 80 :
                                             scalesNode.getFullBounds().getX() - getFullBounds().getWidth(), scalesNode.getFullBounds().getCenterY() );
                    addActionListener( new ActionListener() {
                        @Override public void actionPerformed( ActionEvent e ) {
                            model.set( model.get().animateMatchToScoreCell() );
                        }
                    } );
                }} );
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
                return new MovableFractionNode( model, f, f.toNode(), rootNode );
            }
        } ).foreach( addChild );

        final int newLevel = state.level + 1;
        final ActionListener nextLevel = new ActionListener() {
            @Override public void actionPerformed( ActionEvent e ) {
                model.set( MatchingGameState.initialState( newLevel ).audio( state.audio ) );
            }
        };

        if ( state.scored == state.scoreCells.length() ) {

            final ActionListener playAgain = new ActionListener() {
                @Override public void actionPerformed( ActionEvent e ) {
                    model.set( MatchingGameState.initialState( state.level ).audio( state.audio ) );
                }
            };

            addChild( new VBox( new HTMLImageButtonNode( "Play again", Color.orange ) {{ addActionListener( playAgain ); }},
                                new HTMLImageButtonNode( "Level " + newLevel, Color.green ) {{ addActionListener( nextLevel ); }}
            ) {{
                setOffset( AbstractFractionsCanvas.STAGE_SIZE.getWidth() - getFullWidth(), AbstractFractionsCanvas.STAGE_SIZE.getHeight() / 2 - getFullHeight() / 2 );
            }} );
        }
        addChild( new PImage( state.audio ? Images.SOUND_MAX : Images.SOUND_MIN ) {{
            setOffset( FractionsIntroCanvas.STAGE_SIZE.getWidth() - getFullBounds().getWidth() - INSET, FractionsIntroCanvas.STAGE_SIZE.getHeight() - getFullBounds().getHeight() - INSET );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    model.set( state.audio( !state.audio ) );
                }
            } );
        }} );

        if ( showDeveloperControls ) {
            addChild( new VBox(
                    new HTMLImageButtonNode( "Reset" ) {{
                        addActionListener( new ActionListener() {
                            @Override public void actionPerformed( final ActionEvent e ) {
                                model.set( MatchingGameState.initialState() );
                            }
                        } );
                    }},
                    new HTMLImageButtonNode( "Resample" ) {{
                        addActionListener( new ActionListener() {
                            @Override public void actionPerformed( final ActionEvent e ) {
                                model.set( MatchingGameState.initialState( model.get().level ) );
                            }
                        } );
                    }},
                    new HTMLImageButtonNode( "Skip to level " + newLevel ) {{
                        addActionListener( nextLevel );
                    }}
            ) {{setOffset( 0, 200 );}} );
        }
    }
}

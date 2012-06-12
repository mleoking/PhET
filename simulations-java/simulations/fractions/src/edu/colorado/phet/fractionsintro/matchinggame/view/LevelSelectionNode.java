package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.ToggleButtonNode;
import edu.colorado.phet.fractionsintro.common.view.Colors;
import edu.colorado.phet.fractionsintro.matchinggame.model.GameOverScore;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Polygon;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.umd.cs.piccolo.PNode;

import static fj.Ord.doubleOrd;

/**
 * Node that shows the levels and lets the user choose the level and settings
 *
 * @author Sam Reid
 */
public class LevelSelectionNode extends PNode {
    public LevelSelectionNode( final VoidFunction0 startGame, final GameSettings gameSettings, final Property<List<GameOverScore>> gameOverScores ) {

        final List<PatternNode> patterns = List.list( new PatternNode( FilledPattern.sequentialFill( Pattern.pie( 1 ), 1 ), Color.red ),
                                                      new PatternNode( FilledPattern.sequentialFill( Pattern.horizontalBars( 2 ), 2 ), Colors.LIGHT_GREEN ),
                                                      new PatternNode( FilledPattern.sequentialFill( Pattern.verticalBars( 3 ), 3 ), Colors.LIGHT_BLUE ),
                                                      new PatternNode( FilledPattern.sequentialFill( Pattern.tetrisPiece( 50 ), 4 ), Color.orange ),
                                                      new PatternNode( FilledPattern.sequentialFill( Polygon.create( 60, 5 ), 5 ), Color.magenta ),
                                                      new PatternNode( FilledPattern.sequentialFill( Pattern.sixFlower(), 6 ), Color.yellow ) );

        for ( PatternNode pattern : patterns ) {
            pattern.scale( 90 / pattern.getFullBounds().getWidth() );
        }

        final double maxIconWidth = patterns.map( new F<PatternNode, Double>() {
            @Override public Double f( final PatternNode n ) {
                return n.getFullBounds().getWidth();
            }
        } ).maximum( doubleOrd );
        final double maxIconHeight = patterns.map( new F<PatternNode, Double>() {
            @Override public Double f( final PatternNode n ) {
                return n.getFullBounds().getHeight();
            }
        } ).maximum( doubleOrd );

        //level buttons at the top
        List<LevelIconNode> icons = patterns.map( new F<PatternNode, LevelIconNode>() {
            @Override public LevelIconNode f( final PatternNode patternNode ) {
                final Integer levelIndex = patterns.elementIndex( Equal.<PatternNode>anyEqual(), patternNode ).some();
                int levelName = levelIndex + 1;
                return new LevelIconNode( "Level " + levelName, patternNode, maxIconWidth, maxIconHeight, levelName, gameOverScores );
            }
        } );

        int column = 0;
        int row = 0;
        for ( final LevelIconNode icon : icons ) {
            final Property<Boolean> selected = new Property<Boolean>( false );
            ToggleButtonNode button = new ToggleButtonNode( icon, selected, new VoidFunction0() {
                public void apply() {
                    selected.set( true );
                    gameSettings.level.set( icon.levelName );

                    //Show it pressed in for a minute before starting up.
                    //TODO: make it act more like a button (hold it down and fire on release)
                    new Timer( 100, new ActionListener() {
                        public void actionPerformed( final ActionEvent e ) {

                            startGame.apply();

                            //prep for next time
                            selected.set( false );
                        }
                    } ) {{
                        setInitialDelay( 100 );
                        setRepeats( false );
                    }}.start();
                }
            } );
            addChild( button );
            button.setOffset( column * 200 + 50, row * 250 + 50 );
            column++;
            if ( column >= 3 ) {
                column = 0;
                row++;
            }
        }
    }

    public static class LevelIconNode extends PNode {
        public final Integer levelName;

        public LevelIconNode( final String text, final PatternNode patternNode, final double maxIconWidth, final double maxIconHeight, final Integer levelName, final Property<List<GameOverScore>> gameOverScores ) {
            this.levelName = levelName;
            addChild( new VBox( new PhetPText( text, new PhetFont( 18, true ) ), new PaddedIcon( maxIconWidth, maxIconHeight, patternNode ), new StarSetNode( gameOverScores, levelName ) ) );
        }
    }
}
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.Timer;

import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.ToggleButtonNode;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.common.view.Colors;
import edu.colorado.phet.fractionsintro.matchinggame.model.GameResult;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Polygon.createPolygon;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.*;
import static edu.colorado.phet.fractionsintro.matchinggame.view.FilledPattern.sequentialFill;
import static fj.Ord.doubleOrd;
import static fj.data.List.list;

/**
 * Node that shows the levels and lets the user choose the level and settings
 *
 * @author Sam Reid
 */
public class LevelSelectionNode extends PNode {
    public LevelSelectionNode( final VoidFunction0 startGame, final GameSettings gameSettings, final Property<List<GameResult>> gameResults, final boolean standaloneSim ) {

        final List<PatternNode> moduleLevels = list( new PatternNode( sequentialFill( pie( 1 ), 1 ), Color.red ),
                                                     new PatternNode( sequentialFill( horizontalBars( 2 ), 2 ), Colors.LIGHT_GREEN ),
                                                     new PatternNode( sequentialFill( verticalBars( 3 ), 3 ), Colors.LIGHT_BLUE ),
                                                     new PatternNode( sequentialFill( letterLShapedDiagonal( 15, 2 ), 4 ), Color.orange ),
                                                     new PatternNode( sequentialFill( createPolygon( 60, 5 ), 5 ), Color.magenta ),
                                                     new PatternNode( sequentialFill( sixFlower(), 6 ), Color.yellow ) );

        final List<PatternNode> appLevels = list( new PatternNode( sequentialFill( ringOfHexagons(), 7 ), Color.pink ),
                                                  new PatternNode( sequentialFill( createPolygon( 60, 8 ), 8 ), Color.green ) );

        final List<PatternNode> patterns = standaloneSim ? moduleLevels.append( appLevels ) :
                                           moduleLevels;

        for ( PatternNode pattern : patterns ) {
            pattern.scale( 90 / pattern.getFullBounds().getWidth() );
        }

        //Get bounds for layout so all buttons same size
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
                return new LevelIconNode( MessageFormat.format( Strings.LEVEL__PATTERN, levelName ), patternNode, maxIconWidth, maxIconHeight, levelName, gameResults );
            }
        } );

        int column = 0;
        int row = 0;
        for ( final LevelIconNode icon : icons ) {
            final Property<Boolean> selected = new Property<Boolean>( false );
            ToggleButtonNode button = new ToggleButtonNode( icon, selected, new VoidFunction0() {
                public void apply() {
                    SimSharingManager.sendButtonPressed( UserComponentChain.chain( Components.levelButton, icon.levelName ) );
                    selected.set( true );
                    gameSettings.level.set( icon.levelName );

                    //Show it pressed in for a minute before starting up.
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
            int maxColumns = standaloneSim ? 4 : 3;
            if ( column >= maxColumns ) {
                column = 0;
                row++;
            }
        }
    }

    //Button icon for a single level, shows the level name, a shape and the progress stars
    public static class LevelIconNode extends PNode {
        public final Integer levelName;

        public LevelIconNode( final String text, final PatternNode patternNode, final double maxIconWidth, final double maxIconHeight, final Integer levelName, final Property<List<GameResult>> gameResults ) {
            this.levelName = levelName;
            addChild( new VBox( new PhetPText( text, new PhetFont( 18, true ) ), new PaddedIcon( maxIconWidth, maxIconHeight, patternNode ), new StarSetNode( gameResults, levelName ) ) );
        }
    }
}
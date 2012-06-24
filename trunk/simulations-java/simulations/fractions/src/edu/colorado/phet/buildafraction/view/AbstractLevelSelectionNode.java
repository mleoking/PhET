package edu.colorado.phet.buildafraction.view;

import fj.F;
import fj.data.List;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractionmatcher.model.GameResult;
import edu.colorado.phet.fractionmatcher.view.PaddedIcon;
import edu.colorado.phet.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractionmatcher.view.StarSetNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that shows the levels and lets the user choose the level and settings
 *
 * @author Sam Reid
 */
public class AbstractLevelSelectionNode extends PNode {

    private static @Data class LevelInfo {
        public final String name;
        public final PNode icon;
        public final int maxStars;
        public final int filledStars;

        public static final F<LevelInfo, PNode> _icon = new F<LevelInfo, PNode>() {
            @Override public PNode f( final LevelInfo levelInfo ) {
                return levelInfo.icon;
            }
        };

        public LevelInfo withIcon( final PNode icon ) { return new LevelInfo( name, icon, maxStars, filledStars ); }
    }

    //Rows + Columns
    public AbstractLevelSelectionNode( List<List<LevelInfo>> _info ) {

        final List<PNode> nodes = _info.bind( new F<List<LevelInfo>, List<PNode>>() {
            @Override public List<PNode> f( final List<LevelInfo> list ) {
                return list.map( LevelInfo._icon );
            }
        } );
        final Dimension2DDouble maxSize = PaddedIcon.getMaxSize( nodes );
        List<List<LevelInfo>> info = _info.map( new F<List<LevelInfo>, List<LevelInfo>>() {
            @Override public List<LevelInfo> f( final List<LevelInfo> list ) {
                return list.map( new F<LevelInfo, LevelInfo>() {
                    @Override public LevelInfo f( final LevelInfo info ) {
                        return info.withIcon( new PaddedIcon( maxSize, info.icon ) );
                    }
                } );
            }
        } );

        //level buttons at the top
//        List<LevelIconNode> icons = patterns.map( new F<PatternNode, LevelIconNode>() {
//            @Override public LevelIconNode f( final PatternNode patternNode ) {
//                final Integer levelIndex = patterns.elementIndex( Equal.<PatternNode>anyEqual(), patternNode ).some();
//                int levelName = levelIndex + 1;
//                return new LevelIconNode( MessageFormat.format( Strings.LEVEL__PATTERN, levelName ), patternNode, maxIconWidth, maxIconHeight, levelName, gameResults );
//            }
//        } );
//
//        int column = 0;
//        int row = 0;
//        for ( final LevelIconNode icon : icons ) {
//            final Property<Boolean> selected = new Property<Boolean>( false );
//            ToggleButtonNode button = new ToggleButtonNode( icon, selected, new VoidFunction0() {
//                public void apply() {
//                    SimSharingManager.sendButtonPressed( UserComponentChain.chain( Components.levelButton, icon.levelName ) );
//                    selected.set( true );
//                    gameSettings.level.set( icon.levelName );
//
//                    //Show it pressed in for a minute before starting up.
//                    new Timer( 100, new ActionListener() {
//                        public void actionPerformed( final ActionEvent e ) {
//
//                            startGame.apply();
//
//                            //prep for next time
//                            selected.set( false );
//                        }
//                    } ) {{
//                        setInitialDelay( 100 );
//                        setRepeats( false );
//                    }}.start();
//                }
//            } );
//            addChild( button );
//            button.setOffset( column * 200 + 50, row * 250 + 50 );
//            column++;
//            int maxColumns = standaloneSim ? 4 : 3;
//            if ( column >= maxColumns ) {
//                column = 0;
//                row++;
//            }
//        }
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
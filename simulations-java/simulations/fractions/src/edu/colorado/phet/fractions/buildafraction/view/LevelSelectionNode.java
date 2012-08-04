// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberCardNode;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberDragContext;
import edu.colorado.phet.fractions.common.view.Colors;
import edu.colorado.phet.fractions.common.view.SettingsOnOffPanel;
import edu.colorado.phet.fractions.common.view.SettingsOnOffPanel.Element;
import edu.colorado.phet.fractions.fractionmatcher.model.Pattern;
import edu.colorado.phet.fractions.fractionmatcher.view.PatternNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.games.GameConstants.SOUND_ICON;
import static edu.colorado.phet.common.games.GameConstants.SOUND_OFF_ICON;
import static edu.colorado.phet.fractions.buildafraction.view.LevelType.NUMBERS;
import static edu.colorado.phet.fractions.buildafraction.view.LevelType.SHAPES;
import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.STAGE_SIZE;
import static edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern.sequentialFill;
import static fj.data.List.list;

/**
 * Screen that shows buttons for each level and stars indicating progress.
 *
 * @author Sam Reid
 */
public class LevelSelectionNode extends AbstractLevelSelectionNode {
    public static final Color[] colors = new Color[] { Colors.LIGHT_RED, Colors.LIGHT_BLUE, Colors.LIGHT_GREEN, Colors.LIGHT_ORANGE, Color.magenta, Color.yellow, Color.CYAN, Color.lightGray, Color.pink, Color.orange };

    public LevelSelectionNode( final String title, final BuildAFractionCanvas canvas, BooleanProperty audioEnabled, IntegerProperty selectedPage, F<LevelIdentifier, LevelProgress> gameProgress ) {
        super( title, list( new Page( page1( gameProgress ) ), new Page( page2( gameProgress ) ) ), canvas, selectedPage );

        //Add the audio on/off panel
        addChild( new SettingsOnOffPanel( list( new Element( new PImage( SOUND_OFF_ICON ),
                                                             new PImage( SOUND_ICON ), audioEnabled ) ) ) {{
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, resetAllButton.getFullBounds().getMinY() - getFullBounds().getHeight() - INSET );
        }} );
    }

    private static List<List<LevelInfo>> page1( final F<LevelIdentifier, LevelProgress> gameProgress ) {
        return list( list( shapeLevel( 1, Pattern.pie( 1 ), gameProgress ),
                           shapeLevel( 2, Pattern.verticalBars( 2 ), gameProgress ),
                           shapeLevel( 3, gameProgress ),
                           shapeLevel( 4, gameProgress ),
                           shapeLevel( 5, gameProgress ) ),
                     list( numberLevel( 1, gameProgress ),
                           numberLevel( 2, gameProgress ),
                           numberLevel( 3, gameProgress ),
                           numberLevel( 4, gameProgress ),
                           numberLevel( 5, gameProgress ) ) );
    }

    private static List<List<LevelInfo>> page2( final F<LevelIdentifier, LevelProgress> gameProgress ) {
        return list( list( shapeLevel( 6, gameProgress ),
                           shapeLevel( 7, gameProgress ),
                           shapeLevel( 8, gameProgress ),
                           shapeLevel( 9, gameProgress ),
                           shapeLevel( 10, gameProgress ) ),
                     list( numberLevel( 6, gameProgress ),
                           numberLevel( 7, gameProgress ),
                           numberLevel( 8, gameProgress ),
                           numberLevel( 9, gameProgress ),
                           numberLevel( 10, gameProgress ) ) );
    }

    private static LevelInfo shapeLevel( final int level, final F<LevelIdentifier, LevelProgress> gameProgress ) {
        return shapeLevel( level, getPattern( level ), gameProgress );
    }

    private static Pattern getPattern( final int level ) {
        return level <= 5 ? Pattern.polygon( 80, level ) :
               level == 6 ? Pattern.sixFlower() :
               level == 7 ? Pattern.ringOfHexagons() :
               level == 8 ? Pattern.ninjaStar() :
               level == 9 ? Pattern.grid( 3 ) :
               level == 10 ? Pattern.letterLShapedDiagonal( 10, 5 ) :
               null;
    }

    private static LevelInfo shapeLevel( final int level, Pattern pattern, final F<LevelIdentifier, LevelProgress> gameProgress ) {
        return new LevelInfo( new LevelIdentifier( level - 1, SHAPES ), MessageFormat.format( Strings.LEVEL__PATTERN, level ), new PatternNode( sequentialFill( pattern, level ), colors[( level - 1 ) % colors.length] ),
                              gameProgress.f( new LevelIdentifier( level - 1, SHAPES ) ) );
    }

    private static LevelInfo numberLevel( int level, final F<LevelIdentifier, LevelProgress> gameProgress ) {
        return new LevelInfo( new LevelIdentifier( level - 1, NUMBERS ), MessageFormat.format( Strings.LEVEL__PATTERN, level ), createLevelIcon( level ),
                              gameProgress.f( new LevelIdentifier( level - 1, NUMBERS ) ) );
    }

    private static PNode createLevelIcon( final int level ) {
        return new PNode() {{
            for ( int i = 0; i < level; i++ ) {
                NumberCardNode card = new NumberCardNode( new Dimension2DDouble( level < 10 ? 60 : 70, 75 ), level, new NumberDragContext() {
                    public void endDrag( final NumberCardNode draggableNumberNode ) {
                    }
                } );
                addChild( card );
                card.setOffset( i * 4, i * 4 );
            }
        }};
    }
}
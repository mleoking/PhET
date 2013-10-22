// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.nodes.controlpanel.SettingsOnOffPanel;
import edu.colorado.phet.common.piccolophet.nodes.controlpanel.SettingsOnOffPanel.Feature;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberCardNode;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberDragContext;
import edu.colorado.phet.fractions.common.view.Colors;
import edu.colorado.phet.fractions.fractionmatcher.model.Pattern;
import edu.colorado.phet.fractions.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
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
 * This screen is what is shown initially in the "Build a Fraction" tab, and the user can navigate back to it by pressing the LevelSelectionScreenButton.
 *
 * @author Sam Reid
 */
public class LevelSelectionNode extends AbstractLevelSelectionNode {
    public static final Color[] colors = new Color[]{Colors.LIGHT_RED, Colors.LIGHT_BLUE, Colors.LIGHT_GREEN, Colors.LIGHT_ORANGE, Color.magenta, Color.yellow, Color.CYAN, new Color( 146, 54, 173 ), new Color( 255, 112, 213 ), new Color( 45, 165, 59 )};

    public LevelSelectionNode( final String title, final BuildAFractionCanvas canvas, BooleanProperty audioEnabled, IntegerProperty selectedPage, F<LevelIdentifier, LevelProgress> gameProgress ) {
        super( title, list( new Page( page1( gameProgress ) ), new Page( page2( gameProgress ) ) ), canvas, selectedPage );

        //Add the audio on/off panel
        addChild( new SettingsOnOffPanel( list( new Feature( new PImage( SOUND_OFF_ICON ),
                                                             new PImage( SOUND_ICON ), audioEnabled, Components.soundRadioButton ) ) ) {{
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, resetAllButton.getFullBounds().getMinY() - getFullBounds().getHeight() - INSET );
        }} );
    }

    //List of levels for the first page.
    @SuppressWarnings("unchecked") private static List<List<LevelInfo>> page1( final F<LevelIdentifier, LevelProgress> f ) {
        return list( list( shapeLevel( 1, Pattern.pie( 1 ), f ), //unchecked warning
                           shapeLevel( 2, Pattern.verticalBars( 2 ), f ),
                           shapeLevel( 3, f ),
                           shapeLevel( 4, f ),
                           shapeLevel( 5, f ) ),
                     list( numberLevel( 1, f ),
                           numberLevel( 2, f ),
                           numberLevel( 3, f ),
                           numberLevel( 4, f ),
                           numberLevel( 5, f ) ) );
    }

    //List of levels for the 2nd page.
    @SuppressWarnings("unchecked") private static List<List<LevelInfo>> page2( final F<LevelIdentifier, LevelProgress> f ) {
        return list( list( shapeLevel( 6, f ), //unchecked warning
                           shapeLevel( 7, f ),
                           shapeLevel( 8, f ),
                           shapeLevel( 9, f ),
                           shapeLevel( 10, f ) ),
                     list( numberLevel( 6, f ),
                           numberLevel( 7, f ),
                           numberLevel( 8, f ),
                           numberLevel( 9, f ),
                           numberLevel( 10, f ) ) );
    }

    //Gets the icon that should be shown in the button
    private static Pattern getIconPattern( final int level ) {
        return level <= 5 ? Pattern.polygon( 80, level ) :
               level == 6 ? Pattern.sixFlower() :
               level == 7 ? Pattern.ringOfHexagons() :
               level == 8 ? Pattern.ninjaStar() :
               level == 9 ? Pattern.grid( 3 ) :
               level == 10 ? Pattern.letterLShapedDiagonal( 10, 5 ) :
               null;
    }

    //Convenience method for creating a shape LevelInfo
    private static LevelInfo shapeLevel( final int level, final F<LevelIdentifier, LevelProgress> gameProgress ) {
        return shapeLevel( level, getIconPattern( level ), gameProgress );
    }


    //Creates a shape LevelInfo from the specified pattern and progress
    private static LevelInfo shapeLevel( final int level, Pattern pattern, final F<LevelIdentifier, LevelProgress> gameProgress ) {
        return new LevelInfo( new LevelIdentifier( level - 1, SHAPES ), MessageFormat.format( Strings.LEVEL__PATTERN, level ),
                              new PatternNode( sequentialFill( pattern, level ), colors[( level - 1 ) % colors.length] ),
                              gameProgress.f( new LevelIdentifier( level - 1, SHAPES ) ) );
    }

    //Creates a number LevelInfo
    private static LevelInfo numberLevel( int level, final F<LevelIdentifier, LevelProgress> gameProgress ) {
        return new LevelInfo( new LevelIdentifier( level - 1, NUMBERS ), MessageFormat.format( Strings.LEVEL__PATTERN, level ), createNumberLevelIcon( level ),
                              gameProgress.f( new LevelIdentifier( level - 1, NUMBERS ) ) );
    }

    //Creates an icon for the specified number level.
    private static PNode createNumberLevelIcon( final int level ) {
        return new PNode() {{
            for ( int i = 0; i < level; i++ ) {
                NumberCardNode card = new NumberCardNode( new Dimension2DDouble( level < 10 ? 60 : 80, 75 ), level, new NumberDragContext() {
                    public void endDrag( final NumberCardNode numberCardNode ) {
                    }

                    public void startDrag( final NumberCardNode numberCardNode ) {
                    }
                } );
                addChild( card );
                card.setOffset( i * 4, i * 4 );
            }
        }};
    }
}
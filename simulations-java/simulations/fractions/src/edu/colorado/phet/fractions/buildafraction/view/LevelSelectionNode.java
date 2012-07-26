// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberCardNode;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberDragContext;
import edu.colorado.phet.fractions.common.view.SettingsOnOffPanel;
import edu.colorado.phet.fractions.common.view.SettingsOnOffPanel.Element;
import edu.colorado.phet.fractions.fractionmatcher.model.Pattern;
import edu.colorado.phet.fractions.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractions.fractionsintro.common.view.Colors;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.games.GameConstants.SOUND_ICON;
import static edu.colorado.phet.common.games.GameConstants.SOUND_OFF_ICON;
import static edu.colorado.phet.fractions.buildafraction.view.LevelType.NUMBERS;
import static edu.colorado.phet.fractions.buildafraction.view.LevelType.SHAPES;
import static edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern.sequentialFill;
import static edu.colorado.phet.fractions.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractions.fractionsintro.common.view.AbstractFractionsCanvas.STAGE_SIZE;
import static fj.data.List.list;

/**
 * @author Sam Reid
 */
public class LevelSelectionNode extends AbstractLevelSelectionNode {
    public static Color[] colors = new Color[] { Colors.LIGHT_RED, Colors.LIGHT_BLUE, Colors.LIGHT_GREEN, Colors.LIGHT_ORANGE, Color.magenta, Color.yellow };

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
        return shapeLevel( level, Pattern.polygon( 80, level ), gameProgress );
    }

    private static LevelInfo shapeLevel( final int level, Pattern pattern, final F<LevelIdentifier, LevelProgress> gameProgress ) {
        final LevelProgress f = gameProgress.f( new LevelIdentifier( level - 1, SHAPES ) );
        return new LevelInfo( new LevelIdentifier( level - 1, SHAPES ), "Level " + level, new PatternNode( sequentialFill( pattern, level ), colors[( level - 1 ) % colors.length] ), f );
    }

    private static LevelInfo numberLevel( int level, final F<LevelIdentifier, LevelProgress> gameProgress ) {
        final LevelProgress f = gameProgress.f( new LevelIdentifier( level - 1, NUMBERS ) );
        return new LevelInfo( new LevelIdentifier( level - 1, NUMBERS ), "Level " + level, createLevelIcon( level ), f );
    }

    private static PNode createLevelIcon( final int level ) {
        return new PNode() {{
            for ( int i = 0; i < level; i++ ) {
                NumberCardNode card = new NumberCardNode( new Dimension2DDouble( level < 10 ? 60 : 70, 75 ), level, new NumberDragContext() {
                    public void endDrag( final NumberCardNode draggableNumberNode, final PInputEvent event ) {
                    }
                } );
                addChild( card );
                card.setOffset( i * 4, i * 4 );
            }
        }};
    }
}
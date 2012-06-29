// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.data.List;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractions.fractionmatcher.model.Pattern;
import edu.colorado.phet.fractions.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractions.fractionsintro.common.view.Colors;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern.sequentialFill;

/**
 * @author Sam Reid
 */
public class DefaultLevelSelectionScreen extends AbstractLevelSelectionNode {
    public DefaultLevelSelectionScreen( final String title, final BuildAFractionCanvas canvas2 ) {
        super( title, createInfo(), canvas2 );
    }

    public static Color[] colors = new Color[] { Colors.LIGHT_RED, Colors.LIGHT_BLUE, Colors.LIGHT_GREEN, Colors.LIGHT_ORANGE, Color.magenta, Color.yellow };

    private static List<List<LevelInfo>> createInfo() {
        return List.list( List.list( toShapeLevelInfo( 1, Pattern.pie( 1 ) ),
                                     toShapeLevelInfo( 2, Pattern.verticalBars( 2 ) ),
                                     toShapeLevelInfo( 3 ),
                                     toShapeLevelInfo( 4 ),
                                     toShapeLevelInfo( 5 ) ),
                          List.list( createNumberLevel( 1 ),
                                     createNumberLevel( 2 ),
                                     createNumberLevel( 3 ),
                                     createNumberLevel( 4 ),
                                     createNumberLevel( 5 ) ) );
    }

    private static LevelInfo toShapeLevelInfo( final int level ) {
        return toShapeLevelInfo( level, Pattern.polygon( 80, level ) );
    }

    private static LevelInfo toShapeLevelInfo( final int level, Pattern pattern ) {
        final int levelIndex = level - 1;
        return new LevelInfo( "Level " + level, new PatternNode( sequentialFill( pattern, level ), colors[levelIndex % colors.length] ), 0, 3, levelIndex, LevelType.SHAPES );
    }

    private static LevelInfo createNumberLevel( int level ) {return new LevelInfo( "Level " + level, createLevelIcon( level ), 0, 3, level - 1, LevelType.NUMBERS );}

    private static PNode createLevelIcon( final int level ) {
        return new PhetPText( "" + level );
    }
}
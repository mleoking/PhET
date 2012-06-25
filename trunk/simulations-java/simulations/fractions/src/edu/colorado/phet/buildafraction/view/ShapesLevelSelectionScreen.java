package edu.colorado.phet.buildafraction.view;

import fj.data.List;

import java.awt.Color;

import edu.colorado.phet.fractionmatcher.model.Pattern;
import edu.colorado.phet.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractionsintro.common.view.Colors;

import static edu.colorado.phet.fractionmatcher.view.FilledPattern.sequentialFill;

/**
 * @author Sam Reid
 */
public class ShapesLevelSelectionScreen extends AbstractLevelSelectionNode {
    public ShapesLevelSelectionScreen( final String title, final BuildAFractionCanvas canvas2 ) {
        super( title, createInfo(), canvas2 );
    }

    public static Color[] colors = new Color[] { Colors.LIGHT_RED, Colors.LIGHT_BLUE, Colors.LIGHT_GREEN, Colors.LIGHT_ORANGE, Color.magenta, Color.yellow };

    private static List<List<LevelInfo>> createInfo() {
        return List.list( List.list( toLevelInfo( 1, Pattern.pie( 1 ) ),
                                     toLevelInfo( 2, Pattern.verticalBars( 2 ) ),
                                     toLevelInfo( 3 ),
                                     toLevelInfo( 4 ),
                                     toLevelInfo( 5 ) ),
                          List.list( toLevelInfo( 6 ),
                                     toLevelInfo( 7 ),
                                     toLevelInfo( 8 ),
                                     toLevelInfo( 9 ),
                                     toLevelInfo( 10 ) ) );
    }

    private static LevelInfo toLevelInfo( final int level ) {
        return toLevelInfo( level, Pattern.polygon( 80, level ) );
    }

    private static LevelInfo toLevelInfo( final int level, Pattern pattern ) {
        final int levelIndex = level - 1;
        return new LevelInfo( "Level " + level, new PatternNode( sequentialFill( pattern, level ), colors[levelIndex % colors.length] ), 0, 3, levelIndex );
    }
}
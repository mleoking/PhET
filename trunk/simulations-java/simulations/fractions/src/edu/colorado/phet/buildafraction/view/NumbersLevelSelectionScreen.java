package edu.colorado.phet.buildafraction.view;

import fj.data.List;

import edu.colorado.phet.fractionmatcher.model.Pattern;
import edu.colorado.phet.fractionmatcher.view.FilledPattern;
import edu.colorado.phet.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractionsintro.common.view.Colors;

/**
 * @author Sam Reid
 */
public class NumbersLevelSelectionScreen extends AbstractLevelSelectionNode {
    public NumbersLevelSelectionScreen( final String title, final BuildAFractionCanvas canvas2 ) {
        super( title, createInfo(), canvas2 );
    }

    private static List<List<LevelInfo>> createInfo() {
        return List.list( List.list( new LevelInfo( "Level 1", new PatternNode( FilledPattern.sequentialFill( Pattern.pie( 1 ), 1 ), Colors.LIGHT_RED ), 3, 3, 0 ) ) );
    }
}
package edu.colorado.phet.buildafraction.view;

import fj.F;
import fj.data.List;

import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;

/**
 * @author Sam Reid
 */
public class NumbersLevelSelectionScreen extends AbstractLevelSelectionNode {
    public NumbersLevelSelectionScreen( final String title, final BuildAFractionCanvas canvas ) {
        super( title, createInfo(), canvas );
    }

    private static List<List<LevelInfo>> createInfo() {
        return List.list( List.range( 1, 6 ).map( new F<Integer, LevelInfo>() {
            @Override public LevelInfo f( final Integer integer ) {
                return createLevel( integer );
            }
        } ),
                          List.range( 6, 11 ).map( new F<Integer, LevelInfo>() {
                              @Override public LevelInfo f( final Integer integer ) {
                                  return createLevel( integer );
                              }
                          } ) );
    }

    private static LevelInfo createLevel( int level ) {return new LevelInfo( "Level " + level, new FractionNode( new Fraction( level, level ), 0.35 ), 0, 3, level - 1 );}
}
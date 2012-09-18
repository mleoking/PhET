// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import fj.F;

import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevelList;
import edu.colorado.phet.fractions.buildafraction.view.LevelIdentifier;
import edu.colorado.phet.fractions.buildafraction.view.LevelProgress;
import edu.colorado.phet.fractions.buildafraction.view.LevelType;

/**
 * Model for the Build a Fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {

    public final ConstantDtClock clock = new ConstantDtClock();
    public final BooleanProperty audioEnabled = new BooleanProperty( true );
    public final IntegerProperty selectedPage = new IntegerProperty( 0 ); //REVIEW what is a "page"?

    private final IntegerProperty numberLevel = new IntegerProperty( 0 );
    private final HashMap<Integer, NumberLevel> numberLevels = new HashMap<Integer, NumberLevel>();

    private final IntegerProperty shapeLevel = new IntegerProperty( 0 ); //REVIEW what is a "shape level"?
    private final HashMap<Integer, ShapeLevel> shapeLevels = new HashMap<Integer, ShapeLevel>();

    public final F<LevelIdentifier, LevelProgress> gameProgress = new F<LevelIdentifier, LevelProgress>() {
        @Override public LevelProgress f( final LevelIdentifier id ) {
            if ( id.levelType == LevelType.SHAPES && shapeLevels.containsKey( id.getLevelIndex() ) ) {
                return new LevelProgress( getLevel( id ).filledTargets.get(), getLevel( id ).numTargets );
            }
            else if ( id.levelType == LevelType.NUMBERS && numberLevels.containsKey( id.getLevelIndex() ) ) {
                return new LevelProgress( getLevel( id ).filledTargets.get(), getLevel( id ).numTargets );
            }
            else {
                //REVIEW 3 and 4 are apparently the max stars for various levels. Make them static constants, doc why some levels have 3 while others have 4.
                return new LevelProgress( 0, id.getLevelIndex() <= 4 ? 3 : 4 );
            }
        }
    };
    private final NumberLevelFactory numberLevelFactory;
    private final ShapeLevelFactory shapeLevelFactory;

    //After the user creates their first correct match, all of the collection boxes fade into view
    public final BooleanProperty collectedMatch;

    //Index of the last level, used to decide whether to show the "next" button or not.
    private static int MAX_LEVEL_INDEX = 9;

    public BuildAFractionModel( BooleanProperty collectedMatch ) {
        this( collectedMatch, new ShapeLevelList(), new NumberLevelList() );
    }

    //REVIEW either document constructor params, or document corresponding fields
    public BuildAFractionModel( BooleanProperty collectedMatch, ShapeLevelFactory shapeLevelFactory, NumberLevelFactory numberLevelFactory ) {
        this.collectedMatch = collectedMatch;
        this.numberLevelFactory = numberLevelFactory;
        this.shapeLevelFactory = shapeLevelFactory;
    }

    private Level getLevel( final LevelIdentifier levelIdentifier ) {
        return levelIdentifier.levelType == LevelType.SHAPES ? getShapeLevel( levelIdentifier.levelIndex ) : getNumberLevel( levelIdentifier.levelIndex );
    }

    public NumberLevel getNumberLevel( final Integer level ) {
        if ( !numberLevels.containsKey( level ) ) {
            final NumberLevel newLevel = numberLevelFactory.createLevel( level );
            newLevel.addMatchListener( collectedMatch );
            numberLevels.put( level, newLevel );
        }
        return numberLevels.get( level );
    }

    public ShapeLevel getShapeLevel( final Integer level ) {
        if ( !shapeLevels.containsKey( level ) ) {
            final ShapeLevel newLevel = shapeLevelFactory.createLevel( level );
            newLevel.addMatchListener( collectedMatch );
            shapeLevels.put( level, newLevel );
        }
        return shapeLevels.get( level );
    }

    public void resampleNumberLevel( final int levelIndex ) {
        numberLevels.remove( levelIndex ).dispose();
        final NumberLevel level = numberLevelFactory.createLevel( levelIndex );
        level.addMatchListener( collectedMatch );
        numberLevels.put( levelIndex, level );
    }

    public void resampleShapeLevel( final int levelIndex ) {
        shapeLevels.remove( levelIndex ).dispose();
        final ShapeLevel level = shapeLevelFactory.createLevel( levelIndex );
        level.addMatchListener( collectedMatch );
        shapeLevels.put( levelIndex, level );
    }

    public void resetAll() {
        selectedPage.reset();
        clock.resetSimulationTime();

        numberLevel.reset();
        for ( NumberLevel level : numberLevels.values() ) {
            level.resetAll();
        }
        numberLevels.clear();

        shapeLevel.reset();
        for ( ShapeLevel level : shapeLevels.values() ) {
            level.resetAll();
        }
        shapeLevels.clear();
        audioEnabled.reset();

        //Resets it for both of the collection tabs (tabs 1-2)
        collectedMatch.reset();
    }

    public boolean isLastLevel( final int levelIndex ) { return levelIndex == MAX_LEVEL_INDEX; }

    public boolean isMixedNumbers() { return false; }
}
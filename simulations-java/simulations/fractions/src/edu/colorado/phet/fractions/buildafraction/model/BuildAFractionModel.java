// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import fj.F;
import fj.Unit;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevelList;
import edu.colorado.phet.fractions.buildafraction.view.LevelIdentifier;
import edu.colorado.phet.fractions.buildafraction.view.LevelProgress;
import edu.colorado.phet.fractions.buildafraction.view.LevelType;

import static fj.Unit.unit;

/**
 * Model for the Build a Fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {

    public final ConstantDtClock clock = new ConstantDtClock();
    private final Property<Scene> selectedScene = new Property<Scene>( Scene.SHAPES );
    public final BooleanProperty audioEnabled = new BooleanProperty( true );
    public final IntegerProperty selectedPage = new IntegerProperty( 0 );

    private final IntegerProperty numberLevel = new IntegerProperty( 0 );
    private final ArrayList<NumberLevel> numberLevels;

    private final IntegerProperty shapeLevel = new IntegerProperty( 0 );
    private final ArrayList<ShapeLevel> shapeLevels;

    public final F<LevelIdentifier, LevelProgress> gameProgress = new F<LevelIdentifier, LevelProgress>() {
        @Override public LevelProgress f( final LevelIdentifier i ) {
            final Level level = getLevel( i );
            return new LevelProgress( level.filledTargets.get(), level.numTargets );
        }
    };
    private final F<Unit, ArrayList<NumberLevel>> numberLevelFactory;
    private final F<Unit, ArrayList<ShapeLevel>> shapeLevelFactory;

    public BuildAFractionModel() {
        this( new F<Unit, ArrayList<ShapeLevel>>() {
                  @Override public ArrayList<ShapeLevel> f( final Unit unit ) {
                      return new ShapeLevelList();
                  }
              }, new F<Unit, ArrayList<NumberLevel>>() {
            @Override public ArrayList<NumberLevel> f( final Unit unit ) {
                return new NumberLevelList();
            }
        }
        );
    }

    public BuildAFractionModel( F<Unit, ArrayList<ShapeLevel>> shapeLevelFactory, F<Unit, ArrayList<NumberLevel>> numberLevelFactory ) {
        numberLevels = numberLevelFactory.f( unit() );
        shapeLevels = shapeLevelFactory.f( unit() );
        checkLevelSizes();

        this.numberLevelFactory = numberLevelFactory;
        this.shapeLevelFactory = shapeLevelFactory;
    }

    private void checkLevelSizes() {
        assert numberLevels.size() == shapeLevels.size();
    }

    private Level getLevel( final LevelIdentifier levelIdentifier ) {
        return levelIdentifier.levelType == LevelType.SHAPES ? getShapeLevel( levelIdentifier.levelIndex ) : getNumberLevel( levelIdentifier.levelIndex );
    }

    public NumberLevel getNumberLevel( final int level ) {
        return numberLevels.get( level );
    }

    public ShapeLevel getShapeLevel( final int level ) {
        return shapeLevels.get( level );
    }

    public void resampleNumberLevel( final int levelIndex ) {
        numberLevels.remove( levelIndex ).dispose();
        numberLevels.add( levelIndex, numberLevelFactory.f( unit() ).get( levelIndex ) );
    }

    public void resampleShapeLevel( final int levelIndex ) {
        shapeLevels.remove( levelIndex ).dispose();
        shapeLevels.add( levelIndex, shapeLevelFactory.f( unit() ).get( levelIndex ) );
    }

    public void resetAll() {
        selectedScene.reset();
        selectedPage.reset();
        clock.resetSimulationTime();

        numberLevel.reset();
        for ( NumberLevel level : numberLevels ) {
            level.resetAll();
        }
        numberLevels.clear();
        numberLevels.addAll( numberLevelFactory.f( unit() ) );

        shapeLevel.reset();
        for ( ShapeLevel level : shapeLevels ) {
            level.resetAll();
        }
        shapeLevels.clear();
        shapeLevels.addAll( shapeLevelFactory.f( unit() ) );

        audioEnabled.reset();
    }

    public boolean isLastLevel( final int levelIndex ) {
        checkLevelSizes();
        return levelIndex == numberLevels.size() - 1;
    }
}
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
        @Override public LevelProgress f( final LevelIdentifier id ) {
            final Level level = getLevel( id );
            return new LevelProgress( level.filledTargets.get(), level.numTargets );
        }
    };
    private final F<Unit, ArrayList<NumberLevel>> numberLevelFactory;
    private final F<Unit, ArrayList<ShapeLevel>> shapeLevelFactory;

    //After the user creates their first correct match, all of the collection boxes fade into view
    public final BooleanProperty userCreatedMatch = new BooleanProperty( false );

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
        this.numberLevelFactory = numberLevelFactory;
        this.shapeLevelFactory = shapeLevelFactory;

        numberLevels = numberLevelFactory.f( unit() );
        for ( NumberLevel level : numberLevels ) {
            level.addMatchListener( userCreatedMatch );
        }
        shapeLevels = shapeLevelFactory.f( unit() );
        for ( ShapeLevel level : shapeLevels ) {
            level.addMatchListener( userCreatedMatch );
        }
        checkLevelSizes();
    }

    private void checkLevelSizes() { assert numberLevels.size() == shapeLevels.size(); }

    private Level getLevel( final LevelIdentifier levelIdentifier ) {
        return levelIdentifier.levelType == LevelType.SHAPES ? getShapeLevel( levelIdentifier.levelIndex ) : getNumberLevel( levelIdentifier.levelIndex );
    }

    public NumberLevel getNumberLevel( final int level ) { return numberLevels.get( level ); }

    public ShapeLevel getShapeLevel( final int level ) { return shapeLevels.get( level ); }

    public void resampleNumberLevel( final int levelIndex ) {
        numberLevels.remove( levelIndex ).dispose();
        final NumberLevel level = numberLevelFactory.f( unit() ).get( levelIndex );
        level.addMatchListener( userCreatedMatch );
        numberLevels.add( levelIndex, level );
    }

    public void resampleShapeLevel( final int levelIndex ) {
        shapeLevels.remove( levelIndex ).dispose();
        final ShapeLevel level = shapeLevelFactory.f( unit() ).get( levelIndex );
        level.addMatchListener( userCreatedMatch );
        shapeLevels.add( levelIndex, level );
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
        final ArrayList<NumberLevel> numberLevelList = numberLevelFactory.f( unit() );
        for ( NumberLevel level : numberLevelList ) {
            level.addMatchListener( userCreatedMatch );
        }
        numberLevels.addAll( numberLevelList );

        shapeLevel.reset();
        for ( ShapeLevel level : shapeLevels ) {
            level.resetAll();
        }
        shapeLevels.clear();
        final ArrayList<ShapeLevel> shapeLevelList = shapeLevelFactory.f( unit() );
        for ( ShapeLevel level : shapeLevelList ) {
            level.addMatchListener( userCreatedMatch );
        }
        shapeLevels.addAll( shapeLevelList );

        audioEnabled.reset();
        userCreatedMatch.reset();

//        level.createdFractions.addObserver( new VoidFunction1<List<Fraction>>() {
//                        public void apply( final List<Fraction> fractions ) {
//                            if ( fractions.length() > 0 && fractions.filter( new F<Fraction, Boolean>() {
//                                @Override public Boolean f( final Fraction fraction ) {
//                                    return fraction.approxEquals( mixedFraction.toFraction() );
//                                }
//                            } ).length() > 0 ) {
//
//                            }
//                        }
//                    } );
    }

    public boolean isLastLevel( final int levelIndex ) {
        checkLevelSizes();
        return levelIndex == numberLevels.size() - 1;
    }

    public boolean isMixedNumbers() { return false; }
}
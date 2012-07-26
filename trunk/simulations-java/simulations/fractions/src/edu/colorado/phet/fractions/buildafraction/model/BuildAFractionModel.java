// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import fj.F;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList;
import edu.colorado.phet.fractions.buildafraction.model.pictures.PictureLevel;
import edu.colorado.phet.fractions.buildafraction.model.pictures.PictureLevelList;
import edu.colorado.phet.fractions.buildafraction.view.LevelIdentifier;
import edu.colorado.phet.fractions.buildafraction.view.LevelProgress;
import edu.colorado.phet.fractions.buildafraction.view.LevelType;

/**
 * Model for the Build a Fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {

    public final IntegerProperty numberLevel = new IntegerProperty( 0 );
    public final ArrayList<NumberLevel> numberLevels = new NumberLevelList();

    public final IntegerProperty pictureLevel = new IntegerProperty( 0 );
    public final ArrayList<PictureLevel> pictureLevels = new PictureLevelList();

    public final ConstantDtClock clock = new ConstantDtClock();
    public final Property<Scene> selectedScene = new Property<Scene>( Scene.pictures );

    public final BooleanProperty audioEnabled = new BooleanProperty( true );
    public final IntegerProperty selectedPage = new IntegerProperty( 0 );
    public F<LevelIdentifier, LevelProgress> gameProgress = new F<LevelIdentifier, LevelProgress>() {
        @Override public LevelProgress f( final LevelIdentifier i ) {
            final Level level = getLevel( i );
            return new LevelProgress( level.filledTargets.get(), level.numTargets );
        }
    };

    private Level getLevel( final LevelIdentifier levelIdentifier ) {return levelIdentifier.levelType == LevelType.SHAPES ? getPictureLevel( levelIdentifier.levelIndex ) : getNumberLevel( levelIdentifier.levelIndex );}

    public void resetAll() {
        selectedScene.reset();
        clock.resetSimulationTime();

        numberLevel.reset();
        for ( NumberLevel level : numberLevels ) {
            level.resetAll();
        }
        numberLevels.clear();
        numberLevels.addAll( new NumberLevelList() );

        pictureLevel.reset();
        for ( PictureLevel level : pictureLevels ) {
            level.resetAll();
        }
        pictureLevels.clear();
        pictureLevels.addAll( new PictureLevelList() );

        audioEnabled.reset();
    }

    public NumberLevel getNumberLevel( final int level ) { return numberLevels.get( level ); }

    public PictureLevel getPictureLevel( final int level ) { return pictureLevels.get( level ); }

    public void resample() {
        int n = numberLevel.get();
        int m = pictureLevel.get();
        Scene scene = selectedScene.get();
        resetAll();
        selectedScene.set( scene );
        numberLevel.set( n );
        pictureLevel.set( m );
    }
}
package edu.colorado.phet.buildafraction.model;

import fj.Equal;
import fj.data.List;

import java.util.ArrayList;

import edu.colorado.phet.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.buildafraction.model.numbers.NumberLevelList;
import edu.colorado.phet.buildafraction.model.pictures.PictureLevel;
import edu.colorado.phet.buildafraction.model.pictures.PictureLevelList;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;

/**
 * Model for the Build a Fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {

    public final IntegerProperty numberLevel = new IntegerProperty( 0 );
    public final ArrayList<NumberLevel> numberLevels = new NumberLevelList();
    public final IntegerProperty numberScore = new IntegerProperty( 0 );

    public final IntegerProperty pictureLevel = new IntegerProperty( 0 );
    public final ArrayList<PictureLevel> pictureLevels = new PictureLevelList();

    public final ConstantDtClock clock = new ConstantDtClock();
    public final Property<Scene> selectedScene = new Property<Scene>( Scene.pictures );

    public BuildAFractionModel( final boolean standaloneApp ) {
    }

    public void removeCreatedValueFromNumberLevel( final Fraction value ) {
        final Property<List<Fraction>> fractions = numberLevels.get( numberLevel.get() ).createdFractions;
        fractions.set( fractions.get().delete( value, Equal.<Fraction>anyEqual() ) );
    }

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
    }

    public void addCreatedValue( final Fraction value ) {
        final Property<List<Fraction>> fractions = numberLevels.get( numberLevel.get() ).createdFractions;
        fractions.set( fractions.get().snoc( value ) );
    }

    public Property<List<Fraction>> getNumberCreatedFractions( final int level ) { return numberLevels.get( level ).createdFractions; }

    public Property<List<Fraction>> getPictureCreatedFractions( final int level ) { return pictureLevels.get( level ).createdFractions; }

    public NumberLevel getNumberLevel( final int level ) { return numberLevels.get( level ); }

    public PictureLevel getPictureLevel( final int level ) { return pictureLevels.get( level ); }

    public void goToNumberLevel( final int level ) { numberLevel.set( level ); }

    public void resample() {
        int n = numberLevel.get();
        int m = pictureLevel.get();
        Scene scene = selectedScene.get();
        resetAll();
        selectedScene.set( scene );
        numberLevel.set( n );
        pictureLevel.set( m );
    }

    public void goToPictureLevel( final int level ) { pictureLevel.set( level ); }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab.model;

import fj.F;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.Times;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.util.immutable.Dimension2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.equalitylab.view.EqualityLabCanvas;
import edu.colorado.phet.fractionsintro.intro.model.FactorySet;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.pieset.CakeSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.CircularSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Site;
import edu.colorado.phet.fractionsintro.intro.model.pieset.SliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.StackedHorizontalSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.VerticalSliceFactory;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

/**
 * Model for the Equality Lab tab.  Reuses lots of code from the Intro tab for displaying and allowing interaction with the representations.
 *
 * @author Sam Reid
 */
public class EqualityLabModel {

    //For filling the circular pies in the equality lab, for the primary (left side) fraction, the bottom-right pie fills first,
    //and the design doc shows the pies filling up to the left before going to the upper row.  What about filling up next instead of to the left?
    //It would keep the left and right representations closer together longer and matches with the "more is up" sense in the game tab.
    public final FactorySet factorySet = PrimaryFactorySet();
    public final BooleanProperty locked = new BooleanProperty( true );
    public static double pieY = 225 - 12.5;
    public static double pieDiameter = 135;
    public static double distanceBetweenBars = 5;
    public static final double horizontalSliceY = 25 - 41.5 - 15;

    private static FactorySet PrimaryFactorySet() {
        final Vector2D bucketPosition = new Vector2D( 100, -SliceFactory.stageSize.height + 200 );
        int numPerRow = 3;

        double pieX = 70 - 36 - 146;

        final F<Site, Site> siteMap = new F<Site, Site>() {
            @Override public Site f( final Site s ) {
                return s.eq( 0, 0 ) ? new Site( 1, 2 ) :
                       s.eq( 0, 1 ) ? new Site( 0, 2 ) :
                       s.eq( 0, 2 ) ? new Site( 1, 1 ) :
                       s.eq( 1, 0 ) ? new Site( 0, 1 ) :
                       s.eq( 1, 1 ) ? new Site( 1, 0 ) :
                       s.eq( 1, 2 ) ? new Site( 0, 0 ) :
                       null;
            }
        };
        final Color sliceColor = AbstractFractionsCanvas.LIGHT_GREEN;

        //Use little buckets for everything so it will fit, but not for vertical bars, which are too wide for the little bucket
        Dimension2D littleBucket = new Dimension2D( 250, 100 );
        Dimension2D bigBucket = new Dimension2D( 350, 100 );
        return new FactorySet( new CircularSliceFactory( numPerRow, bucketPosition, littleBucket, pieDiameter, pieX, pieY, siteMap, sliceColor ),
                               new StackedHorizontalSliceFactory( bucketPosition.plus( -20, 0 ), bigBucket, sliceColor, 125, horizontalSliceY, false ),
                               new VerticalSliceFactory( 0, 125, 225, false, bucketPosition, littleBucket, sliceColor, distanceBetweenBars ),

                               //Align the right side of the water glasses with the right edge of the representation control panel
                               new VerticalSliceFactory( -117, 100, 200, true, bucketPosition, littleBucket, sliceColor, distanceBetweenBars ),
                               new CakeSliceFactory( new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 ), littleBucket ) );
    }

    public static FactorySet scaledFactorySet = ScaledFactorySet();

    private static FactorySet ScaledFactorySet() {
        final Vector2D bucketPosition = new Vector2D( 100, -SliceFactory.stageSize.height + 200 );
        int numPerRow = 3;
        double pieX = 85 + 475 - 146;

        final F<Site, Site> siteMap = new F<Site, Site>() {
            @Override public Site f( final Site s ) {
                return s.eq( 0, 0 ) ? new Site( 1, 0 ) :
                       s.eq( 0, 1 ) ? new Site( 0, 0 ) :
                       s.eq( 0, 2 ) ? new Site( 1, 1 ) :
                       s.eq( 1, 0 ) ? new Site( 0, 1 ) :
                       s.eq( 1, 1 ) ? new Site( 1, 2 ) :
                       s.eq( 1, 2 ) ? new Site( 0, 2 ) :
                       null;
            }
        };
        final Color sliceColor = EqualityLabCanvas.LIGHT_BLUE;

        //Use little buckets for everything so it will fit, but not for vertical bars, which are too wide for the little bucket
        //Bucket positions irrelevant for scaled factory, though (since not shown)
        Dimension2D littleBucket = new Dimension2D( 250, 100 );
        Dimension2D bigBucket = new Dimension2D( 350, 100 );

        return new FactorySet( new CircularSliceFactory( numPerRow, bucketPosition, littleBucket, pieDiameter, pieX, pieY, siteMap, sliceColor ),
                               new StackedHorizontalSliceFactory( bucketPosition.plus( -20, 0 ), bigBucket, sliceColor, 125 + 445, horizontalSliceY, true ),
                               new VerticalSliceFactory( 0, 125, 225, false, bucketPosition, littleBucket, sliceColor, distanceBetweenBars ),

                               //Align the left side of the water glasses with the left edge of the right representation control panel
                               new VerticalSliceFactory( 400 - 27, 100, 200, true, bucketPosition, littleBucket, sliceColor, distanceBetweenBars ),
                               new CakeSliceFactory( new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 ), littleBucket ) );
    }

    //The max number of filled containers in this tab is 4.
    private final FractionsIntroModel model = new FractionsIntroModel( IntroState.IntroState( 4, factorySet ), factorySet );
    public final SettableProperty<PieSet> pieSet = model.pieSet;
    public final SettableProperty<PieSet> horizontalBarSet = model.horizontalBarSet;
    public final Clock clock = model.clock;
    public final IntegerProperty numerator = model.numerator;
    public final IntegerProperty denominator = model.denominator;
    public final IntegerProperty scale = new IntegerProperty( 2 );
    public final Times scaledNumerator = model.numerator.times( scale );
    public final Times scaledDenominator = model.denominator.times( scale );
    public final IntegerProperty maximum = model.maximum;
    public final SettableProperty<PieSet> waterGlassSet = model.waterGlassSet;
    public final SettableProperty<Representation> leftRepresentation = model.representation;
    public final Property<Representation> rightRepresentation = new Property<Representation>( leftRepresentation.get() ) {{
        addObserver( new VoidFunction1<Representation>() {
            @Override public void apply( final Representation representation ) {
                if ( locked.get() ) {
                    leftRepresentation.set( representation );
                }
            }
        } );
        leftRepresentation.addObserver( new VoidFunction1<Representation>() {
            @Override public void apply( final Representation representation ) {
                if ( locked.get() ) {
                    set( representation );
                }
            }
        } );
        locked.addObserver( new VoidFunction1<Boolean>() {
            @Override public void apply( final Boolean locked ) {
                if ( locked ) {
                    set( leftRepresentation.get() );
                }
            }
        } );
    }};
    public final SettableProperty<PieSet> rightPieSet = new Property<PieSet>( pieSet.get() ) {{
        final SimpleObserver observer = new SimpleObserver() {
            @Override public void update() {
                set( scaledFactorySet.circularSliceFactory.fromContainerSetState( pieSet.get().toLazyContainerSet().scale( scale.get() ) ).createScaledCopy() );
            }
        };
        pieSet.addObserver( observer );
        scale.addObserver( observer );
    }};
    public final SettableProperty<PieSet> rightHorizontalBars = new Property<PieSet>( horizontalBarSet.get() ) {{
        final SimpleObserver observer = new SimpleObserver() {
            @Override public void update() {
                set( scaledFactorySet.horizontalSliceFactory.fromContainerSetState( horizontalBarSet.get().toLazyContainerSet().scale( scale.get() ) ).createScaledCopy() );
            }
        };
        horizontalBarSet.addObserver( observer );
        scale.addObserver( observer );
    }};
    public final SettableProperty<PieSet> rightWaterGlasses = new Property<PieSet>( waterGlassSet.get() ) {{
        final SimpleObserver observer = new SimpleObserver() {
            @Override public void update() {
                set( scaledFactorySet.waterGlassSetFactory.fromContainerSetState( waterGlassSet.get().toLazyContainerSet().scale( scale.get() ) ).createScaledCopy() );
            }
        };
        waterGlassSet.addObserver( observer );
        scale.addObserver( observer );
    }};

    public void resetAll() {
        model.resetAll();
        scale.reset();
        rightRepresentation.reset();
        locked.reset();
    }
}
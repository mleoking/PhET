// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.equalitylab.model;

import fj.F;
import fj.Unit;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.Times;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.common.util.Dimension2D;
import edu.colorado.phet.fractions.common.view.Colors;
import edu.colorado.phet.fractions.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractions.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.Site;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.CakeSliceFactory;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.CircularSliceFactory;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.FactorySet;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.SliceFactory;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.StackedHorizontalSliceFactory;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.VerticalSliceFactory;
import edu.colorado.phet.fractions.fractionsintro.intro.view.Representation;

import static edu.colorado.phet.fractions.common.view.Colors.LIGHT_BLUE;

/**
 * Model for the Equality Lab tab.  Reuses lots of code from the Intro tab by composition for displaying and allowing interaction with the representations.
 *
 * @author Sam Reid
 */
public class EqualityLabModel {

    //REVIEW: Doc - What, in general, are these constants for?
    //REVIEW: These don't seem to use the normal all caps naming convention for constants.  Any reason for this?
    private static final double pieY = 212.5;
    private static final double pieDiameter = 135;
    private static final double distanceBetweenGlasses = 5;
    private static final double distanceBetweenBars = 12;
    private static final double horizontalSliceY = -1.5;

    private static final double verticalSliceScale = 0.75;

    //REVIEW: In understand the following comment, but I don't get why it seems
    //to be associated with primaryFactorySet.  Please clarify or move.
    //For filling the circular pies in the equality lab, for the primary (left side) fraction, the bottom-right pie fills first, then it goes up
    //so that the left and right representations match up at the center of the screen for as long as possible (until the value increases too high)
    public final FactorySet primaryFactorySet = new F<Unit, FactorySet>() { //REVIEW: Can this be static?  scaledFactorySet is.
        @Override public FactorySet f( final Unit unit ) {
            final Vector2D bucketPosition = new Vector2D( 100, -SliceFactory.stageSize.height + 200 );
            int numPerRow = 3; //REVIEW: Can this be a constant?  The same value is used in the other factory set.

            double pieX = -112;

            final F<Site, Site> siteMap = new SiteMap();

            //REVIEW: Is the following comment a cut-and-paste error?  I only see one size of bucket here.
            //Use little buckets for everything so it will fit, but not for vertical bars, which are too wide for the little bucket
            Dimension2D littleBucket = new Dimension2D( 250, 100 );
            return new FactorySet( new CircularSliceFactory( numPerRow, bucketPosition, littleBucket, pieDiameter, pieX, pieY, siteMap, Colors.CIRCLE_COLOR ),
                                   new StackedHorizontalSliceFactory( bucketPosition.plus( -20, 0 ), littleBucket, Colors.HORIZONTAL_SLICE_COLOR, 125 + 114, horizontalSliceY, false ),
                                   new VerticalSliceFactory( -120, 125 * verticalSliceScale, 225 * verticalSliceScale, false, bucketPosition, littleBucket, Colors.VERTICAL_SLICE_COLOR, distanceBetweenBars, false ),

                                   //Align the right side of the water glasses with the right edge of the representation control panel
                                   new VerticalSliceFactory( -117, 100, 200, true, bucketPosition, littleBucket, Colors.CUP_COLOR, distanceBetweenGlasses, true ),
                                   new CakeSliceFactory( new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 ), littleBucket ) );

        }
    }.f( Unit.unit() );

    //REVIEW: The naming of this factory set is confusing me.  I don't see much
    //scaling going on.  Is this the factory set for the pie sets shown on the
    //right?  Consider better doc or renaming.
    public static final FactorySet scaledFactorySet = new F<Unit, FactorySet>() {
        @Override public FactorySet f( final Unit unit ) {
            final Vector2D bucketPosition = new Vector2D( 100, -SliceFactory.stageSize.height + 200 );
            int numPerRow = 3;
            double pieX = 85 + 475 - 146; //REVIEW: doc - explain why there are several numbers in an equation and not just one.

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

            //REVIEW: If I understand this correctly, the scaledFactorySet
            //is used for the pie sets shown on the right side.  There is no
            //bucket shown on the right side when I run the sim, so why does
            // the bucket matter?  Could there be a "null" bucket used here?
            //Use little buckets for everything so it will fit, but not for vertical bars, which are too wide for the little bucket
            //Bucket positions irrelevant for scaled factory, though (since not shown)
            Dimension2D littleBucket = new Dimension2D( 250, 100 );
            Dimension2D bigBucket = new Dimension2D( 350, 100 );

            return new FactorySet( new CircularSliceFactory( numPerRow, bucketPosition, littleBucket, pieDiameter, pieX, pieY, siteMap, LIGHT_BLUE ),
                                   new StackedHorizontalSliceFactory( bucketPosition.plus( -20, 0 ), bigBucket, LIGHT_BLUE, 125 + 445, horizontalSliceY, true ),
                                   new VerticalSliceFactory( 400, 125 * verticalSliceScale, 225 * verticalSliceScale, false, bucketPosition, littleBucket, LIGHT_BLUE, distanceBetweenBars, false ),

                                   //Align the left side of the water glasses with the left edge of the right representation control panel
                                   new VerticalSliceFactory( 400 - 27, 100, 200, true, bucketPosition, littleBucket, LIGHT_BLUE, distanceBetweenGlasses, true ),
                                   new CakeSliceFactory( new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 ), littleBucket ) );

        }
    }.f( Unit.unit() );

    //The max number of filled containers in this tab is 4.
    private final FractionsIntroModel model = new FractionsIntroModel( IntroState.newState( 4, primaryFactorySet, System.currentTimeMillis() ), primaryFactorySet );

    //REVIEW: doc - It looks like there are a bunch of things that are being
    // set up to be pass through to the contained FractionsIntroModel.  Why
    // not expose it?  My guess is either for code conciseness or because there
    // are some differences in model behavior that need to be 'insulated', but
    // it would be good to explain it.
    public final SettableProperty<PieSet> pieSet = model.pieSet;
    public final SettableProperty<PieSet> horizontalBarSet = model.horizontalBarSet;
    public final SettableProperty<PieSet> verticalBarSet = model.verticalBarSet;
    public final Clock clock = model.clock;
    public final IntegerProperty numerator = model.numerator;
    public final IntegerProperty denominator = model.denominator;
    public final IntegerProperty scale = new IntegerProperty( 2 );
    public final Times scaledNumerator = model.numerator.times( scale );
    public final Times scaledDenominator = model.denominator.times( scale );
    public final IntegerProperty maximum = model.maximum;
    public final SettableProperty<PieSet> waterGlassSet = model.waterGlassSet;
    public final SettableProperty<Representation> leftRepresentation = model.representation;

    //Flag indicating whether the right representation mirrors the left.  If not, then it is a number line.
    public final Property<Boolean> sameAsLeft = new Property<Boolean>( true );

    public final ObservableProperty<Representation> rightRepresentation = new Property<Representation>( leftRepresentation.get() ) {{
        leftRepresentation.addObserver( new VoidFunction1<Representation>() {
            public void apply( final Representation r ) {
                if ( sameAsLeft.get() ) { set( r ); }
            }
        } );
        sameAsLeft.addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean sameAsLeft ) {
                if ( sameAsLeft ) { set( leftRepresentation.get() ); }
                else { set( Representation.NUMBER_LINE );}
            }
        } );
    }};

    //Optionally scaled pies shown on the right when mirroring the left.
    public final SettableProperty<PieSet> scaledPieSet = new Property<PieSet>( pieSet.get() ) {{
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                set( scaledFactorySet.circularSliceFactory.fromContainerSetState( pieSet.get().toLazyContainerSet().scale( scale.get() ) ).createScaledCopy() );
            }
        };
        pieSet.addObserver( observer );
        scale.addObserver( observer );
    }};
    public final SettableProperty<PieSet> rightHorizontalBars = new Property<PieSet>( horizontalBarSet.get() ) {{
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                set( scaledFactorySet.horizontalSliceFactory.fromContainerSetState( horizontalBarSet.get().toLazyContainerSet().scale( scale.get() ) ).createScaledCopy() );
            }
        };
        horizontalBarSet.addObserver( observer );
        scale.addObserver( observer );
    }};
    public final SettableProperty<PieSet> rightVerticalBars = new Property<PieSet>( verticalBarSet.get() ) {{
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                set( scaledFactorySet.verticalSliceFactory.fromContainerSetState( verticalBarSet.get().toLazyContainerSet().scale( scale.get() ) ).createScaledCopy() );
            }
        };
        verticalBarSet.addObserver( observer );
        scale.addObserver( observer );
    }};
    public final SettableProperty<PieSet> rightWaterGlasses = new Property<PieSet>( waterGlassSet.get() ) {{
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                set( scaledFactorySet.waterGlassSetFactory.fromContainerSetState( waterGlassSet.get().toLazyContainerSet().scale( scale.get() ) ).createScaledCopy() );
            }
        };
        waterGlassSet.addObserver( observer );
        scale.addObserver( observer );
    }};

    public long getRandomSeed() { return model.getRandomSeed(); }

    public void resetAll() {
        model.resetAll();
        scale.reset();
        sameAsLeft.reset();
    }
}
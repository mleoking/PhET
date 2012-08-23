// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.equalitylab.model;

import fj.F;
import fj.Unit;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.Times;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.common.util.Size2D;
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

import static edu.colorado.phet.fractions.common.view.Colors.LIGHT_PINK;

/**
 * Model for the Equality Lab tab.  Reuses lots of code from the Intro tab by composition for displaying and allowing interaction with the representations.
 *
 * @author Sam Reid
 */
public class EqualityLabModel {

    //Layout, location and metrics for the different types of objects in the equality lab model
    private static final double PIE_Y = 212.5;
    private static final double PIE_DIAMETER = 135;
    private static final double DISTANCE_BETWEEN_GLASSES = 5;
    private static final double DISTANCE_BETWEEN_BARS = 12;
    private static final double HORIZONTAL_SLICE_Y = -1.5;
    private static final double VERTICAL_SLICE_SCALE = 0.75;
    private static final int NUM_PER_ROW = 3;

    //Set of factories for creating the left hand side (or primary) representations.
    private static final FactorySet primaryFactorySet = new F<Unit, FactorySet>() {
        @Override public FactorySet f( final Unit unit ) {
            final Vector2D bucketPosition = new Vector2D( 100, -SliceFactory.stageSize.height + 200 );

            double pieX = -112;

            //For filling the circular pies in the equality lab, for the primary (left side) fraction, the bottom-right pie fills first, then it goes up
            //so that the left and right representations match up at the center of the screen for as long as possible (until the value increases too high)
            final F<Site, Site> siteMap = new SiteMap();

            //Use smaller buckets than on the intro tab, because there is less room
            Size2D bucketSize = new Size2D( 250, 100 );
            return new FactorySet( new CircularSliceFactory( NUM_PER_ROW, bucketPosition, bucketSize, PIE_DIAMETER, pieX, PIE_Y, siteMap, Colors.CIRCLE_COLOR ),
                                   new StackedHorizontalSliceFactory( bucketPosition, bucketSize, Colors.HORIZONTAL_SLICE_COLOR, 125 + 114, HORIZONTAL_SLICE_Y, false ),
                                   new VerticalSliceFactory( -120, 125 * VERTICAL_SLICE_SCALE, 225 * VERTICAL_SLICE_SCALE, false, bucketPosition, bucketSize, Colors.VERTICAL_SLICE_COLOR, DISTANCE_BETWEEN_BARS, false ),

                                   //Align the right side of the water glasses with the right edge of the representation control panel
                                   new VerticalSliceFactory( -117, 100, 200, true, bucketPosition, bucketSize, Colors.CUP_COLOR, DISTANCE_BETWEEN_GLASSES, true ),
                                   new CakeSliceFactory( new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 ), bucketSize ) );

        }
    }.f( Unit.unit() );

    //Factory set for the representation on the right, which is a "scaled up" version of the one on the left.  That is, right = a/a * left, where a is some small integer.
    public static final FactorySet scaledFactorySet = new F<Unit, FactorySet>() {
        @Override public FactorySet f( final Unit unit ) {
            final Vector2D bucketPosition = new Vector2D( 100, -SliceFactory.stageSize.height + 200 );

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

            //Bucket positions irrelevant for scaled factory, though (since not shown), so just use width=height=0
            Size2D noBucket = new Size2D( 0, 0 );

            return new FactorySet( new CircularSliceFactory( NUM_PER_ROW, bucketPosition, noBucket, PIE_DIAMETER, 414, PIE_Y, siteMap, LIGHT_PINK ),
                                   new StackedHorizontalSliceFactory( bucketPosition.plus( -20, 0 ), noBucket, LIGHT_PINK, 125 + 445, HORIZONTAL_SLICE_Y, true ),
                                   new VerticalSliceFactory( 400, 125 * VERTICAL_SLICE_SCALE, 225 * VERTICAL_SLICE_SCALE, false, bucketPosition, noBucket, LIGHT_PINK, DISTANCE_BETWEEN_BARS, false ),

                                   //Align the left side of the water glasses with the left edge of the right representation control panel
                                   new VerticalSliceFactory( 400 - 27, 100, 200, true, bucketPosition, noBucket, LIGHT_PINK, DISTANCE_BETWEEN_GLASSES, true ),
                                   new CakeSliceFactory( new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 ), noBucket ) );

        }
    }.f( Unit.unit() );

    //The max number of filled containers in this tab is 4.
    private final FractionsIntroModel model = new FractionsIntroModel( IntroState.newState( 4, primaryFactorySet, System.currentTimeMillis() ), primaryFactorySet );

    //Set up convenience access for the components of the main underlying model, to factor out the "model." calls everywhere else
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

    //Get the bounds of the water glass set for use in the canvas layout
    public Rectangle2D getWaterGlassSetNodeBounds() { return primaryFactorySet.waterGlassSetFactory.createEmptyPies( 1, 1 ).head().cells.head().getShape().getBounds2D(); }
}
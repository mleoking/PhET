// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab.model;

import fj.F;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.equalitylab.view.EqualityLabCanvas;
import edu.colorado.phet.fractionsintro.intro.model.FactorySet;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.pieset.CakeSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.CircularSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.HorizontalSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Site;
import edu.colorado.phet.fractionsintro.intro.model.pieset.SliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.VerticalSliceFactory;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

/**
 * Model for the Equality Lab tab
 *
 * @author Sam Reid
 */
public class EqualityLabModel {

    //For filling the circular pies in the equality lab, for the primary (left side) fraction, the bottom-right pie fills first, and the design doc shows the pies filling up to the left before going to the upper row.  What about filling up next instead of to the left?  It would keep the left and right representations closer together longer and matches with the "more is up" sense in the game tab.
    private final FactorySet factorySet = Primary();

    public static FactorySet Primary() {
        final Vector2D bucketPosition = new Vector2D( 100, -SliceFactory.stageSize.height + 200 );
        int numPerRow = 3;
        double pieDiameter = 120;
        double pieX = 85;
        double pieY = 210;

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
        final Color sliceColor = AbstractFractionsCanvas.LightGreen;

        return new FactorySet( new CircularSliceFactory( numPerRow, bucketPosition, pieDiameter, pieX, pieY, siteMap, sliceColor ),
                               new HorizontalSliceFactory( bucketPosition, sliceColor ),
                               new VerticalSliceFactory( 125, 225, false, bucketPosition, sliceColor ),
                               new VerticalSliceFactory( 100, 200, true, bucketPosition, sliceColor ),
                               new CakeSliceFactory( new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 ) ) );
    }

    public static FactorySet scaledFactorySet = Scaled();

    public static FactorySet Scaled() {
        final Vector2D bucketPosition = new Vector2D( 100, -SliceFactory.stageSize.height + 200 );
        int numPerRow = 3;
        double pieDiameter = 120;
        double pieX = 85 + 475;
        double pieY = 210;

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
        final Color sliceColor = EqualityLabCanvas.lightBlue;

        return new FactorySet( new CircularSliceFactory( numPerRow, bucketPosition, pieDiameter, pieX, pieY, siteMap, sliceColor ),
                               new HorizontalSliceFactory( bucketPosition, sliceColor ),
                               new VerticalSliceFactory( 125, 225, false, bucketPosition, sliceColor ),
                               new VerticalSliceFactory( 100, 200, true, bucketPosition, sliceColor ),
                               new CakeSliceFactory( new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 ) ) );
    }

    private final FractionsIntroModel model = new FractionsIntroModel( IntroState.IntroState( 6, factorySet ), factorySet );
    public final SettableProperty<PieSet> pieSet = model.pieSet;
    public final SettableProperty<PieSet> horizontalBarSet = model.horizontalBarSet;
    public final Clock clock = model.clock;
    public final IntegerProperty numerator = model.numerator;
    public final IntegerProperty denominator = model.denominator;
    public final IntegerProperty maximum = model.maximum;
    public final SettableProperty<PieSet> waterGlassSet = model.waterGlassSet;
    public final SettableProperty<Representation> leftRepresentation = model.representation;
    public final Property<Representation> rightRepresentation = new Property<Representation>( leftRepresentation.get() );
    public final IntegerProperty scale = new IntegerProperty( 2 );
    public final SettableProperty<PieSet> rightPieSet = new Property<PieSet>( pieSet.get() ) {{
        final SimpleObserver observer = new SimpleObserver() {
            @Override public void update() {
                set( scaledFactorySet.circularSliceFactory.fromContainerSetState( pieSet.get().toLazyContainerSet().scale( scale.get() ) ).createScaledCopy() );
            }
        };
        pieSet.addObserver( observer );
        scale.addObserver( observer );
    }};

    public void resetAll() {
        model.resetAll();
        scale.reset();
        rightRepresentation.reset();
    }
}
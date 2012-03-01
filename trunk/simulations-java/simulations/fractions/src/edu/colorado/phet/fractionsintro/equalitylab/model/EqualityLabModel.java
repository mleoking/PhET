// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab.model;

import fj.F;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.FactorySet;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.pieset.AbstractSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Site;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

/**
 * Model for the Equality Lab tab
 *
 * @author Sam Reid
 */
public class EqualityLabModel {

    //For filling the circular pies in the equality lab, for the primary (left side) fraction, the bottom-right pie fills first, and the design doc shows the pies filling up to the left before going to the upper row.  What about filling up next instead of to the left?  It would keep the left and right representations closer together longer and matches with the "more is up" sense in the game tab.
    private final FactorySet factorySet = new FactorySet( new Vector2D( 100, -AbstractSliceFactory.stageSize.height + 200 ), 3, 120, 85, 210, new F<Site, Site>() {
        @Override public Site f( final Site s ) {
            return s.eq( 0, 0 ) ? new Site( 1, 2 ) :
                   s.eq( 0, 1 ) ? new Site( 0, 2 ) :
                   s.eq( 0, 2 ) ? new Site( 1, 1 ) :
                   s.eq( 1, 0 ) ? new Site( 0, 1 ) :
                   s.eq( 1, 1 ) ? new Site( 1, 0 ) :
                   s.eq( 1, 2 ) ? new Site( 0, 0 ) :
                   null;
        }
    } );
    private final FactorySet mirrorFactorySet = new FactorySet( new Vector2D( 100, -AbstractSliceFactory.stageSize.height + 200 ), 3, 120, 85, 210, new F<Site, Site>() {
        @Override public Site f( final Site s ) {
            return s.eq( 0, 0 ) ? new Site( 1, 0 ) :
                   s.eq( 0, 1 ) ? new Site( 0, 0 ) :
                   s.eq( 0, 2 ) ? new Site( 1, 1 ) :
                   s.eq( 1, 0 ) ? new Site( 0, 1 ) :
                   s.eq( 1, 1 ) ? new Site( 1, 2 ) :
                   s.eq( 1, 2 ) ? new Site( 0, 2 ) :
                   null;
        }
    } );

    private final FractionsIntroModel model = new FractionsIntroModel( IntroState.IntroState( 6, factorySet ), factorySet );
    public final SettableProperty<PieSet> pieSet = model.pieSet;
    public final SettableProperty<PieSet> horizontalBarSet = model.horizontalBarSet;
    public final Clock clock = model.clock;
    public final IntegerProperty numerator = model.numerator;
    public final IntegerProperty denominator = model.denominator;
    public final IntegerProperty maximum = model.maximum;
    public final SettableProperty<PieSet> waterGlassSet = model.waterGlassSet;
    public final SettableProperty<Representation> leftRepresentation = model.representation;
    public final SettableProperty<Representation> rightRepresentation = new Property<Representation>( leftRepresentation.get() );
    public final SettableProperty<PieSet> rightPieSet = new Property<PieSet>( pieSet.get() ) {{
        //TODO: maybe move translate into mirrorFactorySet
        pieSet.addObserver( new VoidFunction1<PieSet>() {
            @Override public void apply( PieSet pieSet ) {
                set( mirrorFactorySet.CircularSliceFactory.fromContainerSetState( pieSet.toLazyContainerSet() ).translate( 475, 0 ) );
            }
        } );
    }};

    public void resetAll() {
        model.resetAll();
    }
}
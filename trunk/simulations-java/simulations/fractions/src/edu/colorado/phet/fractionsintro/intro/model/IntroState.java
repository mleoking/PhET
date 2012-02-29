// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

/**
 * Immutable state class representing the state of the model at any given instant.  Managed by FractionsIntroModel.
 *
 * @author Sam Reid
 */
@Data public class IntroState {
    public final ContainerSet containerSet;
    public final boolean showReduced;
    public final boolean showMixed;
    public final PieSet pieSet;
    public final PieSet horizontalBarSet;
    public final PieSet verticalBarSet;
    public final PieSet waterGlassSet;
    public final PieSet cakeSet;
    public final int numerator;
    public final int denominator;
    public final Representation representation;
    public final int maximum;

    public static IntroState IntroState( int maximum, FactorySet factories ) {
        int denominator = 1;
        return new IntroState( new ContainerSet( denominator, new Container[] { new Container( 1, new int[] { } ) } ).padAndTrim(), false, false,
                               new PieSet( maximum, factories.CircularSliceFactory ),
                               new PieSet( maximum, factories.HorizontalSliceFactory ),
                               new PieSet( maximum, factories.VerticalSliceFactory ),
                               new PieSet( maximum, factories.WaterGlassSetFactory ),
                               new PieSet( maximum, factories.CakeSliceFactory ), 0, 1, Representation.PIE, maximum );
    }

    public static final IntroState IntroState = IntroState( 1, FactorySet.IntroTab );

    public IntroState pieSet( PieSet pieSet ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, waterGlassSet, cakeSet, numerator, denominator, representation, maximum ); }

    public IntroState representation( Representation representation ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, waterGlassSet, cakeSet, numerator, denominator, representation, maximum ); }

    public IntroState numerator( Integer numerator ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, waterGlassSet, cakeSet, numerator, denominator, representation, maximum ); }

    public IntroState denominator( Integer denominator ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, waterGlassSet, cakeSet, numerator, denominator, representation, maximum ); }

    public IntroState containerSet( ContainerSet containerSet ) { return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, waterGlassSet, cakeSet, numerator, denominator, representation, maximum ); }

    public IntroState horizontalBarSet( PieSet horizontalBarSet ) {return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, waterGlassSet, cakeSet, numerator, denominator, representation, maximum );}

    public IntroState verticalBarSet( PieSet verticalBarSet ) {return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, waterGlassSet, cakeSet, numerator, denominator, representation, maximum );}

    public IntroState maximum( int maximum ) {return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, waterGlassSet, cakeSet, numerator, denominator, representation, maximum );}

    public IntroState waterGlassSet( PieSet waterGlassSet ) {return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, waterGlassSet, cakeSet, numerator, denominator, representation, maximum );}

    public IntroState cakeSet( PieSet cakeSet ) {return new IntroState( containerSet, showReduced, showMixed, pieSet, horizontalBarSet, verticalBarSet, waterGlassSet, cakeSet, numerator, denominator, representation, maximum );}

    //Apply an update rule to all of the pie sets, and updates the container set to match
    public IntroState updatePieSets( F<PieSet, PieSet> f ) {
        final PieSet newPieSet = f.f( pieSet );
        return pieSet( newPieSet ).
                horizontalBarSet( f.f( horizontalBarSet ) ).
                verticalBarSet( f.f( verticalBarSet ) ).
                waterGlassSet( f.f( waterGlassSet ) ).
                cakeSet( f.f( cakeSet ) ).
                containerSet( newPieSet.toContainerSet() );
    }

    //Update all representations from the specified container set
    public IntroState fromContainerSet( ContainerSet c, FactorySet factorySet ) {
        return pieSet( factorySet.CircularSliceFactory.fromContainerSetState( c ) ).
                horizontalBarSet( factorySet.HorizontalSliceFactory.fromContainerSetState( c ) ).
                verticalBarSet( factorySet.VerticalSliceFactory.fromContainerSetState( c ) ).
                waterGlassSet( factorySet.WaterGlassSetFactory.fromContainerSetState( c ) ).
                cakeSet( factorySet.CakeSliceFactory.fromContainerSetState( c ) );
    }
}
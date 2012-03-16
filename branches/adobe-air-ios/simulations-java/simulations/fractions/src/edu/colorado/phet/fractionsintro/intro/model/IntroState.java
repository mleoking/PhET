// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;
import fj.Function;
import lombok.Data;

import java.awt.Color;

import edu.colorado.phet.fractions.util.immutable.Dimension2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.model.containerset.Container;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Site;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.CakeSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.CircularSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.FactorySet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.HorizontalSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.SliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.VerticalSliceFactory;
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

    //Initial state
    public static final IntroState INTRO_STATE = newState( 1, createFactorySet() );

    public static IntroState newState( int maximum, FactorySet factories ) {
        int denominator = 1;
        return new IntroState( new ContainerSet( denominator, new Container[] { new Container( 1, new int[] { } ) } ).padAndTrim(), false, false,
                               new PieSet( maximum, factories.circularSliceFactory ),
                               new PieSet( maximum, factories.horizontalSliceFactory ),
                               new PieSet( maximum, factories.verticalSliceFactory ),
                               new PieSet( maximum, factories.waterGlassSetFactory ),
                               new PieSet( maximum, factories.cakeSliceFactory ), 0, 1, Representation.PIE, maximum );
    }

    //Copy methods
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
        return pieSet( factorySet.circularSliceFactory.fromContainerSetState( c ) ).
                horizontalBarSet( factorySet.horizontalSliceFactory.fromContainerSetState( c ) ).
                verticalBarSet( factorySet.verticalSliceFactory.fromContainerSetState( c ) ).
                waterGlassSet( factorySet.waterGlassSetFactory.fromContainerSetState( c ) ).
                cakeSet( factorySet.cakeSliceFactory.fromContainerSetState( c ) );
    }

    //Create the factory set for the intro tab
    public static FactorySet createFactorySet() {
        final Vector2D bucketPosition = new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 + 20 );
        int numPerRow = 6;
        double pieDiameter = 155;
        double pieX = 0;
        double pieY = 250 + 20;

        final F<Site, Site> siteMap = Function.identity();
        final Color sliceColor = AbstractFractionsCanvas.LIGHT_GREEN;

        final double distanceBetweenBars = 20 * 1.3;
        Dimension2D bucketSize = new Dimension2D( 350, 135 );
        return new FactorySet( new CircularSliceFactory( numPerRow, bucketPosition, bucketSize, pieDiameter, pieX, pieY, siteMap, sliceColor ),
                               new HorizontalSliceFactory( bucketPosition, bucketSize, sliceColor ),
                               new VerticalSliceFactory( -35.5, 125, 200, false, bucketPosition, bucketSize, sliceColor, distanceBetweenBars ),
                               new VerticalSliceFactory( 60, 100, 200, true, bucketPosition, bucketSize, sliceColor, distanceBetweenBars ),
                               new CakeSliceFactory( bucketPosition, bucketSize ) );
    }
}
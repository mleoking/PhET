// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.common.model.Solvent.Water;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Simple model of a solution, consisting of a solvent and a solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solution implements IFluid, Resettable {

    public final Solvent solvent;
    public final Property<Solute> solute;
    public final Property<Double> soluteAmount; // moles
    public final Property<Double> volume; // L
    public final CompositeProperty<Double> concentration; // M (derived property)
    public final CompositeProperty<Double> precipitateAmount; // moles (derived property)
    private final Property<Color> fluidColor; // derived from solute color and concentration //TODO use CompositeProperty

    public Solution( Property<Solute> solute, double soluteAmount, double volume ) {

        this.solvent = new Water();
        this.solute = solute;
        this.soluteAmount = new Property<Double>( soluteAmount );
        this.volume = new Property<Double>( volume );
        this.fluidColor = new Property<Color>( Color.WHITE ); // will be properly initialized when colorUpdater is registered

        // derive concentration
        this.concentration = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                final double volume = Solution.this.volume.get();
                final double soluteAmount = Solution.this.soluteAmount.get();
                if ( volume > 0 ) {
                    return Math.min( getSaturatedConcentration(), soluteAmount / volume ); // M = mol/L
                }
                else {
                    return 0d;
                }
            }
        }, this.solute, this.soluteAmount, this.volume );

        // derive amount of precipitate
        this.precipitateAmount = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                final double volume = Solution.this.volume.get();
                final double soluteAmount = Solution.this.soluteAmount.get();
                if ( volume > 0 ) {
                    return Math.max( 0, volume * ( ( Solution.this.soluteAmount.get() / volume ) - getSaturatedConcentration() ) );
                }
                else {
                    return soluteAmount;
                }
            }
        }, this.solute, this.soluteAmount, this.volume );

        // derive the solution color
        RichSimpleObserver colorUpdater = new RichSimpleObserver() {
            @Override public void update() {
                fluidColor.set( createColor( solvent, Solution.this.solute.get(), concentration.get() ) );
            }
        };
        colorUpdater.observe( this.solute, this.concentration );
    }

    // Convenience method
    public double getSaturatedConcentration() {
        return solute.get().saturatedConcentration;
    }

    public boolean isSaturated() {
        boolean saturated = false;
        if ( volume.get() > 0 ) {
            saturated = ( soluteAmount.get() / volume.get() ) > getSaturatedConcentration();
        }
        return saturated;
    }

    public Color getFluidColor() {
        return fluidColor.get();
    }

    public void addFluidColorObserver( SimpleObserver observer ) {
        fluidColor.addObserver( observer );
    }

    public int getNumberOfPrecipitateParticles() {
        int numberOfParticles = (int) ( solute.get().particlesPerMole * precipitateAmount.get() );
        if ( numberOfParticles == 0 && precipitateAmount.get() > 0 ) {
            numberOfParticles = 1;
        }
        return numberOfParticles;
    }

    public void reset() {
        soluteAmount.reset();
        volume.reset();
    }

    public static final Color createColor( Solvent solvent, Solute solute, double concentration ) {
        Color color = solvent.getFluidColor();
        if ( concentration > 0 ) {
            LinearFunction f = new LinearFunction( 0, solute.saturatedConcentration, 0, 1 );
            color = solute.solutionColor.interpolateLinear( f.evaluate( concentration ) );
        }
        return color;
    }
}

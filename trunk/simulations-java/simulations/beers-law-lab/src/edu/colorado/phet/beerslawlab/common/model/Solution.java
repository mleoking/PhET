// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.common.model.Solvent.Water;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

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
    private final Property<Double> concentration; // M (derived property)
    private final Property<Double> precipitateAmount; // moles (derived property)
    private final Property<Color> fluidColor; // derived from solute color and concentration

    public Solution( Property<Solute> solute, double soluteAmount, double volume ) {

        this.solvent = new Water();
        this.solute = solute;
        this.soluteAmount = new Property<Double>( soluteAmount );
        this.volume = new Property<Double>( volume );

        this.concentration = new Property<Double>( 0d );
        this.precipitateAmount = new Property<Double>( 0d );
        this.fluidColor = new Property<Color>( Color.WHITE ); // will be properly initialized when colorUpdater is registered

        // derive the concentration and precipitate amount
        RichSimpleObserver concentrationUpdater = new RichSimpleObserver() {
            public void update() {
                if ( getVolume() > 0 ) {
                    concentration.set( Math.min( getSaturatedConcentration(), getSoluteAmount() / getVolume() ) ); // M = mol/L
                    precipitateAmount.set( Math.max( 0, getVolume() * ( ( getSoluteAmount() / getVolume() ) - getSaturatedConcentration() ) ) );
                }
                else {
                    concentration.set( 0d );
                    precipitateAmount.set( getSoluteAmount() );
                }
            }
        };
        concentrationUpdater.observe( this.solute, this.soluteAmount, this.volume );

        // derive the solution color
        RichSimpleObserver colorUpdater = new RichSimpleObserver() {
            @Override public void update() {
                fluidColor.set( createColor( solvent, Solution.this.solute.get(), concentration.get() ) );
            }
        };
        colorUpdater.observe( this.solute, this.concentration );
    }

    private double getSoluteAmount() {
        return soluteAmount.get();
    }

    private double getVolume() {
        return volume.get();
    }

    public double getSaturatedConcentration() {
        return solute.get().saturatedConcentration;
    }

    public boolean isSaturated() {
        boolean saturated = false;
        if ( getVolume() > 0 ) {
            saturated = ( getSoluteAmount() / getVolume() ) > getSaturatedConcentration();
        }
        return saturated;
    }

    public double getConcentration() {
        return concentration.get();
    }

    public void addConcentrationObserver( SimpleObserver observer ) {
        concentration.addObserver( observer );
    }

    public double getPrecipitateAmount() {
        return precipitateAmount.get();
    }

    public void addPrecipitateAmountObserver( SimpleObserver observer ) {
        precipitateAmount.addObserver( observer );
    }

    public Color getFluidColor() {
        return fluidColor.get();
    }

    public void addFluidColorObserver( SimpleObserver observer ) {
        fluidColor.addObserver( observer );
    }

    public int getNumberOfPrecipitateParticles() {
        int numberOfParticles = (int) ( solute.get().particlesPerMole * getPrecipitateAmount() );
        if ( numberOfParticles == 0 && getPrecipitateAmount() > 0 ) {
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

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.BLLConstants;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;

/**
 * Simple model of a solution, consisting of water (the solvent) and a solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solution implements Resettable {

    public final Property<Solute> solute;
    public final Property<Double> soluteAmount; // moles
    public final Property<Double> volume; // L
    private final Property<Double> concentration; // M (derived property)
    private final Property<Double> precipitateAmount; // moles (derived property)
    private final Property<Color> solutionColor; // derived from solute color and concentration

    public Solution( Property<Solute> solute, double soluteAmount, double volume ) {

        this.solute = solute;
        this.soluteAmount = new Property<Double>( soluteAmount );
        this.volume = new Property<Double>( volume );

        this.concentration = new Property<Double>( 0d );
        this.precipitateAmount = new Property<Double>( 0d );
        this.solutionColor = new Property<Color>( Color.WHITE ); // will be properly initialized when colorUpdater is registered

        // derive the concentration and precipitate amount
        RichSimpleObserver concentrationUpdater = new RichSimpleObserver() {
            public void update() {
                if ( getVolume() > 0 ) {
                    concentration.set( Math.min( getSaturatedConcentration(), getSoluteAmount() / getVolume() ) ); // M = mol/L
                }
                else {
                    concentration.set( 0d );
                }
                precipitateAmount.set( Math.max( 0, getVolume() * ( ( getSoluteAmount() / getVolume() ) - getSaturatedConcentration() ) ) );
            }
        };
        concentrationUpdater.observe( this.solute, this.soluteAmount, this.volume );

        // derive the solution color
        RichSimpleObserver colorUpdater = new RichSimpleObserver() {
            @Override public void update() {
                solutionColor.set( createColor( Solution.this.solute.get(), concentration.get() ) );
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
        return ( getSoluteAmount() / getVolume() ) > getSaturatedConcentration();
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

    public Color getSolutionColor() {
        return solutionColor.get();
    }

    public void addSolutionColorObserver( SimpleObserver observer ) {
        solutionColor.addObserver( observer );
    }

    public void reset() {
        solute.reset();
        soluteAmount.reset();
        volume.reset();
    }

    private static final Color createColor( Solute solute, double concentration ) {
        LinearFunction f = new LinearFunction( 0, solute.saturatedConcentration, 0, 1 );
        double colorScale = f.evaluate( concentration );
        return ColorUtils.interpolateRBGA( BLLConstants.WATER_COLOR, solute.solutionColor, colorScale );
    }
}

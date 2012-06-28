// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.beaker;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Simple model of a solution
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solution implements Resettable {

    public final Property<Solute> solute;
    public final Property<Double> soluteAmount; // moles
    public final Property<Double> volume; // Liters
    private final Property<Double> concentration; // molarity, moles/Liter (derived property)
    private final Property<Double> precipitateAmount; // moles (derived property)

    public Solution( Solute solute, double soluteAmount, double volume ) {

        this.solute = new Property<Solute>( solute );
        this.soluteAmount = new Property<Double>( soluteAmount );
        this.volume = new Property<Double>( volume );

        this.concentration = new Property<Double>( 0d );
        this.precipitateAmount = new Property<Double>( 0d );

        RichSimpleObserver observer = new RichSimpleObserver() {
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
        observer.observe( this.solute, this.soluteAmount, this.volume );
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

    public double getConcentration() {
        return concentration.get();
    }

    public void addConcentrationObserver( SimpleObserver observer ) {
        concentration.addObserver( observer );
    }

    public void addPrecipitateAmountObserver( SimpleObserver observer ) {
        precipitateAmount.addObserver( observer );
    }

    public void reset() {
        solute.reset();
        soluteAmount.reset();
        volume.reset();
    }
}

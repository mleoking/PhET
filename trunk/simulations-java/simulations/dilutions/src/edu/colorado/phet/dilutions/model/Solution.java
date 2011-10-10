// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.model;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

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
                concentration.set( getSoluteAmount() / getVolume() ); // M = mol/L
                precipitateAmount.set( Math.max( 0, getVolume() * ( getConcentration() - getSoluteMaxConcentration() ) ) );
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

    private double getSoluteMaxConcentration() {
        return solute.get().maxConcentration;
    }

    public double getConcentration() {
        return concentration.get();
    }

    public void addConcentrationObserver( VoidFunction1<Double> observer ) {
        concentration.addObserver( observer );
    }

    public double getPrecipitateAmount() {
        return precipitateAmount.get();
    }

    public void addPrecipitateAmountObserver( VoidFunction1<Double> observer ) {
        precipitateAmount.addObserver( observer );
    }

    public void reset() {
        solute.reset();
        soluteAmount.reset();
        volume.reset();
    }
}

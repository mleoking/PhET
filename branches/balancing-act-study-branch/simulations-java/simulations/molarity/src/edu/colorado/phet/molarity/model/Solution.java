// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.model;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Simple model of a solution
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solution implements Resettable {

    public final Solvent solvent;
    public final Property<Solute> solute;
    public final Property<Double> soluteAmount; // moles
    public final Property<Double> volume; // Liters
    public final CompositeProperty<Double> concentration; // molarity, moles/Liter (derived property)
    public final CompositeProperty<Double> precipitateAmount; // moles (derived property)

    public Solution( Solvent solvent, Solute solute, double soluteAmount, double volume ) {

        this.solvent = solvent;
        this.solute = new Property<Solute>( solute );
        this.soluteAmount = new Property<Double>( soluteAmount );
        this.volume = new Property<Double>( volume );

        // derive concentration
        this.concentration = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                final double volume = Solution.this.volume.get();
                Double soluteAmount = Solution.this.soluteAmount.get();
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
                Double soluteAmount = Solution.this.soluteAmount.get();
                if ( volume > 0 ) {
                    return Math.max( 0, volume * ( ( soluteAmount / volume ) - getSaturatedConcentration() ) );
                }
                else {
                    return soluteAmount;
                }
            }
        }, this.solute, this.soluteAmount, this.volume );
    }

    // Convenience method
    public double getSaturatedConcentration() {
        return solute.get().saturatedConcentration;
    }

    public boolean isSaturated() {
        return precipitateAmount.get() != 0;
    }

    public void reset() {
        solute.reset();
        soluteAmount.reset();
        volume.reset();
        // concentration and precipitateAmount are derived
    }
}

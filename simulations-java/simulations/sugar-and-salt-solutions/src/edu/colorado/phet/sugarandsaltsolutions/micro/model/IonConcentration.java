// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.numberToMoles;

/**
 * Observable property that gives the concentration in mol/m^3 for specific dissolved components (such as Na+ or sucrose)
 *
 * @author Sam Reid
 */
public class IonConcentration extends CompositeDoubleProperty {
    public IonConcentration( final MicroModel microModel, final Class<? extends Particle> type ) {
        super( new Function0<Double>() {
                   public Double apply() {
                       //If there is no water, there is no solution and hence no concentration
                       return microModel.waterVolume.get() == 0 ? 0.0 : numberToMoles( microModel.freeParticles.count( type ) ) / microModel.waterVolume.get();
                   }
               }, microModel.waterVolume );
        VoidFunction1<Particle> listener = new VoidFunction1<Particle>() {
            public void apply( Particle particle ) {
                notifyIfChanged();
            }
        };
        microModel.freeParticles.addItemAddedListener( listener );
        microModel.freeParticles.addItemRemovedListener( listener );
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ethanol.Ethanol;

import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.numberToMoles;

/**
 * Observable property that gives the concentration in mol/m^3 for Ethanol.  This differs from IonConcentration because ethanol
 * enters the model in the freeParticles list, but unlike other particle types should only be counted toward concentration once it is submerged.  Other particles only enter the freeParticle list when submerged.
 * This value therefore also needs to update whenever ethanol particles move, since they could enter the solution, so a listener is attached to the model
 *
 * @author Sam Reid
 */
public class EthanolConcentration extends CompositeDoubleProperty {
    public EthanolConcentration( final MicroModel microModel ) {
        super( new Function0<Double>() {
                   public Double apply() {
                       //If there is no water, there is no solution and hence no concentration
                       return microModel.waterVolume.get() == 0 ? 0.0 : numberToMoles( microModel.freeParticles.filter( new Function1<Particle, Boolean>() {
                           public Boolean apply( Particle particle ) {

                               //Count the ethanol particles that are fully submerged
                               return Ethanol.class.isInstance( particle ) && microModel.solution.shape.get().getBounds2D().contains( particle.getShape().getBounds2D() );
                           }
                       } ).size() ) / microModel.waterVolume.get();
                   }
               }, microModel.waterVolume );
        final VoidFunction1<Particle> listener = new VoidFunction1<Particle>() {
            public void apply( Particle particle ) {
                notifyIfChanged();
            }
        };
        microModel.freeParticles.addElementAddedObserver( listener );
        microModel.freeParticles.addElementRemovedObserver( listener );

        //This value also needs to update whenever ethanol particles move, since they could enter the solution
        microModel.stepFinishedListeners.add( new VoidFunction0() {
            public void apply() {
                listener.apply( null );
            }
        } );
    }
}
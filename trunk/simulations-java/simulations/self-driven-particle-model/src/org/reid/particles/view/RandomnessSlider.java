/* Copyright 2004, Sam Reid */
package org.reid.particles.view;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import org.reid.particles.model.ParticleModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Aug 21, 2005
 * Time: 11:05:16 AM
 * Copyright (c) Aug 21, 2005 by Sam Reid
 */

public class RandomnessSlider extends ModelSlider {
    private ParticleModel particleModel;

    public RandomnessSlider( final ParticleModel particleModel ) {
        super( "Randomness", "radians", 0, Math.PI * 2, particleModel.getRandomness(), new DecimalFormat( "0.00" ) );
        this.particleModel = particleModel;
        setModelTicks( new double[]{0, Math.PI, Math.PI * 2} );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                getParticleModel().setRandomness( getValue() );
            }
        } );
        getParticleModel().addListener( new ParticleModel.Adapter() {
            public void randomnessChanged() {
                setValue( getParticleModel().getRandomness() );
            }
        } );
    }

    private ParticleModel getParticleModel() {
        return particleModel;
    }

//    final ModelSlider randomnessSlider = new ModelSlider( "Randomness", "radians", 0, Math.PI * 2, model.getAngleRandomness(), new DecimalFormat( "0.00" ) );

}
